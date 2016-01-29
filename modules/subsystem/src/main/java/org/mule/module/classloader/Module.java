/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.util.HashMap;
import java.util.Map;

public class Module
{

    private static Module instance = new Module();

    private Map<String, ClassLoader> moduleClassLoaders = new HashMap<>();

    public static Module getInstance(){
        return instance;
    }

    public void addModule(String name, ClassLoader classLoader)
    {
        moduleClassLoaders.put(name, classLoader);
    }

    public ClassLoader getClassLoader(String module)
    {
        return moduleClassLoaders.get(module);
    }
}

