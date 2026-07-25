package com.hcmut.divvy.facade;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <b>AuthFacade</b>
 *
 * <p>Handles orchestration and service delegation for authentication and registration use cases.
 * Inherits generic execute methods from {@link BaseFacade}.
 */
@Component
public class AuthFacade extends BaseFacade {

    public AuthFacade(ApplicationContext applicationContext) {
        super(applicationContext);
    }
}
