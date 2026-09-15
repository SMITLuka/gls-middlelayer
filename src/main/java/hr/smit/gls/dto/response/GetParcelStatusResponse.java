package hr.smit.gls.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.ErrorInfo;
import hr.smit.gls.dto.common.ParcelStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** See "Response class GetParcelStatusResponse" in the API documentation. */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class GetParcelStatusResponse {
    private String clientReference;
    private String deliveryCountryCode;
    private String deliveryZipCode;
    private List<ErrorInfo> getParcelStatusErrors;
    private Long parcelNumber;
    private List<ParcelStatus> parcelStatusList;
    private List<Integer> pod;
    private Double weight;
}
