/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.api;

import static org.mule.config.i18n.MessageFactory.createStaticMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for implementations of {@link FileAttributes}
 *
 * @since 4.0
 */
public abstract class AbstractFileAttributes implements FileAttributes
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileAttributes.class);

    protected transient final Path path;
    protected transient final PathLock lock;

    /**
     * Creates a new instance
     *
     * @param path a {@link Path} pointing to the represented file
     */
    protected AbstractFileAttributes(Path path)
    {
        this(path, new NullPathLock());
    }

    /**
     * Creates a new instance.
     * <p>
     * This constructor allows providing a {@link PathLock}
     * to be released when the {@link #close()} method is invoked
     * or when the {@link InputStream} returned by the
     * {@link #getContent()} method is closed or fully consumed.
     * <p>
     * It is the responsibility of whomever invokes this constructor
     * to invoke (if necessary) the {@link PathLock#tryLock()} method
     * on the supplied {@code lock}.
     *
     * @param path a {@link Path} pointing to the represented file
     * @param lock a {@link PathLock}
     */
    protected AbstractFileAttributes(Path path, PathLock lock)
    {
        this.path = path;
        this.lock = lock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath()
    {
        return path.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return path.getFileName().toString();
    }

    @Override
    public boolean isLocked()
    {
        return lock.isLocked();
    }

    protected LocalDateTime asDateTime(Instant instant)
    {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
