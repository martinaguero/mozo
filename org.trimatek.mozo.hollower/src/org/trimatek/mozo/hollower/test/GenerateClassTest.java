package org.trimatek.mozo.hollower.test;

import java.io.IOException;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.Type;

public class GenerateClassTest {

    public static void main(String[] args) {
        System.out.println("Generating Class");
        
        //Create a ClassGen for our brand new class.
        ClassGen classGen=new ClassGen("SyntheticClass", "java.lang.Object", "d:\\Temp\\mozo\\SyntheticClass.java", Constants.ACC_PUBLIC, null);
        
        //Get a reference to the constant pool of the class. This will be modified as we add methods, fields etc. Note that it already constains
        //a few constants.
        ConstantPoolGen constantPoolGen=classGen.getConstantPool();
        
        //The list of instructions for a method. 
        InstructionList instructionList=new InstructionList();
        
//        //Add the appropriate instructions.
//        
//        //Get the reference to static field out in class java.lang.System.
//        instructionList.append(new GETSTATIC(constantPoolGen.addFieldref("java.lang.System", "out", "Ljava/io/PrintStream;")));
//        
//        //Load the constant
//        instructionList.append(new LDC(constantPoolGen.addString(" You are a real geek!")));
//        
//        //Invoke the method.
//        instructionList.append(new INVOKEVIRTUAL(constantPoolGen.addMethodref("java.io.PrintStream", "println", "(Ljava/lang/String;)V")));
//        
        //Return from the method
        instructionList.append(new RETURN());
        
//        MethodGen methodGen=new MethodGen(Constants.ACC_PUBLIC|Constants.ACC_STATIC, Type.VOID, new Type[]{new ArrayType(Type.STRING, 1)}, new String[]{"args"}, "main", "SyntheticClass", instructionList, constantPoolGen);
//        
//        methodGen.setMaxLocals();//Calculate the maximum number of local variables. 
//        methodGen.setMaxStack();//Very important: must calculate the maximum size of the stack.
//        
//        classGen.addMethod(methodGen.getMethod()); //Add the method to the class
        
        //Print a few things.
        System.out.println("********Constant Pool**********");
        System.out.println(constantPoolGen.getFinalConstantPool());
        System.out.println("********Method**********");
//        System.out.println(methodGen);
        System.out.println("********Instruction List**********");
        System.out.println(instructionList);
        

        //Now generate the class
        JavaClass javaClass=classGen.getJavaClass();
        try {
            //Write the class byte code into a file
            javaClass.dump("SyntheticClass.class");
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //That's it.

    }

}