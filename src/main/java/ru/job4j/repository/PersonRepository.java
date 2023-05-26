package ru.job4j.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.domain.Person;

import java.util.List;

/**
 * Oywayten 25.05.2023.
 */
public interface PersonRepository extends CrudRepository<Person, Integer> {
    @NonNull
    List<Person> findAll();

    Person findByLogin(String login);
}
