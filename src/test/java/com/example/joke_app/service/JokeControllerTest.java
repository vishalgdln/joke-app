package com.example.joke_app.service;

import com.example.joke_app.controller.JokeController;
import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.model.Joke;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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

        when(jokeService.getJokes(2)).thenReturn(ResponseEntity.ok(new DataDtoRes(mockJokes)));

        mockMvc.perform(get("/jokes?count=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jokes[0][0].setup").value("Setup 1"))
                .andExpect(jsonPath("$.jokes[0][0].punchline").value("Punchline 1"))
                .andExpect(jsonPath("$.jokes[0][1].setup").value("Setup 2"))
                .andExpect(jsonPath("$.jokes[0][1].punchline").value("Punchline 2"));
    }

    @Test
    public void testGetJokes_ReturnsError_WhenServiceFails() throws Exception {
        when(jokeService.getJokes(2)).thenThrow(new RuntimeException("Service failed"));

        mockMvc.perform(get("/jokes?count=2"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetJokesById_ReturnsJoke() throws Exception {
        Joke mockJoke = new Joke(1L, "setup", "punchline", "type");
        when(jokeService.getJokesById(1L)).thenReturn(ResponseEntity.ok(mockJoke));

        mockMvc.perform(get("/joke")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.setup").value("setup"))
                .andExpect(jsonPath("$.punchline").value("punchline"));
    }

    @Test
    public void testGetJokesById_NotFound() throws Exception {
        when(jokeService.getJokesById(1L)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/joke").param("id", "1"))
                .andExpect(status().isNotFound());
    }
}

