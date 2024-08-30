package com.example.joke_app.service;

import com.example.joke_app.controller.JokeController;
import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import com.example.joke_app.exception.ValidCountException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
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

    @InjectMocks
    private JokeController jokeController;

    @Test
    public void testGetJokes_ReturnsJokesList() throws Exception {
        List<List<JokeDto>> mockJokes = List.of(
                Arrays.asList(new JokeDto(1L, "Setup 1", "Punchline 1", "type 1"), new JokeDto(2L, "Setup 2", "Punchline 2","type 2")));

        when(jokeService.getJokes(2)).thenReturn(new DataDtoRes(mockJokes));

        mockMvc.perform(get("/jokes?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jokes[0][0].setup").value("Setup 1"))
                .andExpect(jsonPath("$.jokes[0][0].punchline").value("Punchline 1"))
                .andExpect(jsonPath("$.jokes[0][1].setup").value("Setup 2"))
                .andExpect(jsonPath("$.jokes[0][1].punchline").value("Punchline 2"));
    }

    @Test
    public void testGetJokes_ThrowsJokeFetchException() throws Exception {
        when(jokeService.getJokes(anyInt())).thenThrow(new JokeFetchException("External API failed"));

        mockMvc.perform(get("/jokes?count=2"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Service failed: External API failed"));
    }

    @Test
    public void testGetJokes_ThrowsValidCountException() throws Exception {
        when(jokeService.getJokes(anyInt())).thenThrow(new ValidCountException("Count should be greater than zero"));

        mockMvc.perform(get("/jokes?count=0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad Request: Count should be greater than zero"));
    }
}