package com.example.joke_app.service;

import com.example.joke_app.controller.JokeController;
import com.example.joke_app.dto.DtoRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(JokeController.class)
public class JokeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JokeService jokeService;

    @Test
    public void testGetJokes_ReturnsJokesList() throws Exception {
        List<DtoRes> mockJokes =
                Arrays.asList(new DtoRes(1L, "Setup 1", "Punchline 1"), new DtoRes(2L, "Setup 2", "Punchline 2"));

        when(jokeService.getJokes(2)).thenReturn(mockJokes);

        mockMvc.perform(get("/jokes?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].question").value("Setup 1"))
                .andExpect(jsonPath("$.[0].answer").value("Punchline 1"))
                .andExpect(jsonPath("$.[1].question").value("Setup 2"))
                .andExpect(jsonPath("$.[1].answer").value("Punchline 2"));
    }
}