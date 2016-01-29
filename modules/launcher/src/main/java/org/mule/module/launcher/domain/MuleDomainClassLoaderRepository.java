/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.domain;

import org.mule.config.i18n.CoreMessages;
import org.mule.module.classloader.Module;
import org.mule.module.classloader.MuleModule;
import org.mule.module.launcher.DeploymentException;
import org.mule.module.launcher.MuleSharedDomainClassLoader;
import org.mule.module.launcher.artifact.ArtifactClassLoader;
import org.mule.module.launcher.artifact.ShutdownListener;
import org.mule.module.reboot.MuleContainerBootstrapUtils;
import org.mule.util.Preconditions;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MuleDomainClassLoaderRepository implements DomainClassLoaderRepository
{

    private final ClassLoader muleClassLoader;
    private Map<String, ArtifactClassLoader> domainArtifactClassLoaders = new HashMap<String, ArtifactClassLoader>();
    private ArtifactClassLoader defaultDomainArtifactClassLoader;

    public MuleDomainClassLoaderRepository(ClassLoader muleClassLoader)
    {
        this.muleClassLoader = muleClassLoader;
    }

    @Override
    public synchronized ArtifactClassLoader getDomainClassLoader(String domain)
    {
        Preconditions.checkArgument(domain != null, "Domain name cannot be null");
        if (domain.equals(DomainFactory.DEFAULT_DOMAIN_NAME))
        {
            return getDefaultDomainClassLoader();
        }
        if (domainArtifactClassLoaders.containsKey(domain))
        {
            return domainArtifactClassLoaders.get(domain);
        }
        validateDomain(domain);
        //TODO(pablo.kraan): CCL - waht classlaoder must set on the module?
        //TODO(pablo.kraan): CCL - need a descriptor for the domain?
        final MuleModule module = new MuleModule(null);
        ArtifactClassLoader classLoader = new MuleSharedDomainClassLoader(domain, muleClassLoader, module);
        classLoader = createClassLoaderUnregisterWrapper(classLoader);
        domainArtifactClassLoaders.put(domain, classLoader);
        return classLoader;
    }

    @Override
    public ArtifactClassLoader getDefaultDomainClassLoader()
    {
        if (defaultDomainArtifactClassLoader != null)
        {
            return defaultDomainArtifactClassLoader;
        }
        //TODO(pablo.kraan): CCL - which classloader must set on the module?
        //TODO(pablo.kraan): CCL - need a descriptor for the domain?
        final Module module = new MuleModule(null);
        //TODO(pablo.kraan): CCL - track the module once it has a descriptor
        //ModuleTracker.getInstance().addModule(module);
        ArtifactClassLoader classLoader = new MuleSharedDomainClassLoader(DomainFactory.DEFAULT_DOMAIN_NAME, muleClassLoader, module);
        defaultDomainArtifactClassLoader = createClassLoaderUnregisterWrapper(classLoader);
        return defaultDomainArtifactClassLoader;
    }

    private void validateDomain(String domain)
    {
        File domainFolder = new File(MuleContainerBootstrapUtils.getMuleDomainsDir(), domain);
        if (!(domainFolder.exists() && domainFolder.isDirectory()))
        {
            throw new DeploymentException(CoreMessages.createStaticMessage(String.format("Domain %s does not exists", domain)));
        }
    }

    private ArtifactClassLoader createClassLoaderUnregisterWrapper(final ArtifactClassLoader classLoader)
    {
        return new ArtifactClassLoader()
        {
            @Override
            public String getArtifactName()
            {
                return classLoader.getArtifactName();
            }

            @Override
            public URL findResource(String resource)
            {
                return classLoader.findResource(resource);
            }

            @Override
            public URL findLocalResource(String resource)
            {
                return classLoader.findLocalResource(resource);
            }

            @Override
            public ClassLoader getClassLoader()
            {
                return classLoader.getClassLoader();
            }

            @Override
            public void dispose()
            {
                domainArtifactClassLoaders.remove(classLoader.getArtifactName());
                classLoader.dispose();
            }

            @Override
            public void addShutdownListener(ShutdownListener listener)
            {
                classLoader.addShutdownListener(listener);
            }
        };
    }
}
