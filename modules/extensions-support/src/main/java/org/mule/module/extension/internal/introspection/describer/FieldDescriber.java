package org.mule.module.extension.internal.introspection.describer;

import org.mule.extension.api.introspection.declaration.fluent.ParameterDescriptor;
import org.mule.extension.api.introspection.declaration.fluent.WithParameters;

import java.lang.reflect.Field;

interface FieldDescriber
{
    ParameterDescriptor describe(Field field, WithParameters with);
}
