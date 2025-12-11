package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateResourceDTO(
    @NotBlank String name,
    @NotBlank String category,          // friendly label (legacy)
    @NotNull Long locationId,           // relation
    @NotNull Long categoryId,           // relation
    @NotBlank String url,
    @Size(min = 0) List<String> tags
) {}
