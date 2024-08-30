package com.example.joke_app.service;

import com.example.joke_app.Repo.JokeRepository;
import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import com.example.joke_app.exception.ValidCountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JokeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JokeService jokeService;

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private JokeDatabaseService jokeDatabaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetJokes_ReturnsCorrectNumberOfJokes() {
        JokeDto mockJoke = new JokeDto(1L, "Setup Joke", "Punchline Joke", "type");
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class))).thenReturn(ResponseEntity.ok(mockJoke));
        doNothing().when(jokeDatabaseService).saveJokes(anyList());
        DataDtoRes mockResponse = jokeService.getJokes(12);

        assertEquals(2, mockResponse.getJokes().size());  // 2 batches
        assertEquals(12, mockResponse.getJokes().stream().mapToInt(List::size).sum());

        verify(restTemplate, times(12)).getForEntity(anyString(), eq(JokeDto.class));
        verify(jokeDatabaseService, times(2)).saveJokes(anyList());
    }

    @Test
    public void testGetJokes_fetchJokesInBatch_StatusIsNotOk() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenReturn(ResponseEntity.badRequest().body(new JokeDto()));
        assertThrows(JokeFetchException.class, () -> jokeService.getJokes(12));
    }

    @Test
    public void testGetJokes_ReturnsEmptyList_WhenApiFails() {
        int batchSize = 10;

        doThrow(new RuntimeException("API not available"))
                .when(restTemplate).getForEntity("https://official-joke-api.appspot.com/random_joke", JokeDto.class);

        assertThrows(JokeFetchException.class, () -> {
            jokeService.fetchJokesInBatch(batchSize);
        });
    }

    @Test
    public void testGetJokes_ReturnsError_WhenCountLessThanZero()  {
        ValidCountException thrown = assertThrows(ValidCountException.class, () -> {
            jokeService.getJokes(0);
        });
        assertEquals("Count should be greater than zero", thrown.getMessage());
    }

    @Test
    void testFetchJokesInBatch_ResourceAccessException() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        assertThrows(JokeFetchException.class, () -> jokeService.fetchJokesInBatch(1));
    }

    @Test
    void testFetchJokesInBatch_RestClientException() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(JokeFetchException.class, () -> jokeService.fetchJokesInBatch(1));
    }

    @Test
    void testFetchJokesInBatch_GeneralException() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenThrow(new RuntimeException("Unknown error"));

        assertThrows(JokeFetchException.class, () -> jokeService.fetchJokesInBatch(1));
    }
}