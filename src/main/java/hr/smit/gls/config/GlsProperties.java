package hr.smit.gls.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GLS credentials, environment and fixed pickup address, configured per deployment
 * (application.yml / environment variables) - never hardcoded.
 */
@Data
@ConfigurationProperties(prefix = "gls")
public class GlsProperties {

    /** "test" or "prod" - selects which base URL is used. */
    private String environment = "test";

    /**
     * When true, createLabel returns a synthetic PDF + fake parcel number instead of
     * calling MyGLS - lets the Pantheon/ARES plumbing (JSON, headers, acfield write-back,
     * PDF open) be tested before real GLS credentials are available. Never true in prod.
     */
    private boolean mockEnabled = false;

    private String testBaseUrl = "https://api.test.mygls.hr/ParcelService.svc";
    private String prodBaseUrl = "https://api.mygls.hr/ParcelService.svc";

    private String username;
    private String password;
    private Integer clientNumber;

    /** Required by MyGLS API since v.17 - value to be agreed with GLS. */
    private String webshopEngine;

    private PickupAddress pickupAddress = new PickupAddress();

    public String resolveBaseUrl() {
        return "prod".equalsIgnoreCase(environment) ? prodBaseUrl : testBaseUrl;
    }

    @Data
    public static class PickupAddress {
        private String name;
        private String street;
        private String houseNumber;
        private String houseNumberInfo;
        private String city;
        private String zipCode;
        private String countryIsoCode = "HR";
        private String contactName;
        private String contactPhone;
        private String contactEmail;
    }
}
