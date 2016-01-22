/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import org.mule.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class LoaderOverride
{
    // Finished with '.' so that we can use startsWith to verify
    protected String[] systemPackages = {
            "java.",
            "javax.",
            "org.mule.",
            "com.mulesoft.",
            "com.mulesource."
    };

    protected Set<String> overrides = new HashSet<String>();
    protected Set<String> blocked = new HashSet<String>();

    public LoaderOverride(Set<String> rules)
    {
        processOverrides(rules);
    }

    protected void processOverrides(Set<String> overrides)
    {
        if (overrides != null && !overrides.isEmpty())
        {
            for (String override : overrides)
            {
                override = StringUtils.defaultString(override).trim();
                // 'blocked' package definitions come with a '-' prefix
                if (override.startsWith("-"))
                {
                    override = override.substring(1);
                    this.blocked.add(override);
                }
                this.overrides.add(override);

                for (String systemPackage : systemPackages)
                {
                    if (override.startsWith(systemPackage))
                    {
                        throw new IllegalArgumentException("Can't override a system package. Offending value: " + override);
                    }
                }
            }
        }
    }

    public boolean isOverridden(String name)
    {
        // find a match
        boolean overrideMatch = false;
        for (String override : overrides)
        {
            if (name.equals(override) || name.startsWith(override + "."))
            {
                overrideMatch = true;
                break;
            }
        }
        return overrideMatch;
    }

    public boolean isBlocked(String name)
    {
        boolean blockedMatch = false;
        for (String b : blocked)
        {
            if (name.equals(b) || name.startsWith(b + "."))
            {
                blockedMatch = true;
                break;
            }
        }
        return blockedMatch;
    }

}
