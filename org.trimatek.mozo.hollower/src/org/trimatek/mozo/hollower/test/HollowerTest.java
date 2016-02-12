package org.trimatek.mozo.hollower.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.trimatek.mozo.hollower.service.HollowerService;
import org.trimatek.mozo.hollower.service.impl.HollowerServiceImpl;

public class HollowerTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HollowerService hs = new HollowerServiceImpl();
		String path = "F:\\Temp\\mozo\\deep.jar";
		FileInputStream fi = new FileInputStream(new File(path));
		InputStream input = hs.hollow(fi,path.substring(path.lastIndexOf("\\")+1));
		
	    byte[] buffer = new byte[input.available()];
	    input.read(buffer);
	 
	    File targetFile = new File("f:\\Temp\\mozo\\cliente\\martin.jar");
	    OutputStream outStream = new FileOutputStream(targetFile);
	    outStream.write(buffer);
		outStream.close();
		
	}

}
