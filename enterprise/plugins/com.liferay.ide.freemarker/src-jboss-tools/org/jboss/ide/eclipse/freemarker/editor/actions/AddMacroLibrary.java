/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor.actions;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class AddMacroLibrary implements IObjectActionDelegate {

    private IWorkbenchPart part;

    public void run(IAction action) {
        ISelectionProvider provider = part.getSite().getSelectionProvider();
        if (null != provider) {
            if (provider.getSelection() instanceof IStructuredSelection) {
                try {
                    IStructuredSelection selection = (IStructuredSelection) provider.getSelection();
                    Object[] obj = selection.toArray();
                    List documents = new ArrayList();
                    for (int i=0; i<obj.length; i++) {
                        if (obj[i] instanceof IFile) {
                            IFile file = (IFile) obj[i];
                            documents.add(file);
                        }
                        else if (obj[i] instanceof JarEntryFile) {
                        	JarEntryFile jef = (JarEntryFile) obj[i];
                        	documents.add(jef);
                        	System.out.println(jef.getFullPath().makeAbsolute());
                        	System.out.println(jef.getFullPath().makeRelative());
                        	IPath path = jef.getFullPath();
                        	System.out.println(path);
                        	System.out.println(jef.getName());
                        	IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(jef.getFullPath());
                        	System.out.println(resource);
                        }
                        else if (obj[i] instanceof IStorage) {
                        	
                        }
                    }
                    IProject project = null;
                    if (documents.size() > 0) {
                    	// what project?
                    	HashSet projects = new HashSet();
                    	IProject[] p = ResourcesPlugin.getWorkspace().getRoot().getProjects();
                    	for (int i=0; i<p.length; i++) {
                    		projects.add(p[i]);
                    	}
                    	ProjectSelectionDialog dialog = new ProjectSelectionDialog(Display.getCurrent().getActiveShell(), projects);
                    	dialog.setTitle(Messages.AddMacroLibrary_Title);
                    	dialog.setMessage(Messages.AddMacroLibrary_Message);
                    	int rtn = dialog.open();
                    	if (rtn == IDialogConstants.OK_ID) {
                    		if (dialog.getFirstResult() instanceof IJavaProject) {
                    			project = ((IJavaProject) dialog.getFirstResult()).getProject();
                    		}
                    		else {
                    			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.AddMacroLibrary_Error, Messages.AddMacroLibrary_ErrorDesc);
                    		}
                    	}
                    }
                    if (null != project) {
                    	ConfigurationManager.getInstance(project).associateMappingLibraries(
                    			documents, Display.getCurrent().getActiveShell());
                    }
                }
                catch (Exception e) {
                    Plugin.error(e);
                }
            }
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.part = targetPart;
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }
   
    protected boolean shouldForce () {
        return false;
    }
}
