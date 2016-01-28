/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.config;

import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;

import org.mule.module.kryo.KryoObjectSerializer;

/**
 * A {@link OrphanDefinitionParser} to parse instances of {@link KryoObjectSerializer}
 *
 * @since 3.7.0
 */
final class KryoObjectSerializerDefinitionParser extends OrphanDefinitionParser
{

    public KryoObjectSerializerDefinitionParser()
    {
        super(KryoObjectSerializerFactoryBean.class, true);
        addIgnored("name");
    }
}
