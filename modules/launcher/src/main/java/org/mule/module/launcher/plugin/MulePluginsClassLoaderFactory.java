/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.launcher.plugin;

import org.mule.module.classloader.CompositeClassLoader;
import org.mule.module.classloader.FilteringModuleClassLoader;
import org.mule.module.classloader.ModuleClassLoader;
import org.mule.module.classloader.ModuleClassLoaderFilter;
import org.mule.module.classloader.ModuleTracker;
import org.mule.module.classloader.MuleModule;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class MulePluginsClassLoaderFactory
{

    public ClassLoader create(ClassLoader parent, Collection<PluginDescriptor> descriptors)
    {

        //TODO(pablo.kraan): CCL - difference between Server and Application plugin descrptors must be in the name instead of in the package.
        //TODO(pablo.kraan): CCL - plugins should be ordered by name to ensure that they are always created in the same way
        List<ClassLoader> classLoaders = new ArrayList<>(descriptors.size() + 1);

        // First classloader is the parent to maintain parent first approach
        classLoaders.add(parent);

        for (PluginDescriptor descriptor : descriptors)
        {
            URL[] urls = new URL[descriptor.getRuntimeLibs().length + 1];
            urls[0] = descriptor.getRuntimeClassesDir();

            System.arraycopy(descriptor.getRuntimeLibs(), 0, urls, 1, descriptor.getRuntimeLibs().length);

            final MuleModule module = new MuleModule(descriptor);
            ModuleClassLoader moduleClassLoader = new ModuleClassLoader(parent, urls, descriptor.getLoaderOverride(), module);
            module.setClassLoader(moduleClassLoader);
            ModuleTracker.getInstance().addModule(module);
            ModuleClassLoaderFilter filter = new ModuleClassLoaderFilter(descriptor);

            classLoaders.add(new FilteringModuleClassLoader(descriptor.getName(), moduleClassLoader, filter));
        }

        return new CompositeClassLoader(classLoaders);
    }
}
