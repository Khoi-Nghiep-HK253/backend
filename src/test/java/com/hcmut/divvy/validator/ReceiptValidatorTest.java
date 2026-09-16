package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReceiptValidatorTest {

    private final ReceiptValidator validator = new ReceiptValidator();

    @Test
    void validateImageFile_throwsWhenNull() {
        assertThatThrownBy(() -> validator.validateImageFile(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void validateImageFile_throwsWhenEmpty() {
        MockMultipartFile empty = new MockMultipartFile("image", "receipt.jpg", "image/jpeg", new byte[0]);
        assertThatThrownBy(() -> validator.validateImageFile(empty))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateImageFile_throwsWhenNotAnImage() {
        MockMultipartFile pdf = new MockMultipartFile("image", "receipt.pdf", "application/pdf", new byte[]{1, 2});
        assertThatThrownBy(() -> validator.validateImageFile(pdf))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("must be an image");
    }

    @Test
    void validateImageFile_throwsWhenContentTypeMissing() {
        MockMultipartFile noType = new MockMultipartFile("image", "receipt.jpg", null, new byte[]{1, 2});
        assertThatThrownBy(() -> validator.validateImageFile(noType))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void validateImageFile_passesForValidImage() {
        MockMultipartFile jpeg = new MockMultipartFile("image", "receipt.jpg", "image/jpeg", new byte[]{1, 2});
        validator.validateImageFile(jpeg);
    }

    @Test
    void validateExtraction_throwsWhenTotalAmountNull() {
        assertThatThrownBy(() -> validator.validateExtraction(null))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getStatus())
                .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void validateExtraction_passesWhenTotalAmountPresent() {
        validator.validateExtraction(BigDecimal.TEN);
    }
}
