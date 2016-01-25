/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal.command;

import org.mule.DefaultMuleMessage;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.file.api.FileConnector;
import org.mule.extension.file.api.FileInputStream;
import org.mule.extension.file.api.LocalFileAttributes;
import org.mule.extension.file.api.LocalFileSystem;
import org.mule.module.extension.file.api.FileAttributes;
import org.mule.module.extension.file.api.NullPathLock;
import org.mule.module.extension.file.api.PathLock;
import org.mule.module.extension.file.api.command.ReadCommand;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link LocalFileCommand} which implements the {@link ReadCommand} contract
 *
 * @since 4.0
 */
public final class LocalReadCommand extends LocalFileCommand implements ReadCommand
{

    /**
     * {@inheritDoc}
     */
    public LocalReadCommand(LocalFileSystem fileSystem, FileConnector config)
    {
        super(fileSystem, config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //TODO: MULE-8946 DefaultMuleMessage should contain the proper generics
    public MuleMessage read(String filePath, boolean lock)
    {
        Path path = resolveExistingPath(filePath);
        if (Files.isDirectory(path))
        {
            throw cannotReadDirectoryException(path);
        }

        PathLock pathLock;
        if (lock)
        {
            pathLock = fileSystem.lock(path);
        }
        else
        {
            fileSystem.verifyNotLocked(path);
            pathLock = new NullPathLock();
        }

        FileAttributes fileAttributes = new LocalFileAttributes(path);
        return new DefaultMuleMessage(new FileInputStream(path, pathLock, config.getMuleContext()),
                                      fileSystem.getDataType(fileAttributes),
                                      fileAttributes);
    }
}
