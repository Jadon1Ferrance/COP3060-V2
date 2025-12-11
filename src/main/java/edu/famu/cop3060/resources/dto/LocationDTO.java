package edu.famu.cop3060.resources.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationDTO(
    Long id,
    @NotBlank String name,
    String building,
    String room
) {}
