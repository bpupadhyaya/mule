/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.internal.command;

import org.mule.extension.api.runtime.ContentMetadata;
import org.mule.extension.file.api.FileConnector;
import org.mule.extension.file.api.LocalFileAttributes;
import org.mule.extension.file.api.LocalFileSystem;
import org.mule.module.extension.file.api.FileAttributes;
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
    public FileAttributes read(String filePath, boolean lock, ContentMetadata contentMetadata)
    {
        Path path = resolveExistingPath(filePath);
        if (Files.isDirectory(path))
        {
            throw cannotReadDirectoryException(path);
        }

        FileAttributes fileAttributes;
        if (lock)
        {
            fileAttributes = new LocalFileAttributes(path, fileSystem.lock(path));
        }
        else
        {
            fileSystem.verifyNotLocked(path);
            fileAttributes = new LocalFileAttributes(path);
        }

        fileSystem.updateContentMetadata(fileAttributes, contentMetadata);
        return fileAttributes;
    }
}
