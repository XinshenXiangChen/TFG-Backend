package tfgbackend.api.dto;

import tfgbackend.service.GeneratedFilesService;

import java.util.List;

public record GenerateResponse(List<GeneratedFilesService.GeneratedFileInfo> files) {
}
