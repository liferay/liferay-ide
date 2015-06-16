/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Simon Jiang
 */

public class LiferayRuntimeClasspathEntry implements IRuntimeClasspathEntry {

    public LiferayRuntimeClasspathEntry(IRuntimeClasspathEntry runtimeClasspathEntry)
    {
        this.runtimeClasspathEntry = runtimeClasspathEntry;
    }

    private IRuntimeClasspathEntry runtimeClasspathEntry;

    @Override
    public int getType()
    {
        return runtimeClasspathEntry.getType();
    }

    @Override
    public String getMemento() throws CoreException
    {
        Document doc = DebugPlugin.newDocument();
        Element node = doc.createElement("runtimeClasspathEntry"); //$NON-NLS-1$
        doc.appendChild(node);
        node.setAttribute("type", (new Integer(getType())).toString()); //$NON-NLS-1$
        node.setAttribute("path", (new Integer(getClasspathProperty())).toString()); //$NON-NLS-1$
        switch (getType()) {
            case PROJECT :
                node.setAttribute("projectName", getPath().lastSegment()); //$NON-NLS-1$
                break;
            case ARCHIVE :
                IResource res = getResource();
                if (res == null) {
                    node.setAttribute("externalArchive", getPath().toString()); //$NON-NLS-1$
                } else {
                    node.setAttribute("internalArchive", res.getFullPath().toString()); //$NON-NLS-1$
                }
                break;
            case VARIABLE :
            case CONTAINER :
                node.setAttribute("containerPath", getPath().toString()); //$NON-NLS-1$
                break;
        }
        if (getSourceAttachmentPath() != null) {
            node.setAttribute("sourceAttachmentPath", getSourceAttachmentPath().toString()); //$NON-NLS-1$
        }
        if (getSourceAttachmentRootPath() != null) {
            node.setAttribute("sourceRootPath", getSourceAttachmentRootPath().toString()); //$NON-NLS-1$
        }
        if (getJavaProject() != null) {
            node.setAttribute("javaProject", getJavaProject().getElementName()); //$NON-NLS-1$
        }
        return DebugPlugin.serializeDocument(doc);
    }

    @Override
    public IPath getPath()
    {
        return runtimeClasspathEntry.getPath();
    }

    @Override
    public IResource getResource()
    {
        switch (getType()) {
        case CONTAINER:
        case VARIABLE:
        case ARCHIVE:
            return null;
        default:
            return getResource(getPath());
        }
    }

    protected IResource getResource(IPath path) {
        if (path != null) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            // look for files or folders with the given path
            @SuppressWarnings("deprecation")
            IFile[] files = root.findFilesForLocation(path);
            if (files.length > 0) {
                return files[0];
            }
            @SuppressWarnings("deprecation")
            IContainer[] containers = root.findContainersForLocation(path);
            if (containers.length > 0) {
                return containers[0];
            }
            if (path.getDevice() == null) {
                // search relative to the workspace if no device present
                return root.findMember(path);
            }
        }
        return null;
    }

    @Override
    public IPath getSourceAttachmentPath()
    {
        return runtimeClasspathEntry.getSourceAttachmentPath();
    }


    @Override
    public void setSourceAttachmentPath( IPath path )
    {
        runtimeClasspathEntry.setSourceAttachmentPath(path);

    }

    @Override
    public IPath getSourceAttachmentRootPath()
    {
        return runtimeClasspathEntry.getSourceAttachmentRootPath();
    }

    @Override
    public void setSourceAttachmentRootPath( IPath path )
    {
        runtimeClasspathEntry.setSourceAttachmentRootPath( path );

    }

    @Override
    public int getClasspathProperty()
    {
        return runtimeClasspathEntry.getClasspathProperty();
    }

    @Override
    public void setClasspathProperty( int location )
    {
        runtimeClasspathEntry.setClasspathProperty( location );

    }

    @Override
    public String getLocation()
    {
        return runtimeClasspathEntry.getLocation();
    }

    @Override
    public String getSourceAttachmentLocation()
    {
        return runtimeClasspathEntry.getSourceAttachmentLocation();
    }

    @Override
    public String getSourceAttachmentRootLocation()
    {
        return runtimeClasspathEntry.getSourceAttachmentRootLocation();
    }

    @Override
    public String getVariableName()
    {
        return runtimeClasspathEntry.getVariableName();
    }

    @Override
    public IClasspathEntry getClasspathEntry()
    {
        return runtimeClasspathEntry.getClasspathEntry();
    }

    @Override
    public IJavaProject getJavaProject()
    {
        return runtimeClasspathEntry.getJavaProject();
    }

}
