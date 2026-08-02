package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.CurrencyResponse;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.mapper.CurrencyMapper;
import com.hcmut.divvy.repository.CurrencyRepository;
import com.hcmut.divvy.service.CurrencyService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.CurrencyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyValidator currencyValidator;
    private final CurrencyMapper currencyMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyResponse> findAll() {
        return currencyRepository.findAll().stream()
                .map(currencyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse findById(GetCurrencyByIdModel model) {
        Currency currency = currencyValidator.validateCurrencyExists(
                currencyRepository.findById(model.getId()), model.getId());
        return currencyMapper.toResponse(currency);
    }

    @Override
    @Transactional
    public CurrencyResponse create(CreateCurrencyModel model) {
        boolean acronymExists = currencyRepository.findByAcronym(model.getAcronym()).isPresent();
        currencyValidator.validateCurrencyAcronymUnique(acronymExists);

        Currency currency = currencyMapper.toEntity(model);
        currency = currencyRepository.save(currency);
        return currencyMapper.toResponse(currency);
    }

    @Override
    @Transactional
    public CurrencyResponse update(UpdateCurrencyModel model) {
        Currency currency = currencyValidator.validateCurrencyExists(
                currencyRepository.findById(model.getId()), model.getId());

        if (model.getAcronym() != null && !model.getAcronym().equalsIgnoreCase(currency.getAcronym())) {
            boolean acronymExists = currencyRepository.findByAcronym(model.getAcronym()).isPresent();
            currencyValidator.validateCurrencyAcronymUnique(acronymExists);
        }

        currencyMapper.updatePartial(model, currency);
        currency = currencyRepository.save(currency);
        return currencyMapper.toResponse(currency);
    }

    @Override
    @Transactional
    public void delete(DeleteCurrencyModel model) {
        Currency currency = currencyValidator.validateCurrencyExists(
                currencyRepository.findById(model.getId()), model.getId());
        currencyRepository.delete(currency);
    }
}
