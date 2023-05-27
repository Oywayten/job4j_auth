package ru.job4j.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.exeption.UserNotFoundException;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * Oywayten 26.05.2023.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        try {
            person = personRepository.save(person);
        } catch (DataIntegrityViolationException e) {
            log.error("Error save person", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not sign-up");
        }
        return person;
    }

    public boolean update(Person person) {
        Person savedPerson = null;
        if (personRepository.existsById(person.getId())) {
            savedPerson = personRepository.save(person);
        }
        return person.equals(savedPerson);
    }

    public boolean delete(Person person) {
        int id = person.getId();
        if (!personRepository.existsById(id)) {
            throw new UserNotFoundException("User is not found by id = %s".formatted(id));
        }
        personRepository.delete(person);
        return true;
    }

    public Person findByLogin(String username) {
        return personRepository.findByLogin(username);
    }
}
