package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.SurveyResponse;
import com.hcmut.divvy.service.model.CreateSurveyModel;

public interface SurveyService {
    SurveyResponse submitSurvey(CreateSurveyModel model);
}
