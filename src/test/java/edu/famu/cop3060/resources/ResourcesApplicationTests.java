package edu.famu.cop3060.resources;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResourcesApplicationTests {

  @Autowired
  MockMvc mvc;

  @Test
  void listReturnsOkAndHasItems() throws Exception {
    mvc.perform(get("/api/resources"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))));
  }

  @Test
  void detailKnownIdReturnsOkAndName() throws Exception {
    mvc.perform(get("/api/resources/r1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", containsStringIgnoringCase("ML")));
  }
}
