package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** See "PrintDataInfo class" in the API documentation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class PrintDataInfo {
    private String b2CChar;
    private String clientReference;
    private String depot;
    private String depotNumber;
    private String driver;
    private Parcel parcel;
    private Integer parcelId;
    private Long parcelNumber;
    private Long parcelNumberWithCheckdigit;
    private String sort;
    private String displaylanguage;
}
