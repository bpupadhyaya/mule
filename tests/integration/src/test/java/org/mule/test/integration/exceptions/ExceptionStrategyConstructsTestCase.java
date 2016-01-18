/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.processor.MessageProcessor;
import org.mule.message.ExceptionMessage;
import org.mule.functional.exceptions.FunctionalTestException;
import org.mule.functional.junit4.FunctionalTestCase;

import org.junit.Test;

public class ExceptionStrategyConstructsTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/exceptions/exception-strategy-constructs-config-flow.xml";
    }

    @Test
    public void testDefaultExceptionStrategySingleEndpoint() throws Exception
    {
        MuleClient client = muleContext.getClient();

        runFlowAsync("testService", getTestMuleMessage(TEST_PAYLOAD));
        assertExceptionMessage(client.request("test://modelout", RECEIVE_TIMEOUT));

        runFlowAsync("testService1", getTestMuleMessage(TEST_PAYLOAD));
        assertExceptionMessage(client.request("test://service1out", RECEIVE_TIMEOUT));

        runFlowAsync("testflow1", getTestMuleMessage(TEST_PAYLOAD));
        assertExceptionMessage(client.request("test://flow1out", RECEIVE_TIMEOUT));
    }

    private void assertExceptionMessage(MuleMessage out)
    {
        assertNotNull(out);
        assertTrue(out.getPayload() instanceof ExceptionMessage);
        ExceptionMessage exceptionMessage = (ExceptionMessage) out.getPayload();
        assertTrue(exceptionMessage.getException().getClass() == FunctionalTestException.class
                   || exceptionMessage.getException().getCause().getClass() == FunctionalTestException.class);
        assertEquals("test", exceptionMessage.getPayload());
    }

    public static class ExceptionThrowingProcessor implements MessageProcessor
    {

        @Override
        public MuleEvent process(MuleEvent event) throws MuleException
        {
            throw new MessagingException(event, new FunctionalTestException());
        }
    }
}
