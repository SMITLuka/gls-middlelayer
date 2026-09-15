package hr.smit.gls.client;

import hr.smit.gls.config.GlsProperties;
import hr.smit.gls.dto.request.DeleteLabelsRequest;
import hr.smit.gls.dto.request.GetParcelStatusesRequest;
import hr.smit.gls.dto.request.GetPrintedLabelsRequest;
import hr.smit.gls.dto.request.ModifyCODRequest;
import hr.smit.gls.dto.request.PrepareLabelsRequest;
import hr.smit.gls.dto.request.PrintLabelsRequest;
import hr.smit.gls.dto.response.DeleteLabelsResponse;
import hr.smit.gls.dto.response.GetParcelStatusResponse;
import hr.smit.gls.dto.response.GetPrintedLabelsResponse;
import hr.smit.gls.dto.response.ModifyCODResponse;
import hr.smit.gls.dto.response.PrepareLabelsResponse;
import hr.smit.gls.dto.response.PrintLabelsResponse;
import hr.smit.gls.exception.GlsApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Thin wrapper around the MyGLS {@code ParcelService} REST/JSON endpoints - see
 * "Country domain API URLs" and "Parcel service operations" in the API documentation.
 * Talks JSON only; one method per operation actually needed by {@link
 * hr.smit.gls.service.GlsLabelService}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlsApiClient {

    private final RestTemplate restTemplate;
    private final GlsProperties properties;

    public PrintLabelsResponse printLabels(PrintLabelsRequest request) {
        return post("PrintLabels", request, PrintLabelsResponse.class);
    }

    public PrepareLabelsResponse prepareLabels(PrepareLabelsRequest request) {
        return post("PrepareLabels", request, PrepareLabelsResponse.class);
    }

    public GetPrintedLabelsResponse getPrintedLabels(GetPrintedLabelsRequest request) {
        return post("GetPrintedLabels", request, GetPrintedLabelsResponse.class);
    }

    public DeleteLabelsResponse deleteLabels(DeleteLabelsRequest request) {
        return post("DeleteLabels", request, DeleteLabelsResponse.class);
    }

    public ModifyCODResponse modifyCod(ModifyCODRequest request) {
        return post("ModifyCOD", request, ModifyCODResponse.class);
    }

    public GetParcelStatusResponse getParcelStatuses(GetParcelStatusesRequest request) {
        return post("GetParcelStatuses", request, GetParcelStatusResponse.class);
    }

    private <T> T post(String method, Object request, Class<T> responseType) {
        String url = properties.resolveBaseUrl() + "/json/" + method;
        log.info("MyGLS poziv: {} -> {}", method, url);
        try {
            ResponseEntity<T> response = restTemplate.postForEntity(url, request, responseType);
            log.debug("MyGLS odgovor: {} -> {}", method, response.getStatusCode());
            return response.getBody();
        } catch (RestClientException e) {
            log.error("MyGLS poziv nije uspio: {} -> {}", method, url, e);
            throw new GlsApiException("Poziv prema MyGLS API-ju nije uspio (" + method + "): " + e.getMessage(), e);
        }
    }
}
