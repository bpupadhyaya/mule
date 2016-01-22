/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.descriptor;

/**
 * Thrown to indicate any error related to errors in the structure of a plugin
 * file or folder.
 */
public class InvalidPluginException extends RuntimeException
{

    public InvalidPluginException(String message)
    {
        super(message);
    }

    public InvalidPluginException(String s, Throwable throwable)
    {
        super(s, throwable);
    }
}
