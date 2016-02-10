package org.trimatek.mozo.hollower.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HollowerService {

	public OutputStream hollow(InputStream inputStream) throws IOException, ClassNotFoundException;

}
