package com.hcmut.divvy.facade;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <b>GroupFacade</b>
 *
 * <p>Handles orchestration and service delegation for all group-related use cases.
 * Inherits generic execute/executeVoid methods from {@link BaseFacade}.
 *
 * <p>Complex flows combining GroupService with other services
 * (e.g. dissolving a group and recalculating balances) should be implemented here.
 */
@Component
public class GroupFacade extends BaseFacade {

    public GroupFacade(ApplicationContext applicationContext) {
        super(applicationContext);
    }
}
