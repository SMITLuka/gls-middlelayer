package hr.smit.gls.web;

import hr.smit.gls.dto.common.ParcelStatus;
import hr.smit.gls.model.LabelRequest;
import hr.smit.gls.model.LabelResult;
import hr.smit.gls.service.GlsLabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Local test surface for the GLS middleware methods, ahead of wiring an actual Pantheon
 * connector - lets the flow be exercised with curl/Postman against the MyGLS test
 * environment.
 */
@RestController
@RequestMapping("/api/gls")
@RequiredArgsConstructor
public class GlsLabelController {

    private final GlsLabelService glsLabelService;

    @PostMapping("/labels")
    public ResponseEntity<?> createLabel(@RequestBody LabelRequest request) {
        LabelResult result = glsLabelService.createLabel(request);
        if (!result.success()) {
            return ResponseEntity.badRequest().body(result.errors());
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=label-" + result.parcelId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(result.labelPdf());
    }

    @DeleteMapping("/labels/{parcelId}")
    public ResponseEntity<Void> cancelLabel(@PathVariable int parcelId) {
        boolean deleted = glsLabelService.cancelLabel(parcelId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @PatchMapping("/labels/{parcelId}/cod")
    public ResponseEntity<Void> updateCod(@PathVariable int parcelId, @RequestParam BigDecimal amount) {
        boolean updated = glsLabelService.updateCod(parcelId, amount);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/parcels/{parcelNumber}/status")
    public ResponseEntity<List<ParcelStatus>> getStatus(@PathVariable long parcelNumber) {
        return ResponseEntity.ok(glsLabelService.getStatus(parcelNumber));
    }
}
