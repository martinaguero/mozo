package org.trimatek.mozo.hollower.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.JarInputStream;

import org.trimatek.mozo.hollower.service.HollowerService;
import org.trimatek.mozo.hollower.utils.JarUtils;

public class HollowerServiceImpl implements HollowerService {

	@Override
	public OutputStream hollow(InputStream inputStream) {
		List<String> classes = null;
		try {
			classes = JarUtils.listClasses(new JarInputStream(inputStream));
			for (String string : classes) {
				System.out.println(string);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
