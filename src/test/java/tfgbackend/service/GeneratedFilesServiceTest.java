package tfgbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratedFilesServiceTest {

    private static final String METAMODEL_WITH_NURSE = """
            @startuml
            class Staff { + String nif }
            class Nurse { + String shift }
            Staff <|-- Nurse
            @enduml
            """;

    private static final String PATIENTS = """
            @startuml
            class Person { + String nif }
            class Patient { + String nif }
            Person <|-- Patient
            @enduml
            """;

    @TempDir
    Path tempDir;

    @Test
    void generateFromPlantUml_clearsArtifactsFromPreviousGeneration() throws Exception {
        GeneratedFilesService service = new GeneratedFilesService(tempDir);

        service.generateFromPlantUml(METAMODEL_WITH_NURSE);
        assertThat(fileNames(service)).contains("Nurse.java");

        service.generateFromPlantUml(PATIENTS);
        Set<String> names = fileNames(service);

        assertThat(names).doesNotContain("Nurse.java");
        assertThat(names).contains("Patient.java");
    }

    private static Set<String> fileNames(GeneratedFilesService service) throws Exception {
        return service.listGeneratedFiles().stream()
                .map(GeneratedFilesService.GeneratedFileInfo::name)
                .collect(Collectors.toSet());
    }
}
