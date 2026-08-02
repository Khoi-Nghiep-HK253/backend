package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.CurrencyResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface CurrencyService {
    List<CurrencyResponse> findAll();
    CurrencyResponse findById(GetCurrencyByIdModel model);
    CurrencyResponse create(CreateCurrencyModel model);
    CurrencyResponse update(UpdateCurrencyModel model);
    void delete(DeleteCurrencyModel model);
}
