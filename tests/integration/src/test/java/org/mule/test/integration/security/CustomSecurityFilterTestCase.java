/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.security;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.mule.api.MessagingException;
import org.mule.api.MuleMessage;
import org.mule.config.ExceptionHelper;
import org.mule.functional.junit4.FunctionalTestCase;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * See MULE-4916: spring beans inside a security filter
 */
public class CustomSecurityFilterTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/security/custom-security-filter-test.xml";
    }

    @Test
    public void testOutboundAutenticationSend() throws Exception
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("username", "ross");
        props.put("pass", "ross");

        MuleMessage result = runFlow("test", "hi", props).getMessage();

        assertNull(result.getExceptionPayload());

        props.put("pass", "badpass");

        try
        {
            runFlow("test", "hi", props);
            fail("Expected exception.");
        }
        catch(MessagingException e)
        {
            assertThat(ExceptionHelper.getRootException(e), instanceOf(BadCredentialsException.class));
        }
    }
}
