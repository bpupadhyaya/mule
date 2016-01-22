/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import java.io.File;
import java.util.Collections;

public class ModuleDescriptor
{

    //TODO(pablo.kraan): modules should have different descriptors depending on the type (app, connector, mule plugin, etc)
    //Maybe those plugins should have a different descriptor factory.
    private String name;
    private File rootFolder;
    private LoaderExport loaderExport = new LoaderExport(Collections.EMPTY_SET, Collections.EMPTY_SET);
    private LoaderOverride loaderOverride = LoaderOverride.NULL_LOADER_OVERRIDE;

    public ModuleDescriptor(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        //TODO(pablo.kraan): CCL - this method is used on the case of Application. Try to remove it
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

    public LoaderExport getLoaderExport()
    {
        return loaderExport;
    }

    public void setLoaderExport(LoaderExport loaderExport)
    {
        if (loaderExport == null)
        {
            throw new IllegalArgumentException("Loader export cannot be null");
        }

        this.loaderExport = loaderExport;
    }

    public LoaderOverride getLoaderOverride()
    {
        return loaderOverride;
    }

    public void setLoaderOverride(LoaderOverride loaderOverride)
    {
        if (loaderOverride == null)
        {
            throw new IllegalArgumentException("Loader override cannot be null");
        }

        this.loaderOverride = loaderOverride;
    }
}
