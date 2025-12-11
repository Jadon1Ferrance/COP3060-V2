package edu.famu.cop3060.resources.controller;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.service.ResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {
  private static final Logger log = LoggerFactory.getLogger(ResourcesController.class);

  private final ResourcesService svc;

  public ResourcesController(ResourcesService svc) {
    this.svc = svc;
  }

  @GetMapping
  public ResponseEntity<List<ResourceDTO>> list(
      @RequestParam Optional<String> category,
      @RequestParam Optional<String> q) {

    var out = svc.list(category, q);
    log.info("GET /api/resources category={} q={} -> {}", category.orElse("-"), q.orElse("-"), out.size());
    return ResponseEntity.ok(out);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceDTO> byId(@PathVariable String id) {
    var found = svc.byId(id);
    log.info("GET /api/resources/{} -> {}", id, found.isPresent() ? "200" : "404");
    return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
