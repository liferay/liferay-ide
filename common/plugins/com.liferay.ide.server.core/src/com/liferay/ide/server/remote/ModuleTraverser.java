/**********************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Igor Fedorenko & Fabrizio Giustina - Initial API and implementation
 **********************************************************************/
package com.liferay.ide.server.remote;

import com.liferay.ide.core.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.Trace;
import org.eclipse.osgi.util.NLS;
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

/**
 * Temporary solution for https://bugs.eclipse.org/bugs/show_bug.cgi?id=103888
 */
@SuppressWarnings("restriction")
public class ModuleTraverser {

	/**
	 * Facet type for EAR modules
	 */
    public static final String EAR_MODULE = IModuleConstants.JST_EAR_MODULE;

    /**
     * Facet type for Web modules
     */
    public static final String WEB_MODULE = IModuleConstants.JST_WEB_MODULE;

    /**
     * Facet type for utility modules
     */
    public static final String UTILITY_MODULE = IModuleConstants.JST_UTILITY_MODULE;

    /**
     * Name of the custom Java classpath entry attribute that is used to flag entries
     * which should be exposed as module dependencies via the virtual component API.
     */
	public static final String CLASSPATH_COMPONENT_DEPENDENCY = "org.eclipse.jst.component.dependency"; //$NON-NLS-1$
    
	/**
	 * Name of the custom Java classpath entry attribute that is used to flag
	 * the resolved entries of classpath containers that should not be exposed
	 * via the virtual component API.
	 */
	public static final String CLASSPATH_COMPONENT_NON_DEPENDENCY = "org.eclipse.jst.component.nondependency"; //$NON-NLS-1$
	
	/**
	 * Argument values that are used to select component dependency attribute type. 
	 */
	private static final int DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY = 0;
	private static final int DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY = 1;
	private static final int DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_NONDEPENDENCY = 2;

	/**
     * Scans the module using the specified visitor.
     * 
     * @param module module to traverse
     * @param visitor visitor to handle resources
     * @param monitor a progress monitor
     * @throws CoreException
     */
    public static void traverse(IModule module, IModuleVisitor visitor,
            IProgressMonitor monitor) throws CoreException {
        if (module == null || module.getModuleType() == null)
            return;

        String typeId = module.getModuleType().getId();
        IVirtualComponent component = ComponentCore.createComponent(module.getProject());

        if (component == null) {
            // can happen if project has been closed
            Trace.trace(Trace.WARNING, "Unable to create component for module " //$NON-NLS-1$
                    + module.getName());
            return;
        }

        if (EAR_MODULE.equals(typeId)) {
            traverseEarComponent(component, visitor, monitor);
        } else if (WEB_MODULE.equals(typeId)) {
            traverseWebComponent(component, visitor, monitor);
        }
    }

    private static void traverseEarComponent(IVirtualComponent component,
            IModuleVisitor visitor, IProgressMonitor monitor)
            throws CoreException {
    	// Currently the JST Server portion of WTP may not depend on the JST Enterprise portion of WTP
/*        EARArtifactEdit earEdit = EARArtifactEdit
                .getEARArtifactEditForRead(component);
        if (earEdit != null) {
            IVirtualReference[] j2eeComponents = earEdit.getJ2EEModuleReferences();
            for (int i = 0; i < j2eeComponents.length; i++) {
                traverseWebComponent(
                        j2eeComponents[i].getReferencedComponent(), visitor,
                        monitor);
            }
            IVirtualReference[] jarComponents = earEdit.getUtilityModuleReferences();
            for (int i = 0; i < jarComponents.length; i++) {
                IVirtualReference jarReference = jarComponents[i];
                IVirtualComponent jarComponent = jarReference
                        .getReferencedComponent();
                IProject dependentProject = jarComponent.getProject();
                if (!dependentProject.hasNature(JavaCore.NATURE_ID))
                    continue;
                IJavaProject project = JavaCore.create(dependentProject);
                IClasspathEntry cpe = getClasspathEntry(project, jarComponent
                        .getRootFolder().getProjectRelativePath());
                visitor.visitEarResource(null, getOSPath(dependentProject,
                        project, cpe.getOutputLocation()));
            }
        }*/
        visitor.endVisitEarComponent(component);
    }

    private static void traverseWebComponent(IVirtualComponent component,
            IModuleVisitor visitor, IProgressMonitor monitor)
            throws CoreException {

        visitor.visitWebComponent(component);

        IProject proj = component.getProject();
        StructureEdit warStruct = StructureEdit.getStructureEditForRead(proj);
        try {
            WorkbenchComponent comp = warStruct.getComponent();
            if (comp == null) {
                Trace.trace(Trace.SEVERE,
                        "Error getting WorkbenchComponent from war project. IProject=\"" //$NON-NLS-1$
                                + proj + "\" StructureEdit=\"" + warStruct //$NON-NLS-1$
                                + "\" WorkbenchComponent=\"" + comp + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                return;
            }
            traverseWebComponentLocalEntries(comp, visitor, monitor);

            // traverse referenced components
            List children = comp.getReferencedComponents();
            for (Iterator itor = children.iterator(); itor.hasNext();) {
                ReferencedComponent childRef = (ReferencedComponent) itor.next();
                IPath rtFolder = childRef.getRuntimePath();
                URI refHandle = childRef.getHandle();

                if (PlatformURLModuleConnection.CLASSPATH.equals(
                		refHandle.segment(ModuleURIUtil.ModuleURI.SUB_PROTOCOL_INDX))) {
                    IPath refPath = getResolvedPathForArchiveComponent(refHandle);
                    // If an archive component, add to list
                    if (refPath != null) {
                    	if (!refPath.isAbsolute()) {
                    		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(refPath);
                    		IPath refPath2 = file.getLocation();
                    		if (refPath2 != null) {
                    			visitor.visitArchiveComponent(rtFolder, refPath2);
                    		}
                    		else {
                    			Trace.trace(Trace.WARNING, NLS.bind(
                    					"Could not get the location of a referenced component.  It may not exist.  Project={0}, Parent Component={1}, Referenced Component Path={2}", //$NON-NLS-1$
                    					new Object[] { proj.getName(), comp.getName(), refPath}));
                    		}
                    	}
                    	else {
                    		visitor.visitArchiveComponent(rtFolder, refPath);
                    	}
                    }
                    else {
                    	// TODO Determine if any use case would arrive here.
                    }
                } else {
                    try {
                        WorkbenchComponent childCom = warStruct.findComponentByURI(refHandle);
                        if (childCom == null) {
                            continue;
                        }

                        traverseDependentEntries(visitor, rtFolder, childCom,
                                monitor);
                    } catch (UnresolveableURIException e) {
                        TomcatPlugin.log(e);
                    }
                }
            }
        } finally {
            warStruct.dispose();
        }

        visitor.endVisitWebComponent(component);
    }

    private static void traverseWebComponentLocalEntries(
            WorkbenchComponent comp, IModuleVisitor visitor,
            IProgressMonitor monitor) throws CoreException {
        IProject warProject = StructureEdit.getContainingProject(comp);
        if (warProject == null || !warProject.hasNature(JavaCore.NATURE_ID)) {
            return;
        }
        IJavaProject project = JavaCore.create(warProject);

        List res = comp.getResources();
        for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
            ComponentResource childComp = (ComponentResource) itorRes.next();
            IClasspathEntry cpe = getClasspathEntry(project, childComp.getSourcePath());
            if (cpe == null)
                continue;
            visitor.visitWebResource(childComp.getRuntimePath(), getOSPath(
                    warProject, project, cpe.getOutputLocation()));
        }

        // Include tagged classpath entries
        Map classpathDeps = getComponentClasspathDependencies(project, true);
        for (Iterator iterator = classpathDeps.keySet().iterator(); iterator.hasNext();) {
			IClasspathEntry entry = (IClasspathEntry)iterator.next();
			IClasspathAttribute attrib = (IClasspathAttribute)classpathDeps.get(entry);
			boolean isClassFolder = isClassFolderEntry(entry);
			String rtFolder = attrib.getValue();
			if (rtFolder == null) {
				if (isClassFolder) {
					rtFolder = "/WEB-INF/classes"; //$NON-NLS-1$
				} else {
					rtFolder = "/WEB-INF/lib"; //$NON-NLS-1$
				}
			} 
			IPath entryPath = entry.getPath();
			IResource entryRes = ResourcesPlugin.getWorkspace().getRoot().findMember(entryPath);
			if (entryRes != null) {
				entryPath = entryRes.getLocation();
			}
			// TODO Determine if different handling is needed for some use cases
			if (isClassFolder) {
				 visitor.visitWebResource(new Path(rtFolder), 
		                    getOSPath(warProject, project, entry.getPath()));
			} else {
				visitor.visitArchiveComponent(new Path(rtFolder), entryPath);				
			}
		}
    }

    private static void traverseDependentEntries(IModuleVisitor visitor,
            IPath runtimeFolder, WorkbenchComponent component,
            IProgressMonitor monitor) throws CoreException {
        IProject dependentProject = StructureEdit.getContainingProject(component);
        if (!dependentProject.hasNature(JavaCore.NATURE_ID))
            return;
        IJavaProject project = JavaCore.create(dependentProject);
		visitor.visitDependentJavaProject(project);

        String name = component.getName(); // assume it is the same as URI

        // go thru all entries
        List res = component.getResources();
        for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
            ComponentResource childComp = (ComponentResource) itorRes.next();
            IPath rtPath = childComp.getRuntimePath();
            IPath srcPath = childComp.getSourcePath();
            IClasspathEntry cpe = getClasspathEntry(project, srcPath);
            if (cpe != null) {
                visitor.visitDependentComponent(runtimeFolder.append(rtPath)
                        .append(name + ".jar"), getOSPath(dependentProject, //$NON-NLS-1$
                        project, cpe.getOutputLocation()));
            }
            // Handle META-INF/resources
    		String path = rtPath.toString();
    		IFolder resFolder = null;
    		String targetPath = StringUtil.EMPTY;
    		if ("/".equals(path)) { //$NON-NLS-1$
    			resFolder = dependentProject.getFolder(srcPath.append("META-INF/resources")); //$NON-NLS-1$
    		}
    		else if ("/META-INF".equals(path)) { //$NON-NLS-1$
    			resFolder = dependentProject.getFolder(srcPath.append("resources")); //$NON-NLS-1$
    		}
    		else if ("/META-INF/resources".equals(path)) { //$NON-NLS-1$
    			resFolder = dependentProject.getFolder(srcPath);
    		}
    		else if (path.startsWith("/META-INF/resources/")) { //$NON-NLS-1$
    			resFolder = dependentProject.getFolder(srcPath);
    			targetPath = path.substring("/META-INF/resources".length()); //$NON-NLS-1$
    		}
    		if (resFolder != null && resFolder.exists()) {
    			visitor.visitDependentContentResource(new Path(targetPath), resFolder.getLocation());
    		}
        }

        // Include tagged classpath entries
        Map classpathDeps = getComponentClasspathDependencies(project, false);
        for (Iterator iterator = classpathDeps.keySet().iterator(); iterator.hasNext();) {
			IClasspathEntry entry = (IClasspathEntry)iterator.next();
			boolean isClassFolder = isClassFolderEntry(entry);
			String rtFolder = null;
			if (isClassFolder) {
				rtFolder = "/"; //$NON-NLS-1$
			} else {
				rtFolder = "/WEB-INF/lib"; //$NON-NLS-1$
			}
			IPath entryPath = entry.getPath();
			IResource entryRes = ResourcesPlugin.getWorkspace().getRoot().findMember(entryPath);
			if (entryRes != null) {
				entryPath = entryRes.getLocation();
			}
			// TODO Determine if different handling is needed for some use cases
			if (isClassFolder) {
				 visitor.visitDependentComponent(runtimeFolder.append(rtFolder)
		                    .append(name + ".jar"), getOSPath(dependentProject, //$NON-NLS-1$
		                    project, entry.getPath()));
			} else {
				visitor.visitArchiveComponent(new Path(rtFolder), entryPath);
			}
		}
    }

    private static IClasspathEntry getClasspathEntry(IJavaProject project,
            IPath sourcePath) throws JavaModelException {
        sourcePath = project.getPath().append(sourcePath);
        IClasspathEntry[] cp = project.getRawClasspath();
        for (int i = 0; i < cp.length; i++) {
            if (sourcePath.equals(cp[i].getPath()))
                return JavaCore.getResolvedClasspathEntry(cp[i]);
        }
        return null;
    }

    private static IPath getOSPath(IProject project, IJavaProject javaProject,
            IPath outputPath) throws JavaModelException {
        if (outputPath == null)
            outputPath = javaProject.getOutputLocation();
        // If we have the root of a project, return project location
        if (outputPath.segmentCount() == 1) {
        	return ResourcesPlugin.getWorkspace().getRoot().getProject(outputPath.lastSegment())
        			.getLocation();
        }
        // Otherwise return project folder location
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(outputPath)
                .getLocation();
    }

    /*
     * Derived from J2EEProjectUtilities.getResolvedPathForArchiveComponent()
     */
	private static IPath getResolvedPathForArchiveComponent(URI uri) {

		String resourceType = uri.segment(1);
		URI contenturi = ModuleURIUtil.trimToRelativePath(uri, 2);
		String contentName = contenturi.toString();

		if (resourceType.equals("lib")) { //$NON-NLS-1$
			// module:/classpath/lib/D:/foo/foo.jar
			return Path.fromOSString(contentName);

		} else if (resourceType.equals("var")) { //$NON-NLS-1$

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
			Trace.trace(Trace.WARNING,
					NLS.bind("Tomcat publishing could not resolve dependency URI \"{0}\".  A value for classpath variable {1} was not found.", uri, classpathVar)); //$NON-NLS-1$
		}
		return null;
	}

	/*
	 * Derived from ClasspathDependencyUtil.getComponentClasspathDependencies()
	 */
	private static Map getComponentClasspathDependencies(final IJavaProject javaProject, final boolean isWebApp) throws CoreException {

		// get the raw entries
		final Map referencedRawEntries = getRawComponentClasspathDependencies(javaProject);
		final Map<IClasspathEntry, IClasspathAttribute> validRawEntries = new HashMap<IClasspathEntry, IClasspathAttribute>();

		// filter out non-valid referenced raw entries
		final Iterator i = referencedRawEntries.keySet().iterator();
		while (i.hasNext()) {
			final IClasspathEntry entry = (IClasspathEntry) i.next();
			final IClasspathAttribute attrib = (IClasspathAttribute) referencedRawEntries.get(entry);
			if (isValid(entry, attrib, isWebApp, javaProject.getProject())) {
				validRawEntries.put(entry, attrib);
			}
		}

		// if we have no valid raw entries, return empty map
		if (validRawEntries.isEmpty()) {
        	return Collections.EMPTY_MAP;
		}

		// XXX Would like to replace the code below with use of a public JDT API that returns
		// the raw IClasspathEntry for a given resolved IClasspathEntry (see see https://bugs.eclipse.org/bugs/show_bug.cgi?id=183995)
		// The code must currently leverage IPackageFragmentRoot to determine this
		// mapping and, because IPackageFragmentRoots do not maintain IClasspathEntry data, a prior
		// call is needed to getResolvedClasspath() and the resolved IClasspathEntries have to be stored in a Map from IPath-to-IClasspathEntry to
		// support retrieval using the resolved IPackageFragmentRoot
		
		// retrieve the resolved classpath
		final IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		final Map<IPath, IClasspathEntry> pathToResolvedEntry = new HashMap<IPath, IClasspathEntry>();
		
		// store in a map from path to entry
		for (int j = 0; j < entries.length; j++) {
			pathToResolvedEntry.put(entries[j].getPath(), entries[j]);
		}

		final Map<IClasspathEntry, IClasspathAttribute> referencedEntries = new LinkedHashMap<IClasspathEntry, IClasspathAttribute>();
		
		// grab all IPackageFragmentRoots
		final IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
		for (int j = 0; j < roots.length; j++) {
			final IPackageFragmentRoot root = roots[j];
			final IClasspathEntry rawEntry = root.getRawClasspathEntry();
			
			// is the raw entry valid?
			IClasspathAttribute attrib = validRawEntries.get(rawEntry);
			if (attrib == null) {
				continue;
			}
			
			final IPath pkgFragPath = root.getPath();
			final IClasspathEntry resolvedEntry = pathToResolvedEntry.get(pkgFragPath);
			final IClasspathAttribute resolvedAttrib = checkForComponentDependencyAttribute(resolvedEntry,
					DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY);
			// attribute for the resolved entry must either be unspecified or it must be the
			// dependency attribute for it to be included
			if (resolvedAttrib == null || resolvedAttrib.getName().equals(CLASSPATH_COMPONENT_DEPENDENCY)) {
				// filter out resolved entry if it doesn't pass the validation rules
				if (isValid(resolvedEntry, resolvedAttrib != null ? resolvedAttrib : attrib, isWebApp, javaProject.getProject())) {
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

	/*
	 * Derived from ClasspathDependencyUtil.getRawComponentClasspathDependencies()
	 */
	private static Map getRawComponentClasspathDependencies(final IJavaProject javaProject) throws CoreException {
		if (javaProject == null) {
			return Collections.EMPTY_MAP;
		}
		final Map<IClasspathEntry, IClasspathAttribute> referencedRawEntries = new HashMap<IClasspathEntry, IClasspathAttribute>();
		final IClasspathEntry[] entries = javaProject.getRawClasspath();
        for (int i = 0; i < entries.length; i++) {
            final IClasspathEntry entry = entries[i];
            final IClasspathAttribute attrib = checkForComponentDependencyAttribute(entry,
            		DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY);
            if (attrib != null) {
            	referencedRawEntries.put(entry, attrib);
            }
        }
        return referencedRawEntries;
	}

	/*
	 * Derived from ClasspathDependencyUtil.checkForComponentDependencyAttribute()
	 */
	private static IClasspathAttribute checkForComponentDependencyAttribute(final IClasspathEntry entry, final int attributeType) {
		if (entry == null) {
			return null;
		}
	    final IClasspathAttribute[] attributes = entry.getExtraAttributes();
	    for (int i = 0; i < attributes.length; i++) {
	    	final IClasspathAttribute attribute = attributes[i];
	    	final String name = attribute.getName();
	    	if (name.equals(CLASSPATH_COMPONENT_DEPENDENCY)) {
	    		if (attributeType == DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY
	    				|| attributeType == DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_DEPENDENCY) {
	    			return attribute;
	    		}
	    	} else if (name.equals(CLASSPATH_COMPONENT_NON_DEPENDENCY)) {
	    		if (attributeType == DEPENDECYATTRIBUTETYPE_DEPENDENCY_OR_NONDEPENDENCY
	    				|| attributeType == DEPENDECYATTRIBUTETYPE_CLASSPATH_COMPONENT_NONDEPENDENCY) {
	    			return attribute;
	    		}
	    	}
	    }
	    return null;
	}

	/*
	 * Derived from ClasspathDependencyValidator.validateVirtualComponentEntry()
	 */
	private static boolean isValid(final IClasspathEntry entry, final IClasspathAttribute attrib, boolean isWebApp, final IProject project) {
		int kind = entry.getEntryKind();
		boolean isClassFolder = isClassFolderEntry(entry);
		
		if (kind == IClasspathEntry.CPE_PROJECT || kind == IClasspathEntry.CPE_SOURCE) {
			return false;
		}

		String runtimePath = getRuntimePath(attrib, isWebApp, isClassFolder);
		if (!isWebApp) {
			if (!runtimePath.equals("../") && !runtimePath.equals("/")) {  //$NON-NLS-1$//$NON-NLS-2$
				return false;
			}
			if (isClassFolder && !runtimePath.equals("/")) { //$NON-NLS-1$
				return false;
			}
		}
		else {
			if (runtimePath != null && !runtimePath.equals("/WEB-INF/lib") //$NON-NLS-1$
					&& !runtimePath.equals("/WEB-INF/classes") //$NON-NLS-1$
					&& !runtimePath.equals("../")) { //$NON-NLS-1$
				return false;
			}
			if (isClassFolder && !runtimePath.equals("/WEB-INF/classes")) { //$NON-NLS-1$
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Derived from ClasspathDependencyUtil.isClassFolderEntry()
	 */
	private static boolean isClassFolderEntry(final IClasspathEntry entry) {
		if (entry == null || entry.getEntryKind() != IClasspathEntry.CPE_LIBRARY) {
			return false;
		}
		// does the path refer to a file or a folder?
		final IPath entryPath = entry.getPath();
		IPath entryLocation = entryPath;
		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(entryPath);
		if (resource != null) {
			entryLocation = resource.getLocation();
		}
		boolean isFile = true; // by default, assume a jar file
		if (entryLocation.toFile().isDirectory()) {
			isFile = false;
		}
		return !isFile;
	}
	
	/*
	 * Derived from ClasspathDependencyUtil.getRuntimePath()
	 */
	private static String getRuntimePath(final IClasspathAttribute attrib, final boolean isWebApp, final boolean isClassFolder) {
    	if (attrib != null && !attrib.getName().equals(CLASSPATH_COMPONENT_DEPENDENCY)) {
    		return null;
    	}
    	if (attrib == null || attrib.getValue()== null || attrib.getValue().length() == 0) {
    		if (isWebApp) {
    			return isClassFolder ? "/WEB_INF/classes" : "WEB-INF/lib"; //$NON-NLS-1$ //$NON-NLS-2$
    		}
			return isClassFolder ? "/" : "../"; //$NON-NLS-1$ //$NON-NLS-2$
    	}
    	return attrib.getValue();
	}
}
