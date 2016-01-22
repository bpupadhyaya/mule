/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.application;

import org.mule.module.launcher.MuleApplicationClassLoader;
import org.mule.module.launcher.artifact.ArtifactClassLoader;
import org.mule.module.launcher.artifact.ShutdownListener;

import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Defines a classloader that delegates classes and resources resolution to
 * a list of classloaders.
 */
public class CompositeApplicationClassLoader extends org.mule.module.classloader.CompositeClassLoader implements ApplicationClassLoader
{

    protected static final Log logger = LogFactory.getLog(CompositeApplicationClassLoader.class);

    private final String appName;

    public CompositeApplicationClassLoader(String appName, List<ClassLoader> classLoaders)
    {
        super(classLoaders);
        this.appName = appName;
    }

    @Override
    public String getAppName()
    {
        return appName;
    }

    @Override
    public String getArtifactName()
    {
        return this.appName;
    }

    @Override
    public URL findLocalResource(String resourceName)
    {
        for (ClassLoader classLoader : classLoaders)
        {
            if( classLoader instanceof ArtifactClassLoader )
            {
                URL resource = ((ArtifactClassLoader)classLoader).findLocalResource(resourceName);
                if( resource!=null )
                {
                    return resource;
                }
            }
        }
        return null;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this;
    }

    @Override
    public void addShutdownListener(ShutdownListener listener)
    {
        for (ClassLoader classLoader : classLoaders)
        {
            if (classLoader instanceof MuleApplicationClassLoader)
            {
                ((ApplicationClassLoader)classLoader).addShutdownListener(listener);
                return;
            }
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        //TODO(pablo.kraan): CCL -overridden to enable testing. Ugly
        return super.loadClass(name, resolve);
    }

    @Override
    protected String findLibrary(String libname)
    {
        //TODO(pablo.kraan): CCL - overridden to enable testing. Ugly
        return super.findLibrary(libname);
    }
}
