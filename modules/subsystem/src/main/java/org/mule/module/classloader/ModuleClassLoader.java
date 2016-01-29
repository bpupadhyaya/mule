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
 * Defines a class loader to be used inside an mule plugin.
 */
public class ModuleClassLoader extends FineGrainedControlClassLoader
{

    public ModuleClassLoader(ClassLoader parent, URL[] urls, Module module)
    {
        this(parent, urls, LoaderOverride.NULL_LOADER_OVERRIDE, module);
    }

    public ModuleClassLoader(ClassLoader parent, URL[] urls, LoaderOverride loaderOverride, Module module)
    {
        super(urls, parent, loaderOverride, module);
    }
}
