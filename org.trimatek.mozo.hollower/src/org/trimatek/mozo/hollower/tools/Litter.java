package org.trimatek.mozo.hollower.tools;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RETURN;
import org.trimatek.mozo.hollower.Context;

public class Litter {

	public JavaClass buildLiteVersion(JavaClass javaClass, Context ctx)
			throws ClassNotFoundException {

		ClassGen classGen = new ClassGen(javaClass.getClassName(), javaClass
				.getSuperclassName(), ctx.OUTPUT_DIR
				+ javaClass.getClassName(), javaClass.getAccessFlags(),
				javaClass.getInterfaceNames());
		ConstantPoolGen constantPoolGen = classGen.getConstantPool();

		Method[] methods = javaClass.getMethods();
		for (Method method : methods) {
			if (method.isPublic()) {
				MethodGen mg = generateMethod(method, constantPoolGen,
						javaClass.getClassName());
				classGen.addMethod(mg.getMethod());
			}
		}
		for (Field field : javaClass.getFields()) {
			if (field.isPublic()) {
				FieldGen fg = generateField(field, constantPoolGen);
				classGen.addField(fg.getField());
			}
		}

		return classGen.getJavaClass();
	}

	private MethodGen generateMethod(Method method,
			ConstantPoolGen constantPoolGen, String className) {

		InstructionList instructionList = new InstructionList();
		instructionList.append(new RETURN());

		// TODO Pendiente obtener el nombre original de los argumentos
		MethodGen methodGen = new MethodGen(method.getAccessFlags(),
				method.getReturnType(), method.getArgumentTypes(), null,
				method.getName(), className, instructionList, constantPoolGen);

		if (method.getExceptionTable() != null) {
			for (String eName : method.getExceptionTable().getExceptionNames()) {
				methodGen.addException(eName);
			}
		}

		return methodGen;
	}

	private FieldGen generateField(Field field, ConstantPoolGen constantPoolGen) {
		FieldGen fg;
		int flags = Constants.ACC_PUBLIC;
		if (field.isStatic()) {
			flags = flags | Constants.ACC_STATIC;
			if (field.isFinal()) {
				flags = flags | Constants.ACC_FINAL;
			}
			fg = new FieldGen(flags, field.getType(), field.getName(),
					constantPoolGen);
		} else {
			fg = new FieldGen(field, constantPoolGen);
		}
		return fg;
	}

	/*
	 * private String[] getArgNames(Method method) { List<String> argNames = new
	 * ArrayList<String>(); if (!method.getName().equals("<init>")) {
	 * LocalVariable[] locals = method.getLocalVariableTable()
	 * .getLocalVariableTable(); for (LocalVariable lv : locals) { if
	 * (lv.getStartPC() == 0 && lv.getIndex() >= 0 &&
	 * !lv.getName().equals("this")) { argNames.add(lv.getName()); } } } return
	 * argNames.toArray(new String[argNames.size()]); }
	 */
}
