/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.launcher.plugin;

import org.mule.module.classloader.CompositeClassLoader;
import org.mule.module.classloader.ModuleClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class MulePluginsClassLoaderFactory
{

    public ClassLoader create(ClassLoader parent, Collection<PluginDescriptor> plugins)
    {

        //TODO(pablo.kraan): CCL - plugins should be ordered by name to ensure that they are always created in the same way
        List<ClassLoader> classLoaders = new ArrayList<>(plugins.size());

        for (PluginDescriptor plugin : plugins)
        {
            URL[] urls = new URL[plugin.getRuntimeLibs().length + 1];
            urls[0] = plugin.getRuntimeClassesDir();

            System.arraycopy(plugin.getRuntimeLibs(), 0, urls, 1, plugin.getRuntimeLibs().length);

            classLoaders.add(new ModuleClassLoader(parent, urls, plugin.getLoaderOverrides()));
        }

        return new CompositeClassLoader(classLoaders);
    }
}
