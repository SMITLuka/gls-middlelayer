package hr.smit.gls.client;

import hr.smit.gls.config.GlsProperties;
import hr.smit.gls.dto.common.Address;
import hr.smit.gls.dto.common.Parcel;
import hr.smit.gls.dto.request.PrintLabelsRequest;
import hr.smit.gls.dto.response.PrintLabelsResponse;
import hr.smit.gls.exception.GlsApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Verifies the wire format actually sent/received matches the MyGLS API docs -
 * PascalCase JSON field names and the endpoint URL shape - not just that the DTOs
 * compile.
 */
class GlsApiClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private GlsApiClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);

        GlsProperties properties = new GlsProperties();
        properties.setEnvironment("test");

        client = new GlsApiClient(restTemplate, properties);
    }

    @Test
    void printLabels_sendsPascalCaseJsonToCorrectUrl() {
        PrintLabelsRequest request = PrintLabelsRequest.builder()
                .username("api@hairhouse.hr")
                .password(new int[]{1, 2, 3})
                .parcelList(List.of(Parcel.builder()
                        .clientNumber(123456)
                        .clientReference("NAR-1")
                        .deliveryAddress(Address.builder().name("Ana Anić").build())
                        .build()))
                .printPosition(1)
                .build();

        mockServer.expect(requestTo("https://api.test.mygls.hr/ParcelService.svc/json/PrintLabels"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Username").value("api@hairhouse.hr"))
                .andExpect(jsonPath("$.ParcelList[0].ClientReference").value("NAR-1"))
                .andExpect(jsonPath("$.ParcelList[0].DeliveryAddress.Name").value("Ana Anić"))
                .andRespond(withSuccess("{\"Labels\":[37,80,68,70],\"PrintLabelsErrorList\":[],\"PrintLabelsInfoList\":[]}", MediaType.APPLICATION_JSON));

        PrintLabelsResponse response = client.printLabels(request);

        assertThat(response).isNotNull();
        assertThat(response.getLabels()).containsExactly(37, 80, 68, 70);
        mockServer.verify();
    }

    @Test
    void transportFailure_wrappedAsGlsApiException() {
        PrintLabelsRequest request = PrintLabelsRequest.builder()
                .username("api@hairhouse.hr")
                .password(new int[]{1})
                .parcelList(List.of())
                .build();

        mockServer.expect(requestTo("https://api.test.mygls.hr/ParcelService.svc/json/PrintLabels"))
                .andRespond(withServerError());

        assertThrows(GlsApiException.class, () -> client.printLabels(request));
    }
}
