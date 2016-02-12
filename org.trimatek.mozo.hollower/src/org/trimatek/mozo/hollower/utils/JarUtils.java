package org.trimatek.mozo.hollower.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipInputStream;

import org.trimatek.mozo.hollower.Config;
import org.trimatek.mozo.hollower.Context;

public class JarUtils {

	public static List<String> listClasses(JarInputStream jarFile)
			throws IOException {
		List<String> classes = new ArrayList<String>();
		JarEntry jarEntry;
		String className;
		while (true) {
			jarEntry = jarFile.getNextJarEntry();
			if (jarEntry == null) {
				break;
			}
			className = jarEntry.getName();
			if ((className.endsWith(".class"))) {
				classes.add(className.substring(0, className.lastIndexOf('.')));
			}
		}
		jarFile.close();
		return classes;
	}

	public static String getJarName(String jarPath) {
		return jarPath.substring(jarPath.lastIndexOf("\\") + 1,
				jarPath.lastIndexOf("."));
	}

	public static File extractFile(ZipInputStream zipIn, String filePath,
			Context ctx) throws IOException {
		String dirPath = "";
		if (filePath.contains("/")) {
			dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
		}
		new File(ctx.TEMP_DIR + dirPath).mkdirs();
		File file = new File(ctx.TEMP_DIR + filePath);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		byte[] bytesIn = new byte[Config.BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
		return file;
	}

	// public static OutputStream buildHollowedJar(Context ctx){
	// return null;
	// }

	public static OutputStream buildHollowedJar(Context ctx)
			throws FileNotFoundException, IOException {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,
				"1.0");
		JarOutputStream target = new JarOutputStream(new FileOutputStream(
				"f:\\Temp\\mozo\\prueba\\output.jar"), manifest);
		add(new File(ctx.OUTPUT_DIR), target, ctx);
		target.close();

		return null;
	}

	private static void add(File source, JarOutputStream target, Context ctx)
			throws IOException {
		BufferedInputStream in = null;
		try {
			if (source.isDirectory()) {
				String name = source.getPath().replace("\\", "/");
				if (!name.isEmpty()) {
					if (!name.endsWith("/"))
						name += "/";
					JarEntry entry = new JarEntry(putMask(name, ctx.OUTPUT_DIR));
					if (!entry.getName().equals("")) {
						entry.setTime(source.lastModified());
						target.putNextEntry(entry);
						target.closeEntry();
					}
				}
				for (File nestedFile : source.listFiles())
					add(nestedFile, target, ctx);
				return;
			}

			JarEntry entry = new JarEntry(putMask(
					source.getPath().replace("\\", "/"), ctx.OUTPUT_DIR));
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(new FileInputStream(source));

			byte[] buffer = new byte[1024];
			while (true) {
				int count = in.read(buffer);
				if (count == -1)
					break;
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		} finally {
			if (in != null)
				in.close();
		}
	}

	private static String putMask(String dir, String mask) {
		return dir.replace(mask.replace("\\", "/"), "");
	}

}
