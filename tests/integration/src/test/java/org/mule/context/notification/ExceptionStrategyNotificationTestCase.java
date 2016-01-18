/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.context.notification;

import static org.junit.Assert.assertNotNull;
import static org.mule.context.notification.ExceptionStrategyNotification.PROCESS_END;
import static org.mule.context.notification.ExceptionStrategyNotification.PROCESS_START;

import org.mule.component.ComponentException;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class ExceptionStrategyNotificationTestCase extends AbstractNotificationTestCase
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Override
    protected String getConfigFile()
    {
        return "org/mule/test/integration/notifications/exception-strategy-notification-test-flow.xml";
    }

    @Override
    public void doTest() throws Exception
    {
        assertNotNull(runFlow("catch-es", TEST_PAYLOAD));
        assertNotNull(runFlow("choice-es", TEST_PAYLOAD));
        expectedException.expect(ComponentException.class);
        assertNotNull(runFlow("rollback-es", TEST_PAYLOAD));
        assertNotNull(runFlow("default-es", TEST_PAYLOAD));
    }

    @Override
    public RestrictedNode getSpecification()
    {
        return new Node()
                .serial(node(PROCESS_START).serial(node(PROCESS_END)))
                .serial(node(PROCESS_START).serial(node(PROCESS_END)))
                .serial(node(PROCESS_START).serial(node(PROCESS_END)))
                .serial(node(PROCESS_START).serial(node(PROCESS_END)))
                ;
    }

    private RestrictedNode node(int action)
    {
        return new Node(ExceptionStrategyNotification.class, action);
    }

    @Override
    public void validateSpecification(RestrictedNode spec) throws Exception
    {
    }
}
