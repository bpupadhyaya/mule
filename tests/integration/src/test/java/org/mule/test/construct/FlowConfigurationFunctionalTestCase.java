/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.construct;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.mule.DefaultMuleEvent;
import org.mule.MessageExchangePattern;
import org.mule.api.MessagingException;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.source.CompositeMessageSource;
import org.mule.api.source.MessageSource;
import org.mule.api.transformer.Transformer;
import org.mule.api.transport.PropertyScope;
import org.mule.construct.Flow;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.source.StartableCompositeMessageSource;
import org.mule.tck.MuleTestUtils;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.Banana;
import org.mule.tck.testmodels.fruit.Fruit;
import org.mule.tck.testmodels.fruit.FruitBowl;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.transformer.simple.StringAppendTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class FlowConfigurationFunctionalTestCase extends FunctionalTestCase
{

    public FlowConfigurationFunctionalTestCase()
    {
        setDisposeContextPerClass(true);
    }

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/construct/flow.xml";
    }

    @Test
    public void testFlow() throws Exception
    {
        final Flow flow = muleContext.getRegistry().lookupObject("flow");
        assertEquals(5, flow.getMessageProcessors().size());
        assertNotNull(flow.getExceptionListener());

        assertEquals("012xyzabc3",
                     getPayloadAsString(runFlow("flow", getTestMuleMessage("0")).getMessage()));

    }

    @Test
    public void testFlowSynchronous() throws Exception
    {
        runFlow("synchronousFlow", getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient().request("test://synchronous-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        Thread thread = (Thread) message.getPayload();
        assertNotNull(thread);
        assertEquals(Thread.currentThread(), thread);
    }

    @Test
    public void testFlowAynchronous() throws Exception
    {
        runFlowAsync("asynchronousFlow", getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient().request("test://asynchronous-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        Thread thread = (Thread) message.getPayload();
        assertNotNull(thread);
        assertNotSame(Thread.currentThread(), thread);
    }

    @Test
    public void testFlowQueuedAsynchronous() throws Exception
    {
        runFlowAsync("queuedAsynchronousFlow", getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient()
                .request("test://queued-asynchronous-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        Thread thread = (Thread) message.getPayload();
        assertNotNull(thread);
        assertNotSame(Thread.currentThread(), thread);
    }

    @Test
    public void testAsyncAsynchronous() throws Exception
    {
        runFlowAsync("asynchronousAsync", getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient().request("test://asynchronous-async-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        Thread thread = (Thread) message.getPayload();
        assertNotNull(thread);
        assertNotSame(Thread.currentThread(), thread);
    }

    @Test
    public void testAsyncQueuedAsynchronous() throws Exception
    {
        runFlowAsync("queuedAsynchronousAsync", getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient().request("test://queued-asynchronous-async-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        Thread thread = (Thread) message.getPayload();
        assertNotNull(thread);
        assertNotSame(Thread.currentThread(), thread);
    }

    @Test
    public void testFlowCompositeSource() throws Exception
    {
        final Flow flow = muleContext.getRegistry().lookupObject("flow2");
        CompositeMessageSource compositeSource = (CompositeMessageSource) flow.getMessageSource();
        assertEquals(StartableCompositeMessageSource.class, compositeSource.getClass());
        assertEquals(2, flow.getMessageProcessors().size());

        final List<MessageSource> sources = compositeSource.getSources();
        TestSimpleMessageSource source1 = (TestSimpleMessageSource) sources.get(0);
        TestSimpleMessageSource source2 = (TestSimpleMessageSource) sources.get(1);

        assertEquals("01xyz", getPayloadAsString(source1.fireEvent(getTestEvent("0")).getMessage()));
        assertEquals("01xyz", getPayloadAsString(source2.fireEvent(getTestEvent("0")).getMessage()));
    }

    @Test
    public void testInOutFlow() throws Exception
    {
        runFlow("inout", getTestMuleMessage("0"));
        assertEquals("0", getPayloadAsString(muleContext.getClient().request("test://inout-out", RECEIVE_TIMEOUT)));
    }

    @Test
    public void testInOutAppendFlow() throws Exception
    {
        runFlow("inout-append", getTestMuleMessage("0"));
        LocalMuleClient client = muleContext.getClient();
        assertEquals("0inout", getPayloadAsString(client.request("test://inout-append-out", RECEIVE_TIMEOUT)));
    }

    @Test
    public void testSplitAggregateFlow() throws Exception
    {
        final Apple apple = new Apple();
        final Banana banana = new Banana();
        final Orange orange = new Orange();
        final FruitBowl fruitBowl = new FruitBowl(apple, banana);
        fruitBowl.addFruit(orange);

        runFlow("split-aggregate", getTestMuleMessage(fruitBowl));

        final MuleMessage result = muleContext.getClient().request("test://split-aggregate-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(3, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(apple.isBitten());
        assertTrue(banana.isBitten());
        assertTrue(orange.isBitten());

        assertTrue(results.contains(apple));
        assertTrue(results.contains(banana));
        assertTrue(results.contains(orange));
    }

    @Test
    public void testSplitNoParts() throws Exception
    {
        String MESSAGE = "<Order></Order>";
        MuleMessage result = runFlow("split-no-parts", MESSAGE).getMessage();

        assertNotNull(result);
        assertEquals(result.getPayload(), MESSAGE);
    }

    @Test
    public void testSplitAggregateListFlow() throws Exception
    {
        final Apple apple = new Apple();
        final Banana banana = new Banana();
        final Orange orange = new Orange();
        final FruitBowl fruitBowl = new FruitBowl(apple, banana);
        fruitBowl.addFruit(orange);

        runFlow("split-aggregate-list", getTestMuleMessage(fruitBowl.getFruit()));

        final MuleMessage result = muleContext.getClient().request("test://split-aggregate-list-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(3, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(apple.isBitten());
        assertTrue(banana.isBitten());
        assertTrue(orange.isBitten());

        assertTrue(results.contains(apple));
        assertTrue(results.contains(banana));
        assertTrue(results.contains(orange));
    }

    @Test
    public void testSplitAggregateListFlowSingleItem() throws Exception
    {
        final Apple apple = new Apple();
        final FruitBowl fruitBowl = new FruitBowl();
        fruitBowl.addFruit(apple);

        runFlow("split-aggregate-singleton-list", getTestMuleMessage(fruitBowl.getFruit()));

        final LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://split-aggregate-singleton-list-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(1, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(apple.isBitten());

        assertTrue(results.contains(apple));
    }

    @Test
    public void testSplitAggregateResponseListFlow() throws Exception
    {
        final Apple apple = new Apple();
        final Banana banana = new Banana();
        final Orange orange = new Orange();
        final FruitBowl fruitBowl = new FruitBowl(apple, banana);
        fruitBowl.addFruit(orange);

        final MuleMessage result = runFlow("split-aggregate-response-list", getTestMuleMessage(fruitBowl.getFruit())).getMessage();

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(3, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(apple.isBitten());
        assertTrue(banana.isBitten());
        assertTrue(orange.isBitten());

        assertTrue(results.contains(apple));
        assertTrue(results.contains(banana));
        assertTrue(results.contains(orange));
    }

    @Test
    public void testSplitAggregateResponseListFlowSingleItem() throws Exception
    {
        final Apple apple = new Apple();
        final FruitBowl fruitBowl = new FruitBowl();
        fruitBowl.addFruit(apple);

        final MuleMessage result = runFlow("split-aggregate-response-singleton-list", getTestMuleMessage(fruitBowl.getFruit())).getMessage();

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(1, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(apple.isBitten());
        assertTrue(results.contains(apple));
    }

    @Test
    public void testSplitAggregateMapFlow() throws Exception
    {
        Map<String, Fruit> map = new HashMap<String, Fruit>();
        final Apple apple = new Apple();
        final Banana banana = new Banana();
        final Orange orange = new Orange();
        map.put("apple", apple);
        map.put("banana", banana);
        map.put("orange", orange);

        MuleEvent result = runFlow("split-aggregate-map", getTestEvent(map));

        assertNotNull(result);
        assertTrue(result.getMessage().getPayload() instanceof List);
        final MuleMessage[] results = new MuleMessage[3];
        ((List<MuleMessage>) result.getMessage().getPayload()).toArray(results);
        assertEquals(3, results.length);

        assertTrue(apple.isBitten());
        assertTrue(banana.isBitten());
        assertTrue(orange.isBitten());

        assertNotNull(results[0].getProperty("key", PropertyScope.INVOCATION));
        assertNotNull(results[1].getProperty("key", PropertyScope.INVOCATION));
        assertNotNull(results[2].getProperty("key", PropertyScope.INVOCATION));
    }

    @Test
    public void testSplitFilterAggregateFlow() throws Exception
    {
        final Apple apple = new Apple();
        final Banana banana = new Banana();
        final Orange orange = new Orange();
        final FruitBowl fruitBowl = new FruitBowl(apple, banana);
        fruitBowl.addFruit(orange);

        runFlow("split-filter-aggregate", getTestMuleMessage(fruitBowl));

        final LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://split-filter-aggregate-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof List);
        final List<MuleMessage> coll = (List<MuleMessage>) result.getPayload();
        assertEquals(1, coll.size());
        final List<Fruit> results = coll.stream().map(msg -> (Fruit) msg.getPayload()).collect(toList());

        assertTrue(results.contains(apple));
        assertFalse(results.contains(banana));
        assertFalse(results.contains(orange));
    }

    @Test
    public void testMessageChunkSplitAggregateFlow() throws Exception
    {
        String payload = "";
        for (int i = 0; i < 100; i++)
        {
            payload += TEST_MESSAGE;
        }

        runFlow("message-chunk-split-aggregate", getTestMuleMessage(payload));

        LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://message-chunk-split-aggregate-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertNotSame(payload, result.getPayload());
        assertEquals(payload, getPayloadAsString(result));
    }

    @Test
    public void testComponentsFlow() throws Exception
    {
        final MuleMessage result = runFlow("components", getTestMuleMessage("0")).getMessage();

        assertNotNull(result);
        assertNotSame(TEST_MESSAGE + "test", result.getPayload());
    }

    @Test
    public void testWireTapFlow() throws Exception
    {
        runFlow("wiretap", getTestMuleMessage(TEST_MESSAGE));

        final LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://wiretap-out", RECEIVE_TIMEOUT);
        final MuleMessage tapResult = client.request("test://wiretap-tap", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertNotNull(tapResult);
        assertNotSame(result, tapResult);
        assertEquals(TEST_MESSAGE + "inout", getPayloadAsString(result));
        assertEquals(TEST_MESSAGE + "intap", getPayloadAsString(tapResult));
    }

    @Test
    public void testResponseElement() throws Exception
    {
        final MuleMessage result = runFlow("response", getTestMuleMessage("")).getMessage();

        assertNotNull(result);
        assertEquals("abcdefghi", getPayloadAsString(result));
    }

    @Test
    public void testAsyncOneWayEndpoint() throws Exception
    {
        runFlow("async-oneway", getTestMuleMessage("0"));
        LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://async-oneway-out", RECEIVE_TIMEOUT);
        final MuleMessage asyncResult = client.request("test://async-async-oneway-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertNotNull(asyncResult);
        assertEquals("0ac", getPayloadAsString(result));
        assertEquals("0ab", getPayloadAsString(asyncResult));
    }

    @Test
    public void testAsyncSedaOneWayEndpoint() throws Exception
    {
        runFlow("async-seda-oneway", getTestMuleMessage("0"));
        LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://async-seda-oneway-out", RECEIVE_TIMEOUT);
        final MuleMessage asyncResult = client.request("test://async-async-seda-oneway-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertNotNull(asyncResult);
        assertEquals("0ac", getPayloadAsString(result));
        assertEquals("0ab", getPayloadAsString(asyncResult));
    }

    @Test
    public void testAsyncRequestResponseEndpoint() throws Exception
    {
        runFlow("async-requestresponse", getTestMuleMessage("0"));
        LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://async-requestresponse-out", RECEIVE_TIMEOUT);
        final MuleMessage asyncResult = client.request("test://async-async-requestresponse-out", RECEIVE_TIMEOUT);

        assertNotNull(result);
        assertNotNull(asyncResult);
        assertEquals("0ac", getPayloadAsString(result));
        assertEquals("0ab", getPayloadAsString(asyncResult));
    }

    @Test
    public void testAsyncTransactionalEndpoint() throws Exception
    {
        try
        {
            runFlow("async-tx", getTransactedEvent()).getMessage();
        }
        catch (Exception e)
        {
            assertThat(e, instanceOf(MessagingException.class));
            assertThat(e.getMessage(), containsString("The <async> element cannot be used with transactions"));
        }

        final LocalMuleClient client = muleContext.getClient();
        final MuleMessage result = client.request("test://async-requestresponse-out", RECEIVE_TIMEOUT);
        final MuleMessage asyncResult = client.request("test://async-async-oneway-out", RECEIVE_TIMEOUT);

        assertNull(result);
        assertNull(asyncResult);
    }

    private DefaultMuleEvent getTransactedEvent() throws Exception
    {
        FlowConstruct flow = getTestFlow(muleContext);
        InboundEndpoint endpoint = MuleTestUtils.getTestInboundEndpoint("test1", MessageExchangePattern.ONE_WAY, muleContext, null);
        return new DefaultMuleEvent(getTestMuleMessage("0"),
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

    @Test
    public void testMulticaster() throws Exception
    {
        runFlow("multicaster", getTestMuleMessage(TEST_MESSAGE));

        final LocalMuleClient client = muleContext.getClient();
        final MuleMessage result1 = client.request("test://multicaster-out1", RECEIVE_TIMEOUT);
        final MuleMessage result2 = client.request("test://multicaster-out2", RECEIVE_TIMEOUT);
        final MuleMessage result3 = client.request("test://multicaster-out3", RECEIVE_TIMEOUT);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertNotSame(result1, result2);
        assertNotSame(result1, result3);
        assertNotSame(result2, result3);

        assertEquals(TEST_MESSAGE, result1.getPayload());
        assertEquals(TEST_MESSAGE, result1.getPayload());
        assertEquals(TEST_MESSAGE, result1.getPayload());

    }

    @Test
    public void testChoiceWithoutOutboundEndpoints() throws Exception
    {
        assertEquals("foo Hello foo", getPayloadAsString(runFlow("choice2", getTestMuleMessage("foo")).getMessage()));
        assertEquals("bar Hello bar", getPayloadAsString(runFlow("choice2", getTestMuleMessage("bar")).getMessage()));
        assertEquals("egh Hello ?", getPayloadAsString(runFlow("choice2", getTestMuleMessage("egh")).getMessage()));
    }

    @Test
    public void testFlowRef() throws Exception
    {
        final MuleMessage message = runFlow("flow-ref", getTestMuleMessage("0")).getMessage();
        assertEquals("012xyzabc312xyzabc3", getPayloadAsString(message));
    }

    @Test
    public void testInvoke() throws Exception
    {
        final MuleMessage message = runFlow("invoke", getTestMuleMessage("0")).getMessage();
        assertEquals("0recieved",
                     getPayloadAsString(message));
    }

    @Test
    public void testInvoke2() throws Exception
    {
        MuleMessage message = getTestMuleMessage("0");
        message.setProperty("one", "header1val", PropertyScope.INBOUND);
        final MuleMessage response = runFlow("invoke2", message).getMessage();
        assertEquals("header1valrecieved", getPayloadAsString(response));
    }

    @Test
    public void testInvoke3() throws Exception
    {
        // ensure multiple arguments work
        runFlow("invoke3", getTestMuleMessage("0"));
    }

    @Test
    public void testInvoke4() throws Exception
    {
        // ensure no arguments work
        runFlow("invoke4", getTestMuleMessage("0"));
    }

    @Test
    public void testEnrichWithAttributes() throws Exception
    {
        final MuleMessage muleMessage = runFlow("enrich", getTestMuleMessage("0")).getMessage();
        assertEquals("0Hello", muleMessage.getProperty("helloHeader", PropertyScope.OUTBOUND));
    }

    @Test
    public void testEnrichWithElements() throws Exception
    {
        MuleMessage result = runFlow("enrich2", getTestMuleMessage("0")).getMessage();

        assertEquals("0Hello", result.getProperty("helloHeader", PropertyScope.OUTBOUND));
        assertEquals("0Hello", result.getProperty("helloHeader2", PropertyScope.OUTBOUND));
    }

    @Test
    public void testEnrichUsingComponent() throws Exception
    {
        // MULE-5544
        MuleMessage result = runFlow("enrichcomponent", getTestMuleMessage("0")).getMessage();

        assertEquals("0", result.getProperty("echoHeader", PropertyScope.OUTBOUND));
    }

    @Test
    public void testEnrichUsingComponent2() throws Exception
    {
        // MULE-5544
        MuleMessage result = runFlow("enrichcomponent2", getTestMuleMessage("0")).getMessage();

        assertEquals("0", result.getProperty("echoHeader", PropertyScope.OUTBOUND));
    }

    @Test
    public void testLoggerMessage() throws Exception
    {
        runFlow("loggermessage", getTestMuleMessage("0"));
    }

    @Test
    public void testLoggerHeader() throws Exception
    {
        MuleMessage message = getTestMuleMessage("0");
        message.setProperty("toLog", "valueToLog", PropertyScope.INBOUND);
        runFlow("loggerheader", message);
    }

    public static class Pojo
    {

        public void method()
        {
            // does nothing
        }

        public void method(Object arg1, Object arg2)
        {
            // does nothing
        }
    }

    @Test
    public void testCustomMessageRouter() throws Exception
    {
        MuleMessage result = runFlow("customRouter", getTestMuleMessage("")).getMessage();
        assertEquals("abc", ((List<MuleMessage>) result.getPayload()).stream().map(msg -> (String) msg.getPayload())
                .collect(joining()));
    }

    @Test
    public void testPoll() throws Exception
    {
        MuleMessage message = muleContext.getClient().request("test://poll-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        assertEquals(" Hello fooout", getPayloadAsString(message));
    }

    @Test
    public void testPollFlowRef() throws Exception
    {
        MuleMessage message = muleContext.getClient().request("test://poll2-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
        assertEquals("pollappendout", getPayloadAsString(message));
    }

    @Test
    public void testSubFlowMessageFilter() throws Exception
    {
        runFlowAsync("messagefiltersubflow",
                     getTestMuleMessage("0"));
        MuleMessage message = muleContext.getClient().request("test://messagefiltersubflow-out", RECEIVE_TIMEOUT);
        assertNotNull(message);
    }

    @Test
    public void testCustomMessageSource() throws Exception
    {
        Flow flow = (Flow) muleContext.getRegistry().lookupFlowConstruct("customMessageSource");
        TestMessageSource source = (TestMessageSource) flow.getMessageSource();

        MuleEvent result = source.fireEvent(getTestEvent("a"));
        assertEquals("abcd", result.getMessageAsString());
    }

    @Test
    public void testCustomMessageSourceInComposite() throws Exception
    {
        Flow flow = (Flow) muleContext.getRegistry().lookupFlowConstruct("customMessageSourceInComposite");
        CompositeMessageSource compositeSource = (CompositeMessageSource) flow.getMessageSource();
        TestMessageSource source = (TestMessageSource) compositeSource.getSources().get(0);

        MuleEvent result = source.fireEvent(getTestEvent("a"));
        assertEquals("abcd", result.getMessageAsString());
    }


    public static class TestMessageSource implements MessageSource
    {

        private MessageProcessor listener;
        private String appendBefore;
        private String appendAfter;

        MuleEvent fireEvent(MuleEvent event) throws MuleException
        {
            Transformer before = new StringAppendTransformer(appendBefore);
            Transformer after = new StringAppendTransformer(appendAfter);
            before.setMuleContext(muleContext);
            after.setMuleContext(muleContext);
            return after.process(listener.process(before.process(event)));
        }

        public void setAppendBefore(String appendBefore)
        {
            this.appendBefore = appendBefore;
        }

        public void setAppendAfter(String appendAfter)
        {
            this.appendAfter = appendAfter;
        }

        @Override
        public void setListener(MessageProcessor listener)
        {
            this.listener = listener;
        }

    }

    public static class TestSimpleMessageSource implements MessageSource
    {

        private MessageProcessor listener;


        MuleEvent fireEvent(MuleEvent event) throws MuleException
        {
            return listener.process(event);
        }

        @Override
        public void setListener(MessageProcessor listener)
        {
            this.listener = listener;
        }

    }

    public static class ThreadSensingMessageProcessor implements MessageProcessor
    {

        @Override
        public MuleEvent process(MuleEvent event) throws MuleException
        {
            event.getMessage().setPayload(Thread.currentThread());
            return event;
        }
    }

}
