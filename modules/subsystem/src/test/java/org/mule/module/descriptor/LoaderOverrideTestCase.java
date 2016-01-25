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
    //TODO(pablo.kraan): CCL - add test for parent first - rename tests to parent first/only/etc

    @Test
    public void isBlockedFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("org.mycompany.MyClass"));

        assertThat(loaderOverride.useChildOnly("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.useChildOnly("MyClass"), is(false));
        assertThat(loaderOverride.useChildOnly("org.mycompany.MyClassFactory"), is(false));
    }

    @Test
    public void isBlockedNotFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("MyClass"));

        assertThat(loaderOverride.useChildOnly("MyClass"), is(true));
        assertThat(loaderOverride.useChildOnly("MyClassFactory"), is(false));
        assertThat(loaderOverride.useChildOnly("org.mycompany.MyClass"), is(false));
    }

    @Test
    public void isBlockedPackageName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("org.mycompany"));

        assertThat(loaderOverride.useChildOnly("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.useChildOnly("org.mycompany.somepackage.MyClass"), is(true));
    }

    @Test
    public void isOverriddenFQClassName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("org.mycompany.MyClass"), Collections.EMPTY_SET);

        assertThat(loaderOverride.useParentFirst("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.useParentFirst("MyClass"), is(false));
        assertThat(loaderOverride.useParentFirst("org.mycompany.MyClassFactory"), is(false));
    }

    @Test
    public void isOverriddenNotFQClassName() throws Exception
    {

        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("MyClass"), Collections.EMPTY_SET);

        assertThat(loaderOverride.useParentFirst("MyClass"), is(true));
        assertThat(loaderOverride.useParentFirst("MyClassFactory"), is(false));
        assertThat(loaderOverride.useParentFirst("org.mycompany.MyClass"), is(false));
    }

    @Test
    public void isOverriddenPackageName() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("org.mycompany"), Collections.EMPTY_SET);

        assertThat(loaderOverride.useParentFirst("org.mycompany.MyClass"), is(true));
        assertThat(loaderOverride.useParentFirst("org.mycompany.somepackage.MyClass"), is(true));
        assertThat(loaderOverride.useParentFirst("org."), is(false));
    }
}
