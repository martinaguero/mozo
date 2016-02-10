package org.trimatek.mozo.hollower.tools;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.trimatek.mozo.hollower.Config;

public class Litter {

	public JavaClass buildLiteVersion(JavaClass javaClass)
			throws ClassNotFoundException {

		ClassGen classGen = new ClassGen(javaClass.getClassName(), javaClass
				.getSuperClass().getClassName(), Config.OUTPUT_DIR
				+ javaClass.getClassName(), Constants.ACC_PUBLIC,
				javaClass.getInterfaceNames());

		//ConstantPoolGen constantPoolGen = classGen.getConstantPool();

		return classGen.getJavaClass();
	}

}
