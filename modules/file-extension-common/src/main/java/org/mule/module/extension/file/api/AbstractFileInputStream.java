/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.file.api;

import org.mule.api.MuleContext;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * Base class for {@link InputStream} instances returned by connectors
 * which operate over a {@link FileSystem}.
 * <p>
 * It's an {@link AutoCloseInputStream} which also contains the concept
 * of a {@link PathLock} which is released when the stream is closed
 * or fully consumed.
 *
 * @since 4.0
 */
public abstract class AbstractFileInputStream extends AutoCloseInputStream
{

    private static class StreamSupplier implements Supplier<InputStream>
    {

        private volatile InputStream stream;
        private Supplier<InputStream> delegate;

        private StreamSupplier(Supplier<InputStream> streamFactory)
        {
            delegate = () -> {
                synchronized (this)
                {
                    if (stream == null)
                    {
                        stream = streamFactory.get();
                        delegate = () -> stream;
                    }

                    return stream;
                }
            };
        }

        @Override
        public InputStream get()
        {
            return delegate.get();
        }
    }

    private static InputStream createLazyStream(Supplier<InputStream> streamFactory, MuleContext muleContext)
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(muleContext.getExecutionClassLoader());
        return (InputStream) enhancer.create(InputStream.class, new MethodInterceptor()
        {
            private final StreamSupplier streamSupplier = new StreamSupplier(streamFactory);

            @Override
            public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable
            {
                return methodProxy.invoke(streamSupplier.get(), arguments);
            }
        });
    }

    private final PathLock lock;

    public AbstractFileInputStream(Supplier<InputStream> streamFactory, PathLock lock, MuleContext muleContext)
    {
        super(createLazyStream(streamFactory, muleContext));
        this.lock = lock;
    }

    /**
     * Closes the stream and invokes {@link PathLock#release()}
     * on the {@link #lock}
     *
     * @throws IOException in case of error
     */
    @Override
    public final synchronized void close() throws IOException
    {
        try
        {
            doClose();
        }
        finally
        {
            lock.release();
        }
    }

    protected void doClose() throws IOException
    {
        super.close();
    }

    public boolean isLocked()
    {
        return lock.isLocked();
    }
}
