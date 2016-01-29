package org.trimatek.mozo.hollower.tools;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;

public class Hollower {

	public JavaClass hollow(JavaClass javaClass) {
		javaClass = deleteNonPublicFields(javaClass);
		javaClass = deleteNonPublicMethods(javaClass);
		return hollowMethods(javaClass);
	}

	private JavaClass deleteNonPublicFields(JavaClass javaClass) {
		Field[] fields = javaClass.getFields();
		ClassGen cg = new ClassGen(javaClass);
		for (Field field : fields) {
			if (field.isPrivate() || field.isProtected()) {
				cg.removeField(field);
			}
		}
		return cg.getJavaClass();
	}

	private JavaClass deleteNonPublicMethods(JavaClass javaClass) {
		Method[] methods = javaClass.getMethods();
		ClassGen cg = new ClassGen(javaClass);
		for (Method method : methods) {
			if (method.isPrivate() || method.isProtected()) {
				cg.removeMethod(method);
			}
		}
		return cg.getJavaClass();
	}

	private JavaClass hollowMethods(JavaClass javaClass) {
		Method[] methods = javaClass.getMethods();
		ConstantPoolGen cp = new ConstantPoolGen(javaClass.getConstantPool());
		for (int i = 0; i < methods.length; i++) {
			MethodGen mg = new MethodGen(methods[i], javaClass.getClassName(),
					cp);
			System.out.println("quitando instrucciones de: " + javaClass.getClassName());
			System.out.println("método :" + mg.getName());
			if (mg.getInstructionList()!=null && mg.getInstructionList().size() > 3) {
				Method stripped = deleteInstructions(mg);
				if (stripped != null)
					methods[i] = stripped;
			}
		}
		javaClass.setConstantPool(cp.getFinalConstantPool());
		return javaClass;
	}

	private Method deleteInstructions(MethodGen mg) {
		InstructionList il = mg.getInstructionList();
		Method m = null;
		InstructionHandle[] handles = il.getInstructionHandles();
		InstructionHandle ini = handles[0];
		InstructionHandle fin = handles[handles.length - 2];
		InstructionHandle ult = fin.getNext();
		try {
			il.delete(ini, fin);
		} catch (TargetLostException e) {
			InstructionHandle[] targets = e.getTargets();
			for (int i = 0; i < targets.length; i++) {
				InstructionTargeter[] targeters = targets[i].getTargeters();

				for (int j = 0; j < targeters.length; j++)
					targeters[j].updateTarget(targets[i], ult);
			}
		}
		m = mg.getMethod();
		il.dispose();
		return m;
	}

}
