package com.kairlec.koj.backend.config.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.web.reactive.result.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

public abstract class RequestParamMethodArgumentResolverAdapter extends RequestParamMethodArgumentResolver {
    /**
     * Class constructor with a default resolution mode flag.
     *
     * @param factory              a bean factory used for resolving  ${...} placeholder
     *                             and #{...} SpEL expressions in default values, or {@code null} if default
     *                             values are not expected to contain expressions
     * @param registry             for checking reactive type wrappers
     * @param useDefaultResolution in default resolution mode a method argument
     *                             that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
     *                             is treated as a request parameter even if it isn't annotated, the
     *                             request parameter name is derived from the method parameter name.
     */
    public RequestParamMethodArgumentResolverAdapter(ConfigurableBeanFactory factory, ReactiveAdapterRegistry registry, boolean useDefaultResolution) {
        super(factory, registry, useDefaultResolution);
    }

    @Override
    @Nullable
    protected abstract Object resolveNamedValue(@NotNull String name, @NotNull MethodParameter parameter, @NotNull ServerWebExchange exchange);
}
