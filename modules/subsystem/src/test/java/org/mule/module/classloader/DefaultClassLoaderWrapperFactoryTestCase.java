/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mule.api.lifecycle.Startable;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import org.junit.Test;

@SmallTest
public class DefaultClassLoaderWrapperFactoryTestCase extends AbstractMuleTestCase
{

    private DefaultClassLoaderWrapperFactory<Startable> wrapperFactory = new DefaultClassLoaderWrapperFactory<>(Startable.class);

    @Test
    public void wrapsInstance() throws Exception
    {
        Startable deploymentListener = mock(Startable.class);

        Startable wrappedDeploymentListener = wrapperFactory.create(deploymentListener, deploymentListener.getClass().getClassLoader());

        wrappedDeploymentListener.start();
        verify(deploymentListener).start();
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrapsInterfacesOnly() throws Exception
    {
        new DefaultClassLoaderWrapperFactory<>(Integer.class);
    }
}
