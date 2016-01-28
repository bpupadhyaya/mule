/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.DefaultMuleMessage;

import org.mule.api.serialization.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

/**
 * Implementation of kryo's {@link Serializer} capable of handle instances of
 * {@link DefaultMuleMessage}
 * 
 * @since 3.7.0
 */
final class MuleMessageKryoSerializer extends MuleKryoSerializerSupport<DefaultMuleMessage>
{
    private final FieldSerializer<DefaultMuleMessage> serializer;

    public MuleMessageKryoSerializer(Kryo kryo)
    {
        serializer = new FieldSerializer<>(kryo, DefaultMuleMessage.class);
    }

    @Override
    public void write(Kryo kryo, Output output, final DefaultMuleMessage message)
    {
        serializer.write(kryo, output, message);
        kryo.writeClassAndObject(output, message.getPayload());

        new AttachmentSerializer()
        {

            @Override
            protected Object getAttachment(String name)
            {
                return message.getInboundAttachment(name);
            }

            @Override
            protected Collection<String> getAttachmentNames()
            {
                return message.getInboundAttachmentNames();
            }
        }.serializeAttachments(kryo, output);

        new AttachmentSerializer()
        {

            @Override
            protected Object getAttachment(String name)
            {
                return message.getOutboundAttachment(name);
            }

            @Override
            protected Collection<String> getAttachmentNames()
            {
                return message.getOutboundAttachmentNames();
            }
        }.serializeAttachments(kryo, output);
    }

    @Override
    public DefaultMuleMessage read(Kryo kryo, Input input, Class<DefaultMuleMessage> type)
    {
        final DefaultMuleMessage message = serializer.read(kryo, input, DefaultMuleMessage.class);
        init(message);
        message.setPayload(kryo.readClassAndObject(input));

        new AttachmentDeserealizer()
        {

            @Override
            protected void acceptAttachment(String key, DataHandler data) throws Exception
            {
                message.addInboundAttachment(key, data);
            }
        }.deserealizeAttachments(kryo, input);

        new AttachmentDeserealizer()
        {

            @Override
            protected void acceptAttachment(String key, DataHandler data) throws Exception
            {
                message.addOutboundAttachment(key, data);
            }
        }.deserealizeAttachments(kryo, input);

        return message;
    }

    private abstract class AttachmentSerializer
    {

        protected void serializeAttachments(Kryo kryo, Output output)
        {
            Map<String, DataHandler> attachments = new HashMap<String, DataHandler>();
            for (String name : getAttachmentNames())
            {
                attachments.put(name, (DataHandler) getAttachment(name));
            }

            kryo.writeObject(output, attachments);

        }

        protected abstract Collection<String> getAttachmentNames();

        protected abstract Object getAttachment(String name);
    }

    private abstract class AttachmentDeserealizer
    {

        @SuppressWarnings("unchecked")
        protected void deserealizeAttachments(Kryo kryo, Input input)
        {
            Map<String, DataHandler> attachments = kryo.readObject(input, HashMap.class);

            try
            {
                for (Map.Entry<String, DataHandler> entry : attachments.entrySet())
                {
                    acceptAttachment(entry.getKey(), entry.getValue());
                }
            }
            catch (Exception e)
            {
                throw new SerializationException("Exception was found adding attachment to a MuleMessage", e);
            }
        }

        protected abstract void acceptAttachment(String key, DataHandler data) throws Exception;
    }
}
