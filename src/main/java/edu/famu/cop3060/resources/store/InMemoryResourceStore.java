package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Owns ResourceDTO data in memory.
 * IDs are strings r1, r2, ...
 */
@Component
public class InMemoryResourceStore {

    private final Map<String, ResourceDTO> byId = new HashMap<>();
    private final List<ResourceDTO> all = new ArrayList<>();
    private final AtomicInteger seq = new AtomicInteger(0);

    public InMemoryResourceStore() {
        // Seed 8 realistic entries
        seed(new ResourceDTO("r1", "ML Tutoring (VGG16 + Grad-CAM)",
                "Tutoring", "FAMU CS Lab 210", "https://famu.edu/cs/tutoring/ml",
                List.of("ml","explainability","grad-cam","study-support")));

        seed(new ResourceDTO("r2", "Data Science Lab Hours",
                "Lab", "FAMU DS Lab 220", "https://famu.edu/cs/labs/datascience",
                List.of("python","pandas","xgboost","shap")));

        seed(new ResourceDTO("r3", "Advising — CS Undergrad",
                "Advising", "FAMU CESTA Building", "https://famu.edu/cs/advising",
                List.of("degree-plan","internships","career")));

        seed(new ResourceDTO("r4", "Web Dev Peer Help",
                "Tutoring", "Library 3rd Floor", "https://famu.edu/cs/tutoring/web",
                List.of("html","css","js")));

        seed(new ResourceDTO("r5", "Writing Center — Tech Reports",
                "Tutoring", "Williams 104", "https://famu.edu/writing",
                List.of("resume","reports","presentations")));

        seed(new ResourceDTO("r6", "Linux & Git Workshop",
                "Lab", "CS Makerspace", "https://famu.edu/cs/workshops/git",
                List.of("git","github","cli")));

        seed(new ResourceDTO("r7", "Financial Aid Q&A",
                "Advising", "CASS 1st Floor", "https://famu.edu/financialaid",
                List.of("scholarship","aid")));

        seed(new ResourceDTO("r8", "Entrepreneurship @ Crowned Coils",
                "Advising", "Virtual", "https://crownedcoils.com",
                List.of("ecommerce","seo","marketing")));
    }

    private void seed(ResourceDTO r) {
        byId.put(r.id(), r);
        all.add(r);
        // keep seq in sync if you ever call save() later
        try {
            int n = Integer.parseInt(r.id().replaceFirst("r", ""));
            seq.set(Math.max(seq.get(), n));
        } catch (Exception ignored) {}
    }

    /** List copy (unmodifiable). */
    public List<ResourceDTO> findAll() {
        return List.copyOf(all);
    }

    /** Find by id. */
    public Optional<ResourceDTO> findById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** Create a new resource with an auto id r{n}. */
    public ResourceDTO save(ResourceDTO withoutId) {
        String id = "r" + (seq.incrementAndGet());
        ResourceDTO created = new ResourceDTO(
                id,
                withoutId.name(),
                withoutId.category(),
                withoutId.location(),
                withoutId.url(),
                withoutId.tags()
        );
        byId.put(id, created);
        all.add(created);
        return created;
    }

    /** Replace (update) an existing resource by id. Returns updated when present. */
    public Optional<ResourceDTO> replace(ResourceDTO updated) {
        if (!byId.containsKey(updated.id())) return Optional.empty();
        byId.put(updated.id(), updated);
        // keep list in sync
        for (int i = 0; i < all.size(); i++) {
            if (Objects.equals(all.get(i).id(), updated.id())) {
                all.set(i, updated);
                break;
            }
        }
        return Optional.of(updated);
    }

    /** Remove by id (true when removed). */
    public boolean remove(String id) {
        ResourceDTO removed = byId.remove(id);
        if (removed != null) {
            all.removeIf(r -> Objects.equals(r.id(), id));
            return true;
        }
        return false;
    }

    /**
     * Filter by optional category (equalsIgnoreCase) and q (substring on name or any tag, case-insensitive).
     */
    public List<ResourceDTO> findByFilters(Optional<String> category, Optional<String> q) {
        return all.stream()
                .filter(r -> category.map(c -> r.category() != null && r.category().equalsIgnoreCase(c)).orElse(true))
                .filter(r -> {
                    if (q.isEmpty()) return true;
                    String needle = q.get().toLowerCase();
                    boolean nameHit = r.name() != null && r.name().toLowerCase().contains(needle);
                    boolean tagHit = r.tags() != null && r.tags().stream().anyMatch(t -> t != null && t.toLowerCase().contains(needle));
                    return nameHit || tagHit;
                })
                .collect(Collectors.toList());
    }
}
