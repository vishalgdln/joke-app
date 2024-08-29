package com.example.joke_app.controller;

import com.example.joke_app.dto.DataDtoRes;
import com.example.joke_app.model.Joke;
import com.example.joke_app.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeController{

    @Autowired
    JokeService jokeService;

    @GetMapping("/jokes")
    public ResponseEntity<DataDtoRes> getJokes(@RequestParam int count) {
        return jokeService.getJokes(count);
    }

    @GetMapping("/joke")
    public ResponseEntity<Joke> getJokesById(@RequestParam Long id) {
        return jokeService.getJokesById(id);
    }
}
