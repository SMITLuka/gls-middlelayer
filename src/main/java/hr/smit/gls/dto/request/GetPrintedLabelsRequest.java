package hr.smit.gls.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/** See "Request class GetPrintedLabelsRequest" in the API documentation. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class GetPrintedLabelsRequest extends ApiRequestBase {
    private List<Integer> parcelIdList;
    private int printPosition;
    private boolean showPrintDialog;
    private String typeOfPrinter;
}
