/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mule.api.MuleContext;
import org.mule.module.kryo.ExternalizableKryo;
import org.mule.module.kryo.KryoInstanceFactory;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import com.esotericsoftware.kryo.Kryo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class KryoInstanceFactoryTestCase extends AbstractMuleTestCase
{

    @Mock
    private MuleContext muleContext;

    private KryoInstanceFactory factory;

    @Before
    public void setUp() throws Exception
    {
        when(muleContext.getExecutionClassLoader()).thenReturn(getClass().getClassLoader());
        factory = new KryoInstanceFactory();
    }

    @Test
    public void returnsExternalizableKryo() throws Exception
    {
        Kryo kryo = factory.getInstance(muleContext);
        assertThat(kryo, is(instanceOf(ExternalizableKryo.class)));
        assertThat(getClass().getClassLoader(), is(sameInstance(kryo.getClassLoader())));
        verify(muleContext).getExecutionClassLoader();
    }
}
