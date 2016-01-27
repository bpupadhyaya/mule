/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.classloader;

import org.mule.module.descriptor.LoaderExport;
import org.mule.module.descriptor.LoaderExportParser;
import org.mule.module.descriptor.ModuleDescriptor;

import java.util.HashSet;
import java.util.Set;

public class MuleClassLoaderFactory
{

    public static ClassLoader createMuleClassLoader()
    {
        //TODO(pablo.kraan): CCL - Move all this code to a different class
        final ClassLoader muleClassLoader = MuleClassLoaderFactory.class.getClassLoader();
        //TODO(pablo.kraan): CCL - need a descriptor for Mule
        final ModuleDescriptor muleModuleDescriptor = new ModuleDescriptor("MuleCore");
        final LoaderExport loaderExport = createMuleLoaderExport();
        muleModuleDescriptor.setLoaderExport(loaderExport);
        ModuleClassLoaderFilter filter = new ModuleClassLoaderFilter(muleModuleDescriptor);
        FilteringModuleClassLoader filteredMuleClassLoader = new FilteringModuleClassLoader("MuleCore", muleClassLoader, filter);

        return filteredMuleClassLoader;
    }

    public static LoaderExport createMuleLoaderExport()
    {
        Set<String> loaderExports = getMuleUniqueResources();
        loaderExports.addAll(getMuleSharedResources());


        return new LoaderExportParser().parse(loaderExports.toArray(new String[0]));
    }

    //TODO(pablo.kraan): CCL - is really ugly to use this method form the outside
    public static Set<String> getMuleUniqueResources()
    {
        Set<String> loaderExports = new HashSet<>();
        loaderExports.add("java");
        loaderExports.add("javax");
        loaderExports.add("sun");
        loaderExports.add("org.slf4j");
        loaderExports.add("org.apache.logging.log4j");

        loaderExports.add("org.springframework");
        loaderExports.add("org/springframework");
        loaderExports.add("org.mule.module.launcher.artifact.ResourceReleaser");
        //TODO(pablo.kraan): CCL - add to make the test pass
        loaderExports.add("org.mule.module.launcher.DeploymentServiceTestCase");
        loaderExports.add("org.mule.config");
        loaderExports.add("org.mule.module.extension");
        loaderExports.add("org.mule.extension");
        loaderExports.add("org.mule.api");
        loaderExports.add("com.mulesoft");

        //TODO(pablo.kraan): CCL - seems like some classes are not required when running from tests. Apparently is because bootstrap properties included on al lthe other dependencies not included on launcher moduel
        loaderExports.add("org.apache.xerces");
        loaderExports.add("org.apache.commons");
        loaderExports.add("org.dom4j");
        loaderExports.add("org.w3c");
        loaderExports.add("org.mule.module.http");
        loaderExports.add("org.mule.expression");
        loaderExports.add("org.mule.registry");
        //TODO(pablo.kraan): CCL - see how test can have different Isolation rules in order to include mule test code and third party libraries used on a test
        loaderExports.add("org.mule.functional");
        loaderExports.add("org.mule.util");
        loaderExports.add("org.mule.retry");
        loaderExports.add("org.mule.el.mvel");
        loaderExports.add("org.mule.time");
        loaderExports.add("org.mule.internal.connection");
        loaderExports.add("org.mule.security");
        loaderExports.add("org.mule.execution");
        loaderExports.add("org.mule.endpoint");
        //TODO(pablo.kraan): CCL - why do we have fucking classes on the root package?
        //TODO(pablo.kraan): CCL - as with OSGi, we would also like to avoid duplicating package names as you can't exported it or blocked it depending on the used jar.
        loaderExports.add("org.mule.DynamicDataTypeConversionResolver");
        loaderExports.add("org.mule.management.stats");
        loaderExports.add("org.mule.connector");
        loaderExports.add("org.mule.exception");
        loaderExports.add("org.mule.context.notification");
        loaderExports.add("org.mule.construct");

        //TODO(pablo.kraan): CCL - added to make FunctionalTestCases pass
        loaderExports.add("org.mule.tck");
        loaderExports.add("org.mule.test");
        loaderExports.add("org.mule.session");
        loaderExports.add("org.mule.transport");
        loaderExports.add("org.mule.transformer");
        loaderExports.add("com.ctc.wstx");
        loaderExports.add("org.mule.module.ws.functional");
        loaderExports.add("com.sun");
        loaderExports.add("com/sun");
        loaderExports.add("com.sun");
        loaderExports.add("org.mule.module.cxf.PasswordCallback");
        loaderExports.add("com.sun.org.apache.xerces");
        loaderExports.add("org.mule.module.oauth2.internal.authorizationcode.state.ResourceOwnerOAuthContext");
        loaderExports.add("org.mule.module.jaas");
        loaderExports.add("net.sf.saxon");
        loaderExports.add("org.apache.xalan");
        loaderExports.add("jdk.nashorn");
        loaderExports.add("apple.applescript");
        loaderExports.add("org.codehaus.groovy");
        loaderExports.add("org.python");
        loaderExports.add("org.jruby");

        return loaderExports;
    }

    private static Set<String> getMuleSharedResources()
    {
        Set<String> loaderExports = new HashSet<>();
        loaderExports.add("META-INF");

        return loaderExports;
    }

}
