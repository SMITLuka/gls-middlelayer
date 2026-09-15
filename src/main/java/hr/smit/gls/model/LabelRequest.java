package hr.smit.gls.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Input to {@link hr.smit.gls.service.GlsLabelService#createLabel}. {@code codAmount}
 * null or zero means the parcel is not cash-on-delivery.
 */
public record LabelRequest(
        String clientReference,
        RecipientInfo recipient,
        BigDecimal codAmount,
        String codReference,
        String content,
        LocalDate pickupDate
) {
}
