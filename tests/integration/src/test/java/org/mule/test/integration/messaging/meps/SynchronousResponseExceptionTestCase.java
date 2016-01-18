/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.messaging.meps;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.mule.api.MessagingException;
import org.mule.api.transformer.TransformerException;
import org.mule.config.ExceptionHelper;
import org.mule.functional.exceptions.FunctionalTestException;
import org.mule.functional.junit4.FunctionalTestCase;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * see MULE-4512
 */
public class SynchronousResponseExceptionTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/messaging/meps/synchronous-response-exception-flow.xml";
    }

    @Test
    public void testComponentException() throws Exception
    {
        try
        {
            runFlow("ComponentException", "request");
            fail("Expected exception");
        }
        catch (MessagingException e)
        {
            assertThat(ExceptionHelper.getRootException(e), instanceOf(FunctionalTestException.class));
        }
    }

    @Test
    public void testFlowRefInvalidException() throws Exception
    {
        try
        {
            runFlow("FlowRefInvalidException", "request");
            fail("Expected exception");
        }
        catch (MessagingException e)
        {
            assertThat(ExceptionHelper.getRootException(e), instanceOf(NoSuchBeanDefinitionException.class));
        }
    }

    @Test
    public void testTransformerException() throws Exception
    {
        try
        {
            runFlow("TransformerException", "request");
            fail("Expected exception");
        }
        catch (MessagingException e)
        {
            assertThat(ExceptionHelper.getRootException(e), instanceOf(TransformerException.class));
        }
    }
}
