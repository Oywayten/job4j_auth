package ru.job4j.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Oywayten 25.05.2023.
 */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @NotNull(message = "Id must be non null")
    @Positive(message = "Id must be greater than 0", groups = {Operation.IsUser.class})
    private Integer id;

    @NotBlank(message = "login must be not empty", groups = {Operation.OnLogin.class})
    @Length(min = 3, max = 256, message = "login must be min = 6, max = 256")
    private String login;

    @NotBlank(message = "password must be not empty")
    @Length(min = 6, max = 256, message = "length must be min = 6, max = 256")
    private String password;
}