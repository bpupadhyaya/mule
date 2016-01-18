/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.construct;

import static org.junit.Assert.assertEquals;
import org.mule.api.MuleMessage;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.lifecycle.LifecycleTrackerProcessor;

import org.junit.Test;

public class SubFlowTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/construct/sub-flow.xml";
    }

    @Test
    public void testProcessorChainViaProcessorRef() throws Exception
    {
        MuleMessage result = runFlow("ProcessorChainViaProcessorRef", "").getMessage();
        assertEquals("1xyz2", getPayloadAsString(result));

        assertEquals("[setMuleContext, setService, setMuleContext, initialise, start]",
            result.getOutboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("ProcessorChainViaProcessorRef"),
            result.getOutboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testProcessorChainViaFlowRef() throws Exception
    {
        MuleMessage result = runFlow("ProcessorChainViaFlowRef", "").getMessage();

        assertEquals("1xyz2", getPayloadAsString(result));

        assertEquals("[setMuleContext, setService, setMuleContext, initialise, start]",
            result.getOutboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("ProcessorChainViaFlowRef"),
            result.getOutboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }
    
    @Test
    public void testSubFlowViaProcessorRef() throws Exception
    {
        MuleMessage result = runFlow("SubFlowViaProcessorRef", "").getMessage();
        assertEquals("1xyz2", getPayloadAsString(result));

        assertEquals("[setMuleContext, setService, setMuleContext, initialise, start]",
            result.getOutboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("SubFlowViaProcessorRef"),
            result.getOutboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testSubFlowViaFlowRef() throws Exception
    {
        MuleMessage result = runFlow("SubFlowViaFlowRef", "").getMessage();

        assertEquals("1xyz2", getPayloadAsString(result));

        assertEquals("[setMuleContext, setService, setMuleContext, initialise, start]",
            result.getOutboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("SubFlowViaFlowRef"),
            result.getOutboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testFlowviaFlowRef() throws Exception
    {
        assertEquals("1xyz2", getPayloadAsString(runFlow("FlowViaFlowRef", "").getMessage()));
    }

    @Test
    public void testServiceviaFlowRef() throws Exception
    {
        assertEquals("1xyz2", getPayloadAsString(runFlow("ServiceViaFlowRef", "").getMessage()));
    }

    @Test
    public void testFlowWithSubFlowWithComponent() throws Exception
    {
        assertEquals("0", getPayloadAsString(runFlow("flowWithsubFlowWithComponent", "0").getMessage()));

    }

    @Test
    public void testFlowWithSameSubFlowTwice() throws Exception
    {
        assertEquals("0xyzxyz", getPayloadAsString(runFlow("flowWithSameSubFlowTwice", "0").getMessage()));
    }

    @Test
    public void testFlowWithSameSubFlowSingletonTwice() throws Exception
    {
        assertEquals("0xyzxyz", getPayloadAsString(runFlow("flowWithSameSubFlowSingletonTwice", "0").getMessage()));
    }

    @Test
    public void testFlowWithSameGlobalChainTwice() throws Exception
    {
        assertEquals("0xyzxyz", getPayloadAsString(runFlow("flowWithSameGlobalChainTwice", "0").getMessage()));
    }

    @Test
    public void testFlowWithSameGlobalChainSingletonTwice() throws Exception
    {
        assertEquals("0xyzxyz", getPayloadAsString(runFlow("flowWithSameGlobalChainSingletonTwice", "0").getMessage()));
    }

}
