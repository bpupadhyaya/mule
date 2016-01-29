/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.kryo;

import org.mule.module.classloader.Module;

import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.IdentityObjectIntMap;
import com.esotericsoftware.kryo.util.IntMap;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.esotericsoftware.kryo.util.Util;
import com.esotericsoftware.minlog.Log;

//TODO(pablo.kraan): CCL - try to extend original class instead of copy/paste it. Fix header if required
public class ModuleClassResolver implements ClassResolver
{
    public static final byte NAME = -1;
    protected Kryo kryo;
    protected final IntMap<Registration> idToRegistration = new IntMap();
    protected final ObjectMap<Class, Registration> classToRegistration = new ObjectMap();
    protected IdentityObjectIntMap<Class> classToNameId;
    protected IntMap<Class> nameIdToClass;
    protected ObjectMap<String, Class> nameToClass;
    protected int nextNameId;
    private int memoizedClassId = -1;
    private Registration memoizedClassIdValue;
    private Class memoizedClass;
    private Registration memoizedClassValue;

    public ModuleClassResolver() {
    }

    public void setKryo(Kryo kryo) {
        this.kryo = kryo;
    }

    public Registration register(Registration registration) {
        if(registration == null) {
            throw new IllegalArgumentException("registration cannot be null.");
        } else {
            if(registration.getId() != -1) {
                if(Log.TRACE) {
                    Log.trace("kryo", "Register class ID " + registration.getId() + ": " + Util.className(registration.getType()) + " (" + registration.getSerializer().getClass().getName() + ")");
                }

                this.idToRegistration.put(registration.getId(), registration);
            } else if(Log.TRACE) {
                Log.trace("kryo", "Register class name: " + Util.className(registration.getType()) + " (" + registration.getSerializer().getClass().getName() + ")");
            }

            this.classToRegistration.put(registration.getType(), registration);
            if(registration.getType().isPrimitive()) {
                this.classToRegistration.put(Util.getWrapperClass(registration.getType()), registration);
            }

            return registration;
        }
    }

    public Registration registerImplicit(Class type) {
        return this.register(new Registration(type, this.kryo.getDefaultSerializer(type), -1));
    }

    public Registration getRegistration(Class type) {
        if(type == this.memoizedClass) {
            return this.memoizedClassValue;
        } else {
            Registration registration = (Registration)this.classToRegistration.get(type);
            if(registration != null) {
                this.memoizedClass = type;
                this.memoizedClassValue = registration;
            }

            return registration;
        }
    }

    public Registration getRegistration(int classID) {
        return (Registration)this.idToRegistration.get(classID);
    }

    public Registration writeClass(Output output, Class type) {
        if(type != null) {
            Registration registration = this.kryo.getRegistration(type);
            if(registration.getId() == -1) {
                this.writeName(output, type, registration);
            } else {
                if(Log.TRACE) {
                    Log.trace("kryo", "Write class " + registration.getId() + ": " + Util.className(type));
                }

                output.writeVarInt(registration.getId() + 2, true);
            }

            return registration;
        } else {
            if(Log.TRACE || Log.DEBUG && this.kryo.getDepth() == 1) {
                Util.log("Write", (Object)null);
            }

            output.writeVarInt(0, true);
            return null;
        }
    }

    protected void writeName(Output output, Class type, Registration registration) {
        output.writeVarInt(1, true);
        int nameId;
        if(this.classToNameId != null) {
            nameId = this.classToNameId.get(type, -1);
            if(nameId != -1) {
                if(Log.TRACE) {
                    Log.trace("kryo", "Write class name reference " + nameId + ": " + Util.className(type));
                }

                output.writeVarInt(nameId, true);
                return;
            }
        }

        if(Log.TRACE) {
            Log.trace("kryo", "Write class name: " + Util.className(type));
        }

        nameId = this.nextNameId++;
        if(this.classToNameId == null) {
            this.classToNameId = new IdentityObjectIntMap();
        }

        this.classToNameId.put(type, nameId);
        output.writeVarInt(nameId, true);
        output.writeString(type.getName());
        output.writeString("mule-core");
    }

    public Registration readClass(Input input) {
        int classID = input.readVarInt(true);
        switch(classID) {
            case 0:
                if(Log.TRACE || Log.DEBUG && this.kryo.getDepth() == 1) {
                    Util.log("Read", (Object)null);
                }

                return null;
            case 1:
                return this.readName(input);
            default:
                if(classID == this.memoizedClassId) {
                    return this.memoizedClassIdValue;
                } else {
                    Registration registration = (Registration)this.idToRegistration.get(classID - 2);
                    if(registration == null) {
                        throw new KryoException("Encountered unregistered class ID: " + (classID - 2));
                    } else {
                        if(Log.TRACE) {
                            Log.trace("kryo", "Read class " + (classID - 2) + ": " + Util.className(registration.getType()));
                        }

                        this.memoizedClassId = classID;
                        this.memoizedClassIdValue = registration;
                        return registration;
                    }
                }
        }
    }

    protected Registration readName(Input input) {
        int nameId = input.readVarInt(true);
        if(this.nameIdToClass == null) {
            this.nameIdToClass = new IntMap();
        }

        Class type = (Class)this.nameIdToClass.get(nameId);
        if(type == null) {
            String className = input.readString();
            final String moduleId = input.readString();
            final String moduleClassName = moduleId + ":" + className;
            type = this.getTypeByName(moduleClassName);
            if(type == null) {
                try {
                    //TODO(pablo.kraan): CCL - need to obtain module classloader here instead of using kryo's
                    final ClassLoader moduleClassLoader = Module.getInstance().getClassLoader(moduleId);
                    type = Class.forName(className, false, moduleClassLoader);
                } catch (ClassNotFoundException var6) {
                    throw new KryoException("Unable to find class: " + moduleClassName, var6);
                }

                if(this.nameToClass == null) {
                    this.nameToClass = new ObjectMap();
                }

                this.nameToClass.put(moduleClassName , type);
            }

            this.nameIdToClass.put(nameId, type);
            if(Log.TRACE) {
                Log.trace("kryo", "Read class name: " + moduleClassName);
            }
        } else if(Log.TRACE) {
            Log.trace("kryo", "Read class name reference " + nameId + ": " + Util.className(type));
        }

        return this.kryo.getRegistration(type);
    }

    protected Class<?> getTypeByName(String className) {
        return this.nameToClass != null?(Class)this.nameToClass.get(className):null;
    }

    public void reset() {
        if(!this.kryo.isRegistrationRequired()) {
            if(this.classToNameId != null) {
                this.classToNameId.clear();
            }

            if(this.nameIdToClass != null) {
                this.nameIdToClass.clear();
            }

            this.nextNameId = 0;
        }

    }
}
