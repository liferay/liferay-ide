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

package com.liferay.ide.project.ui.migration;

import java.io.File;

import java.lang.ref.WeakReference;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings("deprecation")
public class CUCache {

	public static CompilationUnit getCU(File file, char[] javaSource) {
		synchronized (_map) {
			WeakReference<CompilationUnit> astRef = _map.get(file);

			if ((astRef == null) || (astRef.get() == null)) {
				final CompilationUnit newAst = createCompilationUnit(file.getName(), javaSource);

				_map.put(file, new WeakReference<CompilationUnit>(newAst));

				return newAst;
			}
			else {
				return astRef.get();
			}
		}
	}

	public static void unget(File file) {
		synchronized (_map) {
			_map.remove(file);
		}
	}

	private static CompilationUnit createCompilationUnit(String unitName, char[] javaSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

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

	private static final Map<File, WeakReference<CompilationUnit>> _map = new WeakHashMap<>();

}