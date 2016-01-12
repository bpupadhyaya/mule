/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.capability.xml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.api.registry.ServiceRegistry;
import org.mule.extension.api.introspection.ExtensionFactory;
import org.mule.extension.api.introspection.ExtensionModel;
import org.mule.extension.api.introspection.declaration.fluent.Descriptor;
import org.mule.extension.api.introspection.declaration.spi.ModelEnricher;
import org.mule.extension.api.introspection.property.XmlModelProperty;
import org.mule.module.extension.HeisenbergExtension;
import org.mule.module.extension.internal.DefaultDescribingContext;
import org.mule.module.extension.internal.capability.xml.schema.SchemaGenerator;
import org.mule.module.extension.internal.introspection.AnnotationsBasedDescriber;
import org.mule.module.extension.internal.introspection.DefaultExtensionFactory;
import org.mule.registry.SpiServiceRegistry;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.util.IOUtils;

import java.util.List;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

@SmallTest
public class SchemaGeneratorTestCase extends AbstractMuleTestCase
{

    private SchemaGenerator generator;
    private ExtensionFactory extensionFactory;

    @Before
    public void before()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceRegistry serviceRegistry = mock(ServiceRegistry.class);
        when(serviceRegistry.lookupProviders(ModelEnricher.class, classLoader)).thenReturn(asList(new XmlModelEnricher()));

        extensionFactory = new DefaultExtensionFactory(new SpiServiceRegistry(), getClass().getClassLoader());
        generator = new SchemaGenerator();
    }

    @Test
    public void generate() throws Exception
    {
        String expectedSchema = IOUtils.getResourceAsString("heisenberg.xsd", getClass());

        Descriptor descriptor = new AnnotationsBasedDescriber(HeisenbergExtension.class).describe(new DefaultDescribingContext()).getRootDeclaration();
        ExtensionModel extensionModel = extensionFactory.createFrom(descriptor);

        XmlModelProperty capability = extensionModel.getModelProperty(XmlModelProperty.KEY);

        String schema = generator.generate(extensionModel, capability);
        XMLUnit.setNormalizeWhitespace(Boolean.TRUE);
        XMLUnit.setIgnoreWhitespace(Boolean.TRUE);
        XMLUnit.setIgnoreComments(Boolean.TRUE);
        XMLUnit.setIgnoreAttributeOrder(Boolean.TRUE);

        Diff diff = new Diff(expectedSchema, schema);
        if (!(diff.similar() && diff.identical())) {

            DetailedDiff detDiff = new DetailedDiff(diff);
            List differences = detDiff.getAllDifferences();
            StringBuilder diffLines = new StringBuilder();
            for (Object object : differences) {
                Difference difference = (Difference) object;
                diffLines.append(difference.toString() + '\n');
            }

            assertEquals("The Output of the template manager was not the expected:", expectedSchema, schema);
    }
    }
}
