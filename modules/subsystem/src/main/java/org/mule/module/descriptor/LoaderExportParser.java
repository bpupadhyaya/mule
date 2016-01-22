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
public class LoaderExportParser
{

    public LoaderExport parse(String exportedClasses)
    {
        Set<String> exportedPrefixes = new HashSet<>();
        Set<String> blockedPrefixes = new HashSet<>();

        for (String exported : exportedClasses.split(","))
        {
            if (exported.startsWith("-"))
            {
                blockedPrefixes.add(exported.substring(1, exported.length()));
            }
            else
            {
                exportedPrefixes.add(exported);
            }
        }

        return new LoaderExport(exportedPrefixes, blockedPrefixes);
    }

}
