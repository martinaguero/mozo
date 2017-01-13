package org.trimatek.mozo.dock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.trimatek.mozo.dock.Config.repositorypath;

import org.apache.commons.io.IOUtils;
import org.trimatek.remotezip.model.RemoteZipEntry;
import org.trimatek.remotezip.service.RemoteZipService;
import org.trimatek.remotezip.service.impl.RemoteZipServiceImpl;
import org.trimatek.remotezip.tools.RemoteZipFile;

public class RemoteZipTest {

	public static void main(String[] args) throws IOException {

		RemoteZipService remoteZip = new RemoteZipServiceImpl();
		RemoteZipFile zip = remoteZip.load(repositorypath + "/com.greetings.jar", null);

		int c = 0;
		for (RemoteZipEntry e : zip.getEntries()) {
			System.out.println("[" + c++ + "]" + e.getName() + " cmethod: " + e.getMethod() + " crc: " + e.getCrc()
					+ " zsize: " + e.getSize() + " comp size:  " + e.getCompressedSize() + " time: " + e.getTime());
			if (e.getName().equals("module-info.class")) {
				InputStream inputStream = remoteZip.getEntryStream(e, zip);

				File file = new File("d:\\Temp\\module-info.class");
				OutputStream outputStream = new FileOutputStream(file);
				IOUtils.copy(inputStream, outputStream);
				outputStream.close();

			}

		}

	}

}
