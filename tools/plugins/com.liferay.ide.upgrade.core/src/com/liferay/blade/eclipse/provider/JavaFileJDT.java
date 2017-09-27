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
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.util.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 *
 * Parses a java file and provides some methods for finding search results
 */
@Component(
	property = {
		"file.extension=java",
	},
	service = JavaFile.class
)
@SuppressWarnings("rawtypes")
public class JavaFileJDT extends WorkspaceFile implements JavaFile {

	private static final String[] SERVICE_API_SUFFIXES =  {
		"LocalService",
		"LocalServiceUtil",
		"LocalServiceWrapper",
		"Service",
		"ServiceUtil",
		"ServiceWrapper",
	};

	private CompilationUnit _ast;

	private File _file;

	private final FileHelper _fileHelper = new FileHelper();

	public JavaFileJDT() {
	}

	public JavaFileJDT(File file) {
		setFile(file);
	}

	@Override
	public void setFile(File file) {
		_file = file;

		try {
			final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

			final Collection<ServiceReference<CUCache>> sr = context.getServiceReferences(CUCache.class, "(type=java)");

			ServiceReference<CUCache> ref = sr.iterator().next();

			CUCache cache = context.getService(ref);

			_ast = (CompilationUnit) cache.getCU(file, getJavaSource());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected SearchResult createSearchResult(String searchContext, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {

		return new SearchResult(_file, searchContext, startOffset, endOffset, startLine,
				endLine, fullMatch);
	}

	@Override
	public List<SearchResult> findCatchExceptions(final String[] exceptions) {
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(CatchClause node){
				String exceptionTypeName = node.getException().getType().toString();
				boolean retVal = false;

				for (String exceptionType : exceptions) {
					if ( exceptionTypeName.equals(exceptionType)){
						final int startLine = _ast.getLineNumber(node.getException().getStartPosition());
						final int startOffset = node.getException().getStartPosition();

						int endLine = _ast.getLineNumber(node.getException().getStartPosition() + node.getException().getLength());
							int endOffset = node.getException().getStartPosition() + node.getException().getLength();
							searchResults
									.add(createSearchResult(exceptionTypeName, startOffset,
										endOffset, startLine, endLine, true));

							retVal = true;
					}
				}

				return retVal;
			}
		});

		return searchResults;
	}

	@Override
	public List<SearchResult> findImplementsInterface(final String interfaceName){
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(TypeDeclaration node) {
				ITypeBinding[] superInterfaces = null;

				if (node.resolveBinding() != null) {
					superInterfaces = node.resolveBinding().getInterfaces();

					if (superInterfaces != null && superInterfaces.length > 0) {

						String searchContext = superInterfaces[0].getName();

						if (searchContext.equals(interfaceName)) {
							int startLine = _ast.getLineNumber(
									node.getName().getStartPosition());
							int startOffset = node.getName().getStartPosition();
							int endLine = _ast.getLineNumber(
									node.getName().getStartPosition()
											+ node.getName().getLength());
							int endOffset = node.getName().getStartPosition()
									+ node.getName().getLength();

							searchResults
									.add(createSearchResult(searchContext, startOffset,
											endOffset, startLine, endLine, true));
						}
					}
				}

				return true;
			}
		});

		return searchResults;
	}

	@Override
	public SearchResult findImport(final String importName) {
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(ImportDeclaration node) {
				final String searchContext = node.getName().toString();

				if (importName.equals(searchContext)) {
					int startLine = _ast.getLineNumber(node.getName()
						.getStartPosition());
					int startOffset = node.getName().getStartPosition();
					int endLine = _ast.getLineNumber(node.getName()
						.getStartPosition() + node.getName().getLength());
					int endOffset = node.getName().getStartPosition() +
						node.getName().getLength();

					searchResults.add(createSearchResult(searchContext, startOffset,
						endOffset, startLine, endLine, true));
				}

				return false;
			};
		});

		if (0 != searchResults.size()) {
			return searchResults.get(0);
		}

		return null;
	}

	@Override
	public List<SearchResult> findImports(final String[] imports) {
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(ImportDeclaration node) {
				String searchContext = node.getName().getFullyQualifiedName();

				for (String importName : imports) {
					if (searchContext.startsWith(importName)) {
						final List<String> importsList = new ArrayList<>(Arrays.asList(imports));

						String greedyImport = importName;
						importsList.remove(importName);

						for (String anotherImport : importsList) {
							if (node.getName().toString().contains(anotherImport)
									&& anotherImport.length() > importName.length()) {
								greedyImport = anotherImport;
							}
						}

						int startLine = _ast.getLineNumber(node.getName().getStartPosition());
						int startOffset = node.getName().getStartPosition();
						int endLine = _ast
								.getLineNumber(node.getName().getStartPosition() + node.getName().getLength());
						int endOffset = node.getName().getStartPosition() + greedyImport.length();

						searchResults.add(
								createSearchResult(searchContext, startOffset, endOffset, startLine, endLine, true));
					}
				}

				return false;
			};
		});

		return searchResults;
	}

	@Override
	public List<SearchResult> findMethodDeclaration(
		final String name, final String[] params, final String returnType) {

		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				boolean sameParmSize = true;
				boolean sameReturnType = true;

				if(returnType != null) {
					Type type = node.getReturnType2();

					if(type != null) {
						String returnTypeName = type.resolveBinding().getName();
						if(!returnTypeName.equals(returnType)) {
							sameReturnType = false;
						}
					}else {
						sameReturnType = false;
					}
				}

				String methodName = node.getName().toString();
				List<?> parmsList = node.parameters();

				if (name.equals(methodName) &&
					params.length == parmsList.size()) {

					for (int i = 0; i < params.length; i++) {
						if (!(params[i].trim().equals(parmsList.get(i)
								.toString()
								.substring(0, params[i].trim().length())))) {
							sameParmSize = false;
							break;
						}
					}
				} else {
					sameParmSize = false;
				}

				if (sameParmSize && sameReturnType) {
					final int startLine = _ast.getLineNumber(node.getName()
						.getStartPosition());
					final int startOffset = node.getName().getStartPosition();
					node.accept(new ASTVisitor() {

						@Override
						public boolean visit(Block node) {

							// SimpleName parent can not be MarkerAnnotation and
							// SimpleType
							// SingleVariableDeclaration node contains the
							// parms's type

							int endLine = _ast.getLineNumber(node
								.getStartPosition());
							int endOffset = node.getStartPosition();
							searchResults
									.add(createSearchResult(null, startOffset,
										endOffset, startLine, endLine, true));

							return false;
						};
					});
				}

				return false;
			}
		});

		return searchResults;
	}

	/**
	 * find the method invocations for a particular method on a given type or expression
	 *
	 * @param typeHint the type hint to use when matching expressions
	 * @param expressionValue    the expression only value (no type hint)
	 * @param methodName     the method name
	 * @return    search results
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SearchResult> findMethodInvocations(
		final String typeHint, final String expressionValue, final String methodName,
		final String[] methodParamTypes) {
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodInvocation node) {
				final String methodNameValue = node.getName().toString();
				final Expression expression = node.getExpression();

				ITypeBinding type = null;

				if (expression != null) {
					type = expression.resolveTypeBinding();
				}

				if ( ((methodName.equals(methodNameValue)) || ("*".equals(methodName))) &&
						// if typeHint is not null it must match the type hint and ignore the expression
						// not strictly check the type and will check equals later
						( (typeHint != null && type != null && type.getName().endsWith(typeHint))  ||
						// with no typeHint then expressions can be used to match Static invocation
						 (typeHint == null && expression != null && expression.toString().equals(expressionValue))) ) {

					boolean argumentsMatch = false;

					if (methodParamTypes != null) {
						Expression[] argExpressions = ((List<Expression>)node.arguments()).toArray(new Expression[0]);

						if (argExpressions.length == methodParamTypes.length) {
							//args number matched
							boolean possibleMatch = true;
							// assume all types will match until we find otherwise
							boolean typeMatched = true;
							boolean typeUnresolved = false;

							for(int i = 0; i < argExpressions.length; i++) {
								Expression arg = argExpressions[i];
								ITypeBinding argType = arg.resolveTypeBinding();

								if (argType != null) {
									// can resolve the type
									if (typeMatch(methodParamTypes[i], argType.getQualifiedName())) {
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
								else{
									possibleMatch = false;
									//there are two cases :
									//typeUnresolved : means that  all resolved type is matched and there is unsolved type , need to set fullMatch false
									//typeUnmatched : means that some resolved type is unmatched , no need to add SearchResult

									//do not add searchResults now, just record the state and continue
									//because there maybe unmatched type later which will break this case
									typeUnresolved = true;
								}
							}

							if( typeMatched && typeUnresolved ){
								final int startOffset = expression.getStartPosition();
								final int startLine = _ast.getLineNumber(startOffset);
								final int endOffset = node.getStartPosition() + node.getLength();
								final int endLine = _ast.getLineNumber(endOffset);
								//can't resolve the type but  args number matched  ,  note that the last param is false
								searchResults.add(createSearchResult(null, startOffset, endOffset, startLine, endLine, false));
							}

							if (possibleMatch) {
								argumentsMatch = true;
							}
						}
					}
					//any method args types is OK without setting methodParamTypes
					else {
						argumentsMatch = true;
					}

					if (argumentsMatch) {
						final int startOffset = expression.getStartPosition();
						final int startLine = _ast.getLineNumber(startOffset);
						final int endOffset = node.getStartPosition() + node.getLength();
						final int endLine = _ast.getLineNumber(endOffset);
						boolean isFullMatch = true;

						//endsWith but not equals
						if( typeHint != null && type != null && type.getName().endsWith(typeHint) && !type.getName().equals(typeHint) ) {
							isFullMatch = false;
						}

						searchResults.add(createSearchResult(null, startOffset, endOffset, startLine, endLine, isFullMatch));
					}
				}

				return true;
			}
		});

		return searchResults;
	}

	@Override
	public SearchResult findPackage(final String packageName) {
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {
			@Override
			public boolean visit(PackageDeclaration node) {
				String searchContext = node.getName().toString();

				if (packageName.equals(searchContext)) {
					int startLine = _ast.getLineNumber(node.getName()
						.getStartPosition());
					int startOffset = node.getName().getStartPosition();
					int endLine = _ast.getLineNumber(node.getName()
						.getStartPosition() + node.getName().getLength());
					int endOffset = node.getName().getStartPosition() +
						node.getName().getLength();

					searchResults.add(createSearchResult(searchContext, startOffset,
						endOffset, startLine, endLine, true));
				}

				return false;
			};
		});

		if (0 != searchResults.size()) {
			return searchResults.get(0);
		}

		return null;
	}

	public List<SearchResult> findQualifiedName(final String exception) {
		final List<SearchResult> searchResults = new ArrayList<>();

//		_ast.accept(new ASTVisitor() {
//
//			@Override
//			public boolean visit(QualifiedName node) {
//				String qualifyName = node.getFullyQualifiedName();
//				boolean retVal = false;
//
//				if (qualifyName.equals(exception)) {
//					final int startLine = _ast
//							.getLineNumber(node.getStartPosition());
//					final int startOffset = node.getStartPosition();
//					int endLine = _ast.getLineNumber(
//							node.getStartPosition() + node.getLength());
//					int endOffset = node.getStartPosition() + node.getLength();
//					searchResults.add(createSearchResult(startOffset,
//							endOffset, startLine, endLine, true));
//
//					retVal = true;
//				}
//
//				return retVal;
//			}
//		});

		return searchResults;
	}

	@Override
	public List<SearchResult> findServiceAPIs(final String[] serviceFQNPrefixes) {
		final List<SearchResult> searchResults = new ArrayList<>();

		for (String prefix : serviceFQNPrefixes) {
			for (String suffix : SERVICE_API_SUFFIXES) {
				String serviceFQN = prefix + suffix;
				SearchResult importResult = findImport(serviceFQN);

				if (importResult != null) {
					searchResults.add(importResult);
				}

				String service = serviceFQN.substring(
						serviceFQN.lastIndexOf('.') + 1, serviceFQN.length());

				searchResults.addAll(
						findMethodInvocations(null, service, "*", null));

				searchResults.addAll(
						findMethodInvocations(service, null, "*", null));
			}
		}

		return searchResults;
	}

	@Override
	public List<SearchResult> findSuperClass(final String superClassName){
		final List<SearchResult> searchResults = new ArrayList<>();

		_ast.accept(new ASTVisitor() {

			@Override
			public boolean visit(TypeDeclaration node) {
				ITypeBinding superClass = null;

				if (node.resolveBinding() != null) {
					superClass = node.resolveBinding().getSuperclass();

					if (superClass != null) {
						final String searchContext = superClass.getName();

						if (searchContext.equals(superClassName)) {
							int startLine = _ast.getLineNumber(
									node.getName().getStartPosition());
							int startOffset = node.getName().getStartPosition();
							int endLine = _ast.getLineNumber(
									node.getName().getStartPosition()
											+ node.getName().getLength());
							int endOffset = node.getName().getStartPosition()
									+ node.getName().getLength();

							searchResults
									.add(createSearchResult(searchContext, startOffset,
											endOffset, startLine, endLine, true));
						}
					}
				}

				return true;
			}
		});

		return searchResults;
	}

	protected File getFile() {
		return _file;
	}

	protected char[] getJavaSource() {
		try {
			return _fileHelper.readFile(_file).toCharArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private boolean typeMatch(String expectType, String paramType) {
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
		else if ((expectType.equals("Object") || expectType.equals("java.lang.Object"))
				&& !(paramType.equals("Object[]") || paramType.equals("java.lang.Object[]"))) {
			match = true;
		}
		else if (expectType.equals("long")) {
			if (paramType.equals("long") || paramType.equals("Long") || paramType.equals("java.lang.Long")
					|| paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer")
					|| paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short")
					|| paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte"))
				match = true;
		}
		else if (expectType.equals("Long") || expectType.equals("java.lang.Long")) {
			if (paramType.equals("long") || paramType.equals("Long") || paramType.equals("java.lang.Long")) {
				match = true;
			}
		}
		else if (expectType.equals("int")) {
			if (paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer")
					|| paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short")
					|| paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte"))
				match = true;
		}
		else if (expectType.equals("Integer") || expectType.equals("java.lang.Integer")) {
			if (paramType.equals("int") || paramType.equals("Integer") || paramType.equals("java.lang.Integer")) {
				match = true;
			}
		}
		else if (expectType.equals("short")) {
			if (paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short")
					|| paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte"))
				match = true;
		}
		else if (expectType.equals("Short") || expectType.equals("java.lang.Short")) {
			if (paramType.equals("short") || paramType.equals("Short") || paramType.equals("java.lang.Short")) {
				match = true;
			}
		}
		else if (expectType.equals("byte") || expectType.equals("Byte") || expectType.equals("java.lang.Byte")) {
			if (paramType.equals("byte") || paramType.equals("Byte") || paramType.equals("java.lang.Byte"))
				match = true;
		}
		else if (expectType.equals("double")) {
			if (paramType.equals("double") || paramType.equals("Double") || paramType.equals("java.lang.Double")
					|| paramType.equals("float") || paramType.equals("Float") || paramType.equals("java.lang.Float"))
				match = true;
		}
		else if (expectType.equals("Double") || expectType.equals("java.lang.Double")) {
			if (paramType.equals("double") || paramType.equals("Double") || paramType.equals("java.lang.Double")) {
				match = true;
			}
		}
		else if (expectType.equals("float") || expectType.equals("Float") || expectType.equals("java.lang.Float")) {
			if (paramType.equals("float") || paramType.equals("Float") || paramType.equals("java.lang.Float"))
				match = true;
		}
		else if (expectType.equals("char") || expectType.equals("Character")
				|| expectType.equals("java.lang.Character")) {
			if (paramType.equals("char") || paramType.equals("Character") || paramType.equals("java.lang.Character"))
				match = true;
		}

		return match;
	}

}