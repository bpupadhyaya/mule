/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mule.api.config.MuleProperties.MULE_FLOW_TRACE;
import static org.mule.tck.util.FlowTraceUtils.assertStackElements;
import static org.mule.tck.util.FlowTraceUtils.isFlowStackElement;
import org.mule.api.context.notification.MessageProcessorNotificationListener;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.tck.util.FlowTraceUtils.FlowStackAsserter;
import org.mule.tck.util.FlowTraceUtils.FlowStackAsyncAsserter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class FlowStackTestCase extends FunctionalTestCase
{
    @Rule
    public SystemProperty flowTraceEnabled = new SystemProperty(MULE_FLOW_TRACE, "true");

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/notifications/flow-stack-config.xml";
    }

    @Before
    public void before()
    {
        muleContext.getNotificationManager().addInterfaceToType(
                MessageProcessorNotificationListener.class,
                MessageProcessorNotification.class);

        FlowStackAsserter.stackToAssert = null;
        FlowStackAsyncAsserter.latch = new CountDownLatch(1);
    }

    @Test
    public void flowStatic() throws Exception
    {
        runFlow("flowStatic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowStatic", "/flowStatic/processors/0"));
    }

    @Test
    public void subFlowStatic() throws Exception
    {
        runFlow("subFlowStatic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowStatic/processors/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowStatic", "/subFlowStatic/processors/0"));
    }

    @Test
    public void flowDynamic() throws Exception
    {
        runFlow("flowDynamic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowDynamic", "/flowDynamic/processors/0"));
    }

    @Test
    public void subFlowDynamic() throws Exception
    {
        runFlow("subFlowDynamic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowDynamic/processors/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowDynamic", "/subFlowDynamic/processors/0"));
    }

    @Test
    public void secondFlowStatic() throws Exception
    {
        runFlow("secondFlowStatic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("secondFlowStatic", "/secondFlowStatic/processors/1"));
    }

    @Test
    public void secondSubFlowStatic() throws Exception
    {
        runFlow("secondSubFlowStatic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/secondSubFlowStatic/processors/1/subFlow/subprocessors/0"),
                isFlowStackElement("secondSubFlowStatic", "/secondSubFlowStatic/processors/1"));
    }

    @Test
    public void secondFlowDynamic() throws Exception
    {
        runFlow("secondFlowDynamic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("secondFlowDynamic", "/secondFlowDynamic/processors/1"));
    }

    @Test
    public void secondSubFlowDynamic() throws Exception
    {
        runFlow("secondSubFlowDynamic", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/secondSubFlowDynamic/processors/1/subFlow/subprocessors/0"),
                isFlowStackElement("secondSubFlowDynamic", "/secondSubFlowDynamic/processors/1"));
    }

    @Test
    public void flowStaticWithAsync() throws Exception
    {
        runFlow("flowStaticWithAsync", "payload");

        FlowStackAsyncAsserter.latch.await(1, TimeUnit.SECONDS);

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flowInAsync", "/flowInAsync/processors/0"),
                isFlowStackElement("flowStaticWithAsync", "/flowStaticWithAsync/processors/0/0"));
    }

    @Test
    public void subFlowStaticWithAsync() throws Exception
    {
        runFlow("subFlowStaticWithAsync", "payload");

        FlowStackAsyncAsserter.latch.await(1, TimeUnit.SECONDS);

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlowInAsync", "/subFlowStaticWithAsync/processors/0/0/subFlowInAsync/subprocessors/0"),
                isFlowStackElement("subFlowStaticWithAsync", "/subFlowStaticWithAsync/processors/0/0"));
    }

    @Test
    public void flowDynamicWithAsync() throws Exception
    {
        runFlow("flowDynamicWithAsync", "payload");

        FlowStackAsyncAsserter.latch.await(1, TimeUnit.SECONDS);

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flowInAsync", "/flowInAsync/processors/0"),
                isFlowStackElement("flowDynamicWithAsync", "/flowDynamicWithAsync/processors/0/0"));
    }

    @Test
    public void subFlowDynamicWithAsync() throws Exception
    {
        runFlow("subFlowDynamicWithAsync", "payload");

        FlowStackAsyncAsserter.latch.await(1, TimeUnit.SECONDS);

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlowInAsync", "/subFlowDynamicWithAsync/processors/0/0/subFlowInAsync/subprocessors/0"),
                isFlowStackElement("subFlowDynamicWithAsync", "/subFlowDynamicWithAsync/processors/0/0"));
    }

    @Test
    public void flowStaticWithEnricher() throws Exception
    {
        runFlow("flowStaticWithEnricher", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowStaticWithEnricher", "/flowStaticWithEnricher/processors/0/0"));
    }

    @Test
    public void subFlowStaticWithEnricher() throws Exception
    {
        runFlow("subFlowStaticWithEnricher", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowStaticWithEnricher/processors/0/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowStaticWithEnricher", "/subFlowStaticWithEnricher/processors/0"));
    }

    @Test
    public void flowDynamicWithEnricher() throws Exception
    {
        runFlow("flowDynamicWithEnricher", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowDynamicWithEnricher", "/flowDynamicWithEnricher/processors/0/0"));
    }

    @Test
    public void subFlowDynamicWithEnricher() throws Exception
    {
        runFlow("subFlowDynamicWithEnricher", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowDynamicWithEnricher/processors/0/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowDynamicWithEnricher", "/subFlowDynamicWithEnricher/processors/0/0"));
    }

    @Test
    public void flowStaticWithChoice() throws Exception
    {
        runFlow("flowStaticWithChoice", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowStaticWithChoice", "/flowStaticWithChoice/processors/0/0/0"));
    }

    @Test
    public void subFlowStaticWithChoice() throws Exception
    {
        runFlow("subFlowStaticWithChoice", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowStaticWithChoice/processors/0/0/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowStaticWithChoice", "/subFlowStaticWithChoice/processors/0/0/0"));
    }

    @Test
    public void flowDynamicWithChoice() throws Exception
    {
        runFlow("flowDynamicWithChoice", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowDynamicWithChoice", "/flowDynamicWithChoice/processors/0/0/0"));
    }

    @Test
    public void subFlowDynamicWithChoice() throws Exception
    {
        runFlow("subFlowDynamicWithChoice", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowDynamicWithChoice/processors/0/0/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowDynamicWithChoice", "/subFlowDynamicWithChoice/processors/0/0/0"));
    }

    @Test
    public void flowStaticWithScatterGather() throws Exception
    {
        runFlow("flowStaticWithScatterGather", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowStaticWithScatterGather", "/flowStaticWithScatterGather/processors/0/1/0"));
    }

    @Test
    public void subFlowStaticWithScatterGather() throws Exception
    {
        runFlow("subFlowStaticWithScatterGather", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowStaticWithScatterGather/processors/0/1/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowStaticWithScatterGather", "/subFlowStaticWithScatterGather/processors/0/1"));
    }

    @Test
    public void flowDynamicWithScatterGather() throws Exception
    {
        runFlow("flowDynamicWithScatterGather", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowDynamicWithScatterGather", "/flowDynamicWithScatterGather/processors/0/1/0"));
    }

    @Test
    public void subFlowDynamicWithScatterGather() throws Exception
    {
        runFlow("subFlowDynamicWithScatterGather", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowDynamicWithScatterGather/processors/0/1/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowDynamicWithScatterGather", "/subFlowDynamicWithScatterGather/processors/0/1/0"));
    }

    @Test
    public void flowStaticWithScatterGatherChain() throws Exception
    {
        runFlow("flowStaticWithScatterGatherChain", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowStaticWithScatterGatherChain", "/flowStaticWithScatterGatherChain/processors/0/1/0"));
    }

    @Test
    public void subFlowStaticWithScatterGatherChain() throws Exception
    {
        runFlow("subFlowStaticWithScatterGatherChain", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowStaticWithScatterGatherChain/processors/0/1/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowStaticWithScatterGatherChain", "/subFlowStaticWithScatterGatherChain/processors/0/1/0"));
    }

    @Test
    public void flowDynamicWithScatterGatherChain() throws Exception
    {
        runFlow("flowDynamicWithScatterGatherChain", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));

        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("flow", "/flow/processors/0"),
                isFlowStackElement("flowDynamicWithScatterGatherChain", "/flowDynamicWithScatterGatherChain/processors/0/1/0"));
    }

    @Test
    public void subFlowDynamicWithScatterGatherChain() throws Exception
    {
        runFlow("subFlowDynamicWithScatterGatherChain", "payload");

        assertThat(FlowStackAsserter.stackToAssert, not(nullValue()));
        
        assertStackElements(FlowStackAsserter.stackToAssert,
                isFlowStackElement("subFlow", "/subFlowDynamicWithScatterGatherChain/processors/0/1/0/subFlow/subprocessors/0"),
                isFlowStackElement("subFlowDynamicWithScatterGatherChain", "/subFlowDynamicWithScatterGatherChain/processors/0/1/0"));
    }
}
