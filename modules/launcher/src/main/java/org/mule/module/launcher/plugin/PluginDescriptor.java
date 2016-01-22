/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.plugin;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class PluginDescriptor
{
    private Set<String> loaderOverride = new HashSet<String>();
    private String name;
    private URL runtimeClassesDir;
    private URL[] runtimeLibs;

    public Set<String> getLoaderOverride()
    {
        return loaderOverride;
    }

    public void setLoaderOverride(Set<String> loaderOverride)
    {
        this.loaderOverride = loaderOverride;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public URL getRuntimeClassesDir()
    {
        return runtimeClassesDir;
    }

    public void setRuntimeClassesDir(URL runtimeClassesDir)
    {
        this.runtimeClassesDir = runtimeClassesDir;
    }

    public URL[] getRuntimeLibs()
    {
        return runtimeLibs;
    }

    public void setRuntimeLibs(URL[] runtimeLibs)
    {
        this.runtimeLibs = runtimeLibs;
    }
}
