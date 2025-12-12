package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Simple Location DTO kept as Strings to avoid clashing with Part 1 store.
 */
public record LocationDTO(
        @NotBlank String id,
        @NotBlank String building,
        @NotBlank String room,
        @NotBlank String name
) {}
