/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher;

import org.mule.config.Preferred;
import org.mule.module.launcher.descriptor.ApplicationDescriptor;
import org.mule.module.launcher.descriptor.ApplicationDescriptorFactory;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SmallTest
public class ApplicationDescriptorFactoryTestCase extends AbstractMuleTestCase
{

    @Test
    public void testOverridePreferred() throws Exception
    {
        DefaultAppBloodhound bh = new DefaultAppBloodhound();
        MultiMap overrides = new MultiValueMap();
        overrides.put("properties", new TestApplicationDescriptorFactoryDefault());

        // test with default annotation values
        bh.mergeParserOverrides(overrides);
        assertEquals(1, bh.parserRegistry.size());
        ApplicationDescriptorFactory result = bh.parserRegistry.get("properties");
        assertNotNull(result);
        assertTrue("@Preferred implementation ignored", result instanceof TestApplicationDescriptorFactoryDefault);
    }

    @Test
    public void testBothPreferredWithWeight() throws Exception
    {
        DefaultAppBloodhound bh = new DefaultAppBloodhound();
        MultiMap overrides = new MultiValueMap();
        overrides.put("properties", new TestApplicationDescriptorFactoryDefault());
        overrides.put("properties", new TestApplicationDescriptorFactoryPreferred());

        // test with weigh attribute (we have 3 candidates now)
        bh.mergeParserOverrides(overrides);
        assertEquals(1, bh.parserRegistry.size());
        ApplicationDescriptorFactory result = bh.parserRegistry.get("properties");
        assertNotNull(result);
        assertTrue("@Preferred implementation ignored", result instanceof TestApplicationDescriptorFactoryPreferred);
    }

    @Test
    public void testOverrideWithoutPreferred() throws Exception
    {
        DefaultAppBloodhound bh = new DefaultAppBloodhound();
        MultiMap overrides = new MultiValueMap();
        overrides.put("properties", new TestApplicationDescriptorFactoryNoAnnotation());

        // test with weigh attribute (we have 3 candidates now)
        bh.mergeParserOverrides(overrides);
        assertEquals(1, bh.parserRegistry.size());
        ApplicationDescriptorFactory result = bh.parserRegistry.get("properties");
        assertNotNull(result);
        assertTrue("@Preferred implementation ignored", result instanceof TestApplicationDescriptorFactoryNoAnnotation);
    }

    @Test
    public void testMixedOverrides() throws Exception
    {
        DefaultAppBloodhound bh = new DefaultAppBloodhound();
        MultiMap overrides = new MultiValueMap();
        overrides.put("properties", new TestApplicationDescriptorFactoryNoAnnotation());
        overrides.put("properties", new TestApplicationDescriptorFactoryDefault());

        // test with weigh attribute (we have 3 candidates now)
        bh.mergeParserOverrides(overrides);
        assertEquals(1, bh.parserRegistry.size());
        ApplicationDescriptorFactory result = bh.parserRegistry.get("properties");
        assertNotNull(result);
        assertTrue("@Preferred implementation ignored", result instanceof TestApplicationDescriptorFactoryDefault);
    }


    /**
     * Test parser with annotation default
     */
    @Preferred()
    class TestApplicationDescriptorFactoryDefault implements ApplicationDescriptorFactory
    {

        @Override
        public ApplicationDescriptor parse(File descriptor, String applicationName) throws IOException
        {
            return null;
        }

        public String getSupportedFormat()
        {
            return "properties";
        }
    }

    /**
     * Test parser with weigh annotation
     */
    @Preferred(weight = 10)
    class TestApplicationDescriptorFactoryPreferred implements ApplicationDescriptorFactory
    {

        @Override
        public ApplicationDescriptor parse(File descriptor, String applicationName) throws IOException
        {
            return null;
        }

        public String getSupportedFormat()
        {
            return "properties";
        }

    }


    class TestApplicationDescriptorFactoryNoAnnotation implements ApplicationDescriptorFactory
    {

        @Override
        public ApplicationDescriptor parse(File descriptor, String applicationName) throws IOException
        {
            return null;
        }

        public String getSupportedFormat()
        {
            return "properties";
        }

    }

}
