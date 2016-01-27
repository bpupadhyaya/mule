/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.introspection.describer;

import static org.mule.module.extension.internal.util.MuleExtensionUtils.getDefaultValue;
import org.mule.extension.annotation.api.Parameter;
import org.mule.extension.annotation.api.param.Optional;
import org.mule.extension.api.introspection.DataType;
import org.mule.extension.api.introspection.ExpressionSupport;
import org.mule.extension.api.introspection.declaration.fluent.ParameterDescriptor;
import org.mule.extension.api.introspection.declaration.fluent.WithParameters;
import org.mule.module.extension.internal.model.property.DeclaringMemberModelProperty;
import org.mule.module.extension.internal.util.IntrospectionUtils;

import java.lang.reflect.Field;

final class DefaultFieldDescriber implements FieldDescriber
{

    @Override
    public ParameterDescriptor describe(Field field, WithParameters with)
    {
        Parameter parameter = field.getAnnotation(Parameter.class);
        Optional optional = field.getAnnotation(Optional.class);

        String parameterName = MuleExtensionAnnotationParser.getParameterName(field, parameter);
        ParameterDescriptor parameterDescriptor;
        DataType dataType = IntrospectionUtils.getFieldDataType(field);
        if (optional == null)
        {
            parameterDescriptor = with.requiredParameter(parameterName);
        }
        else
        {
            parameterDescriptor = with.optionalParameter(parameterName).defaultingTo(getDefaultValue(optional));
        }

        parameterDescriptor.ofType(dataType);
        parameterDescriptor.withExpressionSupport(parameter != null ? parameter.expressionSupport() : ExpressionSupport.SUPPORTED);
        parameterDescriptor.withModelProperty(DeclaringMemberModelProperty.KEY, new DeclaringMemberModelProperty(field));

        return parameterDescriptor;
    }
}
