package com.hcmut.divvy.facade;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <b>UserFacade</b>
 *
 * <p>Handles orchestration and service delegation for all user-related use cases.
 * Inherits generic execute methods from {@link BaseFacade}.
 *
 * <p>Complex domain flows combining {@code UserService} with other services
 * (e.g. deactivating a user and recalculating group balances, or inviting users to a group)
 * should be implemented as dedicated methods in this class.
 */
@Component
public class UserFacade extends BaseFacade {

    public UserFacade(ApplicationContext applicationContext) {
        super(applicationContext);
    }
}
