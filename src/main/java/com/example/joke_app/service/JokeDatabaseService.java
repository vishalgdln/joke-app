package com.example.joke_app.service;

import com.example.joke_app.Repo.JokeRepository;
import com.example.joke_app.dto.JokeDto;
import com.example.joke_app.exception.InvalidJokeException;
import com.example.joke_app.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JokeDatabaseService {

    @Autowired
    JokeRepository jokeRepository;

    public void saveJokes(List<JokeDto> jokesDto) {

        if (jokesDto == null || jokesDto.isEmpty()) {
            throw new InvalidJokeException("No Jokes are present");
        }

        List<Joke> jokesToSave = jokesDto.stream()
                .map(Joke::new)
                .filter(joke -> !jokeRepository.existsById(joke.getId()))
                .toList();

        jokeRepository.saveAll(jokesToSave);
    }
}
