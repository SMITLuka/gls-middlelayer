package hr.smit.gls.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Input to {@link hr.smit.gls.service.GlsLabelService#createLabel}. {@code codAmount}
 * null or zero means the parcel is not cash-on-delivery.
 */
public record LabelRequest(
        @NotBlank(message = "clientReference (broj narudžbe) je obavezan") String clientReference,
        @NotNull(message = "Podaci o primatelju su obavezni") @Valid RecipientInfo recipient,
        BigDecimal codAmount,
        String codReference,
        String content,
        LocalDate pickupDate
) {
}
