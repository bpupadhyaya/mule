/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.util.Collections;

import org.junit.Test;

@SmallTest
public class LoaderOverrideTestCase extends AbstractMuleTestCase
{

    @Test
    public void isBlockedFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("org.mycompany.MyClass"));

        assertThat(loaderOverride.isBlocked("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.isBlocked("MyClass"), is(false));
        assertThat(loaderOverride.isBlocked("org.mycompany.MyClassFactory"), is(false));
    }

    @Test
    public void isBlockedNotFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("MyClass"));

        assertThat(loaderOverride.isBlocked("MyClass"), is(true));
        assertThat(loaderOverride.isBlocked("MyClassFactory"), is(false));
        assertThat(loaderOverride.isBlocked("org.mycompany.MyClass"), is(false));
    }

    @Test
    public void isBlockedPackageName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("org.mycompany"));

        assertThat(loaderOverride.isBlocked("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.isBlocked("org.mycompany.somepackage.MyClass"), is(true));
    }

    @Test
    public void isOverriddenFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.singleton("org.mycompany.MyClass"), Collections.EMPTY_SET);

        assertThat(loaderOverride.isOverridden("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.isOverridden("MyClass"), is(false));
        assertThat(loaderOverride.isOverridden("org.mycompany.MyClassFactory"), is(false));
    }

    @Test
    public void isOverriddenNotFQClassName() throws Exception
    {

        LoaderOverride loaderOverride = new LoaderOverride(Collections.singleton("MyClass"), Collections.EMPTY_SET);

        assertThat(loaderOverride.isOverridden("MyClass"), is(true));
        assertThat(loaderOverride.isOverridden("MyClassFactory"), is(false));
        assertThat(loaderOverride.isOverridden("org.mycompany.MyClass"), is(false));
    }

    @Test
    public void isOverriddenPackageName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.singleton("org.mycompany"), Collections.EMPTY_SET);

        assertThat(loaderOverride.isOverridden("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.isOverridden("org.mycompany.somepackage.MyClass"), is(true));
        assertThat(loaderOverride.isOverridden("org."), is(false));
    }
}
