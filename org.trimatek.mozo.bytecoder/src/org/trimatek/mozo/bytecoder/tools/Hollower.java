package org.trimatek.mozo.bytecoder.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
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
		javaClass = hollowMethods(javaClass);
		return hollowConstantPool(javaClass);
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
			System.out.println("quitando instrucciones de: "
					+ javaClass.getClassName());
			System.out.println("método :" + mg.getName());
			if (mg.getInstructionList() != null
					&& mg.getInstructionList().size() > 3) {
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

	private JavaClass hollowConstantPool(JavaClass javaClass) {
		ConstantPool cp = javaClass.getConstantPool();
		Constant[] constants = cp.getConstantPool();
		Constant[] newconst = new Constant[constants.length];
		List<Integer> indexes = loadStringIndexes(constants);
		// newconst = hollowStringLiterals(constants, indexes);
		// newconst = hollowNameAndType(constants);
		// newconst = hollowAll(constants);
		newconst = hollowClass(constants);
		cp.setConstantPool(newconst);
		javaClass.setConstantPool(cp);
		return javaClass;
	}

	private List<Integer> loadStringIndexes(Constant[] constants) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < constants.length; i++) {
			if (ConstantString.class.isInstance(constants[i])) {
				ConstantString cs = (ConstantString) constants[i];
				System.out.println("IDX: " + cs.getStringIndex());
				indexes.add(new Integer(cs.getStringIndex()));
			}
		}
		return indexes;
	}

	private Constant[] hollowStringLiterals(Constant[] constants,
			List<Integer> indexes) {
		Constant[] newconst = new Constant[constants.length];
		for (int i = 0; i < constants.length; i++) {
			if (ConstantUtf8.class.isInstance(constants[i])) {
				ConstantUtf8 cu = (ConstantUtf8) constants[i];
				System.out.println(cu.toString());
				System.out.println("bytes:" + cu.getBytes());
				ConstantUtf8 newcu = null;
				if (indexes.contains(new Integer(i))) {
					newcu = new ConstantUtf8("");
				} else {
					newcu = new ConstantUtf8(cu.getBytes());
				}
				newconst[i] = newcu;
			} else {
				newconst[i] = constants[i];
			}
		}
		return newconst;
	}

	private Constant[] hollowClass(Constant[] constants) {
		Constant[] newconst = new Constant[constants.length];
		for (int i = 0; i < constants.length; i++) {
			if (constants[i] != null)
				newconst[i] = getInstance(constants[i]);
		}
		return newconst;
	}

	private Constant getInstance(Constant constant) {
		System.out.println(constant.getClass().getName());
		switch (constant.getClass().getName()) {
		case "org.apache.bcel.classfile.ConstantClass":
			System.out.println("es class");
			int idx = ((ConstantClass) constant).getNameIndex();
			return new ConstantClass((ConstantClass)constant);
		case "org.apache.bcel.classfile.ConstantUtf8":
			System.out.println("es UTF8");
			return new ConstantUtf8("");
		case "org.apache.bcel.classfile.ConstantString":
			System.out.println("es string");
			int string_idx = ((ConstantString) constant).getStringIndex();
			return new ConstantString(string_idx);
		case "org.apache.bcel.classfile.ConstantMethodref":
			System.out.println("es method ref");
			int class_index = ((ConstantMethodref) constant).getClassIndex();
			int name_and_type_index = ((ConstantMethodref) constant)
					.getNameAndTypeIndex();
			return new ConstantMethodref(class_index, name_and_type_index);
		case "org.apache.bcel.classfile.ConstantNameAndType":
			System.out.println("es name and type");
			int name_index = ((ConstantNameAndType) constant).getNameIndex();
			int signature_index = ((ConstantNameAndType) constant)
					.getSignatureIndex();
			return new ConstantNameAndType(name_index, signature_index);
		case "org.apache.bcel.classfile.ConstantFieldref":
			System.out.println("es field ref");
			int ci = ((ConstantFieldref) constant).getClassIndex();
			int nti = ((ConstantFieldref) constant).getNameAndTypeIndex();
			return new ConstantFieldref(ci, nti);
		case "org.apache.bcel.classfile.ConstantInterfaceMethodref":
			System.out.println("es interface method ref");
			int class_idx = ((ConstantInterfaceMethodref) constant)
					.getClassIndex();
			int name_type_idx = ((ConstantInterfaceMethodref) constant)
					.getNameAndTypeIndex();
			return new ConstantInterfaceMethodref(class_idx, name_type_idx);
		default:
			System.out.println("NINGUNA");
			break;
		}
		return null;
	}
}
