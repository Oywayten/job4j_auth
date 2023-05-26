package ru.job4j.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonService;

import java.util.List;
import java.util.Optional;

/**
 * Oywayten 25.05.2023.
 */
@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final PersonService personService;

    /*
    curl -i http:/localhost:8080/person/
     */
    @GetMapping("/")
    public List<Person> findAll() {
        return personService.findAll();
    }

    /*
    curl -i localhost:8080/person/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Optional<Person> person = personService.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    /*
    curl -H "Content-Type:application/json" -X POST -d "{\"login\":\"job4j@gmail.com\",\"password\":\"123\"}" localhost:8080/person/
     */
    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return new ResponseEntity<>(
                personService.save(person),
                HttpStatus.CREATED
        );
    }

    /*
    curl -i -H "Content-Type: application/json" -X PUT -d "{\"id\":\"11\",\"login\":\"support@job4j.com\",\"password\":\"123\"}" localhost:8080/person/
     */
    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        return personService.update(person) ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }

    /*
    curl -i -X DELETE localhost:8080/person/5
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return personService.delete(person) ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }
}