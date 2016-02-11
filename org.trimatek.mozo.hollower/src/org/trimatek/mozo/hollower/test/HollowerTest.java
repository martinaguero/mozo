package org.trimatek.mozo.hollower.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.trimatek.mozo.hollower.service.HollowerService;
import org.trimatek.mozo.hollower.service.impl.HollowerServiceImpl;

public class HollowerTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HollowerService hs = new HollowerServiceImpl();
		FileInputStream fi = new FileInputStream(new File("F:\\Temp\\mozo\\spring.jar"));
		hs.hollow(fi);

	}

}
