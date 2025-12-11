package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryCategoryStore {

  private final Map<Long, CategoryDTO> byId = new HashMap<>();
  private final List<CategoryDTO> all = new ArrayList<>();
  private final AtomicLong seq = new AtomicLong(200);

  public InMemoryCategoryStore() {
    create(new CategoryDTO(null, "Tutoring"));
    create(new CategoryDTO(null, "Advising"));
    create(new CategoryDTO(null, "Lab"));
  }

  public List<CategoryDTO> findAll() { return List.copyOf(all); }
  public Optional<CategoryDTO> findById(Long id) { return Optional.ofNullable(byId.get(id)); }

  public CategoryDTO create(CategoryDTO draft) {
    long id = seq.incrementAndGet();
    CategoryDTO created = new CategoryDTO(id, draft.name());
    byId.put(id, created);
    all.add(created);
    return created;
  }

  public Optional<CategoryDTO> update(Long id, CategoryDTO draft) {
    if (!byId.containsKey(id)) return Optional.empty();
    CategoryDTO upd = new CategoryDTO(id, draft.name());
    byId.put(id, upd);
    all.replaceAll(c -> c.id().equals(id) ? upd : c);
    return Optional.of(upd);
  }

  public boolean delete(Long id) {
    CategoryDTO prev = byId.remove(id);
    if (prev == null) return false;
    all.removeIf(c -> c.id().equals(id));
    return true;
  }
}
