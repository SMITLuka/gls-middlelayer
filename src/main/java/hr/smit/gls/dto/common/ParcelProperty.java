package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Optional parcel dimensions/packaging - see "ParcelProperty class" in the API documentation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ParcelProperty {
    private String content;
    /** Colli=1, Box=2, Roll=3, Can=4, Case=5, Reel=6, Sack=7 */
    private Integer packageType;
    private Integer height;
    private Integer length;
    private Integer width;
    private BigDecimal weight;
}
