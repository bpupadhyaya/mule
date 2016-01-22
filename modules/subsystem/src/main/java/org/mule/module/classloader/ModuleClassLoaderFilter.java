/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.descriptor.PluginDescriptor;
import org.mule.module.descriptor.ModuleDescriptor;

import java.util.List;

/**
 * Filters classes and resources using a {@link PluginDescriptor} describing
 * exported/blocked names.
 * <p>
 * An exact blocked/exported name match has precedence over a prefix match
 * on a blocked/exported prefix. This enables to export classes or
 * subpackages from a blocked package.
 * </p>
 */
public class ModuleClassLoaderFilter implements ClassLoaderFilter
{

    private final ModuleDescriptor descriptor;

    public ModuleClassLoaderFilter(ModuleDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    @Override
    public boolean accepts(String name)
    {
        return descriptor.getLoaderExport().isExported(name);
    }
}
