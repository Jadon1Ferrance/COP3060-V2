package edu.famu.cop3060.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Minimal happy-path CRUD + paging tests for PA03 Part 2.
 * Run with: mvn test
 */
@SpringBootTest
@AutoConfigureMockMvc
class ResourcesCrudTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    // A) POST /api/locations then GET /api/locations
    @Test
    void createLocation_thenListLocations() throws Exception {
        // create
        var body = """
          {"name":"CESTA Building","building":"CESTA","room":"220"}
        """;
        var createRes = mvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/locations/")))
            .andReturn();

        // list (expect an envelope with content/page/size/…)
        mvc.perform(get("/api/locations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", isA(Iterable.class)))
            .andExpect(jsonPath("$.page", greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.size", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)));
    }

    // B) POST /api/categories
    @Test
    void createCategory() throws Exception {
        var body = """
          {"name":"Tutoring","description":"Academic tutoring & study support"}
        """;
        mvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/categories/")));
    }

    // C) POST /api/resources with valid related IDs -> 201
    @Test
    void createResource_withValidIds() throws Exception {
        // first make a location
        var locBody = """
          {"name":"CS Building","building":"CS","room":"210"}
        """;
        var locRes = mvc.perform(post("/api/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(locBody))
            .andExpect(status().isCreated())
            .andReturn();
        var locLocationHeader = locRes.getResponse().getHeader("Location");
        var locationId = locLocationHeader.substring(locLocationHeader.lastIndexOf('/') + 1);

        // then a category
        var catBody = """
          {"name":"Advising","description":"CS/CE advising"}
        """;
        var catRes = mvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(catBody))
            .andExpect(status().isCreated())
            .andReturn();
        var catLocationHeader = catRes.getResponse().getHeader("Location");
        var categoryId = catLocationHeader.substring(catLocationHeader.lastIndexOf('/') + 1);

        // now create a resource that references them
        var resourceBody = """
          {
            "name":"Financial Aid Q&A",
            "url":"https://famu.edu/financialaid",
            "tags":["scholarship","aid"],
            "locationId": "%s",
            "categoryId": "%s"
          }
        """.formatted(locationId, categoryId);

        mvc.perform(post("/api/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resourceBody))
            .andExpect(status().isCreated())
            // response shape can vary; at least check name is echoed back
            .andExpect(jsonPath("$.name", is("Financial Aid Q&A")));
    }

    // D) GET /api/resources?page=0&size=5&sort=name -> 200 + envelope
    @Test
    void listResources_withPagingAndSort() throws Exception {
        mvc.perform(get("/api/resources")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "name"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", isA(Iterable.class)))
            .andExpect(jsonPath("$.page", is(0)))
            .andExpect(jsonPath("$.size", is(5)))
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(0)))
            .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)));
    }
}
