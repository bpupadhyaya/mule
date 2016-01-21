/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.factory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains all information needed to create and use an mule plugin.
 */
public class PluginDescriptor
{

    private String name;

    private String className;

    private File rootFolder;

    // Plugins are enabled by default
    private boolean enabled = true;

    private Set<String> loaderOverrides = Collections.emptySet();

    private Map<String, String> customProperties = new HashMap<String, String>();
    private List<String> exportedPrefixNames = Collections.EMPTY_LIST;
    private List<String> blockedPrefixNames = Collections.EMPTY_LIST;

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        if (className == null || className.isEmpty())
        {
            throw new IllegalArgumentException("Cannot set a null value on className");
        }
        this.className = className;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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

    public void addCustomProperty(String name, String value)
    {
        customProperties.put(name, value);
    }

    public Map<String, String> getCustomProperties()
    {
        Map<String, String> result = new HashMap<String, String>();
        result.putAll(customProperties);

        return result;
    }

    public void setExportedPrefixNames(List<String> exported)
    {
        this.exportedPrefixNames = Collections.unmodifiableList(exported);
    }

    /**
     * @return an immutable list of exported class prefix names
     */
    public List<String> getExportedPrefixNames()
    {
        return exportedPrefixNames;
    }

    public void setBlockedPrefixNames(List<String> blocked)
    {
        this.blockedPrefixNames = Collections.unmodifiableList(blocked);
    }

    /**
     * @return an immutable list of blocked class prefix names
     */
    public List<String> getBlockedPrefixNames()
    {
        return blockedPrefixNames;
    }
}
