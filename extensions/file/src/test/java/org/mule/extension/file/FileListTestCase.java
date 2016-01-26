/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.mule.api.MuleEvent;
import org.mule.api.temporary.MuleMessage;
import org.mule.extension.file.api.FileInputStream;
import org.mule.extension.file.api.LocalFileAttributes;
import org.mule.module.extension.file.api.FileAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class FileListTestCase extends FileConnectorTestCase
{

    private static final String TEST_FILE_PATTERN = "test-file-%d.html";
    private static final String SUB_DIRECTORY_NAME = "subDirectory";
    private static final String CONTENT = "foo";

    @Override
    protected String getConfigFile()
    {
        return "file-list-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        createTestFiles();
    }

    @Test
    public void listNotRecursive() throws Exception
    {
        List<MuleMessage<FileInputStream, LocalFileAttributes>> list = doList(".", false);
        assertThat(list, hasSize(6));
        assertThat(assertListedFiles(list), is(true));
    }

    @Test
    public void listRecursive() throws Exception
    {
        List<MuleMessage<FileInputStream, LocalFileAttributes>> list = doList(".", true);
        assertThat(list, hasSize(8));
        assertThat(assertListedFiles(list), is(true));
    }

    @Test
    public void notDirectory() throws Exception
    {
        expectedException.expectCause(is(instanceOf(IllegalArgumentException.class)));
        doList(String.format(TEST_FILE_PATTERN, 0), false);
    }

    @Test
    public void notExistingPath() throws Exception
    {
        expectedException.expectCause(is(instanceOf(IllegalArgumentException.class)));
        doList(String.format("whatever", 0), false);
    }

    @Test
    public void listWithEmbeddedMatcher() throws Exception
    {
        List<MuleMessage<FileInputStream, LocalFileAttributes>> list = doList("listWithEmbeddedPredicate", ".", false);
        assertThat(list, hasSize(2));
        assertThat(assertListedFiles(list), is(false));
    }

    @Test
    public void listWithGlobalMatcher() throws Exception
    {
        List<MuleMessage<FileInputStream, LocalFileAttributes>> list = doList("listWithGlobalMatcher", ".", true);
        assertThat(list, hasSize(1));
        FileAttributes file = list.get(0).getAttributes();
        assertThat(file.isDirectory(), is(true));
        assertThat(file.getName(), equalTo(SUB_DIRECTORY_NAME));
    }

    private boolean assertListedFiles(List<MuleMessage<FileInputStream, LocalFileAttributes>> list) throws IOException
    {
        boolean directoryWasFound = false;

        for (MuleMessage<FileInputStream, LocalFileAttributes> message : list)
        {
            LocalFileAttributes attributes = message.getAttributes();
            if (attributes.isDirectory())
            {
                assertThat("two directories found", directoryWasFound, is(false));
                directoryWasFound = true;
                assertThat(attributes.getName(), equalTo(SUB_DIRECTORY_NAME));
            }
            else
            {
                assertThat(attributes.getName(), endsWith(".html"));
                assertThat(IOUtils.toString(message.getPayload()), equalTo(CONTENT));
                assertThat(attributes.getSize(), is(new Long(CONTENT.length())));
            }
        }

        return directoryWasFound;
    }


    private List<MuleMessage<FileInputStream, LocalFileAttributes>> doList(String path, boolean recursive) throws Exception
    {
        return doList("list", path, recursive);
    }

    private List<MuleMessage<FileInputStream, LocalFileAttributes>> doList(String flowName, String path, boolean recursive) throws Exception
    {
        MuleEvent event = getTestEvent("");
        event.setFlowVariable("path", path);
        event.setFlowVariable("recursive", recursive);

        return (List<MuleMessage<FileInputStream, LocalFileAttributes>>) runFlow(flowName, event).getMessage().getPayload();
    }

    private void createTestFiles() throws Exception
    {
        createTestFiles(temporaryFolder.getRoot(), 0, 5);
        createSubDirectory();
    }

    private void createSubDirectory() throws Exception
    {
        createTestFiles(temporaryFolder.newFolder(SUB_DIRECTORY_NAME), 5, 7);
    }

    private void createTestFiles(File parentFolder, int startIndex, int endIndex) throws Exception
    {
        for (int i = startIndex; i < endIndex; i++)
        {
            String name = String.format(TEST_FILE_PATTERN, i);
            File file = new File(parentFolder, name);
            file.createNewFile();
            FileUtils.write(file, CONTENT);
        }
    }
}
