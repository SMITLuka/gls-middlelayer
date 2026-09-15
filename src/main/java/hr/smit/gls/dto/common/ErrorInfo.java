package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** See "ErrorInfo class" and "Appendix A: API error codes" in the API documentation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ErrorInfo {
    private Integer errorCode;
    private String errorDescription;
    private List<String> clientReferenceList;
    private List<Integer> parcelIdList;
}
