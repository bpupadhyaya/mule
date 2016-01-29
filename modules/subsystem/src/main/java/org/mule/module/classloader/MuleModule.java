/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.descriptor.ModuleDescriptor;

public class MuleModule implements Module
{

    private final ModuleDescriptor descriptor;
    private ClassLoader classLoader;

    public MuleModule(ModuleDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    @Override
    public String getName()
    {
        return descriptor.getName();
    }

    @Override
    public Class<?> loadClass(String name) throws java.lang.ClassNotFoundException
    {
        return classLoader.loadClass(name);
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }
}
