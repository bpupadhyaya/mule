/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.util.HashMap;
import java.util.Map;

public class ModuleTracker
{

    private static ModuleTracker instance = new ModuleTracker();

    private Map<String, Module> modules = new HashMap<>();

    public static ModuleTracker getInstance(){
        return instance;
    }

    public void addModule(Module module)
    {
        modules.put(module.getName(), module);
    }

    public Module getModule(String name)
    {
        return modules.get(name);
    }
}

