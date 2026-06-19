package tfgbackend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tfgbackend.service.GeneratedFilesService;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GeneratedFilesController.class)
class GeneratedFilesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeneratedFilesService generatedFilesService;

    @Test
    void generate_whenBodyIsValid_returnsGeneratedFiles() throws Exception {
        var resultFiles = List.of(new GeneratedFilesService.GeneratedFileInfo("schema.dl", "schema.dl", false, 10));
        when(generatedFilesService.generateFromPlantUml(anyString())).thenReturn(resultFiles);

        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plantUml": "@startuml\\nclass A\\n@enduml"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].name").value("schema.dl"))
                .andExpect(jsonPath("$.files[0].path").value("schema.dl"));
    }

    @Test
    void generate_whenInputIsInvalid_returnsBadRequest() throws Exception {
        when(generatedFilesService.generateFromPlantUml(anyString()))
                .thenThrow(new IllegalArgumentException("plantUml is required"));

        mockMvc.perform(post("/api/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "plantUml": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("plantUml is required"));
    }

    @Test
    void fileContent_whenServiceSucceeds_returnsPlainText() throws Exception {
        when(generatedFilesService.readGeneratedFile("schema.dl")).thenReturn("A(x)");

        mockMvc.perform(get("/api/files/content").param("path", "schema.dl"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("A(x)"));
    }

    @Test
    void archive_whenServiceSucceeds_returnsZipAttachment() throws Exception {
        when(generatedFilesService.buildGeneratedFilesZip()).thenReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/api/files/archive"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"generated-files.zip\""));
    }

    @Test
    void listFiles_whenServiceFails_returnsInternalServerError() throws Exception {
        when(generatedFilesService.listGeneratedFiles()).thenThrow(new IOException("disk error"));

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("disk error"));
    }
}
