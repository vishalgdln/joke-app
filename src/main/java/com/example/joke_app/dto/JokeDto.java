package com.example.joke_app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class JokeDto {

    private Long id;
    private String setup;
    private String punchline;
    private String type;
}
