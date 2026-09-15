package hr.smit.gls.client;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * MyGLS JSON API encodes dates in the legacy .NET "/Date(epochMillis)/" format
 * (see PickupDate examples in the API documentation).
 */
public final class GlsDateFormat {

    private static final ZoneId ZAGREB = ZoneId.of("Europe/Zagreb");

    private GlsDateFormat() {
    }

    public static String toGlsDate(LocalDate date) {
        long epochMillis = date.atStartOfDay(ZAGREB).toInstant().toEpochMilli();
        return "/Date(" + epochMillis + ")/";
    }
}
