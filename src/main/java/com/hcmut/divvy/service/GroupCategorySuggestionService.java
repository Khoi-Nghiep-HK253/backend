package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.SuggestCategoryResponse;
import com.hcmut.divvy.service.model.SuggestGroupCategoryModel;

public interface GroupCategorySuggestionService {

    SuggestCategoryResponse suggest(SuggestGroupCategoryModel model);
}
