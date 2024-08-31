package com.example.joke_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoRes {
    private Long id;
    private String question;
    private String answer;

    public DtoRes(JokeDto jokeDto) {
        this.id = jokeDto.getId();
        this.question = jokeDto.getSetup();
        this.answer = jokeDto.getPunchline();
    }
}
