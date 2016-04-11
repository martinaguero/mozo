package org.trimatek.mozo.bytecoder.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;

public class ClassVisitor {

	public static List<String> visit(JavaClass javaClass)
			throws ClassFormatException, IOException {
		Visitor visitor = new Visitor(javaClass);
		DescendingVisitor classWalker = new DescendingVisitor(javaClass,
				visitor);
		classWalker.visit();
		return visitor.getClasses();
	}
}

class Visitor extends EmptyVisitor {

	private JavaClass javaClass;
	private List<String> classes = new ArrayList<String>();

	public List<String> getClasses() {
		return classes;
	}

	public Visitor(JavaClass javaClass) {
		this.javaClass = javaClass;
	}

	public void visitConstantUtf8(ConstantUtf8 obj) {
		ConstantPool cp = javaClass.getConstantPool();
		String s = obj.getBytes();
		classes.add(s);
	}

}