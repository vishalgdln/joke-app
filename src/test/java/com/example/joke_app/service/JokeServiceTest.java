package com.example.joke_app.service;

import com.example.joke_app.Repo.JokeRepository;
import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import com.example.joke_app.model.Joke;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void testGetJokes_ReturnsCorrectNumberOfJokes() {

        JokeDto mockJoke = new JokeDto(1L,"Setup Joke", "Punchline Joke", "type");

        when(restTemplate.getForObject(anyString(), eq(JokeDto.class))).thenReturn(mockJoke);
        when(jokeRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ResponseEntity<DataDtoRes> mockResponse = jokeService.getJokes(19);

        assertEquals(2, mockResponse.getBody().getJokes().size());
        assertEquals(19,mockResponse.getBody().getJokes().stream().mapToInt(List::size).sum());
        verify(restTemplate, times(19)).getForObject(anyString(), eq(JokeDto.class));
    }

    @Test
    public void testGetJokes_ReturnsEmptyList_WhenApiFails() {
        when(restTemplate.getForObject(anyString(), eq(JokeDto.class))).thenThrow(new JokeFetchException("Server not available. Try after later."));
        Exception exception = assertThrows(JokeFetchException.class, () -> {
            jokeService.getJokes(1);
        });
        assertEquals("Server not available. Try again later.", exception.getMessage());
    }

    @Test
    public void testGetJokesById_JokeFound() {
        Long jokeId = 1L;
        Joke mockJoke = new Joke(jokeId, "Setup 1", "Punchline 1", "type");
        when(jokeRepository.findById(jokeId)).thenReturn(Optional.of(mockJoke));

        ResponseEntity<Joke> response = jokeService.getJokesById(jokeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockJoke.getId(), response.getBody().getId());
        assertEquals(mockJoke.getSetup(), response.getBody().getSetup());
        assertEquals(mockJoke.getPunchline(), response.getBody().getPunchline());
    }

    @Test
    public void testGetJokesById_JokeNotFound() {
        Long jokeId = 1L;
        when(jokeRepository.findById(jokeId)).thenReturn(Optional.empty());

        ResponseEntity<Joke> response = jokeService.getJokesById(jokeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetJokes_ReturnsError_WhenCountLessThanZero()  {
        ResponseEntity<DataDtoRes> getCount = jokeService.getJokes(0);
        assertEquals(HttpStatus.BAD_REQUEST, getCount.getStatusCode());
    }

}