/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.components;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.mule.api.MuleMessage;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.message.DefaultExceptionPayload;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test an entry-point resolver used for multiple classes
 */
public class EntryPointResolverCacheTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/components/entry-point-resolver-cache-flow.xml";
    }

    @Test
    public void testCache() throws Exception
    {
        MuleMessage response = null;
        Map<String, Object> propertyMap = new HashMap<String, Object>();
        propertyMap.put("method", "retrieveReferenceData");

        response = runFlow("refServiceOne", "a request", propertyMap).getMessage();
        Object payload = response.getPayload();

        assertThat(payload, instanceOf(String.class));
        assertThat(payload, is("ServiceOne"));

        response = runFlow("refServiceTwo", "another request", propertyMap).getMessage();
        payload = response.getPayload();
        if ((payload == null) || (response.getExceptionPayload() != null))
        {
            DefaultExceptionPayload exPld = (DefaultExceptionPayload) response.getExceptionPayload();
            if (exPld.getException() != null)
            {
                fail(exPld.getException().getMessage());
            }
            else
            {
                fail(exPld.toString());
            }
        }
        assertThat(payload, instanceOf(String.class));
        assertThat(payload, is("ServiceTwo"));

    }

    public interface ReferenceDataService
    {
        String retrieveReferenceData(String refKey);
    }

    public static class RefDataServiceOne implements ReferenceDataService
    {
        @Override
        public String retrieveReferenceData(String refKey)
        {
            return "ServiceOne";
        }
    }

    public static class RefDataServiceTwo implements ReferenceDataService
    {
        @Override
        public String retrieveReferenceData(String refKey)
        {
            return "ServiceTwo";
        }
    }
}
