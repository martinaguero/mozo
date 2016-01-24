package org.trimatek.mozo.scanner.utils;

import static org.trimatek.mozo.scanner.Config.MVN_METADATA;
import static org.trimatek.mozo.scanner.Config.MVN_POM;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtils {

	public static String getDirectory(String line) {
		int count = org.apache.commons.lang3.StringUtils
				.countMatches(line, "/");
		return (count > 0 && count < 2) ? line.substring(0, line.indexOf("/"))
				: null;
	}

	public static String getMVNEntity(String line) {
		String[] parts = line.split(" ");
		for (String part : parts) {
			if (part.equals(MVN_METADATA) || part.endsWith(MVN_POM)) {
				return part.trim();
			}
		}
		return null;
	}

	public static List<String> reverse(BufferedReader br) throws IOException {
		String line;
		List<String> reversed = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			reversed.add(line);
		}
		Collections.reverse(reversed);
		return reversed;
	}
	
	public static String getRepositoryId(String path) {
		String[] dirs = path.split("/");
		return dirs[dirs.length - 1];
	}

}
