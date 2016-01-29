/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleContext;
import org.mule.api.lifecycle.LifecycleUtils;
import org.mule.api.metadata.SimpleDataType;
import org.mule.api.object.ObjectFactory;
import org.mule.module.http.internal.ParameterMap;
import org.mule.transformer.types.CollectionDataType;
import org.mule.transformer.types.ListDataType;
import org.mule.transformer.types.SetDataType;
import org.mule.transformer.types.TypedValue;
import org.mule.util.CopyOnWriteCaseInsensitiveMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;

import java.lang.reflect.InvocationHandler;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.DateSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import org.joda.time.DateTime;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Implementation of {@link ObjectFactory} that creates prototype Kryo instances
 * configured with custom serializers so that it can handle common Mule objects
 *
 * @since 3.7.0
 */
final class KryoInstanceFactory
{

    public Kryo getInstance(MuleContext muleContext) throws Exception
    {
        Kryo kryo = new ExternalizableKryo();
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setClassLoader(muleContext.getExecutionClassLoader());
        addStandardSerializers(kryo);
        addMuleSerializers(kryo, muleContext);

        return kryo;
    }

    private void addMuleSerializers(Kryo kryo, MuleContext muleContext) throws Exception
    {
        kryo.register(TypedValue.class, initSerializer(new TypedValueKryoSerializer(), muleContext));
        //kryo.register(DefaultMuleMessage.class, initSerializer(new MuleMessageKryoSerializer(kryo), muleContext));
        kryo.register(DefaultMuleEvent.class, initSerializer(new MuleEventKryoSerializer(kryo), muleContext));
        kryo.register(CopyOnWriteCaseInsensitiveMap.class, new CopyOnWriteCaseInsensitiveMapSerializer());
        kryo.register(SimpleDataType.class, new SimpleDataTypeKryoSerializer());
        kryo.register(CollectionDataType.class, new CollectionDataTypeKryoSerializer());
        kryo.register(SetDataType.class, new SetDataTypeKryoSerializer());
        kryo.register(ListDataType.class, new ListDataTypeKryoSerializer());
        kryo.register(ParameterMap.class, new ParameterMapKryoSerializer());
    }

    private <T> MuleKryoSerializerSupport<T> initSerializer(MuleKryoSerializerSupport<T> serializer, MuleContext muleContext) throws Exception
    {
        LifecycleUtils.initialiseIfNeeded(serializer, muleContext);
        return serializer;
    }

    private void addStandardSerializers(Kryo kryo)
    {
        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
        kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
        kryo.register(InvocationHandler.class, new JdkProxySerializer());
        kryo.register(Date.class, new DateSerializer(Date.class));
        kryo.register(java.sql.Time.class, new DateSerializer(java.sql.Time.class));
        kryo.register(Timestamp.class, new DateSerializer(Timestamp.class));
        kryo.register(java.sql.Date.class, new DateSerializer(java.sql.Date.class));

        ApacheXercesXMLGregorianCalendarKryoSerializer.registerInto(kryo);
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
        kryo.register(DateTime.class, new JodaDateTimeSerializer());
    }
}
