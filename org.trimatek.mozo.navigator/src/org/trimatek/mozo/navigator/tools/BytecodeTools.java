package org.trimatek.mozo.navigator.tools;

import java.util.HashSet;
import java.util.Set;

import org.trimatek.mozo.bytecoder.service.BytecodeService;
import org.trimatek.mozo.catalog.model.Class;

public class BytecodeTools {

	public static Set<String> findReferences(Set<Class> classes, String namespace, BytecodeService bytecodeService)
			throws Exception {
		Set<String> references = new HashSet<String>();
		for (Class clazz : classes) {
			references.addAll(bytecodeService.listReferences(clazz.getClassName(), clazz.getBytecode(), namespace));
		}
		return references;
	}

}
