package com.example.webapp.unir.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

class CredentialsValidatorTest {

    private final CredentialsValidator validator = new CredentialsValidator("demo", "Demo1234");

    @Test
    void acceptsExactCredentials() {
        assertThat(validator.isValid("demo", "Demo1234")).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "demo, demo1234",      // contrasena incorrecta
            "demo, Demo12345",     // contrasena incorrecta (prefijo valido)
            "Demo, Demo1234",      // usuario case-sensitive
            "otro, Demo1234",      // usuario inexistente
            "demo, ''"             // contrasena vacia
    })
    void rejectsWrongCredentials(String username, String password) {
        assertThat(validator.isValid(username, password)).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void rejectsNullValues(String value) {
        assertThat(validator.isValid(value, "Demo1234")).isFalse();
        assertThat(validator.isValid("demo", value)).isFalse();
    }
}
