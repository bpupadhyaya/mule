/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public abstract class AbstractModuleClassLoaderFactory
{

    public static final String CLASSES_DIR = "classes";
    public static final String LIB_DIR = "lib";
    private static final String JAR_FILE = "*.jar";

    protected void loadJarsFromFolder(List<URL> urls, File folder)
    {
        //TODO(pablo.kraan): CCL - add logging to know what is being loaded from where
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

    protected void addDirectoryToClassLoader(List<URL> urls, File classesFolder)
    {
        if (classesFolder.exists())
        {
            urls.add(getFileUrl(classesFolder));
        }
    }
}
