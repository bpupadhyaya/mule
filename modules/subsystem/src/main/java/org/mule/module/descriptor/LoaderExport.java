/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class LoaderExport
{

    private final Set<String> exportedPrefixNames;
    private final Set<String> blockedPrefixNames;

    public LoaderExport(Set<String> exportedPrefixNames, Set<String> blockedPrefixNames)
    {
        this.exportedPrefixNames = new HashSet<>(exportedPrefixNames);
        this.blockedPrefixNames = new HashSet<>(blockedPrefixNames);
    }

    public boolean isExported(String name)
    {
        return !isBlockedClass(name) && isExportedClass(name) || !isBlockedPrefix(name) && isExportedPrefix(name);
    }

    public Set<String> getExportedPrefixNames()
    {
        return exportedPrefixNames;
    }

    public Set<String> getBlockedPrefixNames()
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

    private boolean hasListedPrefix(String name, Set<String> classes)
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
