package com.citicguoan.training.loader.common

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

/*
 * All the non-abstract derived classes of DataLoader
 * uses this annotation, not @org.springframework.stereotype.Component.
 *
 * So the scope of all the data loader objects are 'prototype', not 'singleton'
 *
 * The data loader objects also disable the '<aop:scoped-proxy/>' mechanism,
 * so those data loader objects cannot be injected into single objects
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Scope(
    ConfigurableBeanFactory.SCOPE_PROTOTYPE,
    proxyMode = ScopedProxyMode.NO
)
@Component
annotation class DataLoaderComponent