/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class LoaderExport
{

    private final List<String> exportedPrefixNames;
    private final List<String> blockedPrefixNames;

    public LoaderExport(List<String> exportedPrefixNames, List<String> blockedPrefixNames)
    {
        this.exportedPrefixNames = new ArrayList<>(exportedPrefixNames);
        this.blockedPrefixNames = new ArrayList<>(blockedPrefixNames);
    }

    public boolean isExported(String name)
    {
        return !isBlockedClass(name) && isExportedClass(name) || !isBlockedPrefix(name) && isExportedPrefix(name);
    }

    public List<String> getExportedPrefixNames()
    {
        return exportedPrefixNames;
    }

    public List<String> getBlockedPrefixNames()
    {
        return blockedPrefixNames;
    }

    private boolean isBlockedPrefix(String name)
    {
        return hasListedPrefix(name, blockedPrefixNames);
    }

    private boolean isBlockedClass(String name)
    {
        return blockedPrefixNames.contains(name);
    }

    private boolean isExportedClass(String name)
    {
        return exportedPrefixNames.contains(name);
    }

    private boolean isExportedPrefix(String name)
    {
        return hasListedPrefix(name, exportedPrefixNames);
    }

    private boolean hasListedPrefix(String name, List<String> classes)
    {
        for (String exported : classes)
        {
            if (name.startsWith(exported))
            {
                return true;
            }
        }

        return false;
    }
}
