/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines a {@link ClassLoader} that filter which classes and resources can
 * be resolved based on a {@link org.mule.module.factory.ModuleDescriptor}
 */
public class FilteringModuleClassLoader extends ClassLoader
{

    protected static final Log logger = LogFactory.getLog(FilteringModuleClassLoader.class);

    private String moduleName;
    private final ClassLoader pluginClassLoader;
    private final ClassLoaderFilter filter;

    public FilteringModuleClassLoader(String moduleName, ClassLoader moduleClassLoader, ClassLoaderFilter filter)
    {
        this.moduleName = moduleName;
        this.pluginClassLoader = moduleClassLoader;
        this.filter = filter;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        if (filter.accepts(name))
        {
            return pluginClassLoader.loadClass(name);
        }
        else
        {
            throw new ClassNotFoundException();
        }
    }

    @Override
    public URL getResource(String name)
    {
        if (filter.accepts(name))
        {
            return pluginClassLoader.getResource(name);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException
    {
        List<URL> filteredResources = new LinkedList<URL>();

        if (filter.accepts(name))
        {
            Enumeration<URL> resources = pluginClassLoader.getResources(name);

            while (resources.hasMoreElements())
            {
                URL url = resources.nextElement();

                boolean accepted = filter.accepts(name);

                if (accepted)
                {
                    filteredResources.add(url);
                }
            }
        }

        return new EnumerationAdapter<URL>(filteredResources);
    }

    @Override
    public String toString()
    {
        return String.format("%s[%s]@%s", getClass().getName(),
                             moduleName,
                             Integer.toHexString(System.identityHashCode(this)));
    }
}
