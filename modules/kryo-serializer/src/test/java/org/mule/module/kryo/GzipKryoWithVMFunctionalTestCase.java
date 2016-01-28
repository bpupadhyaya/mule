/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleMessage;
import org.mule.functional.junit4.FunctionalTestCase;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

public class GzipKryoWithVMFunctionalTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigFile()
    {
        return "vm-kryo-gzip-test.xml";
    }

    @Test
    public void roundTripThroughPersistentVM() throws Exception
    {
        final String payload = RandomStringUtils.randomAlphabetic(1024);
        runFlow("kryo-vm-persistent-queuesFlow", getTestEvent(payload));

        MuleMessage response = muleContext.getClient().request("vm://in", 5000);
        assertThat(response, is(notNullValue()));
        assertThat(getPayloadAsString(response), is(payload));
    }
}
