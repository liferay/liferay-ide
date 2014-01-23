/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.ui.WorkingSets;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;


/**
 * Copied from org.eclipse.m2e.core.ui.internal.actions.SelectionUtil
 */
public class SelectionUtil
{

    /**
     * Checks if the object belongs to a given type and returns it or a suitable adapter.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getType(Object element, Class<T> type) {
      if(element == null) {
        return null;
      }
      if(type.isInstance(element)) {
        return (T) element;
      }
      if(element instanceof IAdaptable) {
        T adapter = (T) ((IAdaptable) element).getAdapter(type);
        if(adapter != null) {
          return adapter;
        }
      }
      return (T) Platform.getAdapterManager().getAdapter(element, type);
    }

    public static IWorkingSet getSelectedWorkingSet(IStructuredSelection selection) {
        Object element = selection == null ? null : selection.getFirstElement();
        if(element == null) {
          return null;
        }

        IWorkingSet workingSet = getType(element, IWorkingSet.class);
        if(workingSet != null) {
          return workingSet;
        }

        IResource resource = getType(element, IResource.class);
        if(resource != null) {
          return WorkingSets.getAssignedWorkingSet(resource.getProject());
        }

        return null;

//        IResource resource = getType(element, IResource.class);
//        if(resource != null) {
//          return getWorkingSet(resource);
//        }

//        IPackageFragmentRoot fragment = getType(element, IPackageFragmentRoot.class);
//        if(fragment != null) {
//          IJavaProject javaProject = fragment.getJavaProject();
//          if(javaProject != null) {
//            IResource resource = getType(javaProject, IResource.class);
//            if(resource != null) {
//              return getWorkingSet(resource.getProject());
//            }
//          }
//        }
      }

}
