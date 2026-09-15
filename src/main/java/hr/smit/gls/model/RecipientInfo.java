package hr.smit.gls.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Simplified recipient/delivery data - the fields the caller (Pantheon connector, or a
 * webshop "KREIRAJ GLS NALJEPNICU" button) is expected to already have on the order.
 * Required fields mirror GLS's "Address class" (see the API documentation).
 */
public record RecipientInfo(
        @NotBlank(message = "Naziv primatelja je obavezan") String name,
        @NotBlank(message = "Ulica je obavezna") String street,
        @NotBlank(message = "Kućni broj je obavezan") String houseNumber,
        String houseNumberInfo,
        @NotBlank(message = "Grad je obavezan") String city,
        @NotBlank(message = "Poštanski broj je obavezan") String zipCode,
        String countryIsoCode,
        String contactName,
        String contactPhone,
        String contactEmail
) {
}
