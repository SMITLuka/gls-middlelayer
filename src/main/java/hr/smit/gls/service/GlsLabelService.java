package hr.smit.gls.service;

import hr.smit.gls.client.GlsApiClient;
import hr.smit.gls.client.GlsBinaryUtil;
import hr.smit.gls.client.GlsDateFormat;
import hr.smit.gls.client.GlsPasswordEncoder;
import hr.smit.gls.config.GlsProperties;
import hr.smit.gls.dto.common.Address;
import hr.smit.gls.dto.common.ErrorInfo;
import hr.smit.gls.dto.common.GlsService;
import hr.smit.gls.dto.common.Parcel;
import hr.smit.gls.dto.common.ParcelStatus;
import hr.smit.gls.dto.common.PrintLabelsInfo;
import hr.smit.gls.dto.request.DeleteLabelsRequest;
import hr.smit.gls.dto.request.GetParcelStatusesRequest;
import hr.smit.gls.dto.request.ModifyCODRequest;
import hr.smit.gls.dto.request.PrintLabelsRequest;
import hr.smit.gls.dto.response.DeleteLabelsResponse;
import hr.smit.gls.dto.response.GetParcelStatusResponse;
import hr.smit.gls.dto.response.ModifyCODResponse;
import hr.smit.gls.dto.response.PrintLabelsResponse;
import hr.smit.gls.mock.MockPdfGenerator;
import hr.smit.gls.model.LabelRequest;
import hr.smit.gls.model.LabelResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Middleware-facing operations for GLS shipping labels - this is what a Pantheon
 * connector (or a "KREIRAJ GLS NALJEPNICU" button handler) is expected to call.
 * Translates the simplified {@link LabelRequest} into the GLS {@link Parcel} wire
 * format and back into a plain {@link LabelResult}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlsLabelService {

    private static final String COD_SERVICE_CODE = "COD";

    private final GlsApiClient glsApiClient;
    private final GlsProperties properties;

    /** Creates and immediately prints a label (PrintLabels = PrepareLabels + GetPrintedLabels). */
    public LabelResult createLabel(LabelRequest request) {
        if (properties.isMockEnabled()) {
            return createMockLabel(request);
        }

        requireGlsCredentials();
        log.info("Kreiranje GLS naljepnice za clientReference={}", request.clientReference());

        PrintLabelsRequest apiRequest = PrintLabelsRequest.builder()
                .username(properties.getUsername())
                .password(GlsPasswordEncoder.toUnsignedByteArray(properties.getPassword()))
                .webshopEngine(properties.getWebshopEngine())
                .parcelList(List.of(buildParcel(request)))
                .printPosition(1)
                .showPrintDialog(false)
                .build();

        PrintLabelsResponse response = glsApiClient.printLabels(apiRequest);

        if (response == null) {
            log.warn("Prazan odgovor od MyGLS servisa za clientReference={}", request.clientReference());
            return LabelResult.failure(List.of("Prazan odgovor od MyGLS servisa."));
        }
        if (hasErrors(response.getPrintLabelsErrorList())) {
            List<String> errors = describeErrors(response.getPrintLabelsErrorList());
            log.warn("MyGLS PrintLabels greške za clientReference={}: {}", request.clientReference(), errors);
            return LabelResult.failure(errors);
        }

        PrintLabelsInfo info = firstOrNull(response.getPrintLabelsInfoList());
        byte[] pdf = GlsBinaryUtil.toBytes(response.getLabels());

        log.info("GLS naljepnica kreirana za clientReference={}: parcelId={}, parcelNumber={}",
                request.clientReference(), info != null ? info.getParcelId() : null, info != null ? info.getParcelNumber() : null);

        return LabelResult.success(
                info != null ? info.getParcelId() : null,
                info != null ? info.getParcelNumber() : null,
                pdf
        );
    }

    /** Cancels (DELETED state) a previously created label, e.g. when an order is cancelled. */
    public boolean cancelLabel(int parcelId) {
        requireGlsCredentials();
        log.info("Storniranje GLS naljepnice parcelId={}", parcelId);

        DeleteLabelsRequest request = DeleteLabelsRequest.builder()
                .username(properties.getUsername())
                .password(GlsPasswordEncoder.toUnsignedByteArray(properties.getPassword()))
                .webshopEngine(properties.getWebshopEngine())
                .parcelIdList(List.of(parcelId))
                .build();

        DeleteLabelsResponse response = glsApiClient.deleteLabels(request);
        boolean deleted = response != null
                && !hasErrors(response.getDeleteLabelsErrorList())
                && response.getSuccessfullyDeletedList() != null
                && !response.getSuccessfullyDeletedList().isEmpty();

        if (!deleted) {
            log.warn("Storniranje GLS naljepnice nije uspjelo za parcelId={}: {}", parcelId,
                    response != null ? describeErrors(response.getDeleteLabelsErrorList()) : "prazan odgovor");
        }
        return deleted;
    }

    /** Updates the COD amount after the label was already created. */
    public boolean updateCod(int parcelId, BigDecimal newAmount) {
        requireGlsCredentials();
        log.info("Izmjena otkupnine parcelId={} na iznos={}", parcelId, newAmount);

        ModifyCODRequest request = ModifyCODRequest.builder()
                .username(properties.getUsername())
                .password(GlsPasswordEncoder.toUnsignedByteArray(properties.getPassword()))
                .webshopEngine(properties.getWebshopEngine())
                .parcelId(parcelId)
                .codAmount(newAmount)
                .build();

        ModifyCODResponse response = glsApiClient.modifyCod(request);
        boolean updated = response != null && response.isSuccessful();

        if (!updated) {
            log.warn("Izmjena otkupnine nije uspjela za parcelId={}: {}", parcelId,
                    response != null ? describeErrors(response.getModifyCODError()) : "prazan odgovor");
        }
        return updated;
    }

    /** Tracking: current status history for a parcel number. */
    public List<ParcelStatus> getStatus(long parcelNumber) {
        requireGlsCredentials();
        log.info("Dohvat statusa za parcelNumber={}", parcelNumber);

        GetParcelStatusesRequest request = GetParcelStatusesRequest.builder()
                .username(properties.getUsername())
                .password(GlsPasswordEncoder.toUnsignedByteArray(properties.getPassword()))
                .webshopEngine(properties.getWebshopEngine())
                .parcelNumber(parcelNumber)
                .returnPOD(false)
                .languageIsoCode("HR")
                .build();

        GetParcelStatusResponse response = glsApiClient.getParcelStatuses(request);
        if (response == null || response.getParcelStatusList() == null) {
            return List.of();
        }
        return response.getParcelStatusList();
    }

    /**
     * gls.mock-enabled=true path - no GLS credentials, no network call. Returns a
     * synthetic PDF and a fake parcel number so the Pantheon/ARES side (JSON body,
     * response headers, acfield write-back, PDF open) can be tested end-to-end while
     * real MyGLS credentials/agreement are still pending.
     */
    private LabelResult createMockLabel(LabelRequest request) {
        int fakeParcelId = (int) (System.currentTimeMillis() % 100_000);
        long fakeParcelNumber = 900_000_000_000L + (System.currentTimeMillis() % 1_000_000_000L);

        log.info("[MOCK] Kreiranje GLS naljepnice za clientReference={} -> parcelId={}, parcelNumber={}",
                request.clientReference(), fakeParcelId, fakeParcelNumber);

        byte[] pdf = MockPdfGenerator.generate(request.clientReference(), fakeParcelNumber);
        return LabelResult.success(fakeParcelId, fakeParcelNumber, pdf);
    }

    private Parcel buildParcel(LabelRequest request) {
        Address delivery = Address.builder()
                .name(request.recipient().name())
                .street(request.recipient().street())
                .houseNumber(request.recipient().houseNumber())
                .houseNumberInfo(request.recipient().houseNumberInfo())
                .city(request.recipient().city())
                .zipCode(request.recipient().zipCode())
                .countryIsoCode(orDefault(request.recipient().countryIsoCode(), "HR"))
                .contactName(request.recipient().contactName())
                .contactPhone(request.recipient().contactPhone())
                .contactEmail(request.recipient().contactEmail())
                .build();

        GlsProperties.PickupAddress pickupCfg = properties.getPickupAddress();
        Address pickup = Address.builder()
                .name(pickupCfg.getName())
                .street(pickupCfg.getStreet())
                .houseNumber(pickupCfg.getHouseNumber())
                .houseNumberInfo(pickupCfg.getHouseNumberInfo())
                .city(pickupCfg.getCity())
                .zipCode(pickupCfg.getZipCode())
                .countryIsoCode(pickupCfg.getCountryIsoCode())
                .contactName(pickupCfg.getContactName())
                .contactPhone(pickupCfg.getContactPhone())
                .contactEmail(pickupCfg.getContactEmail())
                .build();

        boolean hasCod = request.codAmount() != null && request.codAmount().compareTo(BigDecimal.ZERO) > 0;
        List<GlsService> services = new ArrayList<>();
        if (hasCod) {
            services.add(GlsService.builder().code(COD_SERVICE_CODE).build());
        }

        return Parcel.builder()
                .clientNumber(properties.getClientNumber())
                .clientReference(request.clientReference())
                .count(1)
                .codAmount(hasCod ? request.codAmount() : BigDecimal.ZERO)
                .codReference(hasCod ? orDefault(request.codReference(), request.clientReference()) : null)
                .content(request.content())
                .pickupDate(GlsDateFormat.toGlsDate(request.pickupDate() != null ? request.pickupDate() : LocalDate.now()))
                .pickupAddress(pickup)
                .deliveryAddress(delivery)
                .serviceList(services)
                .build();
    }

    private static boolean hasErrors(List<ErrorInfo> errors) {
        return errors != null && !errors.isEmpty();
    }

    private static List<String> describeErrors(List<ErrorInfo> errors) {
        if (errors == null) {
            return List.of();
        }
        return errors.stream()
                .map(e -> "[" + e.getErrorCode() + "] " + e.getErrorDescription())
                .toList();
    }

    /**
     * Fails fast with a clear message instead of letting a missing username/password
     * surface as a raw NullPointerException inside {@link GlsPasswordEncoder}.
     */
    private void requireGlsCredentials() {
        if (isBlank(properties.getUsername()) || isBlank(properties.getPassword())) {
            throw new IllegalStateException(
                    "GLS kredencijali nisu konfigurirani - popuni gls.username i gls.password (application.yml ili application-local.yml).");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static <T> T firstOrNull(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    private static String orDefault(String value, String fallback) {
        return value != null && !value.isBlank() ? value : fallback;
    }
}
