/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.LifecycleUtils;
import org.mule.api.transport.PropertyScope;
import org.mule.module.kryo.ApacheXercesXMLGregorianCalendarKryoSerializer;
import org.mule.module.kryo.KryoObjectSerializer;
import org.mule.serialization.AbstractObjectSerializerContractTestCase;
import org.mule.serialization.NotSerializableTestObject;
import org.mule.tck.size.SmallTest;
import org.mule.transport.NullPayload;

import org.mule.module.kryo.compression.KryoCompressionMode;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@SmallTest
@RunWith(Parameterized.class)
public class KryoObjectSerializerTestCase extends AbstractObjectSerializerContractTestCase
{

    private static final String STREAM_MIME_TYPE = "application/octet-stream";
    private static final String FLOW_VAR = "FLOW_VAR";
    private static final String INBOUND_PROPERTY = "INBOUND_PROPERTY";
    private static final String OUTBOUND_PROPERTY = "OUTBOUND_PROPERTY";
    private static final String SESSION_PROPERTY = "SESSION_PROPERTY";
    private static final String INBOUND_ATTACHMENT = "INBOUND_ATTACHMENT";
    private static final String OUTBOUND_ATTACHMENT = "OUTBOUND_ATTACHMENT";
    private static final String HELLO = "hola amigos!";
    private final KryoCompressionMode compressionMode;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][] {
                {KryoCompressionMode.NONE},
                {KryoCompressionMode.DEFLATE},
                {KryoCompressionMode.GZIP},
        });
    }

    public KryoObjectSerializerTestCase(KryoCompressionMode compressionMode)
    {
        this.compressionMode = compressionMode;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();

        KryoObjectSerializer serializer = new KryoObjectSerializer(compressionMode);
        LifecycleUtils.initialiseIfNeeded(serializer, muleContext);

        this.serializer = serializer;
    }

    @Test
    public void serializeMuleEvent() throws Exception
    {
        NotSerializableTestObject test = new NotSerializableTestObject(HELLO);

        MuleEvent event = getTestEvent(test);
        MuleMessage message = event.getMessage();

        event.setFlowVariable(FLOW_VAR, test);
        event.getSession().setProperty(SESSION_PROPERTY, test);
        message.setProperty(INBOUND_PROPERTY, test, PropertyScope.INBOUND);
        message.setOutboundProperty(OUTBOUND_PROPERTY, test);

        byte[] bytes = serializer.serialize(event);

        event = serializer.deserialize(bytes);
        assertThat(test.getName(), is(((NotSerializableTestObject) event.getFlowVariable(FLOW_VAR)).getName()));
        assertThat(test.getName(), is(((NotSerializableTestObject) event.getSession().getProperty(SESSION_PROPERTY)).getName()));
        assertThat(event.getMuleContext(), is(notNullValue()));
        assertThat(event.getFlowConstruct(), is(notNullValue()));

        message = event.getMessage();
        assertThat(test.getName(), is(((NotSerializableTestObject) message.getPayload()).getName()));
        assertThat(test.getName(), is(((NotSerializableTestObject) message.getOutboundProperty(OUTBOUND_PROPERTY)).getName()));
        assertThat(test.getName(), is(((NotSerializableTestObject) message.getInboundProperty(INBOUND_PROPERTY)).getName()));
    }

    @Test
    public void serializeMuleMessage() throws Exception
    {
        NotSerializableTestObject test = new NotSerializableTestObject(HELLO);

        MuleEvent event = getTestEvent(test);
        MuleMessage message = event.getMessage();
        message.setOutboundProperty(OUTBOUND_PROPERTY, test);
        byte[] bytes = serializer.serialize(message);

        message = serializer.deserialize(bytes);
        assertThat(test.getName(), is(((NotSerializableTestObject) message.getPayload()).getName()));
        assertThat(test.getName(), is(((NotSerializableTestObject) message.getOutboundProperty(OUTBOUND_PROPERTY)).getName()));
    }

    @Test
    public void serializeMuleMessageWithAttachments() throws Exception
    {
        NotSerializableTestObject test = new NotSerializableTestObject(HELLO);

        MuleEvent event = getTestEvent(test);
        DefaultMuleMessage message = (DefaultMuleMessage) event.getMessage();
        message.setOutboundProperty(OUTBOUND_PROPERTY, test);

        message.addInboundAttachment(INBOUND_ATTACHMENT,
                                     new DataHandler(new ByteArrayInputStream(HELLO.getBytes()), STREAM_MIME_TYPE));
        message.addOutboundAttachment(OUTBOUND_ATTACHMENT,
                                      new DataHandler(new ByteArrayInputStream(HELLO.getBytes()), STREAM_MIME_TYPE));

        byte[] bytes = serializer.serialize(message);

        message = serializer.deserialize(bytes);

        assertThat(test.getName(), is(((NotSerializableTestObject) message.getPayload()).getName()));
        assertThat(HELLO, is(read(message.getInboundAttachment(INBOUND_ATTACHMENT))));
        assertThat(HELLO, is(read(message.getOutboundAttachment(OUTBOUND_ATTACHMENT))));
    }

    @Test
    public void serializeNullPayload() throws Exception
    {
        MuleEvent event = getTestEvent(NullPayload.getInstance());
        byte[] bytes = serializer.serialize(event.getMessage());

        MuleMessage message = serializer.deserialize(bytes);
        assertThat(message.getPayload(), is(instanceOf(NullPayload.class)));
    }

    @Test
    public void serializeInputStream() throws Exception
    {
        String value = HELLO;
        ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());

        MuleEvent event = getTestEvent(in);
        byte[] bytes = serializer.serialize(event.getMessage());

        MuleMessage message = serializer.deserialize(bytes);
        in = (ByteArrayInputStream) message.getPayload();
        assertThat(HELLO, is(IOUtils.toString(in)));
    }

    @Test
    public void serializeDataHandlerWithInputStream() throws Exception
    {
        String value = HELLO;
        ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());

        MuleEvent event = getTestEvent(new DataHandler(in, STREAM_MIME_TYPE));
        byte[] bytes = serializer.serialize(event.getMessage());

        MuleMessage message = serializer.deserialize(bytes);
        assertThat(HELLO, is(read((DataHandler) message.getPayload())));
    }

    @Test
    public void serializeDataHandlerWithPojo() throws Exception
    {
        NotSerializableTestObject test = new NotSerializableTestObject(HELLO);

        MuleEvent event = getTestEvent(new DataHandler(test, STREAM_MIME_TYPE));
        byte[] bytes = serializer.serialize(event.getMessage());

        MuleMessage message = serializer.deserialize(bytes);
        DataHandler handler = (DataHandler) message.getPayload();
        test = (NotSerializableTestObject) handler.getContent();
        assertThat(HELLO, is(test.getName()));
    }

    @Test
    public void serializeTheUnserializable() throws Exception
    {
        NotSerializableTestObject test = new NotSerializableTestObject(HELLO);

        byte[] bytes = serializer.serialize(test);
        test = serializer.deserialize(bytes);
        assertThat(HELLO, is(test.getName()));
    }

    @Test
    public void serializeApacheXMLGregorianCalendar() throws Exception
    {
        final BigInteger year = new BigInteger("1983");
        final int month = 4;
        final int day = 20;
        final int hour = 21;
        final int minute = 15;
        final int seconds = 10;
        final int millisecond = 999;
        final int timezone = -3;

        XMLGregorianCalendar calendar = newXmlGregorianCalendar(year, month, day, hour, minute, seconds, timezone);
        calendar.setMillisecond(millisecond);

        byte[] bytes = serializer.serialize(calendar);
        XMLGregorianCalendar test = serializer.deserialize(bytes);
        assertCalendarDeserealization(test, month, year, hour, minute, day, timezone, seconds);
        assertThat(millisecond, is(calendar.getMillisecond()));
    }

    @Test
    public void serializeApacheXMLGregorianCalendarWithFractionalSeconds() throws Exception
    {
        final BigInteger year = new BigInteger("1983");
        final int month = 4;
        final int day = 20;
        final int hour = 21;
        final int minute = 15;
        final int seconds = 10;
        final BigDecimal fractionalSeconds = new BigDecimal("0.5");
        final int timezone = -3;

        XMLGregorianCalendar calendar = newXmlGregorianCalendar(year, month, day, hour, minute, seconds, timezone);
        calendar.setFractionalSecond(fractionalSeconds);

        byte[] bytes = serializer.serialize(calendar);
        XMLGregorianCalendar test = serializer.deserialize(bytes);
        assertCalendarDeserealization(test, month, year, hour, minute, day, timezone, seconds);
        assertThat(fractionalSeconds, is(test.getFractionalSecond()));
    }

    @Test
    public void serializeObjectList() throws Exception
    {
        final int size = 10;
        List<NotSerializableTestObject> objects = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            objects.add(new NotSerializableTestObject(HELLO));
        }

        byte[] bytes = serializer.serialize(objects);
        objects = serializer.deserialize(bytes);

        for (NotSerializableTestObject test : objects)
        {
            assertThat(HELLO, is(test.getName()));
        }
    }

    @Test
    public void serializeSqlTimestamp() throws Exception
    {
        assertDateSerialization(new Timestamp(1401376405000l));
    }

    @Test
    public void serializeSqlDate() throws Exception
    {
        assertDateSerialization(new java.sql.Date(1401376405000l));
    }

    @Test
    public void serializeJavaDate() throws Exception
    {
        assertDateSerialization(new Date(1401376405000l));
    }

    @Test
    public void serializeSqlTime() throws Exception
    {
        assertDateSerialization(new java.sql.Time(1401376405000l));
    }

    private void assertDateSerialization(final Date date)
    {
        byte[] bytes = serializer.serialize(date);

        Object deserialized = serializer.deserialize(bytes);
        assertThat(date.getClass().isInstance(deserialized), is(true));
        assertThat(date, is(equalTo(deserialized)));
    }

    private String read(DataHandler handler) throws Exception
    {
        ByteArrayInputStream in = (ByteArrayInputStream) handler.getContent();
        try
        {
            return IOUtils.toString(in);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    private void assertCalendarDeserealization(XMLGregorianCalendar calendar, int month, BigInteger year, int hour, int minute, int day, int timezone, int seconds)
    {
        assertThat(calendar, is(notNullValue()));
        assertThat(year, is(calendar.getEonAndYear()));
        assertThat(month, is(calendar.getMonth()));

        assertThat(day, is(calendar.getDay()));
        assertThat(hour, is(calendar.getHour()));
        assertThat(minute, is(calendar.getMinute()));
        assertThat(seconds, is(calendar.getSecond()));
        assertThat(timezone, is(calendar.getTimezone()));
    }

    private XMLGregorianCalendar newXmlGregorianCalendar(BigInteger year, int month, int day, int hour, int minute, int seconds, int timezone)
    {
        XMLGregorianCalendar calendar = ApacheXercesXMLGregorianCalendarKryoSerializer.newXmlGregorianCalendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setHour(hour);
        calendar.setMinute(minute);
        calendar.setSecond(seconds);
        calendar.setTimezone(timezone);

        return calendar;
    }
}
