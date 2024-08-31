package com.example.joke_app.service;

import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FetchJokeService {

    @Autowired
    private RestTemplate restTemplate;

    public List<JokeDto> fetchJokesInBatch(int batchSize) {
        return IntStream.range(0, batchSize)
                .parallel()
                .mapToObj(i -> {
                    try {
                        ResponseEntity<JokeDto> response = restTemplate.getForEntity("https://official-joke-api.appspot.com/random_joke", JokeDto.class);
                        if(response.getStatusCode() == HttpStatus.OK)
                        {
                            return response.getBody();
                        }else{
                            throw new JokeFetchException("Failed to fetch joke, status code: " + response.getStatusCode());
                        }
                    }catch (ResourceAccessException e) {
                        throw new JokeFetchException("Failed to fetch joke due to connectivity issues: " + e.getMessage());
                    } catch (RestClientException e) {
                        throw new JokeFetchException("Failed to fetch joke due to server error: " + e.getMessage());
                    }
                })
                .filter(joke -> joke != null)
                .collect(Collectors.toList());
    }
}
