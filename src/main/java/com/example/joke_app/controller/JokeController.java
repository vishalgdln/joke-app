package com.example.joke_app.controller;

import com.example.joke_app.dto.DtoRes;
import com.example.joke_app.service.JokeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JokeController{

    @Autowired
    JokeService jokeService;

    @GetMapping("/jokes")
    public ResponseEntity<List<DtoRes>> getJokes(@RequestParam @Valid @Min(1) @Max(100) int count) {
        return ResponseEntity.ok(jokeService.getJokes(count));
    }
}
