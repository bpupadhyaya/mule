/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all information needed to create and use an mule plugin.
 */
public class PluginDescriptor extends ModuleDescriptor
{

    private String className;
    private Map<String, String> customProperties = new HashMap<String, String>();
    private boolean enabled = true;

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

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
