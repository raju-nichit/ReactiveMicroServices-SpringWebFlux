import oracle.soda.OracleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/soda")
public class SodaController {
    private final SodaService sodaService;

    @Autowired
    public SodaController(SodaService sodaService) {
        this.sodaService = sodaService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDocument(@RequestBody String jsonData) {
        try {
            String key = sodaService.createDocument(jsonData);
            return ResponseEntity.ok("Document created with key: " + key);
        } catch (OracleException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> getDocument(@PathVariable String key) {
        try {
            return ResponseEntity.ok(sodaService.getDocument(key));
        } catch (OracleException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllDocuments() {
        try {
            return ResponseEntity.ok(sodaService.getAllDocuments());
        } catch (OracleException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PutMapping("/{key}")
    public ResponseEntity<String> updateDocument(@PathVariable String key, @RequestBody String newJsonData) {
        try {
            boolean success = sodaService.updateDocument(key, newJsonData);
            return success ? ResponseEntity.ok("Updated successfully") : ResponseEntity.notFound().build();
        } catch (OracleException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> deleteDocument(@PathVariable String key) {
        try {
            boolean success = sodaService.deleteDocument(key);
            return success ? ResponseEntity.ok("Deleted successfully") : ResponseEntity.notFound().build();
        } catch (OracleException e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
