package com.example.joke_app.service;

import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import com.example.joke_app.exception.ValidCountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class JokeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JokeDatabaseService jokeDatabaseService;

    public DataDtoRes getJokes(int count){
        DataDtoRes response = new DataDtoRes();
        List<List<JokeDto>> jokesDto = new ArrayList<>();
        int batches = (int) Math.ceil((double) count / 10);

        if(count <= 0)
        {
            throw new ValidCountException("Count should be greater than zero");
        }

        List<CompletableFuture<List<JokeDto>>> futures = IntStream.range(0, batches)
                .mapToObj(i -> {
                    int batchSize = Math.min(10, count - i * 10);
                    return CompletableFuture.supplyAsync(() -> fetchJokesInBatch(batchSize));
                })
                .toList();

        futures.forEach(future -> {
            try {
                List<JokeDto> batch = future.get();
                jokesDto.add(batch);
                jokeDatabaseService.saveJokes(batch);
            } catch (ExecutionException | InterruptedException e) {
                throw new JokeFetchException("Failed to fetch jokes in parallel: " + e.getMessage());
            }
        });

        response.setJokes(jokesDto);
        return response;
    }

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
                            } catch (Exception e) {
                                throw  new JokeFetchException("Server not available. Try again later.");
                            }
                        })
                .filter(joke -> joke != null)
                .collect(Collectors.toList());
    }
}
