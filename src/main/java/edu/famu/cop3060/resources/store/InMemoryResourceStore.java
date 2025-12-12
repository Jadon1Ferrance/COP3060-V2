package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.CreateResourceDTO;
import edu.famu.cop3060.resources.dto.ResourceDTO;
import edu.famu.cop3060.resources.dto.UpdateResourceDTO;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory resource store that owns the data.
 * Keeps resource records with related IDs (locationId, categoryId)
 * so we can enforce referential integrity on delete.
 */
public class InMemoryResourceStore {

    /** Internal resource model (not exposed outside the store). */
    private static final class Resource {
        final long id;
        String name;
        Long locationId;   // nullable
        Long categoryId;   // nullable
        String url;
        List<String> tags;

        Resource(long id, String name, Long locationId, Long categoryId, String url, List<String> tags) {
            this.id = id;
            this.name = name;
            this.locationId = locationId;
            this.categoryId = categoryId;
            this.url = url;
            this.tags = (tags == null) ? new ArrayList<>() : new ArrayList<>(tags);
        }

        ResourceDTO toDTO() {
            return new ResourceDTO(
                id,
                name,
                locationId,
                categoryId,
                url,
                List.copyOf(tags)
            );
        }
    }

    private final Map<Long, Resource> byId = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public InMemoryResourceStore() {
        // --- Seed 6–8 realistic entries (IDs will be 1..n) ---
        // These are just examples; adjust names/ids as you like.
        create(new CreateResourceDTO("ML Tutoring (VGG16 + Grad-CAM)", 1L, 1L, "https://famu.edu/cs/tutoring/ml", List.of("ml","explainability","grad-cam","study-support")));
        create(new CreateResourceDTO("Writing Center — Tech Reports", 2L, 1L, "https://famu.edu/writing", List.of("resume","reports","presentations")));
        create(new CreateResourceDTO("Linux & Git Workshop", 3L, 2L, "https://famu.edu/makerspace", List.of("git","github","cli")));
        create(new CreateResourceDTO("Financial Aid Q&A", null, 3L, "https://famu.edu/financialaid", List.of("scholarship","aid")));
        create(new CreateResourceDTO("Web Dev Peer Help", 2L, 2L, "https://famu.edu/cs/tutoring/web", List.of("html","css","js")));
        create(new CreateResourceDTO("Entrepreneurship @ Crowned Coils", null, 4L, "https://crownedcoils.com", List.of("ecommerce","seo","marketing")));
    }

    // ---------- CRUD ----------

    public List<ResourceDTO> findAll() {
        return byId.values().stream()
                .sorted(Comparator.comparing(r -> r.name.toLowerCase()))
                .map(Resource::toDTO)
                .collect(Collectors.toUnmodifiableList());
    }

    public Optional<ResourceDTO> findById(long id) {
        var r = byId.get(id);
        return Optional.ofNullable(r).map(Resource::toDTO);
    }

    public ResourceDTO create(CreateResourceDTO dto) {
        long id = seq.getAndIncrement();
        var r = new Resource(
                id,
                dto.name(),
                dto.locationId(),
                dto.categoryId(),
                dto.url(),
                dto.tags()
        );
        byId.put(id, r);
        return r.toDTO();
    }

    public Optional<ResourceDTO> update(long id, UpdateResourceDTO dto) {
        var r = byId.get(id);
        if (r == null) return Optional.empty();
        r.name = dto.name();
        r.locationId = dto.locationId();
        r.categoryId = dto.categoryId();
        r.url = dto.url();
        r.tags = (dto.tags() == null) ? new ArrayList<>() : new ArrayList<>(dto.tags());
        return Optional.of(r.toDTO());
    }

    public boolean delete(long id) {
        return byId.remove(id) != null;
    }

    // ---------- Filtering + Paging helpers ----------

    public List<ResourceDTO> findByFilters(Optional<String> qOpt, Optional<Long> categoryIdOpt, Optional<Long> locationIdOpt) {
        var stream = byId.values().stream();

        if (qOpt.isPresent()) {
            String q = qOpt.get().toLowerCase();
            stream = stream.filter(r ->
                    r.name.toLowerCase().contains(q) ||
                    r.tags.stream().anyMatch(t -> t.toLowerCase().contains(q))
            );
        }
        if (categoryIdOpt.isPresent()) {
            long cid = categoryIdOpt.get();
            stream = stream.filter(r -> Objects.equals(r.categoryId, cid));
        }
        if (locationIdOpt.isPresent()) {
            long lid = locationIdOpt.get();
            stream = stream.filter(r -> Objects.equals(r.locationId, lid));
        }

        return stream.map(Resource::toDTO)
                .collect(Collectors.toUnmodifiableList());
    }

    // ---------- Referential counts (used by delete guards) ----------

    public long countByLocationId(long locationId) {
        return byId.values().stream().filter(r -> Objects.equals(r.locationId, locationId)).count();
    }

    public long countByCategoryId(long categoryId) {
        return byId.values().stream().filter(r -> Objects.equals(r.categoryId, categoryId)).count();
    }
}
