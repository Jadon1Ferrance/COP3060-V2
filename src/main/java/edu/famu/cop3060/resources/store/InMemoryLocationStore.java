package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.LocationDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryLocationStore {

  private final Map<Long, LocationDTO> byId = new HashMap<>();
  private final List<LocationDTO> all = new ArrayList<>();
  private final AtomicLong seq = new AtomicLong(100);

  public InMemoryLocationStore() {
    create(new LocationDTO(null, "FAMU CS Dept", "CASS", "210"));
    create(new LocationDTO(null, "Writing Center", "Library", "3rd Floor"));
    create(new LocationDTO(null, "CS Makerspace", "Innovation Center", "1st"));
  }

  public List<LocationDTO> findAll() { return List.copyOf(all); }
  public Optional<LocationDTO> findById(Long id) { return Optional.ofNullable(byId.get(id)); }

  public LocationDTO create(LocationDTO draft) {
    long id = seq.incrementAndGet();
    LocationDTO created = new LocationDTO(id, draft.name(), draft.building(), draft.room());
    byId.put(id, created);
    all.add(created);
    return created;
  }

  public Optional<LocationDTO> update(Long id, LocationDTO draft) {
    if (!byId.containsKey(id)) return Optional.empty();
    LocationDTO upd = new LocationDTO(id, draft.name(), draft.building(), draft.room());
    byId.put(id, upd);
    all.replaceAll(l -> l.id().equals(id) ? upd : l);
    return Optional.of(upd);
  }

  public boolean delete(Long id) {
    LocationDTO prev = byId.remove(id);
    if (prev == null) return false;
    all.removeIf(l -> l.id().equals(id));
    return true;
  }
}
