package edu.famu.cop3060.resources.service;

import edu.famu.cop3060.resources.dto.CategoryDTO;
import edu.famu.cop3060.resources.dto.PageResponse;
import edu.famu.cop3060.resources.store.InMemoryCategoryStore;
import edu.famu.cop3060.resources.store.InMemoryResourceStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Category service with simple referential delete guard:
 * we refuse to delete if any Resource uses this categoryId.
 */
@Service
public class CategoriesService {

    private final InMemoryCategoryStore categories;
    private final InMemoryResourceStore resources;

    public CategoriesService(InMemoryCategoryStore categories, InMemoryResourceStore resources) {
        this.categories = categories;
        this.resources = resources;
    }

    public CategoryDTO create(CategoryDTO dto) {
        return categories.create(dto);
    }

    public Optional<CategoryDTO> findById(long id) {
        return categories.findById(id);
    }

    public PageResponse<CategoryDTO> findPaged(int page, int size, Optional<String> sortOpt) {
        List<CategoryDTO> all = categories.findAll();

        Comparator<CategoryDTO> cmp = Comparator.comparing(c -> c.name().toLowerCase());
        if (sortOpt.isPresent()) {
            String s = sortOpt.get();
            boolean desc = s.startsWith("-");
            String field = desc ? s.substring(1) : s;
            if ("name".equalsIgnoreCase(field)) {
                cmp = Comparator.comparing(c -> c.name().toLowerCase());
            }
            if (desc) cmp = cmp.reversed();
        }

        all = all.stream().sorted(cmp).toList();
        int total = all.size();
        int from = Math.max(0, Math.min(page * size, total));
        int to = Math.max(from, Math.min(from + size, total));
        List<CategoryDTO> content = all.subList(from, to);

        int totalPages = (size <= 0) ? 1 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(content, page, size, total, totalPages);
    }

    public Optional<CategoryDTO> update(long id, CategoryDTO dto) {
        return categories.update(id, dto);
    }

    public void delete(long id) {
        long inUse = resources.countByCategoryId(id);
        if (inUse > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "category " + id + " is in use by " + inUse + " resources"
            );
        }
        boolean removed = categories.delete(id);
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category " + id + " not found");
        }
    }
}
