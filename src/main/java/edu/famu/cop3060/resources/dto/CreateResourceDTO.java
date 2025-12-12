package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Payload for creating a Resource.
 * Keep fields as Strings to match our in-memory store (category/location are names, not IDs).
 */
public record CreateResourceDTO(
        @NotBlank String name,
        @NotBlank String category,
        @NotBlank String location,
        @NotBlank String url,
        @Size(min = 1) List<@NotBlank String> tags
) {}
