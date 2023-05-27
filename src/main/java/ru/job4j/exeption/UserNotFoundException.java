package ru.job4j.exeption;

/**
 * Oywayten 27.05.2023.
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
