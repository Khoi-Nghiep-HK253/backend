package com.hcmut.divvy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponse {
    private Integer id;
    private Integer userId;
    private String email;
    private String usageGoal;
    private String groupSize;
    private String primaryPainPoint;
    private Integer rating;
    private String feedbackText;
    private LocalDateTime createdAt;
}
