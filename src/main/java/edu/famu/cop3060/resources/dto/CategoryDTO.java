package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Simple Category DTO (string id; name required, description optional).
 */
public record CategoryDTO(
        @NotBlank String id,
        @NotBlank String name,
        String description
) {}
