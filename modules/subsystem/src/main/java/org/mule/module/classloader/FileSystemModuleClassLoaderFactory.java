/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.factory.PluginDescriptor;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates an {@link ModuleClassLoader} accessing to the folder in which
 * a plugin is contained.
 * <p/>
 * <p>
 * Created plugin classloader will include all classes contained inside
 * "classes" subfolder and all jars contained inside "lib" subfolder in case
 * they exists.
 * </p>
 * <p>
 * If plugin descriptor contains loaderOverrides, then the created
 * classLoader will use that to override default class loading order
 * </p>
 */
public class FileSystemModuleClassLoaderFactory extends AbstractModuleClassLoaderFactory implements ModuleClassLoaderFactory
{

    @Override
    public ModuleClassLoader create(PluginDescriptor descriptor)
    {
        //TODO(pablo.kraan): CCL - this method must be pushed up as abstract into  AbstractModuleClassLoaderFactory
        File rootFolder = descriptor.getRootFolder();
        if (rootFolder == null || !rootFolder.exists())
        {
            throw new IllegalArgumentException("Plugin folder does not exists: " + (rootFolder != null ? rootFolder.getName() : null));
        }

        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        List<URL> urls = new LinkedList<URL>();

        addDirectoryToClassLoader(urls, new File(rootFolder, CLASSES_DIR));
        loadJarsFromFolder(urls, new File(rootFolder, LIB_DIR));

        return new ModuleClassLoader(parentClassLoader, urls.toArray(new URL[0]), descriptor.getLoaderOverrides());
    }

}
