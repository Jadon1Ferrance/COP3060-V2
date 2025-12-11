package edu.famu.cop3060.resources.dto;

import java.util.List;

/** Read-only DTO for PA03 Part 1. */
public record ResourceDTO(
    String id,
    String name,
    String category,   // Tutoring, Advising, Lab, etc.
    String location,   // building/room or area
    String url,        // info page or sign-up
    List<String> tags  // e.g., "ml","explainability","web"
) {}
