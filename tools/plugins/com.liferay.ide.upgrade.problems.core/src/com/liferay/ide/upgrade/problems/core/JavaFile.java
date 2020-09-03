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

package com.liferay.ide.upgrade.problems.core;

import java.util.List;

/**
 * @author Gregory Amerson
 */
public interface JavaFile extends SourceFile {

	public List<FileSearchResult> findCatchExceptions(String[] exceptions);

	public List<FileSearchResult> findImplementsInterface(String interfaceName);

	public FileSearchResult findImport(String importName);

	public List<FileSearchResult> findImports(String[] imports);

	public List<FileSearchResult> findMethodDeclaration(String name, String[] params, String returnType);

	public List<FileSearchResult> findMethodInvocations(
		String typeHint, String expressionValue, String methodName, String[] methodParamTypes);

	public FileSearchResult findPackage(String packageName);

	public List<FileSearchResult> findServiceAPIs(String[] serviceApiPrefixes);

	public List<FileSearchResult> findSuperClass(String superClassName);

}