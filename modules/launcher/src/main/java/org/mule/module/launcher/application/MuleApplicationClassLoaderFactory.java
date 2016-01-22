/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.application;

import org.mule.api.config.MuleProperties;
import org.mule.module.classloader.AbstractModuleClassLoaderFactory;
import org.mule.module.classloader.GoodCitizenClassLoader;
import org.mule.module.launcher.MuleApplicationClassLoader;
import org.mule.module.launcher.artifact.ArtifactClassLoader;
import org.mule.module.launcher.descriptor.ApplicationDescriptor;
import org.mule.module.launcher.domain.DomainClassLoaderRepository;
import org.mule.module.launcher.nativelib.NativeLibraryFinderFactory;
import org.mule.module.launcher.plugin.MulePluginsClassLoader;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Creates {@link MuleApplicationClassLoader} instances based on the
 * application descriptor.
 */
public class MuleApplicationClassLoaderFactory extends AbstractModuleClassLoaderFactory implements ApplicationClassLoaderFactory
{

    private final DomainClassLoaderRepository domainClassLoaderRepository;
    private final NativeLibraryFinderFactory nativeLibraryFinderFactory;

    public MuleApplicationClassLoaderFactory(DomainClassLoaderRepository domainClassLoaderRepository, NativeLibraryFinderFactory nativeLibraryFinderFactory)
    {
        this.domainClassLoaderRepository = domainClassLoaderRepository;
        this.nativeLibraryFinderFactory = nativeLibraryFinderFactory;
    }

    @Override
    public ArtifactClassLoader create(ApplicationDescriptor descriptor)
    {
        ClassLoader parent = getParentClassLoader(descriptor);

        //TODO(pablo.kraan): CCL - use MuleFolderUtils
        final String muleHome = System.getProperty(MuleProperties.MULE_HOME_DIRECTORY_PROPERTY);
        String appPath = String.format("%s/apps/%s", muleHome, descriptor.getName());
        final File appDir = new File(appPath);
        final File classesDir = new File(appDir, MuleApplicationClassLoader.PATH_CLASSES);
        List<URL> urls = new LinkedList<URL>();
        addDirectoryToClassLoader(urls, classesDir);

        final File libDir = new File(appDir, MuleApplicationClassLoader.PATH_LIBRARY);
        loadJarsFromFolder(urls, libDir);

        // Add per-app mule modules (if any)
        File libs = new File(muleHome, MuleApplicationClassLoader.PATH_LIBRARY);
        File muleLibs = new File(libs, MuleApplicationClassLoader.PATH_MULE);
        File perAppLibs = new File(muleLibs, MuleApplicationClassLoader.PATH_PER_APP);
        loadJarsFromFolder(urls, perAppLibs);

        return new MuleApplicationClassLoader(descriptor.getName(), parent, descriptor.getLoaderOverride(), nativeLibraryFinderFactory.create(descriptor.getName()), urls.toArray(new URL[0]));
    }

    private ClassLoader getParentClassLoader(ApplicationDescriptor descriptor)
    {
        ClassLoader parent;
        final String domain = descriptor.getDomain();
        if (domain == null)
        {
            parent = domainClassLoaderRepository.getDefaultDomainClassLoader().getClassLoader();
        }
        else
        {
            parent = domainClassLoaderRepository.getDomainClassLoader(domain).getClassLoader();
        }
        final Set<org.mule.module.launcher.plugin.PluginDescriptor> plugins = descriptor.getPlugins();
        if (!plugins.isEmpty())
        {
            // Re-assigns parent if there are shared plugin libraries
            URL[] pluginLibs = descriptor.getSharedPluginLibs();
            if (pluginLibs != null && pluginLibs.length != 0)
            {
                parent = new GoodCitizenClassLoader(pluginLibs, parent);
            }

            // re-assign parent ref if any plugins deployed, will be used by the MuleAppCL
            parent = new MulePluginsClassLoader(parent, plugins);
        }
        return parent;
    }
}
