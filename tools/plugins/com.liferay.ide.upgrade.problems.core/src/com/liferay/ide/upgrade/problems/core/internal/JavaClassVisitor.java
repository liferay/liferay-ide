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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * @author Gregory Amerson
 */
public class JavaClassVisitor extends ASTVisitor {

	public List<FieldDeclaration> getFields() {
		return _fields;
	}

	public List<ImportDeclaration> getImports() {
		return _imports;
	}

	public List<MethodDeclaration> getMethods() {
		return _methods;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		_fields.add(node);

		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		_imports.add(node);

		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		_methods.add(node);

		return super.visit(node);
	}

	private List<FieldDeclaration> _fields = new ArrayList<>();
	private List<ImportDeclaration> _imports = new ArrayList<>();
	private List<MethodDeclaration> _methods = new ArrayList<>();

}