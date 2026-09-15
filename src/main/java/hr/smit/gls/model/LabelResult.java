package hr.smit.gls.model;

import java.util.List;

public record LabelResult(
        boolean success,
        Integer parcelId,
        Long parcelNumber,
        byte[] labelPdf,
        List<String> errors
) {
    public static LabelResult success(Integer parcelId, Long parcelNumber, byte[] pdf) {
        return new LabelResult(true, parcelId, parcelNumber, pdf, List.of());
    }

    public static LabelResult failure(List<String> errors) {
        return new LabelResult(false, null, null, null, errors);
    }
}
