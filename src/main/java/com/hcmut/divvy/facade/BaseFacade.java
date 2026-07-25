package com.hcmut.divvy.facade;

import org.springframework.context.ApplicationContext;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <b>BaseFacade</b>
 *
 * <p>An abstract base class for all domain facades in the system.
 * It provides shared access to Spring's {@link ApplicationContext} and offers
 * generic execution wrapper methods to route requests to specific service classes.
 */
public abstract class BaseFacade {

    protected final ApplicationContext applicationContext;

    protected BaseFacade(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Helper to execute any service method that returns a value.
     *
     * @param serviceClass The target service class.
     * @param action       The functional mapping operation to execute.
     * @param <S>          Service type.
     * @param <R>          Return type.
     * @return The result of the service operation.
     */
    public <S, R> R execute(Class<S> serviceClass, Function<S, R> action) {
        S service = applicationContext.getBean(serviceClass);
        return action.apply(service);
    }

    /**
     * Helper to execute any service method that performs a void operation.
     *
     * @param serviceClass The target service class.
     * @param action       The consumer action to execute.
     * @param <S>          Service type.
     */
    public <S> void executeVoid(Class<S> serviceClass, Consumer<S> action) {
        S service = applicationContext.getBean(serviceClass);
        action.accept(service);
    }
}
