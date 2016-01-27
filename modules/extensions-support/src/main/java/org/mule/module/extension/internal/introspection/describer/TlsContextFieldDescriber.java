/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection.describer;

import static org.mule.module.extension.internal.ExtensionProperties.TLS_ATTRIBUTE_NAME;
import org.mule.api.tls.TlsContextFactory;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.api.introspection.ExpressionSupport;
import org.mule.extension.api.introspection.declaration.fluent.ParameterDescriptor;
import org.mule.extension.api.introspection.declaration.fluent.WithParameters;
import org.mule.module.extension.internal.model.property.DeclaringMemberModelProperty;

import java.lang.reflect.Field;

final class TlsContextFieldDescriber implements FieldDescriber
{

    @Override
    public ParameterDescriptor describe(Field field, WithParameters with)
    {
        ParameterDescriptor descriptor = field.getAnnotation(Optional.class) != null
                                         ? with.optionalParameter(TLS_ATTRIBUTE_NAME)
                                         : with.requiredParameter(TLS_ATTRIBUTE_NAME);

        return descriptor.ofType(TlsContextFactory.class)
                .withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)
                .withModelProperty(DeclaringMemberModelProperty.KEY, new DeclaringMemberModelProperty(field));
    }
}
