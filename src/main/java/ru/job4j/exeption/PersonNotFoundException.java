package ru.job4j.exeption;

/**
 * Oywayten 27.05.2023.
 */
public class PersonNotFoundException extends RuntimeException{
    public PersonNotFoundException(String message) {
        super(message);
    }
}
