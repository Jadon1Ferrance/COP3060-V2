package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Payload for updating a Resource (id stays on the path).
 * All fields required for idempotent updates in this simple project.
 */
public record UpdateResourceDTO(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String location,
        @NotBlank String url,
        @Size(min = 1) List<@NotBlank String> tags
) {}
