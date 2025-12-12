package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.service.CategoriesService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {
    // keep your existing fields / constructor / methods below

  private final CategoriesService svc;
  public CategoriesController(CategoriesService svc) { this.svc = svc; }

  @GetMapping
  public ResponseEntity<PageResponse<CategoryDTO>> list(
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="10") int size,
      @RequestParam(defaultValue="id") String sort
  ) {
    return ResponseEntity.ok(svc.list(page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDTO> get(@PathVariable Long id) {
    return ResponseEntity.ok(svc.get(id));
  }

  @PostMapping
  public ResponseEntity<CategoryDTO> create(@RequestBody @Valid CategoryDTO draft) {
    var created = svc.create(draft);
    return ResponseEntity.created(java.net.URI.create("/api/categories/"+created.id())).body(created);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody @Valid CategoryDTO draft) {
    return ResponseEntity.ok(svc.update(id, draft));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean ok = svc.delete(id);
    return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }
}
