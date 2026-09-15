package hr.smit.gls.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.ErrorInfo;
import hr.smit.gls.dto.common.PrintDataInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** See "Response class GetPrintedLabelsResponse" in the API documentation. */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class GetPrintedLabelsResponse {
    private List<ErrorInfo> getPrintedLabelsErrorList;
    private List<Integer> labels;
    private List<PrintDataInfo> printDataInfoList;
}
