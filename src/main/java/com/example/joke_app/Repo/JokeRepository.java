package com.example.joke_app.Repo;

import com.example.joke_app.model.Joke;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JokeRepository extends JpaRepository<Joke, Long> {
}
