package org.trimatek.mozo.navigator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Class;

public class NavigatorUtils {

	public static List<String> removeRepeated(List<String> references,
			Set<Class> classes) {
		List<String> repeated = new ArrayList<String>();
		for (String className : references) {
			if (contains(classes, className)) {
				repeated.add(className);
			}
		}
		references.removeAll(repeated);
		return references;
	}

	private static boolean contains(Set<Class> classes, String className) {
		for (Class clazz : classes) {
			if (clazz.getClassName().equals(className)) {
				return true;
			}
		}
		return false;
	}

}