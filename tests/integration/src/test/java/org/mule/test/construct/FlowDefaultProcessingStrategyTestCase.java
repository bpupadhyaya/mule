/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.construct;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import org.mule.DefaultMuleEvent;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.PropertyScope;
import org.mule.construct.Flow;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.tck.MuleTestUtils;

import org.junit.Test;

public class FlowDefaultProcessingStrategyTestCase extends FunctionalTestCase
{

    protected static final String PROCESSOR_THREAD = "processor-thread";
    protected static final String FLOW_NAME = "Flow";

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/construct/flow-default-processing-strategy-config.xml";
    }

    @Test
    public void requestResponse() throws Exception
    {
        MuleMessage response = runFlow(FLOW_NAME, TEST_PAYLOAD).getMessage();
        assertThat(response.getPayload().toString(), is(TEST_PAYLOAD));
        MuleMessage message = muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
        assertThat(message.getProperty(PROCESSOR_THREAD, PropertyScope.OUTBOUND), is(Thread.currentThread().getName()));
    }

    @Test
    public void oneWay() throws Exception
    {
        runFlow(FLOW_NAME, getTestEvent(TEST_PAYLOAD, MessageExchangePattern.ONE_WAY)).getMessage();
        MuleMessage message = muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
        assertThat(message.getProperty(PROCESSOR_THREAD, PropertyScope.OUTBOUND), is(not(Thread.currentThread().getName())));
    }

    @Test
    public void requestResponseTransacted() throws Exception
    {
        testTransacted(MessageExchangePattern.REQUEST_RESPONSE);
    }

    @Test
    public void oneWayTransacted() throws Exception
    {
        testTransacted(MessageExchangePattern.ONE_WAY);
    }

    protected void testTransacted(MessageExchangePattern mep) throws Exception
    {
        FlowConstruct flow = getTestFlow(muleContext);
        InboundEndpoint endpoint = MuleTestUtils.getTestInboundEndpoint("test1", mep, muleContext, null);
        runFlow("Flow", getTransactedEvent(flow, endpoint));
        MuleMessage message = muleContext.getClient().request("test://out", RECEIVE_TIMEOUT);
        assertThat(message.getProperty(PROCESSOR_THREAD, PropertyScope.OUTBOUND), is(Thread.currentThread().getName()));
    }

    private DefaultMuleEvent getTransactedEvent(FlowConstruct flow, InboundEndpoint endpoint)
    {
        return new DefaultMuleEvent(getTestMuleMessage(TEST_PAYLOAD),
                                    endpoint.getEndpointURI().getUri(),
                                    endpoint.getName(),
                                    endpoint.getExchangePattern(),
                                    flow,
                                    getTestSession((Flow) flow, muleContext),
                                    endpoint.getResponseTimeout(),
                                    null,
                                    null,
                                    endpoint.getEncoding(),
                                    true,
                                    true,
                                    null,
                                    null);
    }

    public static class ThreadSensingMessageProcessor implements MessageProcessor
    {
        @Override
        public MuleEvent process(MuleEvent event) throws MuleException
        {
            event.getMessage().setOutboundProperty(PROCESSOR_THREAD, Thread.currentThread().getName());
            return event;
        }
    }

}
