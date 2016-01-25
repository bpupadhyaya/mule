/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ftp.internal.command;

import org.mule.DefaultMuleMessage;
import org.mule.api.connection.ConnectionException;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.ftp.internal.FtpConnector;
import org.mule.extension.ftp.internal.FtpFileAttributes;
import org.mule.extension.ftp.internal.FtpFileSystem;
import org.mule.extension.ftp.internal.FtpInputStream;
import org.mule.module.extension.file.api.NullPathLock;
import org.mule.module.extension.file.api.PathLock;
import org.mule.module.extension.file.api.command.ReadCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTPClient;

/**
 * A {@link FtpCommand} which implements the {@link FtpReadCommand}
 *
 * @since 4.0
 */
public final class FtpReadCommand extends FtpCommand implements ReadCommand
{

    /**
     * {@inheritDoc}
     */
    public FtpReadCommand(FtpFileSystem fileSystem, FtpConnector config, FTPClient client)
    {
        super(fileSystem, config, client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    //TODO: MULE-8946 DefaultMuleMessage should contain the proper generics
    public MuleMessage read(String filePath, boolean lock)
    {
        FtpFileAttributes attributes = getExistingFile(filePath);
        if (attributes.isDirectory())
        {
            throw cannotReadDirectoryException(Paths.get(attributes.getPath()));
        }

        try
        {
            attributes = new FtpFileAttributes(resolvePath(filePath), client.listFiles(filePath)[0]);
        }
        catch (Exception e)
        {
            throw exception("Found exception while trying to list path " + filePath, e);
        }

        Path path = Paths.get(attributes.getPath());

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

        try
        {
            return new DefaultMuleMessage(FtpInputStream.newInstance(config, attributes, pathLock),
                                          fileSystem.getDataType(attributes),
                                          attributes);
        }
        catch (ConnectionException e)
        {
            throw exception("Could not obtain connection to fetch file " + path, e);
        }
    }
}
