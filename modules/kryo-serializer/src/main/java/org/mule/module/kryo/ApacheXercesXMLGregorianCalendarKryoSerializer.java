/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.api.MuleRuntimeException;
import org.mule.config.i18n.MessageFactory;
import org.mule.util.ClassUtils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link com.esotericsoftware.kryo.Serializer} for the Apache Xerces
 * implementation of {@link javax.xml.datatype.XMLGregorianCalendar}.
 * Because the class {@link org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl} is protected,
 * Kryo can successfully serialize it but fails to deserealize because it can't instantiate it.
 *
 * This custom serializer adds a workaround for that issue. Also, because that class is protected
 * it can't be directly referenced by this code, so reflection is used to load and instantiate it.
 *
 * @since 3.7.0
 */
final class ApacheXercesXMLGregorianCalendarKryoSerializer extends Serializer<XMLGregorianCalendar>
{

    private static final Logger logger = LoggerFactory.getLogger(ApacheXercesXMLGregorianCalendarKryoSerializer.class);

    /**
     * The canonical name of the apache implementation of {@link javax.xml.datatype.XMLGregorianCalendar}
     */
    private static final String CALENDAR_CLASS_NAME = "org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl";

    /**
     * {@link java.lang.Class} object for the apache implementation of {@link javax.xml.datatype.XMLGregorianCalendar}
     */
    private static Class<? extends XMLGregorianCalendar> CALENDAR_CLASS;

    private static Constructor<? extends XMLGregorianCalendar> constructor;

    public static synchronized void registerInto(Kryo kryo)
    {
        loadClass();

        if (CALENDAR_CLASS == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(String.format("Skipping registration of %s since class %s could not be found in classpath",
                                           ApacheXercesXMLGregorianCalendarKryoSerializer.class.getCanonicalName(),
                                           CALENDAR_CLASS_NAME));
            }

            return;
        }

        if (constructor == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug(String.format("Skipping registration of %s since a public constructor couldn't be obtained from class %s",
                                           ApacheXercesXMLGregorianCalendarKryoSerializer.class.getCanonicalName(),
                                           CALENDAR_CLASS_NAME));
            }

            return;
        }

        kryo.register(CALENDAR_CLASS, new ApacheXercesXMLGregorianCalendarKryoSerializer());
    }

    // Get class and its constructor in a static block. If successful, make the constructor accessible
    private static void loadClass()
    {
        if (CALENDAR_CLASS != null)
        {
            return;
        }

        try
        {
            CALENDAR_CLASS = ClassUtils.getClass(CALENDAR_CLASS_NAME);
        }
        catch (ClassNotFoundException e)
        {
            logger.warn(String.format("Could not find %s in classpath. Check your build", CALENDAR_CLASS_NAME), e);
        }

        try
        {
            constructor = CALENDAR_CLASS.getConstructor();
            constructor.setAccessible(true);
        }
        catch (Exception e)
        {
            logger.warn(String.format("Could not find default constructor of class %s", CALENDAR_CLASS_NAME), e);
        }
    }

    private ApacheXercesXMLGregorianCalendarKryoSerializer(){}

    /**
     * @return a new instance of {@link org.apache.xerces.jaxp.datatype.XMLGregorianCalendarImpl}
     */
    public static XMLGregorianCalendar newXmlGregorianCalendar()
    {
        try
        {
            return constructor.newInstance();
        }
        catch (Exception e)
        {
            throw buildException(String.format("Exception found trying to create instance of class %s", CALENDAR_CLASS_NAME), e);
        }
    }

    @Override
    public void write(Kryo kryo, Output output, XMLGregorianCalendar calendar)
    {
        kryo.writeObjectOrNull(output, calendar.getEonAndYear(), BigInteger.class);
        kryo.writeObjectOrNull(output, calendar.getMonth(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getDay(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getHour(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getMinute(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getSecond(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getMillisecond(), Integer.class);
        kryo.writeObjectOrNull(output, calendar.getFractionalSecond(), BigDecimal.class);
        kryo.writeObjectOrNull(output, calendar.getTimezone(), Integer.class);
    }

    @Override
    public XMLGregorianCalendar read(Kryo kryo, Input input, Class<XMLGregorianCalendar> type)
    {
        XMLGregorianCalendar calendar = newXmlGregorianCalendar();

        calendar.setYear(read(kryo, input, BigInteger.class, "year"));
        calendar.setMonth(read(kryo, input, Integer.class, "month"));
        calendar.setDay(read(kryo, input, Integer.class, "day"));
        calendar.setHour(read(kryo, input, Integer.class, "hour"));
        calendar.setMinute(read(kryo, input, Integer.class, "minute"));
        calendar.setSecond(read(kryo, input, Integer.class, "second"));
        calendar.setMillisecond(read(kryo, input, Integer.class, "millisecond"));
        calendar.setFractionalSecond(read(kryo, input, BigDecimal.class, "fractionalSecond"));
        calendar.setTimezone(read(kryo, input, Integer.class, "timezone"));

        return calendar;
    }

    private <T> T read(Kryo kryo, Input input, Class<T> clazz, String attributeName)
    {
        try
        {
            return kryo.readObjectOrNull(input, clazz);
        }
        catch (Exception e)
        {
            throw buildException(String.format("Found exception trying to deserialize attribute '%s' of class %s", attributeName, clazz.getCanonicalName()), e);
        }
    }

    private static MuleRuntimeException buildException(String message, Throwable e)
    {
        return new MuleRuntimeException(MessageFactory.createStaticMessage(message), e);
    }
}
