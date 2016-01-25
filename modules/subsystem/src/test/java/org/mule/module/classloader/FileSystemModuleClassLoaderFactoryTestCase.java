/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.mule.module.descriptor.LoaderOverride;
import org.mule.module.descriptor.PluginDescriptor;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SmallTest
public class FileSystemModuleClassLoaderFactoryTestCase extends AbstractMuleTestCase
{

    @Rule
    public TemporaryFolder pluginFolder = new TemporaryFolder();

    private FileSystemModuleClassLoaderFactory factory = new FileSystemModuleClassLoaderFactory();
    private PluginDescriptor descriptor;

    @Before
    public void setUp() throws Exception
    {
        descriptor = new PluginDescriptor("test");
        descriptor.setRootFolder(pluginFolder.getRoot());
    }

    @Test
    public void createsEmptyClassLoader() throws Exception
    {
        ModuleClassLoader classLoader = factory.create(descriptor);
        assertThat(classLoader.getURLs(), equalTo(new URL[0]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validatesPluginFolder() throws Exception
    {
        File fakePluginFolder = new File("./fake/folder/for/test");
        descriptor.setRootFolder(fakePluginFolder);
        factory.create(descriptor);
    }

    @Test
    public void addsClassesFolderToClassLoader() throws Exception
    {
        File classesFolder = pluginFolder.newFolder(FileSystemModuleClassLoaderFactory.CLASSES_DIR);

        ModuleClassLoader classLoader = factory.create(descriptor);
        assertThat(classLoader.getURLs(), equalTo(new URL[] {classesFolder.toURI().toURL()}));
    }

    @Test
    public void addJarsFromLibFolderToClassLoader() throws Exception
    {
        File libFolder = pluginFolder.newFolder(FileSystemModuleClassLoaderFactory.LIB_DIR);
        File jarFile = new File(libFolder, "dummy.jar");
        jarFile.createNewFile();

        ModuleClassLoader classLoader = factory.create(descriptor);
        assertThat(classLoader.getURLs(), equalTo(new URL[] {jarFile.toURI().toURL()}));
    }

    @Test
    public void ignoresNonJarsFilesFromLibFolder() throws Exception
    {
        File libFolder = pluginFolder.newFolder(FileSystemModuleClassLoaderFactory.LIB_DIR);
        File jarFile = new File(libFolder, "dummy.txt");
        jarFile.createNewFile();

        ModuleClassLoader classLoader = factory.create(descriptor);
        assertThat(classLoader.getURLs(), equalTo(new URL[] {}));
    }

    @Test
    public void configuresLoaderOverride() throws Exception
    {
        LoaderOverride loaderOverride = new LoaderOverride(Collections.EMPTY_SET, Collections.EMPTY_SET, Collections.singleton("com.dummy"));
        descriptor.setLoaderOverride(loaderOverride);

        ModuleClassLoader moduleClassLoader = factory.create(descriptor);

        assertThat(moduleClassLoader.getLoaderOverride(), is(loaderOverride));
    }
}
