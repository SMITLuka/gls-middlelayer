package hr.smit.gls.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Special service attached to a parcel (e.g. "COD"), see "Service class" and
 * "Appendix B: List of services" in the API documentation.
 *
 * <p>Only {@code code} is populated for now (enough for standard delivery + COD, which
 * covers the current scope). Services that need extra parameters (PSD, INS, SM1, ...)
 * would need their own *Parameter field added here when that service is required.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class GlsService {
    private String code;
}
