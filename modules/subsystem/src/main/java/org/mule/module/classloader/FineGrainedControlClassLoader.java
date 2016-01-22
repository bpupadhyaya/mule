/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.classloader;

import org.mule.module.descriptor.LoaderOverride;

import java.net.URL;

/**
 * TODO document overrides, blocked, systemPackages and syntax for specifying those.
 */
public class FineGrainedControlClassLoader extends GoodCitizenClassLoader
{

    protected String appName;

    protected final LoaderOverride loaderOverride;

    public FineGrainedControlClassLoader(URL[] urls, ClassLoader parent)
    {
        this(urls, parent, LoaderOverride.NULL_LOADER_OVERRIDE);
    }

    public FineGrainedControlClassLoader(URL[] urls, ClassLoader parent, LoaderOverride loaderOverride)
    {
        super(urls, parent);
        this.loaderOverride = loaderOverride;
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        Class<?> result = findLoadedClass(name);

        if (result != null)
        {
            return result;
        }
        if (loaderOverride.isBlocked(name))
        {
            // load this class from the child ONLY, don't attempt parent, let CNFE exception propagate
            result = findClass(name);
        }
        else if (loaderOverride.isOverridden(name))
        {
            // load this class from the child
            try
            {
                result = findClass(name);
            }
            catch (ClassNotFoundException e)
            {
                // let it fail with CNFE
                result = findParentClass(name);
            }
        }
        else
        {
            // no overrides, regular parent-first lookup
            try
            {
                result = findParentClass(name);
            }
            catch (ClassNotFoundException e)
            {
                result = findClass(name);
            }
        }

        if (resolve)
        {
            resolveClass(result);
        }

        return result;
    }

    public LoaderOverride getLoaderOverride()
    {
        //TODO(pablo.kraan): I don' like this getter, looks like is only used for testing purposes
        return loaderOverride;
    }

    protected Class<?> findParentClass(String name) throws ClassNotFoundException
    {
        if (getParent() != null)
        {
            return getParent().loadClass(name);
        }
        else
        {
            return findSystemClass(name);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return super.findClass(name);
    }

}
