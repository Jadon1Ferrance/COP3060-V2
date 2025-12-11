package edu.famu.cop3060.resources.store;

import edu.famu.cop3060.resources.dto.ResourceDTO;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryResourceStore {

  private final Map<String, ResourceDTO> byId = new HashMap<>();
  private final List<ResourceDTO> all = new ArrayList<>();

  public InMemoryResourceStore() {
    // Seed 8 realistic entries (sprinkle your interests)
    seed(new ResourceDTO("r1","ML Tutoring (VGG16 + Grad-CAM)","Tutoring","FAMU CS Lab 210",
            "https://famu.edu/cs/tutoring/ml",
            List.of("ml","explainability","grad-cam","study-support")));

    seed(new ResourceDTO("r2","Data Science Lab Hours","Lab","FAMU DS Lab 220",
            "https://famu.edu/cs/labs/datascience",
            List.of("python","pandas","xgboost","shap")));

    seed(new ResourceDTO("r3","Advising — CS Undergrad","Advising","FAMU CESTA Building",
            "https://famu.edu/cs/advising",
            List.of("degree-plan","internships","career")));

    seed(new ResourceDTO("r4","Web Dev Peer Help","Tutoring","Library 3rd Floor",
            "https://famu.edu/cs/tutoring/web",
            List.of("html","css","js")));

    seed(new ResourceDTO("r5","Writing Center — Tech Reports","Tutoring","Williams 104",
            "https://famu.edu/writing",
            List.of("resume","reports","presentations")));

    seed(new ResourceDTO("r6","Linux & Git Workshop","Lab","CS Makerspace",
            "https://famu.edu/cs/workshops/git",
            List.of("git","github","cli")));

    seed(new ResourceDTO("r7","Financial Aid Q&A","Advising","CASS 1st Floor",
            "https://famu.edu/financialaid",
            List.of("scholarship","aid")));

    seed(new ResourceDTO("r8","Entrepreneurship @ Crowned Coils","Advising","Virtual",
            "https://crownedcoils.com",
            List.of("ecommerce","seo","marketing")));
  }

  private void seed(ResourceDTO r) {
    byId.put(r.id(), r);
    all.add(r);
  }

  public List<ResourceDTO> findAll() {
    return List.copyOf(all);
  }

  public Optional<ResourceDTO> findById(String id) {
    return Optional.ofNullable(byId.get(id));
  }

  public List<ResourceDTO> findByFilters(Optional<String> category, Optional<String> q) {
    return all.stream()
        .filter(r -> category.map(c -> r.category().equalsIgnoreCase(c)).orElse(true))
        .filter(r -> q.map(s -> {
          var needle = s.toLowerCase();
          if (r.name() != null && r.name().toLowerCase().contains(needle)) return true;
          return r.tags() != null && r.tags().stream()
              .filter(Objects::nonNull)
              .anyMatch(t -> t.toLowerCase().contains(needle));
        }).orElse(true))
        .toList();
  }
}
