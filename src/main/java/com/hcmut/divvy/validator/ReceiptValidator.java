package com.hcmut.divvy.validator;

import com.hcmut.divvy.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Component
public class ReceiptValidator {

    public void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException("Receipt image is required.", HttpStatus.BAD_REQUEST);
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("Receipt file must be an image (JPEG, PNG, WEBP, ...).",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public void validateExtraction(BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw new BusinessException(
                    "Could not read a total amount from this receipt. Please try a clearer photo or enter the expense manually.",
                    HttpStatus.UNPROCESSABLE_CONTENT);
        }
    }
}
