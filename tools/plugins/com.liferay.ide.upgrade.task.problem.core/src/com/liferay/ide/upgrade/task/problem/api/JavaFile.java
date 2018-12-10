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

package com.liferay.ide.upgrade.task.problem.api;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public interface JavaFile extends SourceFile {

	public List<SearchResult> findCatchExceptions(String[] exceptions);

	public List<SearchResult> findImplementsInterface(String interfaceName);

	public SearchResult findImport(String importName);

	public List<SearchResult> findImports(String[] imports);

	public List<SearchResult> findMethodDeclaration(String name, String[] params, String returnType);

	public List<SearchResult> findMethodInvocations(
		String typeHint, String expressionValue, String methodName, String[] methodParamTypes);

	public SearchResult findPackage(String packageName);

	public List<SearchResult> findServiceAPIs(String[] serviceApiPrefixes);

	public List<SearchResult> findSuperClass(String superClassName);

}