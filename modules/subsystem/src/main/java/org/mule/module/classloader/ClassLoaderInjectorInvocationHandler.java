/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Wraps an object inside a dynamic proxy which ensure that all method
 * invocations on that object are execute inside a given class loader.
 */
public class ClassLoaderInjectorInvocationHandler implements InvocationHandler
{

    private final ClassLoader classLoader;
    private final Object delegate;

    public ClassLoaderInjectorInvocationHandler(Object delegate, ClassLoader classLoader)
    {
        this.classLoader = classLoader;
        this.delegate = delegate;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();

        try
        {
            Thread.currentThread().setContextClassLoader(classLoader);

            return method.invoke(delegate, args);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(originalLoader);
        }
    }

    /**
     * Creates a dynamic proxy for a given object.
     *
     * @param delegate    object to be proxied. Not null.
     * @param classLoader classloader that has to be used when accessing the delegate. Not null.
     * @param interfaces  interfaces to be implemented in the dynamic proxy. Not null.
     * @return a not null instance that implements all the required interfaces
     */
    public static Object createProxy(Object delegate, ClassLoader classLoader, Class<?>[] interfaces)
    {
        InvocationHandler handler = new ClassLoaderInjectorInvocationHandler(delegate, classLoader);
        return Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
}
