/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

/**
 * Defines a class loader to be used inside an mule plugin.
 */
public class ModuleClassLoader extends FineGrainedControlClassLoader
{

    //TODO(pablo.kraan): CCL - check classloader creation to inject proper overrides
    public ModuleClassLoader(ClassLoader parent, URL[] urls)
    {
        this(parent, urls, Collections.<String>emptySet());
    }

    public ModuleClassLoader(ClassLoader parent, URL[] urls, Set<String> overrides)
    {
        super(urls, parent, overrides);
    }
}
