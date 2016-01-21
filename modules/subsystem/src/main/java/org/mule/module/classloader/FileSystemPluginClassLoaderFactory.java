/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.factory.PluginDescriptor;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Creates an {@link PluginClassLoader} accessing to the folder in which
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
public class FileSystemPluginClassLoaderFactory implements PluginClassLoaderFactory
{

    public static final String CLASSES_DIR = "classes";
    public static final String LIB_DIR = "lib";
    private static final String JAR_FILE = "*.jar";

    @Override
    public PluginClassLoader create(PluginDescriptor descriptor)
    {
        File rootFolder = descriptor.getRootFolder();
        if (rootFolder == null || !rootFolder.exists())
        {
            throw new IllegalArgumentException("Plugin folder does not exists: " + (rootFolder != null ? rootFolder.getName() : null));
        }

        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        List<URL> urls = new LinkedList<URL>();

        addDirectoryToClassLoader(urls, new File(rootFolder, CLASSES_DIR));
        loadJarsFromFolder(urls, new File(rootFolder, LIB_DIR));

        return new PluginClassLoader(parentClassLoader, urls.toArray(new URL[0]), descriptor.getLoaderOverrides());
    }

    private void loadJarsFromFolder(List<URL> urls, File folder)
    {
        if (!folder.exists())
        {
            return;
        }

        FilenameFilter fileFilter = new WildcardFileFilter(JAR_FILE);
        File[] files = folder.listFiles(fileFilter);
        for (File jarFile : files)
        {
            urls.add(getFileUrl(jarFile));

        }
    }

    private URL getFileUrl(File jarFile)
    {
        try
        {
            return jarFile.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            // Should not happen as folder already exists
            throw new IllegalStateException("Cannot create plugin class loader", e);
        }
    }

    private void addDirectoryToClassLoader(List<URL> urls, File classesFolder)
    {
        if (classesFolder.exists())
        {
            urls.add(getFileUrl(classesFolder));
        }
    }
}
