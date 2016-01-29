/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.classloader;

import static org.junit.Assert.assertEquals;
import org.mule.module.descriptor.LoaderOverride;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.util.ClassUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

import org.junit.Test;

@SmallTest
public class FineGrainedControlClassLoaderTestCase extends AbstractMuleTestCase
{

    @Test
    public void usesChildFirstByDefault() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[] { bye() }, parent, null);
        assertEquals("Bye", callHi(ext));
    }

    @Test
    public void usesParentFirstWithOverride() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("mypackage"), Collections.EMPTY_SET);

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[] { bye() }, parent, loaderOverride, null);

        assertEquals("Hello", callHi(ext));
    }

    @Test
    public void usesParentFirstWithMissingOverride() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.singleton("mypackage"), Collections.EMPTY_SET);

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[0], parent, loaderOverride, null);
        assertEquals("Hello", callHi(ext));
    }

    @Test
    public void usesChildOnlyWithBlockOverride() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("mypackage"));

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[] { bye() }, parent, loaderOverride, null);
        assertEquals("Bye", callHi(ext));
    }

    @Test(expected = ClassNotFoundException.class)
    public void usesChildOnlyWithBlockOverrideAndClassIsMissing() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("mypackage"));

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[0], parent, loaderOverride, null);
        callHi(ext);
    }

    @Test
    public void usesParentOnly() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[] { hello() }, Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.singleton("mypackage"), Collections.EMPTY_SET, Collections.EMPTY_SET);

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[] { bye() }, parent, loaderOverride, null);

        assertEquals("Hello", callHi(ext));
    }

    @Test(expected = ClassNotFoundException.class)
    public void usesParentOnlyStillWhenClassNotFound() throws Exception
    {
        URLClassLoader parent = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());

        LoaderOverride loaderOverride = new LoaderOverride(Collections.singleton("mypackage"), Collections.EMPTY_SET, Collections.EMPTY_SET);

        FineGrainedControlClassLoader ext = new FineGrainedControlClassLoader(new URL[] {bye()}, parent, loaderOverride, null);

        callHi(ext);
    }

    private URL hello()
    {
        return ClassUtils.getResource("classloader-test-hello.jar", this.getClass());
    }

    private URL bye()
    {
        return ClassUtils.getResource("classloader-test-bye.jar", this.getClass());
    }

    private String callHi(ClassLoader loader) throws Exception
    {
        Class cls = loader.loadClass("mypackage.MyClass");
        Method method = cls.getMethod("hi");
        return (String) method.invoke(cls.newInstance());
    }
}
