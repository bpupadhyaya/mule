/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import java.util.Collections;
import java.util.Set;

/**
 *
 */
public class LoaderOverride
{
    public static final LoaderOverride NULL_LOADER_OVERRIDE = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET);

    private final Set<String> overrides;
    private final Set<String> blocked;

    public LoaderOverride(Set<String> overrides, Set<String> blocked)
    {
        this.overrides = overrides;
        this.blocked = blocked;
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
