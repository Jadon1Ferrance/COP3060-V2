package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
    Long id,
    @NotBlank String name
) {}
