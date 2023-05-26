package ru.job4j.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
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
        return personRepository.save(person);
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
        boolean result = personRepository.existsById(id);
        if (result) {
            personRepository.delete(person);
        }
        return result;
    }
}
