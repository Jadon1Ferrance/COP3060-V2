package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.CreateResourceDTO;
import edu.famu.cop3060.resources.dto.UpdateResourceDTO;
import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.service.ResourcesService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {
    // keep your existing fields / constructor / methods below


  private final ResourcesService svc;
  public ResourcesController(ResourcesService svc) { this.svc = svc; }

  // list with optional filters + paging/sorting
  @GetMapping
  public ResponseEntity<PageResponse<ResourceDTO>> list(
      @RequestParam Optional<String> category,
      @RequestParam Optional<String> q,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="10") int size,
      @RequestParam(defaultValue="id") String sort
  ) {
    return ResponseEntity.ok(svc.listPaged(category, q, page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceDTO> get(@PathVariable String id) {
    return ResponseEntity.ok(
        svc.get(id).orElseThrow(() -> new NotFoundException("resource "+id+" not found"))
    );
  }

  @PostMapping
  public ResponseEntity<ResourceDTO> create(@RequestBody @Valid CreateResourceDTO draft) {
    var created = svc.create(draft);
    return ResponseEntity.created(java.net.URI.create("/api/resources/"+created.id())).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResourceDTO> update(@PathVariable String id, @RequestBody @Valid UpdateResourceDTO draft) {
    return ResponseEntity.ok(svc.update(id, draft));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    svc.delete(id);
    return ResponseEntity.noContent().build();
  }
}

