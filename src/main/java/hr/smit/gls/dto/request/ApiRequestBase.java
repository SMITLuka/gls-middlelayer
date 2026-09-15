package hr.smit.gls.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Common fields sent with every MyGLS API call - see "APIRequestBase" in the API
 * documentation. {@code password} must already be SHA-512 hashed, see
 * {@link hr.smit.gls.client.GlsPasswordEncoder}.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public abstract class ApiRequestBase {
    /** Documented as "do not use" - left null/empty. */
    private List<Integer> clientNumberList;
    private int[] password;
    private String username;
    private String webshopEngine;
}
