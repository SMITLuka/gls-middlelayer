package hr.smit.gls.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/** See "Request class ModifyCODRequest" in the API documentation. */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ModifyCODRequest extends ApiRequestBase {
    private BigDecimal codAmount;
    /** Required if parcelNumber is null. */
    private Integer parcelId;
    /** Required if parcelId is null. */
    private Long parcelNumber;
}
