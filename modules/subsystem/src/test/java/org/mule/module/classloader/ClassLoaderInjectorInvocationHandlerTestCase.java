/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.net.URL;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@SmallTest
public class ClassLoaderInjectorInvocationHandlerTestCase extends AbstractMuleTestCase
{

    @Test
    public void delegatesMethodInvocation() throws Exception
    {
        TestDelegate delegate = mock(TestDelegate.class);
        ClassLoader classLoader = getContextClassLoader();

        TestDelegate proxy = (TestDelegate) ClassLoaderInjectorInvocationHandler.createProxy(delegate, classLoader, new Class<?>[] {TestDelegate.class});

        proxy.doStuff();

        verify(delegate).doStuff();
    }

    @Test
    public void usesPluginClassLoaderOnMethodDelegation() throws Exception
    {
        final ClassLoader classLoader = new ModuleClassLoader(Thread.currentThread().getContextClassLoader(), new URL[0]);
        TestDelegate delegate = mock(TestDelegate.class);
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat(getContextClassLoader(), equalTo(classLoader));

                return null;
            }
        }).when(delegate).doStuff();

        TestDelegate proxy = (TestDelegate) ClassLoaderInjectorInvocationHandler.createProxy(delegate, classLoader, new Class<?>[] {TestDelegate.class});

        proxy.doStuff();
    }

    @Test
    public void restoresOriginalClassLoaderAfterMethodDelegation() throws Exception
    {
        ClassLoader originalClassLoader = getContextClassLoader();

        TestDelegate delegate = mock(TestDelegate.class);
        ClassLoader classLoader = new ModuleClassLoader(Thread.currentThread().getContextClassLoader(), new URL[0]);

        ClassLoaderInjectorInvocationHandler.createProxy(delegate, classLoader, new Class<?>[] {TestDelegate.class});

        assertSame(originalClassLoader, getContextClassLoader());
    }

    private ClassLoader getContextClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    public static interface TestDelegate
    {

        void doStuff();
    }
}
