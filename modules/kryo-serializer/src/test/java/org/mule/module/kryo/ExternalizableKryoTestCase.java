/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.mule.module.kryo.ExternalizableKryo;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;

import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SmallTest
public class ExternalizableKryoTestCase extends AbstractMuleTestCase
{

    private ExternalizableKryo kryo;

    @Before
    public void setUp()
    {
        kryo = new ExternalizableKryo();
    }

    @After
    public void tearDown() throws Exception
    {
        assertThat(kryo.getClassBeingRegistered(), is(nullValue()));
    }

    @Test
    public void registerPojo() throws Exception
    {
        Registration registration = kryo.register(Banana.class);
        assertThat(Banana.class.getCanonicalName().hashCode(), is(registration.getId()));
        assertThat(Banana.class, is(equalTo(registration.getType())));
    }

    @Test
    public void registerPojoTwice() throws Exception
    {
        Registration registration1 = kryo.register(Banana.class);
        Registration registration2 = kryo.register(Banana.class);
        assertThat(registration1.getId(), is(registration2.getId()));
        assertThat(Banana.class, is(equalTo(registration1.getType())));
        assertThat(registration1.getType(), is(equalTo(registration2.getType())));
    }

    @Test
    public void registerMultiplePojos() throws Exception
    {
        Registration registration1 = kryo.register(Apple.class);
        Registration registration2 = kryo.register(Banana.class);
        assertThat(registration1.getId(), is(not(registration2.getId())));
        assertThat(Apple.class, is(equalTo(registration1.getType())));
        assertThat(Banana.class, is(equalTo(registration2.getType())));
    }

    @Test(expected = IllegalStateException.class)
    public void getIdWithoutRegisteringClass() throws Exception
    {
        kryo.getNextRegistrationId();
    }

    @Test
    public void deserealizeByDifferentInstance() throws Exception
    {
        Banana banana = new Banana();
        banana.peel();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream);

        kryo.writeClassAndObject(output, banana);
        output.close();

        byte[] bananaBytes = stream.toByteArray();

        // reset kryo
        setUp();

        Input input = new Input(new ByteArrayInputStream(bananaBytes));
        banana = (Banana) kryo.readClassAndObject(input);
        assertThat(banana.isPeeled(), is(true));
    }

}
