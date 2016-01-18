/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification.processors;

import static org.junit.Assert.assertNotNull;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.source.CompositeMessageSource;
import org.mule.api.source.MessageSource;
import org.mule.component.ComponentException;
import org.mule.construct.Flow;
import org.mule.context.notification.Node;
import org.mule.context.notification.RestrictedNode;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class MessageProcessorNotificationTestCase extends AbstractMessageProcessorNotificationTestCase
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/notifications/message-processor-notification-test-flow.xml";
    }

    @Override
    public void doTest() throws Exception
    {
        List<String> testList = Arrays.asList("test", "with", "collection");
        assertNotNull(runFlow("singleMP", TEST_PAYLOAD));
        assertNotNull(runFlow("processorChain", TEST_PAYLOAD));
        assertNotNull(runFlow("customProcessor", TEST_PAYLOAD));
        assertNotNull(runFlow("choice", TEST_PAYLOAD));
        assertNotNull(runFlow("scatterGather", TEST_PAYLOAD));

        assertNotNull(runFlow("foreach", TEST_PAYLOAD));
        assertNotNull(runFlow("enricher", TEST_PAYLOAD));
        //assertNotNull(runFlow("in-async", TEST_PAYLOAD));
        assertNotNull(runFlow("filters", TEST_PAYLOAD));
        assertNotNull(runFlow("idempotent-msg-filter", TEST_PAYLOAD));
        assertNotNull(runFlow("idempotent-secure-hash-msg-filter", TEST_PAYLOAD));
        assertNotNull(runFlow("subflow", TEST_PAYLOAD));
        assertNotNull(runFlow("catch-es", TEST_PAYLOAD));
        expectedException.expect(ComponentException.class);
        runFlow("rollback-es", TEST_PAYLOAD);
        assertNotNull(runFlow("choice-es", TEST_PAYLOAD));
        CompositeMessageSource composite = (CompositeMessageSource) ((Flow) muleContext.getRegistry().lookupFlowConstruct("composite-source")).getMessageSource();
        assertNotNull(((TestMessageSource) composite.getSources().get(0)).fireEvent(getTestEvent(TEST_PAYLOAD)));
        assertNotNull(((TestMessageSource) composite.getSources().get(1)).fireEvent(getTestEvent(TEST_PAYLOAD)));
        assertNotNull(runFlow("first-successful", TEST_PAYLOAD));
        assertNotNull(runFlow("round-robin", TEST_PAYLOAD));
        assertNotNull(runFlow("collectionAggregator", testList));
        assertNotNull(runFlow("customAggregator", testList));
        assertNotNull(runFlow("chunkAggregator", TEST_PAYLOAD));
        assertNotNull(runFlow("wire-tap", TEST_PAYLOAD));
    }

    @Override
    public RestrictedNode getSpecification()
    {
        return new Node()
                //singleMP
                .serial(prePost())

                //processorChain
                .serial(pre()) //Message Processor Chain
                .serial(prePost()) //logger-1
                .serial(prePost()) //logger-2
                .serial(post()) //Message Processor Chain

                //custom-processor
                .serial(prePost())
                .serial(prePost())

                //choice
                .serial(pre()) //choice
                .serial(prePost())    //otherwise-logger
                .serial(post())

                // scatter-gather
                .serial(pre()) // scatter-gather
                .serial(new Node()
                                .parallel(pre() // route 1 chain
                                                  .serial(prePost()) // route 1 first logger
                                                  .serial(prePost()) // route 1 second logger
                                                  .serial(post())) // route 1 chain
                                .parallel(prePost())) // route 0 logger
                .serial(post()) // scatter-gather

                //foreach
                .serial(pre()) //foreach
                .serial(prePost())    //logger-loop-1
                .serial(prePost())    //logger-loop-2
                .serial(post())
                .serial(prePost())    //MP after the Scope

                //enricher
                .serial(pre()) //append-string
                .serial(prePost())
                .serial(post())
                .serial(pre()) //chain
                .serial(prePost())
                .serial(prePost())
                .serial(post())

                ////async             //This is unstable
                //.serial(prePost())
                //.serial(prePost())
                //.serial(prePost())

                //filter
                .serial(pre())
                .serial(prePost())
                .serial(post())

                //idempotent-message-filter
                .serial(pre())          //open message filter
                .serial(prePost())      //message processor
                .serial(post())         //close mf

                //idempotent-secure-hash-message-filter
                .serial(pre())          //open message filter
                .serial(prePost())      //message processor
                .serial(post())         //close mf

                //subflow
                .serial(prePost())
                .serial(pre())
                .serial(pre())
                .serial(prePost())
                .serial(post())
                .serial(post())

                //catch-es
                .serial(prePost())
                .serial(prePost())

                //rollback-es
                .serial(prePost())
                .serial(prePost())

                //choice-es
                .serial(prePost())
                .serial(prePost())

                //composite-source
                .serial(prePost())
                .serial(prePost())

                //first-successful
                .serial(prePost())
                .serial(prePost())

                //round-robin
                .serial(prePost())
                .serial(prePost())

                //collection-aggregator
                .serial(pre())      //open Splitter, unpacks three messages
                .serial(prePost())  //1st message on Logger
                .serial(prePost())  //gets to Aggregator
                .serial(prePost())  //2nd message on Logger
                .serial(prePost())  //gets to Aggregator
                .serial(prePost())  //3rd message on Logger
                .serial(prePost())  //gets to Aggregator and packs the three messages, then close
                .serial(post())     //close Splitter

                //custom-aggregator
                .serial(pre())      //open Splitter, unpacks three messages
                .serial(prePost())  //1st message, open Aggregator
                .serial(prePost())  //2nd message
                .serial(pre())      //3rd message, packs the three messages
                .serial(prePost())  //Logger process packed message
                .serial(post())     //close Aggregator
                .serial(post())     //close Splitter

                //chunk-aggregator
                .serial(pre())      //start Splitter
                .serial(prePost())  //1st message on Logger
                .serial(prePost())  //gets to Aggregator
                .serial(prePost())  //2nd message on Logger
                .serial(prePost())  //gets to Aggregator
                .serial(prePost())  //3rd message on Logger
                .serial(prePost())  //gets to Aggregator
                .serial(prePost())  //4th message on Logger
                .serial(pre())      //gets to Aggregator and packs four messages
                .serial(prePost())  //packed message get to the second Logger
                .serial(post())     //close Aggregator
                .serial(post())     //close Splitter

                //wire-tap
                .serial(prePost())
                .serial(prePost())

                ;
    }

    @Override
    public void validateSpecification(RestrictedNode spec) throws Exception
    {
    }

    public static class TestMessageSource implements MessageSource
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

}
