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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Simon Jiang
 * @author Lovett Li
 */
public class NewLiferayModuleProjectOpMethods {

	@SuppressWarnings({"unchecked", "deprecation"})
	public static boolean addProperties(File dest, List<String> properties) throws Exception {
		if (ListUtil.isEmpty(properties)) {
			return false;
		}

		try {
			ASTParser parser = ASTParser.newParser(AST.JLS8);

			String readContents = FileUtil.readContents(dest, true);

			parser.setSource(readContents.toCharArray());

			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			parser.setResolveBindings(true);

			CompilationUnit cu = (CompilationUnit)parser.createAST(new NullProgressMonitor());

			cu.recordModifications();

			Document document = new Document(new String(readContents));

			cu.accept(
				new ASTVisitor() {

					@Override
					public boolean visit(NormalAnnotation node) {
						Name name = node.getTypeName();

						String qualifiedName = name.getFullyQualifiedName();

						if (qualifiedName.equals("Component")) {
							ASTRewrite rewrite = ASTRewrite.create(cu.getAST());

							AST ast = cu.getAST();

							List<ASTNode> values = node.values();

							boolean hasProperty = false;

							for (ASTNode astNode : values) {
								if (astNode instanceof MemberValuePair) {
									MemberValuePair pairNode = (MemberValuePair)astNode;

									SimpleName simpleName = pairNode.getName();

									String fullQualifiedName = simpleName.getFullyQualifiedName();

									if (fullQualifiedName.equals("property")) {
										Expression express = pairNode.getValue();

										if (express instanceof ArrayInitializer) {
											ListRewrite lrw = rewrite.getListRewrite(
												express, ArrayInitializer.EXPRESSIONS_PROPERTY);

											ArrayInitializer initializer = (ArrayInitializer)express;

											List<ASTNode> expressions = initializer.expressions();

											ASTNode propertyNode = null;

											for (int i = properties.size() - 1; i >= 0; i--) {
												StringLiteral stringLiteral = ast.newStringLiteral();

												stringLiteral.setLiteralValue(properties.get(i));

												if (ListUtil.isNotEmpty(expressions)) {
													propertyNode = expressions.get(expressions.size() - 1);

													lrw.insertAfter(stringLiteral, propertyNode, null);
												}
												else {
													lrw.insertFirst(stringLiteral, null);
												}
											}
										}

										hasProperty = true;
									}
								}
							}

							if (!hasProperty) {
								ListRewrite clrw = rewrite.getListRewrite(node, NormalAnnotation.VALUES_PROPERTY);

								ASTNode lastNode = values.get(values.size() - 1);

								ArrayInitializer newArrayInitializer = ast.newArrayInitializer();

								MemberValuePair propertyMemberValuePair = ast.newMemberValuePair();

								propertyMemberValuePair.setName(ast.newSimpleName("property"));
								propertyMemberValuePair.setValue(newArrayInitializer);

								clrw.insertBefore(propertyMemberValuePair, lastNode, null);

								ListRewrite newLrw = rewrite.getListRewrite(
									newArrayInitializer, ArrayInitializer.EXPRESSIONS_PROPERTY);

								for (String property : properties) {
									StringLiteral stringLiteral = ast.newStringLiteral();

									stringLiteral.setLiteralValue(property);

									newLrw.insertAt(stringLiteral, 0, null);
								}
							}

							try (OutputStream fos = Files.newOutputStream(dest.toPath())) {
								TextEdit edits = rewrite.rewriteAST(document, null);

								edits.apply(document);

								String content = document.get();

								fos.write(content.getBytes());

								fos.flush();
							}
							catch (Exception e) {
								ProjectCore.logError(e);
							}
						}

						return super.visit(node);
					}

				});

			return true;
		}
		catch (Exception e) {
			ProjectCore.logError("error when adding properties to " + dest.getAbsolutePath(), e);

			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean checkComponentAnnotation(File dest) throws Exception {
		try {
			ASTParser parser = ASTParser.newParser(AST.JLS8);

			String readContents = FileUtil.readContents(dest, true);

			parser.setSource(readContents.toCharArray());

			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			parser.setResolveBindings(true);

			CompilationUnit cu = (CompilationUnit)parser.createAST(new NullProgressMonitor());

			CheckComponentAnnotationVistor componentAnnotationVistor = new CheckComponentAnnotationVistor();

			cu.accept(componentAnnotationVistor);

			return componentAnnotationVistor.hasComponentAnnotation();
		}
		catch (Exception e) {
			ProjectCore.logError("error when adding properties to " + dest.getAbsolutePath(), e);
		}

		return false;
	}

	public static final Status execute(NewLiferayModuleProjectOp op, ProgressMonitor pm) {
		IProgressMonitor monitor = ProgressMonitorBridge.create(pm);

		monitor.beginTask("Creating Liferay module project (this process may take several minutes)", 100);

		Status retval = Status.createOkStatus();

		Throwable errorStack = null;

		try {
			NewLiferayProjectProvider<BaseModuleOp> projectProvider = _getter.get(op.getProjectProvider());

			IStatus status = projectProvider.createNewProject(op, monitor);

			retval = StatusBridge.create(status);

			String projectName = _getter.get(op.getProjectName());

			IPath location = PathBridge.create(_getter.get(op.getLocation()));

			IPath projectLocation = location;

			String lastSegment = location.lastSegment();

			if ((location != null) && (location.segmentCount() > 0) && !lastSegment.equals(projectName)) {
				projectLocation = location.append(projectName);
			}

			List<IPath> finalClassPaths = _getClassFilePath(projectLocation);

			for (IPath classFilePath : finalClassPaths) {
				File finalClassFile = classFilePath.toFile();

				if (FileUtil.exists(finalClassFile)) {
					ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

					List<String> properties = new ArrayList<>();

					for (PropertyKey propertyKey : propertyKeys) {
						properties.add(_getter.get(propertyKey.getName()) + "=" + _getter.get(propertyKey.getValue()));
					}

					if (addProperties(finalClassFile, properties)) {
						IProject project = CoreUtil.getProject(_getter.get(op.getProjectName()));

						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}
				}
			}

			if (retval.ok()) {
				_updateBuildAndVersionPrefs(op);
			}
			else if ((retval.severity() == Status.Severity.ERROR) && (retval.exception() != null)) {
				errorStack = retval.exception();
			}
		}
		catch (Exception e) {
			errorStack = e;
		}

		if (errorStack != null) {
			String readableStack = CoreUtil.getStackTrace(errorStack);

			ProjectCore.logError(readableStack);

			return Status.createErrorStatus(readableStack + "\t Please see Eclipse error log for more details.");
		}

		return retval;
	}

	public static String getMavenParentPomGroupId(NewLiferayModuleProjectOp op, String projectName, IPath path) {
		String retval = null;

		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<BaseModuleOp> provider = _getter.get(op.getProjectProvider());

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> groupIds = provider.getData("parentGroupId", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(groupIds)) {
				retval = groupIds.get(0);
			}
		}

		return retval;
	}

	public static String getMavenParentPomVersion(NewLiferayModuleProjectOp op, String projectName, IPath path) {
		File parentProjectDir = path.toFile();

		NewLiferayProjectProvider<BaseModuleOp> provider = _getter.get(op.getProjectProvider());

		IStatus locationStatus = provider.validateProjectLocation(projectName, path);

		if (locationStatus.isOK() && FileUtil.hasChildren(parentProjectDir)) {
			List<String> versions = provider.getData("parentVersion", String.class, parentProjectDir);

			if (ListUtil.isNotEmpty(versions)) {
				return versions.get(0);
			}
		}

		return "";
	}

	private static void _getClassFile(File packageRoot, List<IPath> classFiles) {
		File[] children = packageRoot.listFiles();

		if (ListUtil.isNotEmpty(children)) {
			for (File child : children) {
				if (child.isDirectory()) {
					_getClassFile(child, classFiles);
				}
				else {
					try {
						boolean hasComponentAnnotation = checkComponentAnnotation(child.getAbsoluteFile());

						if (hasComponentAnnotation) {
							classFiles.add(new Path(child.getAbsolutePath()));
						}
					}
					catch (Exception e) {
						ProjectCore.logError(e);
					}
				}
			}
		}
	}

	private static List<IPath> _getClassFilePath(IPath projecLocation) {
		File packageRoot = FileUtil.getFile(projecLocation.append("src/main/java"));

		List<IPath> classFiles = new ArrayList<>();

		_getClassFile(packageRoot, classFiles);

		return classFiles;
	}

	private static void _updateBuildAndVersionPrefs(NewLiferayModuleProjectOp op) {
		try {
			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(ProjectCore.PLUGIN_ID);

			prefs.put(ProjectCore.PREF_DEFAULT_LIFERAY_VERSION_OPTION, SapphireUtil.getText(op.getLiferayVersion()));
			prefs.put(
				ProjectCore.PREF_DEFAULT_MODULE_PROJECT_BUILD_TYPE_OPTION,
				SapphireUtil.getText(op.getProjectProvider()));

			prefs.flush();
		}
		catch (Exception e) {
			String msg = "Error updating default project build type or version.";

			ProjectCore.logError(msg, e);
		}
	}

	private static final SapphireContentAccessor _getter = new SapphireContentAccessor() {
	};

	private static class CheckComponentAnnotationVistor extends ASTVisitor {

		public CheckComponentAnnotationVistor() {
		}

		public boolean hasComponentAnnotation() {
			return _hasComponentAnnotation;
		}

		@Override
		public boolean visit(NormalAnnotation node) {
			Name name = node.getTypeName();

			String qualifiedName = name.getFullyQualifiedName();

			if (qualifiedName.equals("Component")) {
				_hasComponentAnnotation = true;
			}

			return super.visit(node);
		}

		private boolean _hasComponentAnnotation = false;

	}

}