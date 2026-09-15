package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** Shipment/label data - see "Parcel class" in the API documentation. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class Parcel {
    private Integer clientNumber;
    private String clientReference;
    private Integer count;
    private BigDecimal codAmount;
    private String codReference;
    private String codCurrency;
    private String content;
    /** Pre-formatted via {@link hr.smit.gls.client.GlsDateFormat#toGlsDate}. */
    private String pickupDate;
    private Address pickupAddress;
    private Address deliveryAddress;
    private List<GlsService> serviceList;
    private String senderIdentityCardNumber;
    private Integer pickupType;
    private List<ParcelProperty> parcelPropertyList;
}
