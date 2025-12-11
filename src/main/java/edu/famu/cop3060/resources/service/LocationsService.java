package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.store.InMemoryLocationStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LocationsService {
  private final InMemoryLocationStore store;
  public LocationsService(InMemoryLocationStore store) { this.store = store; }

  public PageResponse<LocationDTO> list(int page, int size, String sort) {
    List<LocationDTO> all = store.findAll();
    Comparator<LocationDTO> cmp = switch (sort) {
      case "-name" -> Comparator.comparing(LocationDTO::name, String.CASE_INSENSITIVE_ORDER).reversed();
      case "name" -> Comparator.comparing(LocationDTO::name, String.CASE_INSENSITIVE_ORDER);
      case "-building" -> Comparator.comparing(LocationDTO::building, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)).reversed();
      case "building" -> Comparator.comparing(LocationDTO::building, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
      default -> Comparator.comparing(LocationDTO::id);
    };
    all = all.stream().sorted(cmp).toList();

    int from = Math.max(0, page * size);
    int to = Math.min(all.size(), from + size);
    List<LocationDTO> slice = from >= all.size() ? List.of() : all.subList(from, to);

    int totalPages = (int)Math.ceil(all.size() / (double)size);
    return new PageResponse<>(slice, page, size, all.size(), totalPages);
  }

  public LocationDTO get(Long id) {
    return store.findById(id).orElseThrow(() -> new NotFoundException("location "+id+" not found"));
  }

  public LocationDTO create(LocationDTO draft) { return store.create(draft); }
  public LocationDTO update(Long id, LocationDTO draft) { return store.update(id, draft).orElseThrow(() -> new NotFoundException("location "+id+" not found")); }
  public boolean delete(Long id) { return store.delete(id); }
}
