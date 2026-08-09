package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.CurrencyResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface CurrencyService {

    /**
     * Returns all supported currencies in the system.
     *
     * @return list of all currencies
     */
    List<CurrencyResponse> findAll();

    /**
     * Returns a single currency by its ID.
     *
     * @param model contains the currency ID
     * @return the matching currency; throws 404 if not found
     */
    CurrencyResponse findById(GetCurrencyByIdModel model);

    /**
     * Creates a new currency entry.
     *
     * @param model currency name and acronym (e.g. VND, USD)
     * @return the newly created currency
     */
    CurrencyResponse create(CreateCurrencyModel model);

    /**
     * Updates an existing currency's name or acronym.
     *
     * @param model currency ID and updated fields
     * @return the updated currency
     */
    CurrencyResponse update(UpdateCurrencyModel model);

    /**
     * Permanently deletes a currency by its ID.
     *
     * @param model contains the currency ID to delete
     */
    void delete(DeleteCurrencyModel model);
}
