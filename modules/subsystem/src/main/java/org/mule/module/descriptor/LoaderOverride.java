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
 * Overrides the default classloading mode for resources in a module.
 * <p/>
 * There are four classloading modes available to load resources:
 * _ Child first/parent last: a resource will be searched on the child classloader and
 * if not found there, on the parent classloader after.
 * _ Child only: a resource will be searched on the child classloader only, no attempt to
 * search on the parent classloader will be made.
 * _ Parent first/child last: a resource will be searched on the parent classloader and
 * if not found there, on the child classloader after.
 * _Parent only: a resource will be searched on the parent classloader only, no attempt to
 * search on the child classloader will be made.
 */
public class LoaderOverride
{

    //TODO(pablo.kraan): CCL - this constant must be used ONLY for testing purposes
    public static final LoaderOverride NULL_LOADER_OVERRIDE = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.EMPTY_SET);

    private final Set<String> parentOnly;
    private final Set<String> parentFirst;
    private final Set<String> childOnly;

    public LoaderOverride(Set<String> parentOnly, Set<String> parentFirst, Set<String> childOnly)
    {
        this.parentOnly = parentOnly;
        this.parentFirst = parentFirst;
        this.childOnly = childOnly;
    }

    public boolean useParentOnly(String name)
    {
        return findMatch(name, this.parentOnly);
    }

    public boolean useParentFirst(String name)
    {
        return findMatch(name, this.parentFirst);
    }

    private boolean findMatch(String name, Set<String> parentOnly)
    {
        // find a match
        boolean matched = false;
        for (String override : parentOnly)
        {
            if (name.equals(override) || name.startsWith(override + "."))
            {
                matched = true;
                break;
            }
        }
        return matched;
    }

    public boolean useChildOnly(String name)
    {
        return findMatch(name, this.childOnly);
    }
}
