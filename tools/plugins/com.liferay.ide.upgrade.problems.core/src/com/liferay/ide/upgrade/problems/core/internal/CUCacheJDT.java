/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.problems.core.CUCache;

import java.io.File;

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = "type=java", service = CUCache.class)
public class CUCacheJDT extends BaseCUCache implements CUCache<CompilationUnit> {

	@Override
	public CompilationUnit getCU(File file, Supplier<char[]> javaSource) {
		CompilationUnit retval = null;

		synchronized (_lock) {
			Long lastModified = _fileModifiedTimeMap.get(file);

			if ((lastModified != null) && lastModified.equals(file.lastModified())) {
				WeakReference<CompilationUnit> reference = _cuMap.get(file);

				retval = reference.get();
			}

			if (retval == null) {
				char[] chars = javaSource.get();

				CompilationUnit newAst = _createCompilationUnit(file.getName(), chars);

				_fileModifiedTimeMap.put(file, file.lastModified());
				_cuMap.put(file, new WeakReference<CompilationUnit>(newAst));

				retval = newAst;
			}
		}

		return retval;
	}

	@Override
	public void unget(File file) {
		synchronized (_lock) {
			_fileModifiedTimeMap.remove(file);
			_cuMap.remove(file);
		}
	}

	private CompilationUnit _createCompilationUnit(String unitName, char[] javaSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS11);

		Map<String, String> options = JavaCore.getOptions();

		JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);

		parser.setCompilerOptions(options);

		// setUnitName for resolve bindings

		parser.setUnitName(unitName);

		String[] sources = {""};
		String[] classpath = {""};

		// setEnvironment for resolve bindings even if the args is empty

		parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, true);

		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		parser.setSource(javaSource);
		parser.setIgnoreMethodBodies(false);

		return (CompilationUnit)parser.createAST(null);
	}

	private static final Map<File, WeakReference<CompilationUnit>> _cuMap = new HashMap<>();
	private static final Map<File, Long> _fileModifiedTimeMap = new HashMap<>();
	private static final Object _lock = new Object();

}