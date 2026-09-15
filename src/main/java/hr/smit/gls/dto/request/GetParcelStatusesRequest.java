package hr.smit.gls.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** See "Request class GetParcelStatusesRequest" in the API documentation. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class GetParcelStatusesRequest extends ApiRequestBase {
    private long parcelNumber;
    private boolean returnPOD;
    /** ISO 639-1: HR, CS, HU, RO, SK, SL. Default EN in the API if omitted. */
    private String languageIsoCode;
}
