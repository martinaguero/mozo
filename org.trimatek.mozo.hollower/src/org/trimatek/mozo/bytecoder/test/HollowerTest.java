package org.trimatek.mozo.bytecoder.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.bytecoder.service.impl.BytecodeServiceImpl;
import org.trimatek.mozo.catalog.model.Version;

public class HollowerTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		BytecodeService hs = new BytecodeServiceImpl();
		String path = "F:\\Temp\\mozo\\originales\\standard.jar";
		File jarFile = new File(path);
		Version version = new Version();
		version.setData(jarFile);
		version = hs.buildJarProxy(version);
		
		System.out.println(version.getDataProxy().getName());
		
//		InputStream input = hs.buildJarProxy(version);
//	    byte[] buffer = new byte[input.available()];
//	    input.read(buffer);
//	 
//	    File targetFile = new File("f:\\Temp\\mozo\\ahuecados\\starndar-ahuec.jar");
//	    OutputStream outStream = new FileOutputStream(targetFile);
//	    outStream.write(buffer);
//		outStream.close();
		
	}

}
