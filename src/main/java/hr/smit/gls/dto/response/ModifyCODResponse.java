package hr.smit.gls.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.ErrorInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** See "Response class ModifyCODResponse" in the API documentation. */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ModifyCODResponse {
    private List<ErrorInfo> modifyCODError;
    private boolean successful;
}
