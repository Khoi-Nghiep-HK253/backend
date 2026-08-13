package com.hcmut.divvy.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyModel {
    private Integer userId;
    private String email;
    private String usageGoal;
    private String groupSize;
    private String primaryPainPoint;
    private Integer rating;
    private String feedbackText;
}
