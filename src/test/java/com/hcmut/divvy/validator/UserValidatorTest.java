package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.common.exception.ResourceNotFoundException;
import com.hcmut.divvy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("hungtri")
                .email("hung@example.com")
                .hashPassword(passwordEncoder.encode("123456"))
                .build();
    }

    @Test
    void validateCreateUser_throwsWhenUsernameExists() {
        assertThatThrownBy(() -> validator.validateCreateUser(true, false))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void validateCreateUser_throwsWhenEmailExists() {
        assertThatThrownBy(() -> validator.validateCreateUser(false, true))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void validateCreateUser_passesWhenNeitherExists() {
        validator.validateCreateUser(false, false);
    }

    @Test
    void validateChangePassword_throwsWhenCurrentPasswordWrong() {
        assertThatThrownBy(() -> validator.validateChangePassword("wrong-password", "newpass1", user, passwordEncoder))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Current password is incorrect.");
    }

    @Test
    void validateChangePassword_throwsWhenNewPasswordSameAsOld() {
        assertThatThrownBy(() -> validator.validateChangePassword("123456", "123456", user, passwordEncoder))
                .isInstanceOf(BusinessException.class)
                .hasMessage("New password must be different from the current password.");
    }

    @Test
    void validateChangePassword_passesWithCorrectCurrentAndDifferentNewPassword() {
        validator.validateChangePassword("123456", "newpass1", user, passwordEncoder);
    }

    @Test
    void validateOwnership_throwsWhenUsernameMismatch() {
        assertThatThrownBy(() -> validator.validateOwnership(user, "someone-else"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void validateOwnership_passesWhenUsernameMatches() {
        validator.validateOwnership(user, "hungtri");
    }

    @Test
    void validateUserInMap_returnsUserWhenPresent() {
        Map<Integer, User> userMap = Map.of(1, user);
        assertThat(validator.validateUserInMap(userMap, 1)).isEqualTo(user);
    }

    @Test
    void validateUserInMap_throwsWhenAbsent() {
        Map<Integer, User> userMap = Map.of(1, user);
        assertThatThrownBy(() -> validator.validateUserInMap(userMap, 2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateUserExists_returnsUserWhenPresent() {
        assertThat(validator.validateUserExists(Optional.of(user), "id", 1)).isEqualTo(user);
    }

    @Test
    void validateUserExists_throwsWhenEmpty() {
        assertThatThrownBy(() -> validator.validateUserExists(Optional.empty(), "id", 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: '1'");
    }
}
