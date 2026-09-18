package hr.smit.gls.service;

import hr.smit.gls.client.GlsApiClient;
import hr.smit.gls.config.GlsProperties;
import hr.smit.gls.dto.common.ErrorInfo;
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
import hr.smit.gls.dto.common.SuccessfullyDeleted;
import hr.smit.gls.model.LabelRequest;
import hr.smit.gls.model.LabelResult;
import hr.smit.gls.model.RecipientInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GlsLabelServiceTest {

    private GlsApiClient glsApiClient;
    private GlsProperties properties;
    private GlsLabelService service;

    @BeforeEach
    void setUp() {
        glsApiClient = mock(GlsApiClient.class);
        properties = new GlsProperties();
        properties.setUsername("api@hairhouse.hr");
        properties.setPassword("test-password");
        properties.setClientNumber(123456);
        properties.setWebshopEngine("custom");

        GlsProperties.PickupAddress pickup = properties.getPickupAddress();
        pickup.setName("Hair House Professional");
        pickup.setStreet("Skladišna");
        pickup.setHouseNumber("1");
        pickup.setCity("Zagreb");
        pickup.setZipCode("10000");
        pickup.setCountryIsoCode("HR");

        service = new GlsLabelService(glsApiClient, properties);
    }

    private LabelRequest sampleRequest(BigDecimal codAmount) {
        RecipientInfo recipient = new RecipientInfo(
                "Ana Anić", "Ilica", "12", null, "Zagreb", "10000", "HR",
                null, "+385911234567", null
        );
        return new LabelRequest("NAR-2026-00001", recipient, codAmount, null, "Kozmetika", null);
    }

    @Test
    void createLabel_mockEnabled_returnsSyntheticLabelWithoutCallingGlsOrCredentials() {
        properties.setMockEnabled(true);
        properties.setUsername(null);
        properties.setPassword(null);

        LabelResult result = service.createLabel(sampleRequest(new BigDecimal("10.00")));

        assertThat(result.success()).isTrue();
        assertThat(result.parcelId()).isNotNull();
        assertThat(result.parcelNumber()).isNotNull();
        assertThat(result.labelPdf()).isNotEmpty();
        assertThat(new String(result.labelPdf(), java.nio.charset.StandardCharsets.US_ASCII)).startsWith("%PDF-1.4");
        verifyNoInteractions(glsApiClient);
    }

    @Test
    void createLabel_missingCredentials_throwsIllegalStateInsteadOfNpe() {
        properties.setUsername(null);
        properties.setPassword(null);

        assertThrows(IllegalStateException.class, () -> service.createLabel(sampleRequest(null)));
        verifyNoInteractions(glsApiClient);
    }

    @Test
    void createLabel_withCod_addsCodServiceAndReturnsPdf() {
        PrintLabelsInfo info = new PrintLabelsInfo();
        info.setParcelId(555);
        info.setParcelNumber(987654321L);
        info.setClientReference("NAR-2026-00001");

        PrintLabelsResponse response = new PrintLabelsResponse();
        response.setLabels(List.of(37, 80, 68, 70)); // "%PDF"
        response.setPrintLabelsInfoList(List.of(info));
        response.setPrintLabelsErrorList(List.of());

        when(glsApiClient.printLabels(any(PrintLabelsRequest.class))).thenReturn(response);

        LabelResult result = service.createLabel(sampleRequest(new BigDecimal("42.90")));

        assertThat(result.success()).isTrue();
        assertThat(result.parcelId()).isEqualTo(555);
        assertThat(result.parcelNumber()).isEqualTo(987654321L);
        assertThat(new String(result.labelPdf())).isEqualTo("%PDF");

        ArgumentCaptor<PrintLabelsRequest> captor = ArgumentCaptor.forClass(PrintLabelsRequest.class);
        org.mockito.Mockito.verify(glsApiClient).printLabels(captor.capture());
        Parcel parcel = captor.getValue().getParcelList().get(0);

        assertThat(parcel.getCodAmount()).isEqualByComparingTo("42.90");
        assertThat(parcel.getServiceList()).extracting("code").containsExactly("COD");
        assertThat(parcel.getDeliveryAddress().getName()).isEqualTo("Ana Anić");
        assertThat(parcel.getPickupAddress().getCity()).isEqualTo("Zagreb");
        assertThat(parcel.getClientNumber()).isEqualTo(123456);
    }

    @Test
    void createLabel_withoutCod_noServiceAndZeroAmount() {
        PrintLabelsResponse response = new PrintLabelsResponse();
        response.setLabels(List.of());
        response.setPrintLabelsInfoList(List.of());
        response.setPrintLabelsErrorList(List.of());
        when(glsApiClient.printLabels(any(PrintLabelsRequest.class))).thenReturn(response);

        service.createLabel(sampleRequest(null));

        ArgumentCaptor<PrintLabelsRequest> captor = ArgumentCaptor.forClass(PrintLabelsRequest.class);
        org.mockito.Mockito.verify(glsApiClient).printLabels(captor.capture());
        Parcel parcel = captor.getValue().getParcelList().get(0);

        assertThat(parcel.getServiceList()).isEmpty();
        assertThat(parcel.getCodAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createLabel_apiReturnsErrors_returnsFailureWithDescriptions() {
        ErrorInfo error = new ErrorInfo();
        error.setErrorCode(23);
        error.setErrorDescription("The house number cannot be 0");

        PrintLabelsResponse response = new PrintLabelsResponse();
        response.setPrintLabelsErrorList(List.of(error));
        when(glsApiClient.printLabels(any(PrintLabelsRequest.class))).thenReturn(response);

        LabelResult result = service.createLabel(sampleRequest(null));

        assertThat(result.success()).isFalse();
        assertThat(result.errors()).containsExactly("[23] The house number cannot be 0");
    }

    @Test
    void createLabel_nullResponse_returnsFailure() {
        when(glsApiClient.printLabels(any(PrintLabelsRequest.class))).thenReturn(null);

        LabelResult result = service.createLabel(sampleRequest(null));

        assertThat(result.success()).isFalse();
        assertThat(result.errors()).isNotEmpty();
    }

    @Test
    void cancelLabel_successfulDeletion_returnsTrue() {
        SuccessfullyDeleted deleted = new SuccessfullyDeleted();
        deleted.setParcelId(555);
        DeleteLabelsResponse response = new DeleteLabelsResponse();
        response.setDeleteLabelsErrorList(List.of());
        response.setSuccessfullyDeletedList(List.of(deleted));
        when(glsApiClient.deleteLabels(any(DeleteLabelsRequest.class))).thenReturn(response);

        assertThat(service.cancelLabel(555)).isTrue();
    }

    @Test
    void cancelLabel_withErrors_returnsFalse() {
        ErrorInfo error = new ErrorInfo();
        error.setErrorCode(4);
        error.setErrorDescription("Parcel ID not exists");
        DeleteLabelsResponse response = new DeleteLabelsResponse();
        response.setDeleteLabelsErrorList(List.of(error));
        when(glsApiClient.deleteLabels(any(DeleteLabelsRequest.class))).thenReturn(response);

        assertThat(service.cancelLabel(999)).isFalse();
    }

    @Test
    void updateCod_successful_returnsTrue() {
        ModifyCODResponse response = new ModifyCODResponse();
        response.setSuccessful(true);
        when(glsApiClient.modifyCod(any(ModifyCODRequest.class))).thenReturn(response);

        assertThat(service.updateCod(555, new BigDecimal("10.00"))).isTrue();
    }

    @Test
    void getStatus_returnsEmptyListWhenResponseHasNone() {
        GetParcelStatusResponse response = new GetParcelStatusResponse();
        when(glsApiClient.getParcelStatuses(any(GetParcelStatusesRequest.class))).thenReturn(response);

        assertThat(service.getStatus(123L)).isEmpty();
    }

    @Test
    void getStatus_returnsStatusList() {
        ParcelStatus status = new ParcelStatus();
        status.setStatusCode("5");
        status.setStatusDescription("The parcel has been delivered.");
        GetParcelStatusResponse response = new GetParcelStatusResponse();
        response.setParcelStatusList(List.of(status));
        when(glsApiClient.getParcelStatuses(any(GetParcelStatusesRequest.class))).thenReturn(response);

        assertThat(service.getStatus(123L)).containsExactly(status);
    }
}
