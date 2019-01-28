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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.eclipse.provider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.CUCache;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.JavaFile;
import com.liferay.ide.upgrade.plan.tasks.core.problem.api.SearchResult;
import com.liferay.ide.upgrade.plan.tasks.core.problem.util.FileHelper;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

/**
 * Parses a java file and provides some methods for finding search results
 * @author Gregory Amerson
 */
@Component(property = "file.extension=java", service = JavaFile.class)
@SuppressWarnings("rawtypes")
public class JavaFileJDT extends WorkspaceFile implements JavaFile {

	public JavaFileJDT() {
	}

	public JavaFileJDT(File file) {
		setFile(file);
	}

	@Override
	public List<SearchResult> findCatchExceptions(String[] exceptions) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(CatchClause node) {
					SingleVariableDeclaration exception = node.getException();

					Type type = exception.getType();

					String exceptionTypeName = type.toString();

					boolean retVal = false;

					for (String exceptionType : exceptions) {
						if (exceptionTypeName.equals(exceptionType)) {
							int startLine = _ast.getLineNumber(exception.getStartPosition());
							int startOffset = exception.getStartPosition();

							int endLine = _ast.getLineNumber(exception.getStartPosition() + exception.getLength());
							int endOffset = exception.getStartPosition() + exception.getLength();

							searchResults.add(
								createSearchResult(
									exceptionTypeName, startOffset, endOffset, startLine, endLine, true));

							retVal = true;
						}
					}

					return retVal;
				}

			});

		return searchResults;
	}

	@Override
	public List<SearchResult> findImplementsInterface(String interfaceName) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(TypeDeclaration node) {
					ITypeBinding typeBinding = node.resolveBinding();

					if (typeBinding != null) {
						ITypeBinding[] superInterfaces = typeBinding.getInterfaces();

						if (ListUtil.isNotEmpty(superInterfaces)) {
							String searchContext = superInterfaces[0].getName();

							SimpleName nodeName = node.getName();

							if (searchContext.equals(interfaceName)) {
								int startLine = _ast.getLineNumber(nodeName.getStartPosition());
								int startOffset = nodeName.getStartPosition();
								int endLine = _ast.getLineNumber(nodeName.getStartPosition() + nodeName.getLength());
								int endOffset = nodeName.getStartPosition() + nodeName.getLength();

								searchResults.add(
									createSearchResult(
										searchContext, startOffset, endOffset, startLine, endLine, true));
							}
						}
					}

					return true;
				}

			});

		return searchResults;
	}

	@Override
	public SearchResult findImport(String importName) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(ImportDeclaration node) {
					Name nodeName = node.getName();

					String searchContext = nodeName.toString();

					if (importName.equals(searchContext)) {
						int startLine = _ast.getLineNumber(nodeName.getStartPosition());
						int startOffset = nodeName.getStartPosition();
						int endLine = _ast.getLineNumber(nodeName.getStartPosition() + nodeName.getLength());
						int endOffset = nodeName.getStartPosition() + nodeName.getLength();

						searchResults.add(
							createSearchResult(searchContext, startOffset, endOffset, startLine, endLine, true));
					}

					return false;
				}

			});

		if (ListUtil.isNotEmpty(searchResults)) {
			return searchResults.get(0);
		}

		return null;
	}

	@Override
	public List<SearchResult> findImports(String[] imports) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(ImportDeclaration node) {
					Name name = node.getName();

					String searchContext = name.getFullyQualifiedName();

					for (String importName : imports) {
						if (searchContext.startsWith(importName)) {
							List<String> importsList = new ArrayList<>(Arrays.asList(imports));

							String greedyImport = importName;
							importsList.remove(importName);

							for (String anotherImport : importsList) {
								String s = name.toString();

								if (s.contains(anotherImport) && (anotherImport.length() > importName.length())) {
									greedyImport = anotherImport;
								}
							}

							int startLine = _ast.getLineNumber(name.getStartPosition());
							int startOffset = name.getStartPosition();
							int endLine = _ast.getLineNumber(name.getStartPosition() + name.getLength());
							int endOffset = name.getStartPosition() + greedyImport.length();

							searchResults.add(
								createSearchResult(searchContext, startOffset, endOffset, startLine, endLine, true));
						}
					}

					return false;
				}

			});

		return searchResults;
	}

	@Override
	public List<SearchResult> findMethodDeclaration(String name, String[] params, String returnType) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(MethodDeclaration node) {
					boolean sameParmSize = true;

					boolean sameReturnType = true;

					if (returnType != null) {
						Type type = node.getReturnType2();

						if (type != null) {
							ITypeBinding typeBinding = type.resolveBinding();

							String returnTypeName = typeBinding.getName();

							if (!returnTypeName.equals(returnType)) {
								sameReturnType = false;
							}
						}
						else {
							sameReturnType = false;
						}
					}

					SimpleName nodeName = node.getName();

					String methodName = nodeName.toString();

					List<?> parmsList = node.parameters();

					if (name.equals(methodName) && (params.length == parmsList.size())) {
						for (int i = 0; i < params.length; i++) {
							Object x = parmsList.get(i);

							String s = x.toString();

							String param = params[i];

							param = param.trim();

							if (!param.equals(s.substring(0, param.length()))) {
								sameParmSize = false;

								break;
							}
						}
					}
					else {
						sameParmSize = false;
					}

					if (sameParmSize && sameReturnType) {
						int startLine = _ast.getLineNumber(nodeName.getStartPosition());
						int startOffset = nodeName.getStartPosition();

						node.accept(
							new ASTVisitor() {

								@Override
								public boolean visit(Block node) {

									// SimpleName parent can not be MarkerAnnotation and
									// SimpleType
									// SingleVariableDeclaration node contains the
									// parms's type

									int endLine = _ast.getLineNumber(node.getStartPosition());
									int endOffset = node.getStartPosition();

									searchResults.add(
										createSearchResult(null, startOffset, endOffset, startLine, endLine, true));

									return false;
								}

							});
					}

					return false;
				}

			});

		return searchResults;
	}

	/**
	 * find the method invocations for a particular method on a given type or
	 * expression
	 *
	 * @param typeHint
	 *            the type hint to use when matching expressions
	 * @param expressionValue
	 *            the expression only value (no type hint)
	 * @param methodName
	 *            the method name
	 * @return search results
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SearchResult> findMethodInvocations(
		String typeHint, String expressionValue, String methodName, String[] methodParamTypes) {

		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(MethodInvocation node) {
					SimpleName nodeName = node.getName();

					String methodNameValue = nodeName.toString();

					Expression expression = node.getExpression();

					ITypeBinding type = null;

					if (expression != null) {
						try {
							Optional<ITypeBinding> optional = _typeCache.get(expression);

							type = optional.orElse(null);
						}
						catch (ExecutionException ee) {
						}
					}

					if (((methodName.equals(methodNameValue)) ||
						("*".equals(methodName))) &&

						// if typeHint is not null it must match the type hint and
						// ignore the expression
						// not strictly check the type and will check equals later

						((typeHint != null && type != null &&
							StringUtil.endsWith(type.getName(), typeHint)) ||

							// with no typeHint then expressions can be used to
							// match Static invocation

							(typeHint == null && expression != null &&
								StringUtil.equals(expression.toString(), expressionValue)))) {

						boolean argumentsMatch = false;

						if (methodParamTypes != null) {
							Expression[] argExpressions =
								((List<Expression>)node.arguments()).toArray(new Expression[0]);

							if (argExpressions.length == methodParamTypes.length) {

								// args number matched

								boolean possibleMatch = true;

								// assume all types will match until we find
								// otherwise

								boolean typeMatched = true;
								boolean typeUnresolved = false;

								for (int i = 0; i < argExpressions.length; i++) {
									Expression arg = argExpressions[i];

									ITypeBinding argType = arg.resolveTypeBinding();

									if (argType != null) {

										// can resolve the type

										if (_typeMatch(methodParamTypes[i], argType.getQualifiedName())) {

											// type matched

											continue;
										}
										else {

											// type unmatched

											possibleMatch = false;
											typeMatched = false;

											break;
										}
									}
									else {
										possibleMatch = false;

										// there are two cases :
										// typeUnresolved : means that all resolved
										// type is matched and there is unsolved
										// type , need to set fullMatch false
										// typeUnmatched : means that some resolved
										// type is unmatched , no need to add
										// SearchResult

										// do not add searchResults now, just record
										// the state and continue
										// because there maybe unmatched type later
										// which will break this case

										typeUnresolved = true;
									}
								}

								if (typeMatched && typeUnresolved) {
									int startOffset = expression.getStartPosition();

									int startLine = _ast.getLineNumber(startOffset);

									int endOffset = node.getStartPosition() + node.getLength();

									int endLine = _ast.getLineNumber(endOffset);

									// can't resolve the type but args number
									// matched , note that the last param is false

									searchResults.add(
										createSearchResult(null, startOffset, endOffset, startLine, endLine, false));
								}

								if (possibleMatch) {
									argumentsMatch = true;
								}
							}
						}

							// any method args types is OK without setting
							// methodParamTypes

						else {
							argumentsMatch = true;
						}

						if (argumentsMatch) {
							int startOffset = expression.getStartPosition();

							int startLine = _ast.getLineNumber(startOffset);

							int endOffset = node.getStartPosition() + node.getLength();

							int endLine = _ast.getLineNumber(endOffset);

							boolean fullMatch = true;

							// endsWith but not equals

							if ((typeHint != null) && (type != null)) {
								String typeName = type.getName();

								if (typeName.endsWith(typeHint) && !typeName.equals(typeHint)) {
									fullMatch = false;
								}
							}

							searchResults.add(
								createSearchResult(null, startOffset, endOffset, startLine, endLine, fullMatch));
						}
					}

					return true;
				}

			});

		return searchResults;
	}

	@Override
	public SearchResult findPackage(String packageName) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(PackageDeclaration node) {
					Name nodeName = node.getName();

					String searchContext = nodeName.toString();

					if (packageName.equals(searchContext)) {
						int startLine = _ast.getLineNumber(nodeName.getStartPosition());
						int startOffset = nodeName.getStartPosition();
						int endLine = _ast.getLineNumber(nodeName.getStartPosition() + nodeName.getLength());
						int endOffset = nodeName.getStartPosition() + nodeName.getLength();

						searchResults.add(
							createSearchResult(searchContext, startOffset, endOffset, startLine, endLine, true));
					}

					return false;
				}

			});

		if (ListUtil.isNotEmpty(searchResults)) {
			return searchResults.get(0);
		}

		return null;
	}

	public List<SearchResult> findQualifiedName(String exception) {
		List<SearchResult> searchResults = new ArrayList<>();

		// _ast.accept(new ASTVisitor() {

		//

		// @Override
		// public boolean visit(QualifiedName node) {
		// String qualifyName = node.getFullyQualifiedName();
		// boolean retVal = false;

		//

		// if (qualifyName.equals(exception)) {
		// final int startLine = _ast
		// .getLineNumber(node.getStartPosition());
		// final int startOffset = node.getStartPosition();
		// int endLine = _ast.getLineNumber(
		// node.getStartPosition() + node.getLength());
		// int endOffset = node.getStartPosition() + node.getLength();
		// searchResults.add(createSearchResult(startOffset,
		// endOffset, startLine, endLine, true));

		//

		// retVal = true;
		// }

		//

		// return retVal;
		// }
		// });

		return searchResults;
	}

	@Override
	public List<SearchResult> findServiceAPIs(String[] serviceFQNPrefixes) {
		List<SearchResult> searchResults = new ArrayList<>();

		for (String prefix : serviceFQNPrefixes) {
			for (String suffix : _SERVICE_API_SUFFIXES) {
				String serviceFQN = prefix + suffix;

				SearchResult importResult = findImport(serviceFQN);

				if (importResult != null) {
					searchResults.add(importResult);
				}

				String service = serviceFQN.substring(serviceFQN.lastIndexOf('.') + 1, serviceFQN.length());

				searchResults.addAll(findMethodInvocations(null, service, "*", null));

				searchResults.addAll(findMethodInvocations(service, null, "*", null));
			}
		}

		return searchResults;
	}

	@Override
	public List<SearchResult> findSuperClass(String superClassName) {
		List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(
			new ASTVisitor() {

				@Override
				public boolean visit(TypeDeclaration node) {
					ITypeBinding typeBinding = node.resolveBinding();

					if (typeBinding != null) {
						ITypeBinding superClass = typeBinding.getSuperclass();

						if (superClass != null) {
							String searchContext = superClass.getName();

							if (searchContext.equals(superClassName)) {
								SimpleName nodeName = node.getName();

								int startLine = _ast.getLineNumber(nodeName.getStartPosition());
								int startOffset = nodeName.getStartPosition();
								int endLine = _ast.getLineNumber(nodeName.getStartPosition() + nodeName.getLength());
								int endOffset = nodeName.getStartPosition() + nodeName.getLength();

								searchResults.add(
									createSearchResult(
										searchContext, startOffset, endOffset, startLine, endLine, true));
							}
						}
					}

					return true;
				}

			});

		return searchResults;
	}

	@Override
	public void setFile(File file) {
		_file = file;

		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());

			BundleContext context = bundle.getBundleContext();

			Collection<ServiceReference<CUCache>> sr = context.getServiceReferences(CUCache.class, "(type=java)");

			Iterator<ServiceReference<CUCache>> iterator = sr.iterator();

			ServiceReference<CUCache> ref = iterator.next();

			@SuppressWarnings("unchecked")
			CUCache<CompilationUnit> cache = context.getService(ref);

			_ast = cache.getCU(file, () -> getJavaSource());
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected SearchResult createSearchResult(
		String searchContext, int startOffset, int endOffset, int startLine, int endLine, boolean fullMatch) {

		return new SearchResult(_file, searchContext, startOffset, endOffset, startLine, endLine, fullMatch);
	}

	protected File getFile() {
		return _file;
	}

	protected char[] getJavaSource() {
		try {
			String s = _fileHelper.readFile(_file);

			return s.toCharArray();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	private boolean _typeMatch(String expectType, String paramType) {
		boolean match = false;

		if (expectType.equals(paramType)) {
			match = true;
		}
		else if (paramType.equals("null")) {
			match = true;
		}
		else if (expectType.endsWith(paramType) || paramType.endsWith(expectType)) {
			match = true;
		}
		else if (expectType.endsWith("Object[]") && paramType.endsWith("[]")) {
			match = true;
		}
		else if ((expectType.equals("Object") || expectType.equals("java.lang.Object")) &&
				 !(paramType.equals("Object[]") || paramType.equals("java.lang.Object[]"))) {

			match = true;
		}
		else if (expectType.equals("long")) {
			if (paramType.equals("long") || paramType.equals("Long") || paramType.equals("java.lang.Long") ||
				paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer") ||
				paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short") ||
				paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte")) {

				match = true;
			}
		}
		else if (expectType.equals("Long") || expectType.equals("java.lang.Long")) {
			if (paramType.equals("long") || paramType.equals("Long") || paramType.equals("java.lang.Long")) {
				match = true;
			}
		}
		else if (expectType.equals("int")) {
			if (paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer") ||
				paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short") ||
				paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte")) {

				match = true;
			}
		}
		else if (expectType.equals("Integer") || expectType.equals("java.lang.Integer")) {
			if (paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer")) {
				match = true;
			}
		}
		else if (expectType.equals("short")) {
			if (paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short") ||
				paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte")) {

				match = true;
			}
		}
		else if (expectType.equals("Short") || expectType.equals("java.lang.Short")) {
			if (paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short")) {
				match = true;
			}
		}
		else if (expectType.equals("byte") || expectType.equals("Byte") || expectType.equals("java.lang.Byte")) {
			if (paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte")) {
				match = true;
			}
		}
		else if (expectType.equals("double")) {
			if (paramType.equals("double") || paramType.equals("Double") || paramType.equals("java.lang.Double") ||
				paramType.equals("float") || paramType.equals("Float") || paramType.equals("java.lang.Float")) {

				match = true;
			}
		}
		else if (expectType.equals("Double") || expectType.equals("java.lang.Double")) {
			if (paramType.equals("double") || paramType.equals("Double") || paramType.equals("java.lang.Double")) {
				match = true;
			}
		}
		else if (expectType.equals("float") || expectType.equals("Float") || expectType.equals("java.lang.Float")) {
			if (paramType.equals("float") || paramType.equals("Float") || paramType.equals("java.lang.Float")) {
				match = true;
			}
		}
		else if (expectType.equals("char") || expectType.equals("Character") ||
				 expectType.equals("java.lang.Character")) {

			if (paramType.equals("char") || paramType.equals("Character") || paramType.equals("java.lang.Character")) {
				match = true;
			}
		}

		return match;
	}

	private static final String[] _SERVICE_API_SUFFIXES =
		{"LocalService", "LocalServiceUtil", "LocalServiceWrapper", "Service", "ServiceUtil", "ServiceWrapper"};

	private CompilationUnit _ast;
	private File _file;
	private final FileHelper _fileHelper = new FileHelper();

	private LoadingCache<Expression, Optional<ITypeBinding>> _typeCache = CacheBuilder.newBuilder().build(
		new CacheLoader<Expression, Optional<ITypeBinding>>() {

			@Override
			public Optional<ITypeBinding> load(Expression key) throws Exception {
				return Optional.ofNullable(key.resolveTypeBinding());
			}

		});

}