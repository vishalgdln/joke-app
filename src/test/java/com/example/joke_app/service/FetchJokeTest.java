package com.example.joke_app.service;

import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.GlobalExceptionHandler;
import com.example.joke_app.exception.JokeFetchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FetchJokeTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FetchJokeService fetchJokeService;

    @Test
    public void testGetJokes_fetchJokesInBatch_StatusIsNotOk() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenReturn(ResponseEntity.badRequest().body(new JokeDto()));
        assertThrows(JokeFetchException.class, () -> fetchJokeService.fetchJokesInBatch(8));
    }

    @Test
    void testFetchJokesInBatch_ResourceAccessException() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        assertThrows(JokeFetchException.class, () -> fetchJokeService.fetchJokesInBatch(1));
    }

    @Test
    void testFetchJokesInBatch_RestClientException() {
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class)))
                .thenThrow(new RestClientException("Server error"));

        assertThrows(JokeFetchException.class, () -> fetchJokeService.fetchJokesInBatch(1));
    }

    @Test
    void testBatch_ReturnCorrentBatch() {
        JokeDto mockJoke = new JokeDto(1L, "Setup Joke", "Punchline Joke", "type");
        when(restTemplate.getForEntity(anyString(), eq(JokeDto.class))).thenReturn(ResponseEntity.ok(mockJoke));

        List<JokeDto> mockResponse = fetchJokeService.fetchJokesInBatch(10);

        assertEquals(10, mockResponse.size());
        verify(restTemplate, times(10)).getForEntity(anyString(), eq(JokeDto.class));
    }

    @Test
    void testFetchJokeExceptionHandlerDirectly() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        JokeFetchException exception = new JokeFetchException("Invalid Jokes");

        ResponseEntity<String> response = exceptionHandler.jokeFetchException(exception);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Service failed: Invalid Jokes", response.getBody());
    }
}
