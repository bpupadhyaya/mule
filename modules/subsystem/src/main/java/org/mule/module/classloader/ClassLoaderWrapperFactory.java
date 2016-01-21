/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

/**
 *  Creates an classLoader wrapper for a given interface.
 */
public interface ClassLoaderWrapperFactory<T>
{

    T create(T instance, ClassLoader classLoader);
}
