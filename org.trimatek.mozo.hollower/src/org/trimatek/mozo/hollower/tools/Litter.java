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
import org.trimatek.mozo.hollower.Config;

public class Litter {

	public JavaClass buildLiteVersion(JavaClass javaClass)
			throws ClassNotFoundException {

		ClassGen classGen = new ClassGen(javaClass.getClassName(), javaClass
				.getSuperClass().getClassName(), Config.OUTPUT_DIR
				+ javaClass.getClassName(), Constants.ACC_PUBLIC,
				javaClass.getInterfaceNames());
		ConstantPoolGen constantPoolGen = classGen.getConstantPool();
		
		Method[] methods = javaClass.getMethods();
		for (Method method : methods) {
			if (method.isPublic()) {
				MethodGen mg = generateMethod(method, constantPoolGen, javaClass.getClassName());
				classGen.addMethod(mg.getMethod());
			}
		}
		Field [] fields = javaClass.getFields();
		for (Field field : fields) {
			if(field.isPublic()){
				FieldGen fg = new FieldGen(field, constantPoolGen);
				classGen.addField(fg.getField());
			}
		}

		return classGen.getJavaClass();
	}

	private MethodGen generateMethod(Method method,
			ConstantPoolGen constantPoolGen, String className) {

		InstructionList instructionList = new InstructionList();
		instructionList.append(new RETURN());

		// TODO agregar el nombre de los métodos originales
		MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(), null, method.getName(),
				className, instructionList, constantPoolGen);

	return methodGen;
	}

}
