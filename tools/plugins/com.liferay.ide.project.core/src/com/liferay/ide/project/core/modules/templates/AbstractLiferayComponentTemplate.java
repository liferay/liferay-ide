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

package com.liferay.ide.project.core.modules.templates;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.IComponentTemplate;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.java.JavaPackageName;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractLiferayComponentTemplate
	implements IComponentTemplate<NewLiferayComponentOp>, Comparable<IComponentTemplate> {

	public AbstractLiferayComponentTemplate() {
	}

	public int compareTo(IComponentTemplate componentTemplate) {
		if (componentTemplate != null) {
			return displayName.compareTo(componentTemplate.getDisplayName());
		}

		return 0;
	}

	public void createSampleFile(IFile newFile, String srcFileName) throws CoreException {
		try {
			Class<?> clazz = getClass();

			URL sampleFileURL = clazz.getClassLoader().getResource(TEMPLATE_DIR + "/" + srcFileName);

			File file = new File(FileLocator.toFileURL(sampleFileURL).getFile());

			String sampleContent = FileUtil.readContents(file, true);

			if (newFile.getParent() instanceof IFolder) {
				CoreUtil.prepareFolder((IFolder)newFile.getParent());
			}

			newFile.create(new ByteArrayInputStream(sampleContent.getBytes()), true, null);
		}
		catch (IOException ioe) {
			throw new CoreException(ProjectCore.createErrorStatus(ioe));
		}
	}

	public void createSampleFile(IFile newFile, String srcFileName, String oldReplaceConent, String newReplaceContent)
		throws CoreException {

		try {
			Class<?> clazz = getClass();

			URL sampleFileURL = clazz.getClassLoader().getResource(TEMPLATE_DIR + "/" + srcFileName);

			File file = new File(FileLocator.toFileURL(sampleFileURL).getFile());

			String sampleContent = FileUtil.readContents(file, true);

			String newCoentent = sampleContent.replace(oldReplaceConent, newReplaceContent);

			if (newFile.getParent() instanceof IFolder) {
				CoreUtil.prepareFolder((IFolder)newFile.getParent());
			}

			newFile.create(new ByteArrayInputStream(newCoentent.getBytes()), true, null);
		}
		catch (IOException ioe) {
			throw new CoreException(ProjectCore.createErrorStatus(ioe));
		}
	}

	public void doExecute(NewLiferayComponentOp op, IProgressMonitor monitor) throws CoreException {
		try {
			initializeOperation(op);

			project = CoreUtil.getProject(projectName);

			if (project == null) {
				return;
			}

			liferayProject = LiferayCore.create(project);

			if (liferayProject == null) {
				return;
			}

			initFreeMarker();

			IFile srcFile = prepareClassFile(componentClassName);

			doSourceCodeOperation(srcFile);

			doNewPropertiesOperation();

			doMergeResourcesOperation();

			doMergeBndOperation();

			doMergeDependencyOperation();

			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	protected void createFile(IFile newFile, byte[] input) throws CoreException {
		if (newFile.getParent() instanceof IFolder) {
			CoreUtil.prepareFolder((IFolder)newFile.getParent());
		}

		newFile.create(new ByteArrayInputStream(input), true, null);
	}

	protected void createFileInResouceFolder(IFolder sourceFolder, String filePath, File resourceFile)
		throws CoreException {

		IFile projectFile = getProjectFile(sourceFolder, filePath);

		if (FileUtil.notExists(projectFile)) {
			String readContents = FileUtil.readContents(resourceFile, true);

			createFile(projectFile, readContents.getBytes());
		}
	}

	protected IPackageFragment createJavaPackage(IJavaProject javaProject, String packageName) {
		IPackageFragmentRoot packRoot = getSourceFolder(javaProject);

		if (packRoot == null) {
			return null;
		}

		IPackageFragment pack = packRoot.getPackageFragment(packageName);

		if (pack == null) {
			pack = packRoot.getPackageFragment("");
		}

		if (pack.exists()) {
			return pack;
		}

		try {
			return packRoot.createPackageFragment(pack.getElementName(), true, null);
		}
		catch (CoreException ce) {
			ProjectCore.logError(ce);
		}

		return null;
	}

	protected void createResorcesFolder(IProject project) throws CoreException {
		IFolder resourceFolder = liferayProject.getSourceFolder("resources");

		if (FileUtil.exists(resourceFolder)) {
			return;
		}

		IJavaProject javaProject = JavaCore.create(project);

		List<IClasspathEntry> existingRawClasspath = Arrays.asList(javaProject.getRawClasspath());

		List<IClasspathEntry> newRawClasspath = new ArrayList<>();

		IClasspathAttribute[] attributes = {JavaCore.newClasspathAttribute("FROM_GRADLE_MODEL", "true")};

		IPath path = project.getFullPath().append("src/main/resources");

		IClasspathEntry resourcesEntry = JavaCore.newSourceEntry(path, new IPath[0], new IPath[0], null, attributes);

		newRawClasspath.add(resourcesEntry);

		for (IClasspathEntry entry : existingRawClasspath) {
			newRawClasspath.add(entry);
		}

		javaProject.setRawClasspath(newRawClasspath.toArray(new IClasspathEntry[0]), new NullProgressMonitor());

		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	protected void doMergeBndOperation() throws CoreException {
		BndProperties bndProperty = new BndProperties();

		IFile file = project.getFile("bnd.bnd");

		if (FileUtil.notExists(file)) {
			return;
		}

		File bndFile = FileUtil.getFile(file);

		initBndProperties(bndFile, bndProperty);

		try (OutputStream out = Files.newOutputStream(bndFile.toPath())) {
			setBndProperties(bndProperty);

			bndProperty.store(out, null);
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}
	}

	protected void doMergeDependencyOperation() throws CoreException {
		IProjectBuilder builder = liferayProject.adapt(IProjectBuilder.class);

		builder.updateProjectDependency(project, getComponentDependency());
	}

	protected void doMergeResourcesOperation() throws CoreException {
	}

	protected void doNewPropertiesOperation() throws CoreException {
	}

	protected void doSourceCodeOperation(IFile srcFile) throws CoreException {
		File file = srcFile.getLocation().toFile();

		try (OutputStream fos = Files.newOutputStream(file.toPath())) {
			Template temp = cfg.getTemplate(getTemplateFile());

			Map<String, Object> root = getTemplateMap();

			Writer out = new OutputStreamWriter(fos);

			temp.process(root, out);

			fos.flush();
		}
		catch (IOException | TemplateException e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}
	}

	protected String getBundleSymbolicName() {
		BndProperties bndProperty = new BndProperties();

		IFile file = project.getFile("bnd.bnd");

		File bndFile = FileUtil.getFile(file);

		initBndProperties(bndFile, bndProperty);

		return bndProperty.getPropertyValue("Bundle-SymbolicName");
	}

	protected List<String[]> getComponentDependency() throws CoreException {
		List<String[]> dependencyList = new ArrayList<>();

		for (String[] dependency : _DEPENDENCY) {
			dependencyList.add(dependency);
		}

		return dependencyList;
	}

	protected String getExtensionClass() {
		return null;
	}

	protected List<String> getImports() {
		List<String> imports = new ArrayList<>();

		imports.add("org.osgi.service.component.annotations.Component");

		return imports;
	}

	protected IProject getProject() {
		return CoreUtil.getProject(projectName);
	}

	protected IFile getProjectFile(IFolder sourceFolder, String filePath) {
		IFile retval = null;

		if (sourceFolder != null) {
			retval = sourceFolder.getFile(new Path(filePath));
		}

		return retval;
	}

	protected List<String> getProperties() {
		return properties;
	}

	protected IPackageFragmentRoot getSourceFolder(IJavaProject javaProject) {
		try {
			for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
				if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
					return root;
				}
			}
		}
		catch (Exception e) {
			ProjectCore.logError(e);
		}

		return null;
	}

	protected String getSuperClass() {
		return null;
	}

	protected abstract String getTemplateFile();

	protected Map<String, Object> getTemplateMap() {
		Map<String, Object> root = new HashMap<>();

		root.put("classname", componentClassName);
		root.put("componentfolder", componentClassName.toLowerCase());
		root.put("componentNameWithoutTemplateName", componentNameWithoutTemplateName);
		root.put("extensionclass", getExtensionClass());
		root.put("importlibs", getImports());
		root.put("packagename", packageName);
		root.put("projectname", projectName);
		root.put("properties", getProperties());
		root.put("simplemodelclass", simpleModelClass);
		root.put("supperclass", getSuperClass());

		return root;
	}

	protected void initBndProperties(File bndFile, BndProperties bndProperty) {
		try {
			bndProperty.load(bndFile);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	protected void initFreeMarker() throws CoreException {
		try {
			Class<?> clazz = getClass();

			URL templateURL = clazz.getClassLoader().getResource(TEMPLATE_DIR);

			cfg.setDirectoryForTemplateLoading(new File(FileLocator.toFileURL(templateURL).getFile()));

			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		}
		catch (IOException ioe) {
			throw new CoreException(ProjectCore.createErrorStatus(ioe));
		}
	}

	protected void initializeOperation(NewLiferayComponentOp op) {
		projectName = op.getProjectName().content(true);
		packageName = op.getPackageName().content(true);
		componentClassName = op.getComponentClassName().content(true);

		IComponentTemplate<NewLiferayComponentOp> componentTemplate = op.getComponentClassTemplateName().content(true);

		templateName = componentTemplate.getShortName();

		serviceName = op.getServiceName().content(true);
		modelClass = op.getModelClass().content(true);

		componentNameWithoutTemplateName = componentClassName.replace(templateName, "");

		if (modelClass != null) {
			int modeClassPos = modelClass.lastIndexOf(".");

			simpleModelClass = modelClass.substring(modeClassPos + 1);
		}

		ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

		for (int i = 0; i < propertyKeys.size(); i++) {
			PropertyKey propertyKey = propertyKeys.get(i);

			StringBuilder sb = new StringBuilder();

			sb.append(propertyKey.getName().content(true));
			sb.append("=");
			sb.append(propertyKey.getValue().content(true));

			if (i != (propertyKeys.size() - 1)) {
				sb.append(",");
			}
			else {
				sb.append("");
			}

			properties.add(sb.toString());
		}
	}

	protected IFile prepareClassFile(String className) throws CoreException {
		IFile file = null;

		try {
			IFolder sourceFolder = liferayProject.getSourceFolder("java");
			IJavaProject javaProject = JavaCore.create(project);

			if (packageName != null) {
				IPackageFragment pack = createJavaPackage(javaProject, packageName.toString());

				if (pack == null) {
					throw new CoreException(ProjectCore.createErrorStatus("Can't create package folder"));
				}

				String fileName = className + ".java";

				IPath packageFullPath = new Path(packageName.toString().replace('.', IPath.SEPARATOR));

				if (FileUtil.notExists(packageFullPath)) {
					CoreUtil.prepareFolder(sourceFolder.getFolder(packageFullPath));
				}

				IPath javaFileFullPath = packageFullPath.append(fileName);

				file = sourceFolder.getFile(javaFileFullPath);
			}
		}
		catch (Exception e) {
			throw new CoreException(ProjectCore.createErrorStatus(e));
		}

		return file;
	}

	protected void setBndProperties(BndProperties bndProperty) {
	}

	protected static final String TEMPLATE_DIR = "com/liferay/ide/project/core/modules/templates";

	protected File[] bndTemplateFiles;
	protected Configuration cfg = new Configuration();
	protected String componentClassName;
	protected String componentNameWithoutTemplateName;
	protected File[] dependenciesTemplateFiles;
	protected String displayName;
	protected ILiferayProject liferayProject;
	protected String modelClass;
	protected JavaPackageName packageName;
	protected IProject project;
	protected String projectName;
	protected List<String> properties = new ArrayList<>();
	protected String serviceName;
	protected String shortName;
	protected String simpleModelClass;
	protected File[] sourceTemplateFiles;
	protected String templateName;

	private static final String[][] _DEPENDENCY = {
		{"com.liferay.portal", "com.liferay.portal.kernel", "2.0.0"},
		{"org.osgi", "org.osgi.service.component.annotations", "1.3.0"}
	};

}