package com.example.joke_app.service;

import com.example.joke_app.Repo.JokeRepository;
import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import com.example.joke_app.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class JokeService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    JokeRepository jokeRepository;

    public ResponseEntity<DataDtoRes> getJokes(int count) {
        DataDtoRes response = new DataDtoRes();
        List<List<JokeDto>> jokesDto = new ArrayList<>();
        int batches = (int) Math.ceil((double) count / 10);

        if(count <= 0)
        {
            return ResponseEntity.badRequest().body(response);
        }

        for (int i = 0; i < batches; i++) {
            int batchSize = Math.min(10, count - i * 10);
            List<JokeDto> batch = fetchJokesInBatch(batchSize);
            jokesDto.add(batch);
            List<Joke> jokesToSave = batch.stream().map(Joke::new).toList();
            jokeRepository.saveAll(jokesToSave);
        }

        response.setJokes(jokesDto);
        return ResponseEntity.ok(response);
    }

    private List<JokeDto> fetchJokesInBatch(int batchSize) {
        return IntStream.range(0, batchSize)
                .parallel()
                .mapToObj(i -> {
                            try {
                                return restTemplate.getForObject("https://official-joke-api.appspot.com/random_joke", JokeDto.class);
                            } catch (Exception e) {
                                throw  new JokeFetchException("Server not available. Try again later.");
                            }
                        })
                .filter(joke -> joke != null)
                .collect(Collectors.toList());
    }

    public ResponseEntity<Joke> getJokesById(Long id) {
        Optional<Joke> joke = jokeRepository.findById(id);
        if(joke.isPresent())
        {
            return ResponseEntity.ok(joke.get());
        }
        return ResponseEntity.notFound().build();
    }
}
