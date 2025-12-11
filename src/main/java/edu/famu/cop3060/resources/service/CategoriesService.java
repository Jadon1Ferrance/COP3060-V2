package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.exception.NotFoundException;
import edu.famu.cop3060.resources.store.InMemoryCategoryStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class CategoriesService {
  private final InMemoryCategoryStore store;
  public CategoriesService(InMemoryCategoryStore store) { this.store = store; }

  public PageResponse<CategoryDTO> list(int page, int size, String sort) {
    List<CategoryDTO> all = store.findAll();
    Comparator<CategoryDTO> cmp = switch (sort) {
      case "-name" -> Comparator.comparing(CategoryDTO::name, String.CASE_INSENSITIVE_ORDER).reversed();
      case "name" -> Comparator.comparing(CategoryDTO::name, String.CASE_INSENSITIVE_ORDER);
      default -> Comparator.comparing(CategoryDTO::id);
    };
    all = all.stream().sorted(cmp).toList();

    int from = Math.max(0, page * size);
    int to = Math.min(all.size(), from + size);
    List<CategoryDTO> slice = from >= all.size() ? List.of() : all.subList(from, to);

    int totalPages = (int)Math.ceil(all.size() / (double)size);
    return new PageResponse<>(slice, page, size, all.size(), totalPages);
  }

  public CategoryDTO get(Long id) {
    return store.findById(id).orElseThrow(() -> new NotFoundException("category "+id+" not found"));
  }

  public CategoryDTO create(CategoryDTO draft) { return store.create(draft); }
  public CategoryDTO update(Long id, CategoryDTO draft) { return store.update(id, draft).orElseThrow(() -> new NotFoundException("category "+id+" not found")); }
  public boolean delete(Long id) { return store.delete(id); }
}
