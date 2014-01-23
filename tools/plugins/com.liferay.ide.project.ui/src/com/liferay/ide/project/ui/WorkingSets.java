/*******************************************************************************
 * Copyright (c) 2013 Igor Fedorenko
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Igor Fedorenko - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.project.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;


/**
 * Copied from org.eclipse.m2e.core.ui.internal.WorkingSets
 */
public class WorkingSets
{

    public static String[] getWorkingSets() {
        List<String> workingSets = new ArrayList<String>();
        IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
        for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
          if(workingSet.isVisible()) {
            workingSets.add(workingSet.getName());
          }
        }
        return workingSets.toArray(new String[workingSets.size()]);
    }

    /**
     * Returns one of the working sets the element directly belongs to. Returns {@code null} if the element does not
     * belong to any working set.
     *
     * @since 1.5
     */
    public static IWorkingSet getAssignedWorkingSet(IResource element) {
      IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
        for(IAdaptable adaptable : workingSet.getElements()) {
          if(adaptable.getAdapter(IResource.class) == element) {
            return workingSet;
          }
        }
      }
      return null;
    }

    /**
     * Returns all working sets the element directly belongs to. Returns empty collection if the element does not belong
     * to any working set. The order of returned working sets is not specified.
     *
     * @since 1.5
     */
    public static List<IWorkingSet> getAssignedWorkingSets(IResource element) {
      List<IWorkingSet> list = new ArrayList<IWorkingSet>();
      IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
      for(IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
        for(IAdaptable adaptable : workingSet.getElements()) {
          if(adaptable.getAdapter(IResource.class) == element) {
            list.add(workingSet);
          }
        }
      }
      return list;
    }

}
