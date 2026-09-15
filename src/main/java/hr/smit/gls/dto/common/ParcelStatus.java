package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** See "ParcelStatus class" in the API documentation. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ParcelStatus {
    private String depotCity;
    private String depotNumber;
    private String statusCode;
    private LocalDateTime statusDate;
    private String statusDescription;
    private String statusInfo;
}
