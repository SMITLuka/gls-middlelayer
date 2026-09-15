package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Pickup or delivery address - see "Address class" in the API documentation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class Address {
    private String name;
    private String street;
    private String houseNumber;
    private String houseNumberInfo;
    private String city;
    private String zipCode;
    private String countryIsoCode;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
