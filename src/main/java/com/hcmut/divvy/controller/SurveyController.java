package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.SurveyRequest;
import com.hcmut.divvy.dto.response.SurveyResponse;
import com.hcmut.divvy.mapper.SurveyMapper;
import com.hcmut.divvy.service.SurveyService;
import com.hcmut.divvy.service.model.CreateSurveyModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
@Tag(name = "Surveys", description = "APIs for user feedback & onboarding surveys")
public class SurveyController {

    private final SurveyService surveyService;
    private final SurveyMapper surveyMapper;

    @PostMapping
    @Operation(summary = "Submit onboarding survey", description = "Stores user responses for the onboarding survey")
    public ResponseEntity<ApiResponse<SurveyResponse>> submitSurvey(@Valid @RequestBody SurveyRequest request) {
        CreateSurveyModel model = surveyMapper.toModel(request);
        SurveyResponse response = surveyService.submitSurvey(model);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Thank you for successfully submitting the survey!"));
    }
}