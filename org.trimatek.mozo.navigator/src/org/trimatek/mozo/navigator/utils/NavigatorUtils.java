package org.trimatek.mozo.navigator.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trimatek.mozo.catalog.model.Class;

public class NavigatorUtils {

	public static Set<String> removeRepeated(Set<String> references, Set<Class> classes) {
		Set<String> repeated = new HashSet<String>();
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

	public static List<String> toUniques(List<String> refs) {
		Set<String> refsUniques = new HashSet<String>(refs);
		refs.clear();
		refs.addAll(refsUniques);
		return refs;
	}

}
