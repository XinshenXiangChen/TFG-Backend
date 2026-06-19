package tfgbackend.service;

import datalogllm.pipeline.PlantUmlPipeline;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class GeneratedFilesService {

    private static final Path GENERATED_ROOT = Path.of("target", "generated");
    private static final String JAVA_PACKAGE = "generated";
    private static final String LIVELINESS_PACKAGE = "generated.liveness";

    public List<GeneratedFileInfo> generateFromPlantUml(String plantUml) throws IOException {
        if (plantUml == null || plantUml.isBlank()) {
            throw new IllegalArgumentException("plantUml is required");
        }
        Files.createDirectories(GENERATED_ROOT);
        // Use non-LLM generation path so backend works without GEMINI_API_KEY.
        PlantUmlPipeline.generateDatalogAndJson(plantUml, GENERATED_ROOT);
        PlantUmlPipeline.generateJavaAndSqlFromUmlMetamodel(plantUml, GENERATED_ROOT, JAVA_PACKAGE, LIVELINESS_PACKAGE);
        return listGeneratedFiles();
    }

    public List<GeneratedFileInfo> listGeneratedFiles() throws IOException {
        if (!Files.exists(GENERATED_ROOT)) return List.of();

        List<GeneratedFileInfo> files = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(GENERATED_ROOT)) {
            stream
                    .filter(path -> !path.equals(GENERATED_ROOT))
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> {
                        boolean isDirectory = Files.isDirectory(path);
                        String relative = GENERATED_ROOT.relativize(path).toString().replace("\\", "/");
                        String name = path.getFileName().toString();
                        long size = isDirectory ? 0L : safeFileSize(path);
                        files.add(new GeneratedFileInfo(name, relative, isDirectory, size));
                    });
        }
        return files;
    }

    public String readGeneratedFile(String relativePath) throws IOException {
        Path resolved = resolveSafe(relativePath);
        if (Files.isDirectory(resolved)) {
            throw new IllegalArgumentException("Path points to a directory");
        }
        return Files.readString(resolved, StandardCharsets.UTF_8);
    }

    public byte[] buildGeneratedFilesZip() throws IOException {
        if (!Files.exists(GENERATED_ROOT)) return new byte[0];

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(baos)) {

            try (Stream<Path> stream = Files.walk(GENERATED_ROOT)) {
                stream.filter(path -> !Files.isDirectory(path))
                        .sorted(Comparator.comparing(Path::toString))
                        .forEach(path -> {
                            String zipEntryName = GENERATED_ROOT.relativize(path).toString().replace("\\", "/");
                            try {
                                zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
                                zipOutputStream.write(Files.readAllBytes(path));
                                zipOutputStream.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException ioException) throw ioException;
                throw e;
            }
            zipOutputStream.finish();
            return baos.toByteArray();
        }
    }

    private Path resolveSafe(String relativePath) {
        Path resolved = GENERATED_ROOT.resolve(relativePath).normalize().toAbsolutePath();
        Path root = GENERATED_ROOT.toAbsolutePath().normalize();
        if (!resolved.startsWith(root)) throw new IllegalArgumentException("Invalid path");
        if (!Files.exists(resolved)) throw new IllegalArgumentException("File not found");
        return resolved;
    }

    private long safeFileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0L;
        }
    }

    public record GeneratedFileInfo(String name, String path, boolean directory, long size) {
    }
}
