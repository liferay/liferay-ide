/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.CUCache;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"type=java"
	},
	service = CUCache.class
)
public class CUCacheJDT extends BaseCUCache implements CUCache<CompilationUnit> {

	private static final Map<File, WeakReference<CompilationUnit>> _map = new WeakHashMap<>();

	@Override
	public CompilationUnit getCU(File file, char[] javaSource) {
		synchronized (_map) {
			WeakReference<CompilationUnit> astRef = _map.get(file);

			if (astRef == null || astRef.get() == null) {
				final CompilationUnit newAst = createCompilationUnit(file.getName(), javaSource);

				_map.put(file, new WeakReference<CompilationUnit>(newAst));

				return newAst;
			}
			else {
				return astRef.get();
			}
		}
	}

	@Override
	public void unget(File file) {
		synchronized (_map) {
			_map.remove(file);
		}
	}

	private CompilationUnit createCompilationUnit(String unitName, char[] javaSource) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		Map<String, String> options = JavaCore.getOptions();

		JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);

		parser.setCompilerOptions(options);

		//setUnitName for resolve bindings
		parser.setUnitName(unitName);

		String[] sources = { "" };
		String[] classpath = { "" };
		//setEnvironment for resolve bindings even if the args is empty
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);

		parser.setResolveBindings(true);
		parser.setStatementsRecovery(true);
		parser.setBindingsRecovery(true);
		parser.setSource(javaSource);
		parser.setIgnoreMethodBodies(false);

		return (CompilationUnit)parser.createAST(null);
	}
}
