package com.example.joke_app.service;

import com.example.joke_app.Repo.JokeRepository;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.model.Joke;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JokeDatabaseServiceTest {

    @Mock
    private JokeRepository jokeRepository;

    @InjectMocks
    private JokeDatabaseService jokeDatabaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveJokes_ShouldSaveJokesToRepository_WhenJokesDtoListIsNotEmpty() {
        JokeDto jokeDto1 = new JokeDto(1L, "Setup Joke 1", "Punchline Joke 1", "type");
        JokeDto jokeDto2 = new JokeDto(2L, "Setup Joke 2", "Punchline Joke 2", "type");
        List<JokeDto> jokesDto = Arrays.asList(jokeDto1, jokeDto2);

        jokeDatabaseService.saveJokes(jokesDto);

        ArgumentCaptor<List<Joke>> jokeCaptor = ArgumentCaptor.forClass(List.class);
        verify(jokeRepository, times(1)).saveAll(jokeCaptor.capture());
        List<Joke> savedJokes = jokeCaptor.getValue();

        assertEquals(2, savedJokes.size());
        assertEquals("Setup Joke 1", savedJokes.get(0).getSetup());
        assertEquals("Punchline Joke 1", savedJokes.get(0).getPunchline());
        assertEquals("Setup Joke 2", savedJokes.get(1).getSetup());
        assertEquals("Punchline Joke 2", savedJokes.get(1).getPunchline());
    }

    @Test
    void testSaveJokes_ShouldNotSaveToRepository_WhenJokesDtoListIsEmpty() {
        List<JokeDto> jokesDto = Collections.emptyList();
        jokeDatabaseService.saveJokes(jokesDto);

        verify(jokeRepository, times(0)).saveAll(anyList());
    }

    @Test
    void testSaveJokes_ShouldNotSaveToRepository_WhenJokesDtoListIsNull() {
        List<JokeDto> jokesDto = null;
        jokeDatabaseService.saveJokes(jokesDto);

        verify(jokeRepository, times(0)).saveAll(anyList());
    }

}
