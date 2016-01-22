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

    public LoaderOverride parse(String overrideString)
    {
        //TODO(pablo.kraan): Add tests for this class
        final Set<String> overridden = new HashSet<>();
        final Set<String> blocked = new HashSet<>();

        Set<String> overrides = new HashSet<>();
        final String[] values = overrideString.split(",");
        Collections.addAll(overrides, values);

        if (overrides != null && !overrides.isEmpty())
        {
            for (String override : overrides)
            {
                override = StringUtils.defaultString(override).trim();
                // 'blocked' package definitions come with a '-' prefix
                if (override.startsWith("-"))
                {
                    override = override.substring(1);
                    blocked.add(override);
                }
                else
                {
                    overridden.add(override);
                }

                for (String systemPackage : systemPackages)
                {
                    if (override.startsWith(systemPackage))
                    {
                        throw new IllegalArgumentException("Can't override a system package. Offending value: " + override);
                    }
                }

            }
        }

        return new LoaderOverride(overridden, blocked);
    }
}
