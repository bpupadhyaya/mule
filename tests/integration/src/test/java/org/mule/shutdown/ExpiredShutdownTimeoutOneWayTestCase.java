/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.shutdown;

import static org.junit.Assert.assertTrue;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.junit4.rule.SystemProperty;

import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;

public class ExpiredShutdownTimeoutOneWayTestCase extends AbstractShutdownTimeoutRequestResponseTestCase
{
    @Rule
    public SystemProperty contextShutdownTimeout = new SystemProperty("contextShutdownTimeout", "100");

    @Override
    protected String getConfigFile()
    {
        return "shutdown-timeout-one-way-config.xml";
    }

    @Test
    public void testStaticComponent() throws Exception
    {
        doShutDownTest("staticComponentFlow");
    }

    @Test
    public void testScriptComponent() throws Exception
    {
        doShutDownTest("scriptComponentFlow");
    }

    @Test
    public void testExpressionTransformer() throws Exception
    {
        doShutDownTest("expressionTransformerFlow");
    }

    private void doShutDownTest(final String flowName) throws MuleException, InterruptedException
    {
        final MuleClient client = muleContext.getClient();
        final boolean[] results = new boolean[] {false};

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    DefaultMuleMessage muleMessage = new DefaultMuleMessage(TEST_MESSAGE, new HashMap<String, Object>(), muleContext);
                    runFlowAsync(flowName, muleMessage);

                    MuleMessage response = client.request("test://response", RECEIVE_TIMEOUT);
                    results[0] = response == null;
                }
                catch (Exception e)
                {
                    // Ignore
                }
            }
        };
        t.start();

        // Make sure to give the request enough time to get to the waiting portion of the feed.
        waitLatch.await();

        muleContext.stop();

        t.join();

        assertTrue("Was able to process message ", results[0]);
    }
}
