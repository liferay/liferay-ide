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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.internal.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ModuleTraverser {

	/**
	 * Name of the custom Java classpath entry attribute that is used to flag
	 * entries which should be exposed as module dependencies via the virtual
	 * component API.
	 */
	public static final String CLASSPATH_COMPONENT_DEPENDENCY = "org.eclipse.jst.component.dependency";

	/**
	 * Name of the custom Java classpath entry attribute that is used to flag
	 * the resolved entries of classpath containers that should not be exposed
	 * via the virtual component API.
	 */
	public static final String CLASSPATH_COMPONENT_NON_DEPENDENCY = "org.eclipse.jst.component.nondependency";

	/**
	 * Facet type for EAR modules
	 */
	public static final String EAR_MODULE = IModuleConstants.JST_EAR_MODULE;

	/**
	 * Facet type for utility modules
	 */
	public static final String UTILITY_MODULE = IModuleConstants.JST_UTILITY_MODULE;

	/**
	 * Facet type for Web modules
	 */
	public static final String WEB_MODULE = IModuleConstants.JST_WEB_MODULE;

	/**
	 * Scans the module using the specified visitor.
	 *
	 * @param module
	 *            module to traverse
	 * @param visitor
	 *            visitor to handle resources
	 * @param monitor
	 *            a progress monitor
	 * @throws CoreException
	 */
	public static void traverse(IModule module, IModuleVisitor visitor, IProgressMonitor monitor) throws CoreException {
		IModuleType moduleType = module.getModuleType();

		if ((module == null) || (moduleType == null)) {
			return;
		}

		String typeId = moduleType.getId();
		IVirtualComponent component = ComponentCore.createComponent(module.getProject());

		if (component == null) {
			return;
		}

		if (EAR_MODULE.equals(typeId)) {
			_traverseEarComponent(component, visitor);
		}
		else if (WEB_MODULE.equals(typeId)) {
			_traverseWebComponent(component, visitor);
		}
	}

	/**
	 * Derived from
	 * ClasspathDependencyUtil.checkForComponentDependencyAttribute()
	 */
	private static IClasspathAttribute _checkForComponentDependencyAttribute(IClasspathEntry entry, int attributeType) {
		if (entry == null) {
			return null;
		}

		IClasspathAttribute[] attributes = entry.getExtraAttributes();

		for (IClasspathAttribute attribute : attributes) {
			String name = attribute.getName();

			if (name.equals(CLASSPATH_COMPONENT_DEPENDENCY)) {
				if ((attributeType == _DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY) ||
					(attributeType == _DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY)) {

					return attribute;
				}
			}
			else if (name.equals(CLASSPATH_COMPONENT_NON_DEPENDENCY)) {
				if ((attributeType == _DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY) ||
					(attributeType == _DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_NONDEPENDENCY)) {

					return attribute;
				}
			}
		}

		return null;
	}

	/**
	 * Derived from ClasspathDependencyUtil.isClassFolderEntry()
	 */
	private static boolean _classFolderEntry(IClasspathEntry entry) {
		if ((entry == null) || (entry.getEntryKind() != IClasspathEntry.CPE_LIBRARY)) {
			return false;
		}

		// does the path refer to a file or a folder?

		IPath entryPath = entry.getPath();

		IPath entryLocation = entryPath;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot workspaceRoot = workspace.getRoot();

		IResource resource = workspaceRoot.findMember(entryPath);

		if (resource != null) {
			entryLocation = resource.getLocation();
		}

		boolean file = true;
		File entryFile = entryLocation.toFile();

		if (entryFile.isDirectory()) {
			file = false;
		}

		return !file;
	}

	private static IClasspathEntry _getClasspathEntry(IJavaProject project, IPath sourcePath)
		throws JavaModelException {

		IPath projectPath = project.getPath();

		sourcePath = projectPath.append(sourcePath);

		IClasspathEntry[] cp = project.getRawClasspath();

		for (int i = 0; i < cp.length; i++) {
			if (sourcePath.equals(cp[i].getPath())) {
				return JavaCore.getResolvedClasspathEntry(cp[i]);
			}
		}

		return null;
	}

	/**
	 * Derived from ClasspathDependencyUtil.getComponentClasspathDependencies()
	 */
	private static Map _getComponentClasspathDependencies(IJavaProject javaProject, boolean webApp)
		throws CoreException {

		// get the raw entries

		Map referencedRawEntries = _getRawComponentClasspathDependencies(javaProject);
		Map<IClasspathEntry, IClasspathAttribute> validRawEntries = new HashMap<>();

		// filter out non-valid referenced raw entries

		Set keySet = referencedRawEntries.keySet();

		Iterator i = keySet.iterator();

		while (i.hasNext()) {
			IClasspathEntry entry = (IClasspathEntry)i.next();

			IClasspathAttribute attrib = (IClasspathAttribute)referencedRawEntries.get(entry);

			if (_valid(entry, attrib, webApp)) {
				validRawEntries.put(entry, attrib);
			}
		}

		// if we have no valid raw entries, return empty map

		if (validRawEntries.isEmpty()) {
			return Collections.emptyMap();
		}

		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		Map<IPath, IClasspathEntry> pathToResolvedEntry = new HashMap<>();

		// store in a map from path to entry

		for (int j = 0; j < entries.length; j++) {
			pathToResolvedEntry.put(entries[j].getPath(), entries[j]);
		}

		Map<IClasspathEntry, IClasspathAttribute> referencedEntries = new LinkedHashMap<>();

		// grab all IPackageFragmentRoots

		IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();

		for (IPackageFragmentRoot root : roots) {
			IClasspathEntry rawEntry = root.getRawClasspathEntry();

			// is the raw entry valid?

			IClasspathAttribute attrib = validRawEntries.get(rawEntry);

			if (attrib == null) {
				continue;
			}

			IPath pkgFragPath = root.getPath();

			IClasspathEntry resolvedEntry = pathToResolvedEntry.get(pkgFragPath);

			IClasspathAttribute resolvedAttrib = _checkForComponentDependencyAttribute(
				resolvedEntry, _DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY);

			/*
			 * attribute for the resolved entry must either be unspecified or it must be the
			 * dependency attribute for it to be included
			 */
			String resolvedAttribName = resolvedAttrib.getName();

			if ((resolvedAttrib == null) || resolvedAttrib.equals(CLASSPATH_COMPONENT_DEPENDENCY)) {

				// filter out resolved entry if it doesn't pass the validation rules

				if (_valid(resolvedEntry, (resolvedAttrib != null) ? resolvedAttrib : attrib, webApp)) {
					if (resolvedAttrib != null) {

						// if there is an attribute on the sub-entry, use that

						attrib = resolvedAttrib;
					}

					referencedEntries.put(resolvedEntry, attrib);
				}
			}
		}

		return referencedEntries;
	}

	private static IPath _getOSPath(IJavaProject javaProject, IPath outputPath) throws JavaModelException {
		if (outputPath == null) {
			outputPath = javaProject.getOutputLocation();
		}

		// If we have the root of a project, return project location

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot workspaceRoot = workspace.getRoot();

		if (outputPath.segmentCount() == 1) {
			IProject workspaceProject = workspaceRoot.getProject(outputPath.lastSegment());

			return workspaceProject.getLocation();
		}

		// Otherwise return project folder location

		IFolder outputFolder = workspaceRoot.getFolder(outputPath);

		return outputFolder.getLocation();
	}

	/**
	 * Derived from
	 * ClasspathDependencyUtil.getRawComponentClasspathDependencies()
	 */
	private static Map _getRawComponentClasspathDependencies(IJavaProject javaProject) throws CoreException {
		if (javaProject == null) {
			return Collections.emptyMap();
		}

		Map<IClasspathEntry, IClasspathAttribute> referencedRawEntries = new HashMap<>();
		IClasspathEntry[] entries = javaProject.getRawClasspath();

		for (IClasspathEntry entry : entries) {
			IClasspathAttribute attrib = _checkForComponentDependencyAttribute(
				entry, _DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY);

			if (attrib != null) {
				referencedRawEntries.put(entry, attrib);
			}
		}

		return referencedRawEntries;
	}

	/**
	 * Derived from J2EEProjectUtilities.getResolvedPathForArchiveComponent()
	 */
	private static IPath _getResolvedPathForArchiveComponent(URI uri) {
		String resourceType = uri.segment(1);
		URI contenturi = ModuleURIUtil.trimToRelativePath(uri, 2);

		String contentName = contenturi.toString();

		if (resourceType.equals("lib")) {

			// module:/classpath/lib/D:/foo/foo.jar

			return Path.fromOSString(contentName);
		}
		else if (resourceType.equals("var")) {

			// module:/classpath/var/<CLASSPATHVAR>/foo.jar

			String classpathVar = contenturi.segment(0);
			URI remainingPathuri = ModuleURIUtil.trimToRelativePath(contenturi, 1);

			String remainingPath = remainingPathuri.toString();

			String[] classpathvars = JavaCore.getClasspathVariableNames();
			boolean found = false;

			for (int i = 0; i < classpathvars.length; i++) {
				if (classpathVar.equals(classpathvars[i])) {
					found = true;

					break;
				}
			}

			if (found) {
				IPath path = JavaCore.getClasspathVariable(classpathVar);

				if (path != null) {
					URI finaluri = URI.createURI(path.toOSString() + IPath.SEPARATOR + remainingPath);

					return Path.fromOSString(finaluri.toString());
				}
			}
		}

		return null;
	}

	/**
	 * Derived from ClasspathDependencyUtil.getRuntimePath()
	 */
	private static String _getRuntimePath(IClasspathAttribute attrib, boolean webApp, boolean classFolder) {
		String attribName = attrib.getName();

		if ((attrib != null) && !attribName.equals(CLASSPATH_COMPONENT_DEPENDENCY)) {
			return null;
		}

		String attribValue = attrib.getValue();

		if ((attrib == null) || (attribValue == null) || (attribValue.length() == 0)) {
			if (webApp) {
				if (classFolder) {
					return "/WEB_INF/classes";
				}

				return "WEB-INF/lib";
			}

			if (classFolder) {
				return "/";
			}

			return "../";
		}

		return attrib.getValue();
	}

	private static void _traverseDependentEntries(
			IModuleVisitor visitor, IPath runtimeFolder, WorkbenchComponent component)
		throws CoreException {

		IProject dependentProject = StructureEdit.getContainingProject(component);

		if (!dependentProject.hasNature(JavaCore.NATURE_ID)) {
			return;
		}

		IJavaProject project = JavaCore.create(dependentProject);

		visitor.visitDependentJavaProject(project);

		// assume it is the same as URI

		String name = component.getName();

		// go thru all entries

		List res = component.getResources();

		for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
			ComponentResource childComp = (ComponentResource)itorRes.next();

			IPath rtPath = childComp.getRuntimePath();
			IPath srcPath = childComp.getSourcePath();

			IClasspathEntry cpe = _getClasspathEntry(project, srcPath);

			if (cpe != null) {
				IPath rtFolder = runtimeFolder.append(rtPath);

				visitor.visitDependentComponent(
					rtFolder.append(name + ".jar"), _getOSPath(project, cpe.getOutputLocation()));
			}

			// Handle META-INF/resources

			String path = rtPath.toString();
			IFolder resFolder = null;
			String targetPath = StringPool.EMPTY;

			if ("/".equals(path)) {
				resFolder = dependentProject.getFolder(srcPath.append("META-INF/resources"));
			}
			else if ("/META-INF".equals(path)) {
				resFolder = dependentProject.getFolder(srcPath.append("resources"));
			}
			else if ("/META-INF/resources".equals(path)) {
				resFolder = dependentProject.getFolder(srcPath);
			}
			else if (path.startsWith("/META-INF/resources/")) {
				resFolder = dependentProject.getFolder(srcPath);
				targetPath = path.substring("/META-INF/resources".length());
			}

			if (FileUtil.exists(resFolder)) {
				visitor.visitDependentContentResource(new Path(targetPath), resFolder.getLocation());
			}
		}

		// Include tagged classpath entries

		Map classpathDeps = _getComponentClasspathDependencies(project, false);

		Set classpathDepsKeySet = classpathDeps.keySet();

		for (Iterator iterator = classpathDepsKeySet.iterator(); iterator.hasNext();) {
			IClasspathEntry entry = (IClasspathEntry)iterator.next();

			boolean classFolder = _classFolderEntry(entry);

			String rtFolder = null;

			if (classFolder) {
				rtFolder = "/";
			}
			else {
				rtFolder = "/WEB-INF/lib";
			}

			IPath entryPath = entry.getPath();

			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot workspaceRoot = workspace.getRoot();

			IResource entryRes = workspaceRoot.findMember(entryPath);

			if (entryRes != null) {
				entryPath = entryRes.getLocation();
			}

			// TODO Determine if different handling is needed for some use cases

			if (classFolder) {
				IPath rtPath = runtimeFolder.append(rtFolder);

				visitor.visitDependentComponent(rtPath.append(name + ".jar"), _getOSPath(project, entry.getPath()));
			}
			else {
				visitor.visitArchiveComponent(new Path(rtFolder), entryPath);
			}
		}
	}

	private static void _traverseEarComponent(IVirtualComponent component, IModuleVisitor visitor)
		throws CoreException {

		// Currently the JST Server portion of WTP may not depend on the JST Enterprise portion of WTP

		/*
		 * EARArtifactEdit earEdit = EARArtifactEdit
		 * .getEARArtifactEditForRead(component); if (earEdit != null) {
		 * IVirtualReference[] j2eeComponents =
		 * earEdit.getJ2EEModuleReferences(); for (int i = 0; i <
		 * j2eeComponents.length; i++) { traverseWebComponent(
		 * j2eeComponents[i].getReferencedComponent(), visitor, monitor); }
		 * IVirtualReference[] jarComponents =
		 * earEdit.getUtilityModuleReferences(); for (int i = 0; i <
		 * jarComponents.length; i++) { IVirtualReference jarReference =
		 * jarComponents[i]; IVirtualComponent jarComponent = jarReference
		 * .getReferencedComponent(); IProject dependentProject =
		 * jarComponent.getProject(); if
		 * (!dependentProject.hasNature(JavaCore.NATURE_ID)) continue;
		 * IJavaProject project = JavaCore.create(dependentProject);
		 * IClasspathEntry cpe = getClasspathEntry(project, jarComponent
		 * .getRootFolder().getProjectRelativePath());
		 * visitor.visitEarResource(null, getOSPath(dependentProject, project,
		 * cpe.getOutputLocation())); } }
		 */
		visitor.endVisitEarComponent(component);
	}

	private static void _traverseWebComponent(IVirtualComponent component, IModuleVisitor visitor)
		throws CoreException {

		visitor.visitWebComponent(component);

		IProject proj = component.getProject();

		StructureEdit warStruct = StructureEdit.getStructureEditForRead(proj);

		try {
			WorkbenchComponent comp = warStruct.getComponent();

			if (comp == null) {
				return;
			}

			_traverseWebComponentLocalEntries(comp, visitor);

			// traverse referenced components

			List children = comp.getReferencedComponents();

			for (Iterator itor = children.iterator(); itor.hasNext();) {
				ReferencedComponent childRef = (ReferencedComponent)itor.next();

				IPath rtFolder = childRef.getRuntimePath();
				URI refHandle = childRef.getHandle();

				if (PlatformURLModuleConnection.CLASSPATH.equals(
						refHandle.segment(ModuleURIUtil.ModuleURI.SUB_PROTOCOL_INDX))) {

					IPath refPath = _getResolvedPathForArchiveComponent(refHandle);

					// If an archive component, add to list

					if (refPath != null) {
						if (!refPath.isAbsolute()) {
							IWorkspace workspace = ResourcesPlugin.getWorkspace();

							IWorkspaceRoot workspaceRoot = workspace.getRoot();

							IFile file = workspaceRoot.getFile(refPath);

							IPath refPath2 = file.getLocation();

							if (refPath2 != null) {
								visitor.visitArchiveComponent(rtFolder, refPath2);
							}
							else {
							}
						}
						else {
							visitor.visitArchiveComponent(rtFolder, refPath);
						}
					}
					else {

						// TODO Determine if any use case would arrive here.

					}
				}
				else {
					try {
						WorkbenchComponent childCom = warStruct.findComponentByURI(refHandle);

						if (childCom == null) {
							continue;
						}

						_traverseDependentEntries(visitor, rtFolder, childCom);
					}
					catch (UnresolveableURIException uurie) {
						LiferayServerCore.logError(uurie);
					}
				}
			}
		}
		finally {
			warStruct.dispose();
		}

		visitor.endVisitWebComponent(component);
	}

	private static void _traverseWebComponentLocalEntries(WorkbenchComponent comp, IModuleVisitor visitor)
		throws CoreException {

		IProject warProject = StructureEdit.getContainingProject(comp);

		if ((warProject == null) || !warProject.hasNature(JavaCore.NATURE_ID)) {
			return;
		}

		IJavaProject project = JavaCore.create(warProject);

		List res = comp.getResources();

		for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
			ComponentResource childComp = (ComponentResource)itorRes.next();

			IClasspathEntry cpe = _getClasspathEntry(project, childComp.getSourcePath());

			if (cpe == null) {
				continue;
			}

			visitor.visitWebResource(childComp.getRuntimePath(), _getOSPath(project, cpe.getOutputLocation()));
		}

		// Include tagged classpath entries

		Map classpathDeps = _getComponentClasspathDependencies(project, true);

		Set classpathDepsKeySet = classpathDeps.keySet();

		for (Iterator iterator = classpathDepsKeySet.iterator(); iterator.hasNext();) {
			IClasspathEntry entry = (IClasspathEntry)iterator.next();

			IClasspathAttribute attrib = (IClasspathAttribute)classpathDeps.get(entry);

			boolean classFolder = _classFolderEntry(entry);

			String rtFolder = attrib.getValue();

			if (rtFolder == null) {
				if (classFolder) {
					rtFolder = "/WEB-INF/classes";
				}
				else {
					rtFolder = "/WEB-INF/lib";
				}
			}

			IPath entryPath = entry.getPath();

			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IWorkspaceRoot workspaceRoot = workspace.getRoot();

			IResource entryRes = workspaceRoot.findMember(entryPath);

			if (entryRes != null) {
				entryPath = entryRes.getLocation();
			}

			// TODO Determine if different handling is needed for some use cases

			if (classFolder) {
				visitor.visitWebResource(new Path(rtFolder), _getOSPath(project, entry.getPath()));
			}
			else {
				visitor.visitArchiveComponent(new Path(rtFolder), entryPath);
			}
		}
	}

	/**
	 * Derived from ClasspathDependencyValidator.validateVirtualComponentEntry()
	 */
	private static boolean _valid(IClasspathEntry entry, IClasspathAttribute attrib, boolean webApp) {
		int kind = entry.getEntryKind();
		boolean classFolder = _classFolderEntry(entry);

		if ((kind == IClasspathEntry.CPE_PROJECT) || (kind == IClasspathEntry.CPE_SOURCE)) {
			return false;
		}

		String runtimePath = _getRuntimePath(attrib, webApp, classFolder);

		if (!webApp) {
			if (!runtimePath.equals("../") && !runtimePath.equals("/")) {
				return false;
			}

			if (classFolder && !runtimePath.equals("/")) {
				return false;
			}
		}
		else {
			if ((runtimePath != null) && !runtimePath.equals("/WEB-INF/lib") &&
				!runtimePath.equals("/WEB-INF/classes") && !runtimePath.equals("../")) {

				return false;
			}

			if (classFolder && !runtimePath.equals("/WEB-INF/classes")) {
				return false;
			}
		}

		return true;
	}

	private static final int _DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY = 1;

	private static final int _DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_NONDEPENDENCY = 2;

	/**
	 * Argument values that are used to select component dependency attribute
	 * type.
	 */
	private static final int _DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY = 0;

}