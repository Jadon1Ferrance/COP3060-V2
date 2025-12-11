package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.*;
import edu.famu.cop3060.resources.exception.InvalidReferenceException;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import edu.famu.cop3060.resources.store.InMemoryCategoryStore;
import edu.famu.cop3060.resources.store.InMemoryLocationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class ResourcesService {
  private static final Logger log = LoggerFactory.getLogger(ResourcesService.class);

  private final InMemoryResourceStore resources;
  private final InMemoryLocationStore locations;
  private final InMemoryCategoryStore categories;

  public ResourcesService(
      InMemoryResourceStore resources,
      InMemoryLocationStore locations,
      InMemoryCategoryStore categories
  ) {
    this.resources = resources;
    this.locations = locations;
    this.categories = categories;
  }

  public List<ResourceDTO> list(Optional<String> category, Optional<String> q) {
    return resources.findByFilters(category, q);
  }

  public PageResponse<ResourceDTO> listPaged(Optional<String> category, Optional<String> q, int page, int size, String sort) {
    var filtered = list(category, q).stream();

    // simple sort (name asc/desc or category)
    Comparator<ResourceDTO> cmp = switch (sort) {
      case "-name" -> Comparator.comparing(ResourceDTO::name, String.CASE_INSENSITIVE_ORDER).reversed();
      case "name" -> Comparator.comparing(ResourceDTO::name, String.CASE_INSENSITIVE_ORDER);
      case "-category" -> Comparator.comparing(ResourceDTO::category, String.CASE_INSENSITIVE_ORDER).reversed();
      case "category" -> Comparator.comparing(ResourceDTO::category, String.CASE_INSENSITIVE_ORDER);
      default -> Comparator.comparing(ResourceDTO::id);
    };

    var all = filtered.sorted(cmp).toList();
    int from = Math.max(0, page * size);
    int to = Math.min(all.size(), from + size);
    var slice = from >= all.size() ? List.<ResourceDTO>of() : all.subList(from, to);
    int totalPages = (int)Math.ceil(all.size() / (double)size);
    return new PageResponse<>(slice, page, size, all.size(), totalPages);
  }

  public Optional<ResourceDTO> get(String id) {
    return resources.findById(id);
  }

  private void validateRefs(Long locationId, Long categoryId) {
    if (locations.findById(locationId).isEmpty())
      throw new InvalidReferenceException("locationId "+locationId+" does not exist");
    if (categories.findById(categoryId).isEmpty())
      throw new InvalidReferenceException("categoryId "+categoryId+" does not exist");
  }

  public ResourceDTO create(CreateResourceDTO draft) {
    validateRefs(draft.locationId(), draft.categoryId());
    // keep Part-1 ResourceDTO shape; expand via relations only for validation
    var id = "r" + UUID.randomUUID().toString().substring(0,8);
    var created = new ResourceDTO(
        id,
        draft.name(),
        draft.category(),
        // expand location name for user-friendly results (keeps Part-1 look)
        locations.findById(draft.locationId()).map(l -> l.name()).orElse(""),
        draft.url(),
        draft.tags() == null ? List.of() : draft.tags()
    );
    resources.seed(created); // simple add method; if you don’t have seed(public), use a create helper that mutates store
    log.info("Created resource {} with refs: locationId={}, categoryId={}", id, draft.locationId(), draft.categoryId());
    return created;
  }

  public ResourceDTO update(String id, UpdateResourceDTO draft) {
    validateRefs(draft.locationId(), draft.categoryId());
    var existing = resources.findById(id).orElseThrow(() -> new NotFoundException("resource "+id+" not found"));
    var upd = new ResourceDTO(
        existing.id(),
        draft.name(),
        draft.category(),
        locations.findById(draft.locationId()).map(l -> l.name()).orElse(""),
        draft.url(),
        draft.tags() == null ? List.of() : draft.tags()
    );
    resources.replace(upd);
    return upd;
  }

  public void delete(String id) {
    var ok = resources.remove(id);
    if (!ok) throw new NotFoundException("resource "+id+" not found");
  }
}
