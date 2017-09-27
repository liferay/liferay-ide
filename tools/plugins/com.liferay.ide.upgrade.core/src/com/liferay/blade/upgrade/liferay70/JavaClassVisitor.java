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

package com.liferay.blade.upgrade.liferay70;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class JavaClassVisitor extends ASTVisitor
{

	List<MethodDeclaration> methods = new ArrayList<>();
	List<FieldDeclaration> fields = new ArrayList<>();
	List<ImportDeclaration> imports = new ArrayList<>();

	@Override
	public boolean visit( MethodDeclaration node )
	{
		methods.add( node );

		return super.visit( node );
	}

	public List<MethodDeclaration> getMethods()
	{
		return methods;
	}

	@Override
	public boolean visit( FieldDeclaration node )
	{
		fields.add( node );

		return super.visit( node );
	}

	public List<FieldDeclaration> getFields()
	{
		return fields;
	}

	@Override
	public boolean visit( ImportDeclaration node )
	{
		imports.add( node );

		return super.visit( node );
	}

	public List<ImportDeclaration> getImports()
	{
		return imports;
	}
}