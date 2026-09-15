package hr.smit.gls.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.ErrorInfo;
import hr.smit.gls.dto.common.SuccessfullyDeleted;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** See "Response class DeleteLabelsResponse" in the API documentation. */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class DeleteLabelsResponse {
    private List<ErrorInfo> deleteLabelsErrorList;
    private List<SuccessfullyDeleted> successfullyDeletedList;
}
