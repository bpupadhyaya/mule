/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.util.Preconditions;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.util.DefaultStreamFactory;
import com.esotericsoftware.kryo.util.MapReferenceResolver;

/**
 * Subclass of {@link com.esotericsoftware.kryo.Kryo} which uses consistent, repeteable and predictable classes ids.
 * When Kryo serializes and object, it doesn't store its canonical class name. Instead, it uses a sequential ID to
 * refer to each encountered class and only stores that id. That is fine when the types of all serialized objects is
 * predictable. Because Mule supports custom cloud connectors, components and transformers, this condition is not met.
 * <p/>
 * So, suppose there's a Mule instance storing instances of Apple and Banana into a persistent queue. When serializing,
 * Kryo will assign the id N to the class Apple and the id N+1 to the class Banana.
 * If there's another Mule running in a separate JVM, polling from that queue concurrently, there's a good chance that
 * this different instance of Kryo finds a Banana before it finds an Apple, swapping their ids and resulting in a
 * deserealization error.
 * <p/>
 * Another possible scenario is one in which the second instance tries to deserialize a class it hasn't yet registered,
 * returning in an exception with a message like &quot;encountered unregistered class ID&quot;. This scenario is
 * specially likely in recovery and cluster scenarios. This scenario is also fixed by this because Kryo will attempt to
 * create a new registration for this unknown type and as long as the ids match no issue should raise.
 *
 * @since 3.7.0
 */
final class ExternalizableKryo extends Kryo
{

    private Class<?> classBeingRegistered = null;

    public ExternalizableKryo()
    {
        super(new ModuleClassResolver(), new MapReferenceResolver(), new DefaultStreamFactory());
    }

    @Override
    public Registration register(Class type, Serializer serializer)
    {
        classBeingRegistered = type;
        try
        {
            return super.register(type, serializer);
        }
        finally
        {
            classBeingRegistered = null;
        }
    }

    @Override
    public int getNextRegistrationId()
    {
        Preconditions.checkState(classBeingRegistered != null, "Cannot generate an id while no class is being registered");
        return classBeingRegistered.getCanonicalName().hashCode();
    }

    protected Class<?> getClassBeingRegistered()
    {
        return classBeingRegistered;
    }

}
