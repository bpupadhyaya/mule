/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import org.mule.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class LoaderOverrideParser
{

    // Finished with '.' so that we can use startsWith to verify
    protected final String[] systemPackages = {
            "java.",
            "javax.",
            "org.mule.",
            "com.mulesoft.",
            "com.mulesource."
    };

    private final Set<String> parentOnly;

    public LoaderOverrideParser(Set<String> parentOnly)
    {
        //TODO(pablo.kraan): CCL - can parentOnly and system packages be joined?
        this.parentOnly = parentOnly;
    }

    public LoaderOverride parse(String overrideString)
    {
        //TODO(pablo.kraan): Add tests for this class
        final Set<String> parentFirst = new HashSet<>();
        final Set<String> childOnly = new HashSet<>();

        Set<String> overrides = new HashSet<>();
        final String[] values = overrideString.split(",");
        Collections.addAll(overrides, values);

        if (overrides != null && !overrides.isEmpty())
        {
            for (String resource : overrides)
            {
                resource = StringUtils.defaultString(resource).trim();
                // 'resource' package definitions come with a '-' prefix
                if (resource.startsWith("-"))
                {
                    resource = resource.substring(1);
                    childOnly.add(resource);
                }
                else
                {
                    parentFirst.add(resource);
                }

                for (String systemPackage : systemPackages)
                {
                    if (resource.startsWith(systemPackage))
                    {
                        throw new IllegalArgumentException("Cannot override a system package. Offending value: " + resource);
                    }
                }

            }
        }

        return new LoaderOverride(parentOnly, parentFirst, childOnly);
    }
}
