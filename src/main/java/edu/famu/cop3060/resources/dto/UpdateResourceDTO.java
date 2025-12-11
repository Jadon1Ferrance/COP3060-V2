package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateResourceDTO(
    @NotBlank String name,
    @NotBlank String category,
    @NotNull Long locationId,
    @NotNull Long categoryId,
    @NotBlank String url,
    @Size(min = 0) List<String> tags
) {}
