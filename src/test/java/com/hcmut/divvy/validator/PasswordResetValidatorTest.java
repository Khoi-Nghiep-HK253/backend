package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import com.hcmut.divvy.entity.PasswordResetToken;
import com.hcmut.divvy.entity.User;
import com.hcmut.divvy.service.model.ResetPasswordModel;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordResetValidatorTest {

    private final PasswordResetValidator validator = new PasswordResetValidator();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final User user = User.builder().id(1).hashPassword(passwordEncoder.encode("old-password")).build();

    @Test
    void validateToken_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateToken(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateToken_throwsWhenExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();
        assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.GONE);
    }

    @Test
    void validateToken_throwsWhenAlreadyUsed() {
        PasswordResetToken token = PasswordResetToken.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(true)
                .build();
        assertThatThrownBy(() -> validator.validateToken(token))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Reset token has already been used.");
    }

    @Test
    void validateToken_passesWhenValid() {
        PasswordResetToken token = PasswordResetToken.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();
        validator.validateToken(token);
    }

    @Test
    void validateResetPasswordRequest_throwsWhenPasswordsDoNotMatch() {
        ResetPasswordModel model = ResetPasswordModel.builder()
                .newPassword("new-password").confirmPassword("different").build();

        assertThatThrownBy(() -> validator.validateResetPasswordRequest(model, null, null, passwordEncoder))
                .isInstanceOf(BusinessException.class)
                .hasMessage("New password and confirm password do not match.");
    }

    @Test
    void validateResetPasswordRequest_throwsWhenTokenInvalid() {
        ResetPasswordModel model = ResetPasswordModel.builder()
                .newPassword("new-password").confirmPassword("new-password").build();

        assertThatThrownBy(() -> validator.validateResetPasswordRequest(model, null, user, passwordEncoder))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateResetPasswordRequest_throwsWhenNewPasswordSameAsCurrent() {
        PasswordResetToken token = PasswordResetToken.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(30)).used(false).build();
        ResetPasswordModel model = ResetPasswordModel.builder()
                .newPassword("old-password").confirmPassword("old-password").build();

        assertThatThrownBy(() -> validator.validateResetPasswordRequest(model, token, user, passwordEncoder))
                .isInstanceOf(BusinessException.class)
                .hasMessage("New password must be different from the current password.");
    }

    @Test
    void validateResetPasswordRequest_passesWhenValid() {
        PasswordResetToken token = PasswordResetToken.builder()
                .expiresAt(LocalDateTime.now().plusMinutes(30)).used(false).build();
        ResetPasswordModel model = ResetPasswordModel.builder()
                .newPassword("brand-new-password").confirmPassword("brand-new-password").build();

        validator.validateResetPasswordRequest(model, token, user, passwordEncoder);
    }
}
