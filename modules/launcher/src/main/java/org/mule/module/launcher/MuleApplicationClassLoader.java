/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher;

import org.mule.module.launcher.application.ApplicationClassLoader;
import org.mule.module.launcher.artifact.AbstractArtifactClassLoader;
import org.mule.module.launcher.nativelib.NativeLibraryFinder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

public class MuleApplicationClassLoader extends AbstractArtifactClassLoader implements ApplicationClassLoader
{

    /**
     * Library directory in Mule application.
     */
    public static final String PATH_LIBRARY = "lib";

    /**
     * Classes and resources directory in Mule application.
     */
    public static final String PATH_CLASSES = "classes";

    /**
     * Directory for standard mule modules and transports
     */
    public static final String PATH_MULE = "mule";

    /**
     * Sub-directory for per-application mule modules and transports
     */
    public static final String PATH_PER_APP = "per-app";

    //TODO(pablo.kraan): CCL - is this constant required?
    public static final URL[] CLASSPATH_EMPTY = new URL[0];

    private final String appName;
    private final File classesDir;
    private final NativeLibraryFinder nativeLibraryFinder;

    public MuleApplicationClassLoader(String appName, ClassLoader parentCl, NativeLibraryFinder nativeLibraryFinder, URL[] urls)
    {
        this(appName, parentCl, Collections.<String>emptySet(), nativeLibraryFinder, urls);
    }

    public MuleApplicationClassLoader(String appName, ClassLoader parentCl, Set<String> loaderOverrides, NativeLibraryFinder nativeLibraryFinder, URL[] urls)
    {
        super(parentCl, urls, loaderOverrides);
        this.appName = appName;
        this.nativeLibraryFinder = nativeLibraryFinder;

        classesDir = new File(MuleFoldersUtil.getAppFolder(appName), PATH_CLASSES);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return super.findClass(name);
    }

    @Override
    public URL getResource(String name)
    {
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException
    {
        return super.getResources(name);
    }

    public String getAppName()
    {
        return appName;
    }

    @Override
    public String toString()
    {
        return String.format("%s[%s]@%s", getClass().getName(),
                             appName,
                             Integer.toHexString(System.identityHashCode(this)));
    }

    @Override
    public String getArtifactName()
    {
        return getAppName();
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this;
    }

    @Override
    protected String findLibrary(String name)
    {
        String libraryPath = super.findLibrary(name);

        libraryPath = nativeLibraryFinder.findLibrary(name, libraryPath);

        return libraryPath;
    }

    @Override
    protected String[] getLocalResourceLocations()
    {
        return new String[] {classesDir.getAbsolutePath()};
    }
}
