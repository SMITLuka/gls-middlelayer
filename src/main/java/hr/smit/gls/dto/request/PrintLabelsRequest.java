package hr.smit.gls.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import hr.smit.gls.dto.common.Parcel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/** See "Request class PrintLabelsRequest" in the API documentation. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class PrintLabelsRequest extends ApiRequestBase {
    private List<Parcel> parcelList;
    private int printPosition;
    private boolean showPrintDialog;
    /** A4_2x2, A4_4x1, Connect, Thermo, ThermoZPL */
    private String typeOfPrinter;
}
