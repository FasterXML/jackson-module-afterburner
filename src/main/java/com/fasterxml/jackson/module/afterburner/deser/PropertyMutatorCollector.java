package com.fasterxml.jackson.module.afterburner.deser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;

import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;

import com.fasterxml.jackson.module.afterburner.util.DynamicPropertyAccessorBase;
import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

/**
 * Simple collector used to keep track of properties for which code-generated
 * mutators are needed.
 */
public class PropertyMutatorCollector
    extends DynamicPropertyAccessorBase
{
    private static final Type STRING_TYPE = Type.getType(String.class);
    private static final Type OBJECT_TYPE = Type.getType(Object.class);

    private final ArrayList<SettableIntMethodProperty> _intSetters = new ArrayList<SettableIntMethodProperty>();
    private final ArrayList<SettableLongMethodProperty> _longSetters = new ArrayList<SettableLongMethodProperty>();
    private final ArrayList<SettableStringMethodProperty> _stringSetters = new ArrayList<SettableStringMethodProperty>();
    private final ArrayList<SettableObjectMethodProperty> _objectSetters = new ArrayList<SettableObjectMethodProperty>();

    private final ArrayList<SettableIntFieldProperty> _intFields = new ArrayList<SettableIntFieldProperty>();
    private final ArrayList<SettableLongFieldProperty> _longFields = new ArrayList<SettableLongFieldProperty>();
    private final ArrayList<SettableStringFieldProperty> _stringFields = new ArrayList<SettableStringFieldProperty>();
    private final ArrayList<SettableObjectFieldProperty> _objectFields = new ArrayList<SettableObjectFieldProperty>();

    private final Class<?> beanClass;
    private final String beanClassName;

    public PropertyMutatorCollector(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.beanClassName = Type.getInternalName(beanClass);
    }
    
    /*
    /**********************************************************
    /* Methods for collecting properties
    /**********************************************************
     */

    public SettableIntMethodProperty addIntSetter(SettableBeanProperty prop) {
        return _add(_intSetters, new SettableIntMethodProperty(prop, null, _intSetters.size()));
    }
    public SettableLongMethodProperty addLongSetter(SettableBeanProperty prop) {
        return _add(_longSetters, new SettableLongMethodProperty(prop, null, _longSetters.size()));
    }
    public SettableStringMethodProperty addStringSetter(SettableBeanProperty prop) {
        return _add(_stringSetters, new SettableStringMethodProperty(prop, null, _stringSetters.size()));
    }
    public SettableObjectMethodProperty addObjectSetter(SettableBeanProperty prop) {
        return _add(_objectSetters, new SettableObjectMethodProperty(prop, null, _objectSetters.size()));
    }

    public SettableIntFieldProperty addIntField(SettableBeanProperty prop) {
        return _add(_intFields, new SettableIntFieldProperty(prop, null, _intFields.size()));
    }
    public SettableLongFieldProperty addLongField(SettableBeanProperty prop) {
        return _add(_longFields, new SettableLongFieldProperty(prop, null, _longFields.size()));
    }
    public SettableStringFieldProperty addStringField(SettableBeanProperty prop) {
        return _add(_stringFields, new SettableStringFieldProperty(prop, null, _stringFields.size()));
    }
    public SettableObjectFieldProperty addObjectField(SettableBeanProperty prop) {
        return _add(_objectFields, new SettableObjectFieldProperty(prop, null, _objectFields.size()));
    }

    public boolean isEmpty() {
        return _intSetters.isEmpty()
            && _longSetters.isEmpty()
            && _stringSetters.isEmpty()
            && _objectSetters.isEmpty()
            && _intFields.isEmpty()
            && _longFields.isEmpty()
            && _stringFields.isEmpty()
            && _objectFields.isEmpty()
        ;
    }
    
    /*
    /**********************************************************
    /* Code generation; high level
    /**********************************************************
     */

    /**
     * Method for building generic mutator class for specified bean
     * type.
     */
    public BeanPropertyMutator buildMutator(MyClassLoader classLoader)
    {
        // if we weren't passed a class loader, we will base it on value type CL, try to use parent
        if (classLoader == null) {
            classLoader = new MyClassLoader(beanClass.getClassLoader(), true);
        }

        String srcName = beanClass.getName() + "$Access4JacksonDeserializer";

        String generatedClass = internalClassName(srcName);
        Class<?> accessorClass = null;
        try {
            accessorClass = classLoader.loadClass(srcName);
        } catch (ClassNotFoundException e) { }
        if (accessorClass == null) {
            accessorClass = generateMutatorClass(classLoader, srcName, generatedClass);
        }
        try {
            return (BeanPropertyMutator) accessorClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate accessor class '"+srcName+"': "+e.getMessage(), e);
        }
    }

    public Class<?> generateMutatorClass(MyClassLoader classLoader, String srcName, String generatedClass)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String superClass = internalClassName(BeanPropertyMutator.class.getName());

        // muchos important: level at least 1.5 to get generics!!!
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, generatedClass, null, superClass, null);
        cw.visitSource(srcName + ".java", null);

        // add default (no-arg) constructor:
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();

        // and then add various accessors; first field accessors:
        if (!_intFields.isEmpty()) {
            _addFields(cw, _intFields, "intField", Type.INT_TYPE, ILOAD);
        }
        if (!_longFields.isEmpty()) {
            _addFields(cw, _longFields, "longField", Type.LONG_TYPE, LLOAD);
        }
        if (!_stringFields.isEmpty()) {
            _addFields(cw, _stringFields, "stringField", STRING_TYPE, ALOAD);
        }
        if (!_objectFields.isEmpty()) {
            _addFields(cw, _objectFields, "objectField", OBJECT_TYPE, ALOAD);
        }

        // and then method accessors:
        if (!_intSetters.isEmpty()) {
            _addSetters(cw, _intSetters, "intSetter", Type.INT_TYPE, ILOAD);
        }
        if (!_longSetters.isEmpty()) {
            _addSetters(cw, _longSetters, "longSetter", Type.LONG_TYPE, LLOAD);
        }
        if (!_stringSetters.isEmpty()) {
            _addSetters(cw, _stringSetters, "stringSetter", STRING_TYPE, ALOAD);
        }
        if (!_objectSetters.isEmpty()) {
            _addSetters(cw, _objectSetters, "objectSetter", OBJECT_TYPE, ALOAD);
        }

        cw.visitEnd();
        byte[] byteCode = cw.toByteArray();
        return classLoader.loadAndResolve(srcName, byteCode);
    }
    
    /*
    /**********************************************************
    /* Code generation; method-based getters
    /**********************************************************
     */

    private <T extends OptimizedSettableBeanProperty<T>> void _addSetters(ClassWriter cw, List<T> props,
            String methodName, Type parameterType, int loadValueCode)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(Ljava/lang/Object;I"+parameterType+")V", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClassName);
        int localVarIndex = 4 + (parameterType.equals(Type.LONG_TYPE) ? 1 : 0);
        mv.visitVarInsn(ASTORE, localVarIndex); // 3 args (0 == this), so 4 is the first local var slot, 5 for long

        boolean mustCast = parameterType.equals(OBJECT_TYPE);
        // Ok; minor optimization, 4 or less accessors, just do IFs; over that, use switch
        if (props.size() <= 4) {
            _addSettersUsingIf(mv, props, loadValueCode, localVarIndex, mustCast);
        } else {
            _addSettersUsingSwitch(mv, props, loadValueCode, localVarIndex, mustCast);
        }
        // and if no match, generate exception:
        generateException(mv, beanClassName, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }

    /*
    /**********************************************************
    /* Code generation; field-based getters
    /**********************************************************
     */

    private <T extends OptimizedSettableBeanProperty<T>> void _addFields(ClassWriter cw, List<T> props,
            String methodName, Type parameterType, int loadValueCode)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "(Ljava/lang/Object;I"+parameterType+")V", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClassName);
        int localVarIndex = 4 + (parameterType.equals(Type.LONG_TYPE) ? 1 : 0);
        mv.visitVarInsn(ASTORE, localVarIndex); // 3 args (0 == this), so 4 is the first local var slot, 5 for long

        boolean mustCast = parameterType.equals(OBJECT_TYPE);
        // Ok; minor optimization, less than 4 accessors, just do IFs; over that, use switch
        if (props.size() < 4) {
            _addFieldsUsingIf(mv, props, loadValueCode, localVarIndex, mustCast);
        } else {
            _addFieldsUsingSwitch(mv, props, loadValueCode, localVarIndex, mustCast);
        }
        // and if no match, generate exception:
        generateException(mv, beanClassName, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }
    
    /*
    /**********************************************************
    /* Helper methods, method accessor creation
    /**********************************************************
     */

    private <T extends OptimizedSettableBeanProperty<T>> void _addSettersUsingIf(MethodVisitor mv,
            List<T> props, int loadValueCode, int beanIndex, boolean mustCast)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)
        Label next = new Label();
        // first: check if 'index == 0'
        mv.visitJumpInsn(IFNE, next); // "if not zero, goto L (skip stuff)"
        // call first getter:
        mv.visitVarInsn(ALOAD, beanIndex); // load local for cast bean
        mv.visitVarInsn(loadValueCode, 3);
        Method method = (Method) (props.get(0).getMember().getMember());
        Type type = Type.getType(method.getParameterTypes()[0]);
        if (mustCast) {
            mv.visitTypeInsn(CHECKCAST, type.getInternalName());
        }
        // to fix [Issue-5] (don't assume return type is 'void'), we need to:
        Type returnType = Type.getType(method.getReturnType());

        mv.visitMethodInsn(INVOKEVIRTUAL, beanClassName, method.getName(),
                "("+type+")"+returnType,  method.getDeclaringClass().isInterface());
        mv.visitInsn(RETURN);

        // And from this point on, loop a bit
        for (int i = 1, len = props.size(); i < len; ++i) {
            mv.visitLabel(next);
            next = new Label();
            mv.visitVarInsn(ILOAD, 2); // load second arg (index)
            mv.visitInsn(ALL_INT_CONSTS[i]);
            mv.visitJumpInsn(IF_ICMPNE, next);
            mv.visitVarInsn(ALOAD, beanIndex); // load bean
            mv.visitVarInsn(loadValueCode, 3);
            method = (Method) (props.get(i).getMember().getMember());
            type = Type.getType(method.getParameterTypes()[0]);

            returnType = Type.getType(method.getReturnType());

            if (mustCast) {
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClassName, method.getName(),
                    "("+type+")"+returnType, method.getDeclaringClass().isInterface());
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(next);
    }

    private <T extends OptimizedSettableBeanProperty<T>> void _addSettersUsingSwitch(MethodVisitor mv,
            List<T> props, int loadValueCode, int beanIndex, boolean mustCast)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)

        Label[] labels = new Label[props.size()];
        for (int i = 0, len = labels.length; i < len; ++i) {
            labels[i] = new Label();
        }
        Label defaultLabel = new Label();
        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
        for (int i = 0, len = labels.length; i < len; ++i) {
            mv.visitLabel(labels[i]);
            mv.visitVarInsn(ALOAD, beanIndex); // load bean
            mv.visitVarInsn(loadValueCode, 3);
            Method method = (Method) (props.get(i).getMember().getMember());
            Type type = Type.getType(method.getParameterTypes()[0]);

            Type returnType = Type.getType(method.getReturnType());

            if (mustCast) {
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());
            }
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClassName, method.getName(),
                    "("+type+")"+returnType, method.getDeclaringClass().isInterface());
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(defaultLabel);
    }

    private <T extends OptimizedSettableBeanProperty<T>> void _addFieldsUsingIf(MethodVisitor mv,
            List<T> props, int loadValueCode, int beanIndex, boolean mustCast)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)
        Label next = new Label();
        // first: check if 'index == 0'
        mv.visitJumpInsn(IFNE, next); // "if not zero, goto L (skip stuff)"

        // first field accessor
        mv.visitVarInsn(ALOAD, beanIndex); // load local for cast bean
        mv.visitVarInsn(loadValueCode, 3);
        AnnotatedField field = (AnnotatedField) props.get(0).getMember();
        Type type = Type.getType(field.getRawType());
        if (mustCast) {
            mv.visitTypeInsn(CHECKCAST, type.getInternalName());
        }
        mv.visitFieldInsn(PUTFIELD, beanClassName, field.getName(), type.getDescriptor());
        mv.visitInsn(RETURN);

        // And from this point on, loop a bit
        for (int i = 1, len = props.size(); i < len; ++i) {
            mv.visitLabel(next);
            next = new Label();
            mv.visitVarInsn(ILOAD, 2); // load second arg (index)
            mv.visitInsn(ALL_INT_CONSTS[i]);
            mv.visitJumpInsn(IF_ICMPNE, next);
            mv.visitVarInsn(ALOAD, beanIndex); // load bean
            mv.visitVarInsn(loadValueCode, 3);
            field = (AnnotatedField) props.get(i).getMember();
            type = Type.getType(field.getRawType());
            if (mustCast) {
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());
            }
            mv.visitFieldInsn(PUTFIELD, beanClassName, field.getName(), type.getDescriptor());
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(next);
    }

    private <T extends OptimizedSettableBeanProperty<T>> void _addFieldsUsingSwitch(MethodVisitor mv,
            List<T> props, int loadValueCode, int beanIndex, boolean mustCast)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)

        Label[] labels = new Label[props.size()];
        for (int i = 0, len = labels.length; i < len; ++i) {
            labels[i] = new Label();
        }
        Label defaultLabel = new Label();
        mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
        for (int i = 0, len = labels.length; i < len; ++i) {
            mv.visitLabel(labels[i]);
            mv.visitVarInsn(ALOAD, beanIndex); // load bean
            mv.visitVarInsn(loadValueCode, 3); // put 'value' to stack
            AnnotatedField field = (AnnotatedField) props.get(i).getMember();
            Type type = Type.getType(field.getRawType());
            if (mustCast) {
                mv.visitTypeInsn(CHECKCAST, type.getInternalName());
            }
            mv.visitFieldInsn(PUTFIELD, beanClassName, field.getName(), type.getDescriptor());
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(defaultLabel);
    }
}
