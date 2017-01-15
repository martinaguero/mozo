package org.trimatek.mozo.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download implements Command {

	private static final int BUFFER_SIZE = 4096;

	public Object exec(String path) throws IOException {
		String result = "El archivo no pudo ser descargado";
		URL url = new URL(path);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();
		System.out.println("Response code: " + responseCode);
		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			// String disposition =
			// httpConn.getHeaderField("Content-Disposition");
			// String contentType = httpConn.getContentType();
			// int contentLength = httpConn.getContentLength();

			/*
			 * if (disposition != null) { // extracts file name from header
			 * field int index = disposition.indexOf("filename="); if (index >
			 * 0) { fileName = disposition.substring(index + 10,
			 * disposition.length() - 1); } } else {
			 */
			// extracts file name from URL
			fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
			// }
			/*
			 * System.out.println("Content-Type = " + contentType);
			 * System.out.println("Content-Disposition = " + disposition);
			 * System.out.println("Content-Length = " + contentLength);
			 * System.out.println("fileName = " + fileName);
			 */
			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = System.getProperty("user.dir") + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				System.out.println("descargando....");
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			result = "Archivo descargado en: " + saveFilePath;
		}
		httpConn.disconnect();
		return result;
	}
}
