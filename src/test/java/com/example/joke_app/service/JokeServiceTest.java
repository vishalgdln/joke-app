package com.example.joke_app.service;

import com.example.joke_app.dto.DtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {

    @InjectMocks
    private JokeService jokeService;

    @Mock
    private JokeDatabaseService jokeDatabaseService;

    @Mock
    private FetchJokeService fetchJokeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetJokes_ReturnsCorrectNumberOfJokes() {
        List<JokeDto> mockBatch = IntStream.range(0, 10)
                .mapToObj(i -> new JokeDto((long) i, "Setup " + i, "Punchline " + i, "type"))
                .toList();

        when(fetchJokeService.fetchJokesInBatch(10)).thenReturn(mockBatch);
        when(fetchJokeService.fetchJokesInBatch(2)).thenReturn(mockBatch.subList(0, 2));

        doNothing().when(jokeDatabaseService).saveJokes(anyList());
        List<DtoRes> mockResponse = jokeService.getJokes(12);
        assertEquals(12, mockResponse.size());

        verify(jokeDatabaseService, times(2)).saveJokes(anyList());
    }

    @Test
    public void testGetJokes_ReturnsExceptionOnParral() {
        when(fetchJokeService.fetchJokesInBatch(anyInt()))
                .thenThrow(new RuntimeException("Error fetching jokes"));

        assertThrows(JokeFetchException.class, () -> jokeService.getJokes(1));
    }
}