/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.factory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ModuleDescriptor
{

    //TODO(pablo.kraan): modules should have different descriptors depending on the type (app, connector, mule plugin, etc)
    //Maybe those plugins should have a different descriptor factory.
    private String name;
    private File rootFolder;
    private Set<String> loaderOverrides = Collections.emptySet();
    protected List<String> exportedPrefixNames = Collections.EMPTY_LIST;
    protected List<String> blockedPrefixNames = Collections.EMPTY_LIST;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public File getRootFolder()
    {
        return rootFolder;
    }

    public void setRootFolder(File rootFolder)
    {
        if (rootFolder == null)
        {
            throw new IllegalArgumentException("Root folder cannot be null");
        }

        this.rootFolder = rootFolder;
    }

    public void setLoaderOverride(Set<String> loaderOverrides)
    {
        if (loaderOverrides == null)
        {
            throw new IllegalArgumentException("Loader overrides cannot be null");
        }

        this.loaderOverrides = Collections.unmodifiableSet(loaderOverrides);
    }

    public Set<String> getLoaderOverrides()
    {
        return loaderOverrides;
    }

}
