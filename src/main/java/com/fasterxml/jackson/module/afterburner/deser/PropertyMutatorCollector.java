package com.fasterxml.jackson.module.afterburner.deser;

import static org.codehaus.jackson.org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.org.objectweb.asm.*;

import com.fasterxml.jackson.module.afterburner.util.DynamicPropertyAccessorBase;
import com.fasterxml.jackson.module.afterburner.util.MyClassLoader;

/**
 * Simple collector used to keep track of properties for which code-generated
 * mutators are needed.
 */
public class PropertyMutatorCollector
    extends DynamicPropertyAccessorBase
{
    private final ArrayList<SettableIntMethodProperty> _intSetters = new ArrayList<SettableIntMethodProperty>();
    private final ArrayList<SettableLongMethodProperty> _longSetters = new ArrayList<SettableLongMethodProperty>();
    
    private final ArrayList<SettableIntFieldProperty> _intFields = new ArrayList<SettableIntFieldProperty>();
    private final ArrayList<SettableLongFieldProperty> _longFields = new ArrayList<SettableLongFieldProperty>();
    
    public PropertyMutatorCollector() { }
    
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

    public SettableIntFieldProperty addIntField(SettableBeanProperty prop) {
        return _add(_intFields, new SettableIntFieldProperty(prop, null, _intFields.size()));
    }
    public SettableLongFieldProperty addLongField(SettableBeanProperty prop) {
        return _add(_longFields, new SettableLongFieldProperty(prop, null, _longFields.size()));
    }

    public boolean isEmpty() {
        return _intSetters.isEmpty()
            && _longSetters.isEmpty()
            && _intFields.isEmpty()
            && _longFields.isEmpty()
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
    public BeanPropertyMutator buildMutator(Class<?> beanType,
            MyClassLoader classLoader)
    {
        // if we weren't passed a class loader, we will base it on value type CL, try to use parent
        if (classLoader == null) {
            classLoader = new MyClassLoader(beanType.getClassLoader(), true);
        }
        
        String srcName = beanType.getName() + "$Access4JacksonDeserializer";
        
        String generatedClass = internalClassName(srcName);
        Class<?> accessorClass = null;
        try {
            accessorClass = classLoader.loadClass(srcName);
        } catch (ClassNotFoundException e) { }
        if (accessorClass == null) {
            accessorClass = generateMutatorClass(beanType, classLoader, srcName, generatedClass);
        }
        try {
            return (BeanPropertyMutator) accessorClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate accessor class '"+srcName+"': "+e.getMessage(), e);
        }
    }
        
    public Class<?> generateMutatorClass(Class<?> beanType,
            MyClassLoader classLoader, String srcName, String generatedClass)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String superClass = internalClassName(BeanPropertyMutator.class.getName());
        
        // muchos important: level at least 1.5 to get generics!!!
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, generatedClass, null, superClass, null);
        cw.visitSource(srcName + ".java", null);

        // add default (no-arg) constructor:
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClass, "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
        
        final String beanClass = internalClassName(beanType.getName());
 
        // and then add various accessors; first field accessors:
        if (!_intFields.isEmpty()) {
            _addIntFields(cw, _intFields, beanClass);
        }
        if (!_longFields.isEmpty()) {
            _addLongFields(cw, _longFields, beanClass);
        }

        // and then method accessors:
        if (!_intSetters.isEmpty()) {
            _addIntSetters(cw, _intSetters, beanClass);
        }
        if (!_longSetters.isEmpty()) {
            _addLongSetters(cw, _longSetters, beanClass);
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

    private static void _addIntSetters(ClassWriter cw, List<SettableIntMethodProperty> props,
            String beanClass)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "intSetter", "(Ljava/lang/Object;II)V", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClass);
        mv.visitVarInsn(ASTORE, 4); // 3 args (0 == this), so 4 is the first local var slot

        // Ok; minor optimization, 4 or less accessors, just do IFs; over that, use switch
        if (props.size() <= 4) {
            _addSettersUsingIf(mv, props, beanClass, ILOAD, 4);
        } else {
            _addSettersUsingSwitch(mv, props, beanClass, ILOAD, 4);
        }
        // and if no match, generate exception:
        _generateException(mv, beanClass, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }

    private static void _addLongSetters(ClassWriter cw, List<SettableLongMethodProperty> props,
            String beanClass)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "longSetter", "(Ljava/lang/Object;IJ)V", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClass);
        mv.visitVarInsn(ASTORE, 5); // 3 args (0 == this), so 4 is the first local var slot
        if (props.size() <= 4) {
            _addSettersUsingIf(mv, props, beanClass, LLOAD, 5);
        } else {
            _addSettersUsingSwitch(mv, props, beanClass, LLOAD, 5);
        }
        // and if no match, generate exception:
        _generateException(mv, beanClass, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }
    
    /*
    /**********************************************************
    /* Code generation; field-based getters
    /**********************************************************
     */
    
    private static void _addIntFields(ClassWriter cw, List<SettableIntFieldProperty> props,
            String beanClass)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "intField", "(Ljava/lang/Object;II)V", /*generic sig*/null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClass);
        mv.visitVarInsn(ASTORE, 4); // 3 args (0 == this), so 4 is the first local var slot

        // Ok; minor optimization, less than 4 accessors, just do IFs; over that, use switch
        if (props.size() < 4) {
            _addFieldsUsingIf(mv, props, beanClass, ILOAD, 4);
        } else {
            _addFieldsUsingSwitch(mv, props, beanClass, ILOAD, 4);
        }
        // and if no match, generate exception:
        _generateException(mv, beanClass, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }

    private static void _addLongFields(ClassWriter cw, List<SettableLongFieldProperty> props,
            String beanClass)
    {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "longField", "(Ljava/lang/Object;IJ)V", null, null);
        mv.visitCode();
        // first: cast bean to proper type
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, beanClass);
        mv.visitVarInsn(ASTORE, 5); // 3 args (0 == this), BUT longs use up 2 slots; hence 5
        if (props.size() < 4) {
            _addFieldsUsingIf(mv, props, beanClass, LLOAD, 5);
        } else {
            _addFieldsUsingSwitch(mv, props, beanClass, LLOAD, 5);
        }
        // and if no match, generate exception:
        _generateException(mv, beanClass, props.size());
        mv.visitMaxs(0, 0); // don't care (real values: 1,1)
        mv.visitEnd();
    }
    
    /*
    /**********************************************************
    /* Helper methods, method accessor creation
    /**********************************************************
     */

    private static <T extends OptimizedSettableBeanProperty<T>> void _addSettersUsingIf(MethodVisitor mv,
            List<T> props, String beanClass, int loadValueCode, int beanIndex)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)
        Label next = new Label();
        // first: check if 'index == 0'
        mv.visitJumpInsn(IFNE, next); // "if not zero, goto L (skip stuff)"
        // call first getter:
        mv.visitVarInsn(ALOAD, beanIndex); // load local for cast bean
        mv.visitVarInsn(loadValueCode, 3);
        Method method = (Method) (props.get(0).getMember().getMember());
        Class<?> type = method.getParameterTypes()[0];
        mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, method.getName(), "("+Type.getDescriptor(type)+")V");
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
            type = method.getParameterTypes()[0];
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, method.getName(), "("+Type.getDescriptor(type)+")V");
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(next);
    }        

    private static <T extends OptimizedSettableBeanProperty<T>> void _addSettersUsingSwitch(MethodVisitor mv,
            List<T> props, String beanClass, int loadValueCode, int beanIndex)
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
            Class<?> type = method.getParameterTypes()[0];
            mv.visitMethodInsn(INVOKEVIRTUAL, beanClass, method.getName(), "("+Type.getDescriptor(type)+")V");
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(defaultLabel);
    }        

    private static <T extends OptimizedSettableBeanProperty<T>> void _addFieldsUsingIf(MethodVisitor mv,
            List<T> props, String beanClass, int loadValueCode, int beanIndex)
    {
        mv.visitVarInsn(ILOAD, 2); // load second arg (index)
        Label next = new Label();
        // first: check if 'index == 0'
        mv.visitJumpInsn(IFNE, next); // "if not zero, goto L (skip stuff)"

        // first field accessor
        mv.visitVarInsn(ALOAD, beanIndex); // load local for cast bean
        mv.visitVarInsn(loadValueCode, 3);
        AnnotatedField field = (AnnotatedField) props.get(0).getMember();
        mv.visitFieldInsn(PUTFIELD, beanClass, field.getName(), Type.getDescriptor(field.getRawType()));
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
            mv.visitFieldInsn(PUTFIELD, beanClass, field.getName(), Type.getDescriptor(field.getRawType()));
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(next);
    }        

    private static <T extends OptimizedSettableBeanProperty<T>> void _addFieldsUsingSwitch(MethodVisitor mv,
            List<T> props, String beanClass, int loadValueCode, int beanIndex)
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
            mv.visitFieldInsn(PUTFIELD, beanClass, field.getName(), Type.getDescriptor(field.getRawType()));
            mv.visitInsn(RETURN);
        }
        mv.visitLabel(defaultLabel);
    }        
}
