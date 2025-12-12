package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.LocationDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.store.InMemoryLocationStore;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Location service with simple referential delete guard:
 * we refuse to delete if any Resource uses this locationId.
 */
@Service
public class LocationsService {

    private final InMemoryLocationStore locations;
    private final InMemoryResourceStore resources;

    public LocationsService(InMemoryLocationStore locations, InMemoryResourceStore resources) {
        this.locations = locations;
        this.resources = resources;
    }

    public LocationDTO create(LocationDTO dto) {
        return locations.create(dto);
    }

    public Optional<LocationDTO> findById(long id) {
        return locations.findById(id);
    }

    public PageResponse<LocationDTO> findPaged(int page, int size, Optional<String> sortOpt) {
        List<LocationDTO> all = locations.findAll();

        Comparator<LocationDTO> cmp = Comparator.comparing(l -> l.name().toLowerCase());
        if (sortOpt.isPresent()) {
            String s = sortOpt.get();
            boolean desc = s.startsWith("-");
            String field = desc ? s.substring(1) : s;
            if ("name".equalsIgnoreCase(field)) {
                cmp = Comparator.comparing(l -> l.name().toLowerCase());
            } else if ("building".equalsIgnoreCase(field)) {
                cmp = Comparator.comparing(l -> l.building() == null ? "" : l.building().toLowerCase());
            }
            if (desc) cmp = cmp.reversed();
        }

        all = all.stream().sorted(cmp).toList();
        int total = all.size();
        int from = Math.max(0, Math.min(page * size, total));
        int to = Math.max(from, Math.min(from + size, total));
        List<LocationDTO> content = all.subList(from, to);

        int totalPages = (size <= 0) ? 1 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(content, page, size, total, totalPages);
    }

    public Optional<LocationDTO> update(long id, LocationDTO dto) {
        return locations.update(id, dto);
    }

    public void delete(long id) {
        long inUse = resources.countByLocationId(id);
        if (inUse > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "location " + id + " is in use by " + inUse + " resources"
            );
        }
        boolean removed = locations.delete(id);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "location " + id + " not found");
        }
    }
}
