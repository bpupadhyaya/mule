/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.factory.ModuleDescriptor;

/**
 * Create instances of { @link PluginClassLoader}
 */
public interface ModuleClassLoaderFactory
{

    /**
     * Creates a {@link ModuleClassLoader} from a plugin folder structure.
     *
     * @param descriptor defines the plugin which will use the classloader. Not null.
     * @return a not null classloader
     */
    ClassLoader create(ModuleDescriptor descriptor);
}
