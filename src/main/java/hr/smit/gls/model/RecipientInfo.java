package hr.smit.gls.model;

/**
 * Simplified recipient/delivery data - the fields the caller (Pantheon connector, or a
 * webshop "KREIRAJ GLS NALJEPNICU" button) is expected to already have on the order.
 */
public record RecipientInfo(
        String name,
        String street,
        String houseNumber,
        String houseNumberInfo,
        String city,
        String zipCode,
        String countryIsoCode,
        String contactName,
        String contactPhone,
        String contactEmail
) {
}
