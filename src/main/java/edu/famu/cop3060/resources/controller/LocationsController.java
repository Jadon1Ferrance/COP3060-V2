package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.service.LocationsService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationsController {
    // keep your existing fields / constructor / methods below

  private final LocationsService svc;
  public LocationsController(LocationsService svc) { this.svc = svc; }

  @GetMapping
  public ResponseEntity<PageResponse<LocationDTO>> list(
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="10") int size,
      @RequestParam(defaultValue="id") String sort
  ) {
    return ResponseEntity.ok(svc.list(page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<LocationDTO> get(@PathVariable Long id) {
    return ResponseEntity.ok(svc.get(id));
  }

  @PostMapping
  public ResponseEntity<LocationDTO> create(@RequestBody @Valid LocationDTO draft) {
    var created = svc.create(draft);
    return ResponseEntity.created(java.net.URI.create("/api/locations/"+created.id())).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LocationDTO> update(@PathVariable Long id, @RequestBody @Valid LocationDTO draft) {
    return ResponseEntity.ok(svc.update(id, draft));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean ok = svc.delete(id);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
