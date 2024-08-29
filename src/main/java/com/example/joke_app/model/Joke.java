package com.example.joke_app.model;

import com.example.joke_app.dto.JokeDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Joke {

    @Id
    private Long id;
    private String setup;
    private String punchline;
    private String type;

    public Joke(JokeDto jokeDto) {
        this.id = jokeDto.getId();
        this.setup = jokeDto.getSetup();
        this.punchline = jokeDto.getPunchline();
        this.type = jokeDto.getType();
    }
}