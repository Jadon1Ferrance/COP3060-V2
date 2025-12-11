package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourcesService {

  private final InMemoryResourceStore store;

  public ResourcesService(InMemoryResourceStore store) {
    this.store = store;
  }

  public List<ResourceDTO> list(Optional<String> category, Optional<String> q) {
    return store.findByFilters(category, q);
  }

  public Optional<ResourceDTO> byId(String id) {
    return store.findById(id);
  }
}
