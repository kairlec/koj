package com.kairlec.koj.backend.config

import com.kairlec.koj.backend.config.adapter.RequestParamMethodArgumentResolverAdapter
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.springframework.web.server.ServerWebExchange


@Configuration
class WebArgumentResolversConfig(
    private val applicationContext: ConfigurableApplicationContext,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(
            FormDataMethodArgumentResolver(
                applicationContext.beanFactory,
                ReactiveAdapterRegistry.getSharedInstance(),
                true
            )
        )
    }

    internal inner class FormDataMethodArgumentResolver(
        factory: ConfigurableBeanFactory,
        registry: ReactiveAdapterRegistry,
        useDefaultResolution: Boolean
    ) : RequestParamMethodArgumentResolverAdapter(factory, registry, useDefaultResolution) {

        @Suppress("UNCHECKED_CAST")
        override fun resolveNamedValue(name: String, parameter: MethodParameter, exchange: ServerWebExchange): Any? {
            val requestParams = exchange.request.queryParams
            val value = resolveParameterByParam(name, parameter, requestParams)
            if (value != null) return value
            return (exchange.attributes[RequestBodyMultiReadableFilter.FORM_DATA_ATTR] as? MultiValueMap<String, Any>?)?.let {
                resolveParameterByForm(
                    name,
                    parameter,
                    it
                )
            }
        }

        private fun resolveParameterByParam(
            name: String,
            parameter: MethodParameter,
            data: MultiValueMap<String, String>
        ): Any? {
            val values = data[name] ?: return null
            if (Collection::class.java.isAssignableFrom(parameter.parameterType)) {
                if (values.size == 1) {
                    return values.single().split(',')
                }
                return values
            }
            return data.getFirst(name)
        }

        private fun resolveParameterByForm(
            name: String,
            parameter: MethodParameter,
            data: MultiValueMap<String, Any>
        ): Any? {
            val values = data[name] ?: return null
            if (Collection::class.java.isAssignableFrom(parameter.parameterType)) {
                if (values.size == 1) {
                    val value = values.single()
                    return if (value is Collection<*>) {
                        value
                    } else {
                        values.single().toString().split(',')
                    }
                }
                return values
            }
            return data.getFirst(name)
        }
    }
}