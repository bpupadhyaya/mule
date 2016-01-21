/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

/**
 * Creates a wrapper to set the correct context class loader before each method
 * call on the delegate.
 */
public class DefaultClassLoaderWrapperFactory<T> implements ClassLoaderWrapperFactory<T>
{

    private final Class[] classes;

    public DefaultClassLoaderWrapperFactory(Class<T> clz)
    {
        if (!clz.isInterface())
        {
            throw new IllegalArgumentException("Class must be an interface");
        }

        classes = new Class[] {clz};
    }

    @Override
    public T create(T instance, ClassLoader classLoader)
    {
        Object proxy = ClassLoaderInjectorInvocationHandler.createProxy(instance, classLoader, classes);
        return (T) proxy;
    }
}
