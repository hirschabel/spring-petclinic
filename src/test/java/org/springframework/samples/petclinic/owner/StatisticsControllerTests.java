package org.springframework.samples.petclinic.owner;

import org.assertj.core.util.Lists;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

@WebMvcTest(value = StatisticsController.class)
public class StatisticsControllerTests {

    private static final int TEST_OWNER_ID = 0;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository pets;

    @MockBean
    private OwnerRepository owners;

    private Owner george;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        Pet max = new Pet();
        PetType dog = new PetType();
        dog.setName("dog");
        dog.setId(0);
        max.setId(0);
        max.setType(dog);
        max.setName("Max");
        max.setOwner(george);
        max.setBirthDate(LocalDate.of(2001, 1, 1));
        max.setDeathDate(LocalDate.of(2002, 1, 1));
        george.setPetsInternal(Collections.singleton(max));
        given(this.pets.findAllPets()).willReturn(Collections.singletonList(max));
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
    }

    @Test
    void testOwnerStatistics() throws Exception {
        mockMvc.perform(get("/statistics/{Type}", "Owner")).andExpect(status().isOk())
            .andExpect(model().attribute("values", not(empty())))
            .andExpect(model().attribute("values", new BaseMatcher<List<Statistics>>() {
                @Override
                public void describeTo(Description description) {
                    description.appendText("Something went wrong!");
                }

                @Override
                public boolean matches(Object item) {
                    @SuppressWarnings("unchecked")
                    List<Statistics> stats = (List<Statistics>)item;
                    Statistics stat = stats.get(0);
                    return stat.getSzoveg().equals("George Franklin") && stat.getAverage() == 1.0;
                }

            }));
    }

    @Test
    void testTypeStatistics() throws Exception {
        mockMvc.perform(get("/statistics/{Type}", "Type")).andExpect(status().isOk())
            .andExpect(model().attribute("values", not(empty())))
            .andExpect(model().attribute("values", new BaseMatcher<List<Statistics>>() {
                @Override
                public void describeTo(Description description) {
                    description.appendText("Something went wrong!");
                }

                @Override
                public boolean matches(Object item) {
                    @SuppressWarnings("unchecked")
                    List<Statistics> stats = (List<Statistics>)item;
                    Statistics stat = stats.get(0);
                    return stat.getSzoveg().equals("dog") && stat.getAverage() == 1.0;
                }

            }));
    }
}
