package org.trimatek.mozo.bytecoder.test;

import java.io.FileInputStream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.JavaClass;

public class DependencyEmitter extends EmptyVisitor {

	  private JavaClass javaClass;
	  
	  public DependencyEmitter(JavaClass javaClass) {
	    this.javaClass = javaClass;
	  }
	  
	  @Override
	  public void visitConstantClass(ConstantClass obj) {
	    ConstantPool cp = javaClass.getConstantPool();
	    String bytes = obj.getBytes(cp);
	    System.out.println(bytes);
	  }
	  
	  public static void main(String[] args) throws Exception {
		  ClassParser cp = new ClassParser(new FileInputStream("F:\\Temp\\mozo\\Signature.class"),
					"org.apache.bcel.classfile.Signature");
		  
		  
		  JavaClass javaClass = cp.parse();
	    DependencyEmitter visitor = new DependencyEmitter(javaClass);
	    DescendingVisitor classWalker = new DescendingVisitor(javaClass, visitor);
	    classWalker.visit();
	  }

	}