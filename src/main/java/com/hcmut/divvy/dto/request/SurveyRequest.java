package com.hcmut.divvy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyRequest {
    private Integer userId;

    @Email(message = "Email khong hop le")
    private String email;

    private String usageGoal;
    private String groupSize;
    private String primaryPainPoint;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating must be at most 5 stars")
    private Integer rating;

    private String feedbackText;
}