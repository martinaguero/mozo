package org.trimatek.mozo.hollower.service;

import java.io.IOException;
import java.io.InputStream;

public interface HollowerService {

	public InputStream hollow(InputStream inputStream, String jarName) throws IOException, ClassNotFoundException;

}
