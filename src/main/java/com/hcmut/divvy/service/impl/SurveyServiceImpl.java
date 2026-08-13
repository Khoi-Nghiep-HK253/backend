package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.SurveyResponse;
import com.hcmut.divvy.entity.Survey;
import com.hcmut.divvy.mapper.SurveyMapper;
import com.hcmut.divvy.repository.SurveyRepository;
import com.hcmut.divvy.service.SurveyService;
import com.hcmut.divvy.service.model.CreateSurveyModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;

    @Override
    @Transactional
    public SurveyResponse submitSurvey(CreateSurveyModel model) {
        Survey survey = surveyMapper.toEntity(model);
        Survey saved = surveyRepository.save(survey);
        log.info("Survey submitted successfully id={}", saved.getId());

        return surveyMapper.toResponse(saved);
    }
}