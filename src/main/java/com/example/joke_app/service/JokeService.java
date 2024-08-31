package com.example.joke_app.service;

import com.example.joke_app.dto.DtoRes;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.JokeFetchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@Service
public class JokeService {

    @Autowired
    private JokeDatabaseService jokeDatabaseService;

    @Autowired
    private FetchJokeService fetchJokeService;

    public List<DtoRes> getJokes(int count){
        List<DtoRes> response = new ArrayList<>();
        List<JokeDto> jokesDto = new ArrayList<>();
        int batches = (int) Math.ceil((double) count / 10);

        List<CompletableFuture<List<JokeDto>>> futures = IntStream.range(0, batches)
                .mapToObj(i -> {
                    int batchSize = Math.min(10, count - i * 10);
                    return CompletableFuture.supplyAsync(() -> fetchJokeService.fetchJokesInBatch(batchSize));
                })
                .toList();

        futures.forEach(future -> {
            try {
                List<JokeDto> batch = future.get();
                jokesDto.addAll(batch);
                jokeDatabaseService.saveJokes(batch);
            } catch (ExecutionException | InterruptedException e) {
                throw new JokeFetchException("Failed to fetch jokes in parallel: " + e.getMessage());
            }
        });
        response = jokesDto.stream().map(DtoRes::new).toList();
        return response;
    }
}
