/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.DefaultMuleEvent;
import org.mule.api.construct.FlowConstruct;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

/**
 * Implementation of kryo's {@link Serializer} capable of handle instances of
 * {@link DefaultMuleEvent}
 * 
 * @since 3.7.0
 */
final class MuleEventKryoSerializer extends MuleKryoSerializerSupport<DefaultMuleEvent>
{
    private final FieldSerializer<DefaultMuleEvent> serializer;

    public MuleEventKryoSerializer(Kryo kryo)
    {
        serializer = new FieldSerializer<>(kryo, DefaultMuleEvent.class);
    }

    @Override
    public void write(Kryo kryo, Output output, DefaultMuleEvent event)
    {
        serializer.write(kryo, output, event);
        FlowConstruct construct = event.getFlowConstruct();
        kryo.writeObjectOrNull(output, construct != null ? construct.getName() : null, String.class);
    }

    @Override
    public DefaultMuleEvent read(Kryo kryo, Input input, Class<DefaultMuleEvent> type)
    {
        DefaultMuleEvent event = serializer.read(kryo, input, DefaultMuleEvent.class);

        String constructName = kryo.readObjectOrNull(input, String.class);

        if (constructName != null)
        {
            event.setTransientServiceName(constructName);
        }

        init(event);
        return event;
    }

    @Override
    public DefaultMuleEvent copy(Kryo kryo, DefaultMuleEvent original)
    {
        return (DefaultMuleEvent) DefaultMuleEvent.copy(original);
    }

}
