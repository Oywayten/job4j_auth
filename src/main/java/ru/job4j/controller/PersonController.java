package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.exeption.UserNotFoundException;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Oywayten 25.05.2023.
 */
@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());
    private final ObjectMapper objectMapper;

    private final PersonService personService;

    private final BCryptPasswordEncoder encoder;

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
        if (person.isEmpty()) {
            throw new UserNotFoundException("User is not found by id = %s".formatted(id));
        }
        return new ResponseEntity<>(
                person.orElseThrow(() -> new UserNotFoundException("User is not found by id = %s".formatted(id))),
                HttpStatus.OK
        );
    }

    /*
    curl -i -H "Content-Type: application/json" -X PUT -d "{\"id\":\"11\",\"login\":\"support@job4j.com\",\"password\":\"123\"}" localhost:8080/person/
     */
    @PutMapping("/")
    public ResponseEntity<Person> update(@RequestBody Person person) {
        String login = person.getLogin();
        String password = person.getPassword();
        loginAndPasswordValidate(login, password);
        person.setPassword(encoder.encode(password));
        boolean isUpdated = personService.update(person);
        person.setPassword(password);
        return isUpdated ? ResponseEntity.ok(person) : ResponseEntity.internalServerError().build();
    }

    private static void loginAndPasswordValidate(String login, String password) {
        if (password == null || login == null) {
            throw new NullPointerException("Password and login mustn't be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must by more than 5 chars");
        }
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

    /*
    curl -H "Content-Type: application/json" -X POST -d {"""login""":"""admin""","""password""":"""password"""} "localhost:8080/person/sign-up"
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Person> signUp(@RequestBody Person person) {
        String login = person.getLogin();
        String password = person.getPassword();
        loginAndPasswordValidate(login, password);
        person.setPassword(encoder.encode(password));
        person = personService.save(person);
        person.setPassword(password);
        return new ResponseEntity<>(
                person,
                HttpStatus.CREATED
        );
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
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