package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Operation;
import ru.job4j.domain.Person;
import ru.job4j.exeption.PersonNotFoundException;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Oywayten 25.05.2023.
 */
@RestController
@RequestMapping("/person")
@AllArgsConstructor
@Tag(name="Person", description="Operations about person")
public class PersonController {

    public static final String PERSON_NOT_FOUND_BY_ID_S = "Person not found by id = %s";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;

    private final PersonService personService;

    private final BCryptPasswordEncoder encoder;

    /*
    curl -i http:/localhost:8080/person/
     */
    @GetMapping("/")
    @SecurityRequirement(name = "JWT")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Search all persons",
            description = "Allows you to find all persons"
    )
    public ResponseEntity<List<Person>> findAll() {
        List<Person> personList = personService.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .header("Title", "personList")
                .body(personList);
    }

    /*
    curl -i localhost:8080/person/1
     */

    @GetMapping("/{id}")
    @SecurityRequirement(name = "JWT")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Person search",
            description = "Allows you to find a person by ID"
    )
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Optional<Person> personOptional = personService.findById(id);
        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException(PERSON_NOT_FOUND_BY_ID_S.formatted(id));
        }
        return new ResponseEntity<>(
                personOptional.orElseThrow(() -> new PersonNotFoundException(PERSON_NOT_FOUND_BY_ID_S.formatted(id))),
                HttpStatus.OK
        );
    }

    @PatchMapping
    @SecurityRequirement(name = "JWT")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Change password",
            description = "Allows you to change the person's password"
    )
    @Validated(Operation.IsPerson.class)
    public ResponseEntity<Person> patch(@Valid @RequestBody Person person) {
        String password = person.getPassword();
        int id = person.getId();
        Optional<Person> personOptional = personService.findById(id);
        if (personOptional.isEmpty()) {
            throw new PersonNotFoundException(PERSON_NOT_FOUND_BY_ID_S.formatted(id));
        }
        person = personOptional.get();
        person.setPassword(encoder.encode(password));
        boolean isUpdated = personService.update(person);
        person.setPassword(password);
        return isUpdated
                ? ResponseEntity.ok(person)
                : ResponseEntity.internalServerError().build();
    }

    /*
    curl -i -H "Content-Type: application/json" -X PUT -d "{\"id\":\"11\",\"login\":\"support@job4j.com\",\"password\":\"123\"}" localhost:8080/person/
     */
    @PutMapping("/")
    @SecurityRequirement(name = "JWT")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Person update",
            description = "Allows you to update person"
    )
    @Validated({Operation.OnLogin.class, Operation.IsPerson.class})

    public ResponseEntity<Person> update(@Valid @RequestBody Person person) {
        String password = person.getPassword();
        person.setPassword(encoder.encode(password));
        boolean isUpdated = personService.update(person);
        person.setPassword(password);
        return isUpdated
                ? ResponseEntity.ok(person)
                : ResponseEntity.internalServerError().build();
    }

    /*
    curl -i -X DELETE localhost:8080/person/5
     */
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "JWT")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Person delete",
            description = "Allows you to delete person"
    )
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return personService.delete(person)
                ? ResponseEntity.ok().header("Title", "delete").build()
                : ResponseEntity.internalServerError().build();
    }

    /*
    curl -H "Content-Type: application/json" -X POST -d {"""login""":"""admin""","""password""":"""password"""} "localhost:8080/person/sign-up"
     */
    @PostMapping("/sign-up")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Person registration",
            description = "Allows you to register a person"
    )
    @Validated(Operation.OnLogin.class)
    public ResponseEntity<Person> signUp(@Valid @RequestBody @Parameter(description = "Person") Person person) {
        String password = person.getPassword();
        person.setPassword(encoder.encode(password));
        person = personService.save(person);
        person.setPassword(password);
        return new ResponseEntity<>(
                person,
                new MultiValueMapAdapter<>(Map.of("Title", List.of("signUp"))),
                HttpStatus.CREATED
        );
    }

    @ExceptionHandler(value = {PersonNotFoundException.class})
    public void exceptionHandler(Exception e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }
}