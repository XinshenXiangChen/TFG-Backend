package tfgbackend.api;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfgbackend.api.dto.GenerateRequest;
import tfgbackend.api.dto.GenerateResponse;
import tfgbackend.service.GeneratedFilesService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GeneratedFilesController {

    private final GeneratedFilesService generatedFilesService;

    public GeneratedFilesController(GeneratedFilesService generatedFilesService) {
        this.generatedFilesService = generatedFilesService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody GenerateRequest request) {
        try {
            List<GeneratedFilesService.GeneratedFileInfo> files =
                    generatedFilesService.generateFromPlantUml(request.plantUml());
            return ResponseEntity.ok(new GenerateResponse(files));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<?> listFiles() {
        try {
            return ResponseEntity.ok(generatedFilesService.listGeneratedFiles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/files/content", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> fileContent(@RequestParam String path) {
        try {
            return ResponseEntity.ok(generatedFilesService.readGeneratedFile(path));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/files/archive")
    public ResponseEntity<?> archive() {
        try {
            byte[] zip = generatedFilesService.buildGeneratedFilesZip();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment().filename("generated-files.zip").build().toString())
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(zip);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
