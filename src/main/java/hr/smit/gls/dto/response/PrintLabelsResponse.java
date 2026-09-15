package hr.smit.gls.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.ErrorInfo;
import hr.smit.gls.dto.common.PrintLabelsInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * See "Response class PrintLabelsResponse" in the API documentation. {@code labels} is
 * the raw JSON int array - convert with {@link hr.smit.gls.client.GlsBinaryUtil#toBytes}.
 */
@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class PrintLabelsResponse {
    private List<Integer> labels;
    private List<ErrorInfo> printLabelsErrorList;
    private List<PrintLabelsInfo> printLabelsInfoList;
}
