/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import java.io.File;

/**
 * Defines a way to create a {@link PluginDescriptor} from a folder.
 */
public interface ModuleDescriptorFactory
{

    //TODO(pablo.kraan): CCL - why is this class in CE if its used only on EE?
    /**
     * Creates a plugin descriptor from a plugin folder.
     *
     * @param pluginFolder folder containing plugin files
     * @return a non null descriptor
     * @throws InvalidPluginException if the factory is not able to create a
     *         descriptor from the folder.
     */
    PluginDescriptor create(File pluginFolder) throws InvalidPluginException;

}
