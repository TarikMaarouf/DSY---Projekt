package org.example.animal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(new Animal("DK-1", LocalDate.of(2026, 10, 20), 110.0, "Hansens Gård"));
        repository.save(new Animal("DK-2", LocalDate.of(2026, 10, 20), 95.5, "Jensens Gård"));
        repository.save(new Animal("DK-3", LocalDate.of(2026, 10, 21), 120.0, "Hansens Gård"));
    }

    @Test
    void createAnimalReturns201AndLocation() throws Exception {
        String json = """
                {"registrationNumber":"DK-9","arrivalDate":"2026-10-22","weight":100.0,"origin":"Nygård"}
                """;
        mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/animals/DK-9"));
    }

    @Test
    void createDuplicateAnimalReturns409() throws Exception {
        String json = """
                {"registrationNumber":"DK-1","arrivalDate":"2026-10-22","weight":100.0,"origin":"Nygård"}
                """;
        mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void createInvalidAnimalReturns400() throws Exception {
        String json = """
                {"registrationNumber":"","arrivalDate":"2026-10-22","weight":-5,"origin":""}
                """;
        mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAnimalReturnsAnimal() throws Exception {
        mockMvc.perform(get("/animals/DK-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Hansens Gård"))
                .andExpect(jsonPath("$.weight").value(110.0));
    }

    @Test
    void getUnknownAnimalReturns404() throws Exception {
        mockMvc.perform(get("/animals/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAnimalsByDate() throws Exception {
        mockMvc.perform(get("/animals").param("date", "2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAnimalsByOrigin() throws Exception {
        mockMvc.perform(get("/animals").param("origin", "Hansens Gård"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}
