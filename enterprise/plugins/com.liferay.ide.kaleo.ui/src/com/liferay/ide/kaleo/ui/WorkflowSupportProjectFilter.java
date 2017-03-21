/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.kaleo.core.WorkflowSupportManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Gregory Amerson
 */
public class WorkflowSupportProjectFilter extends ViewerFilter
{

    /**
     * Returns <code>false</code> if the given element is the Kaleo Designer support project, and <code>true</code>
     * otherwise.
     *
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     */
    public boolean select( Viewer viewer, Object parentElement, Object element )
    {
        IProject project = null;

        if( element instanceof IJavaProject )
        {
            project = ( (IJavaProject) element ).getProject();
        }
        else if( element instanceof IProject )
        {
            project = (IProject) element;
        }

        if( project != null )
        {
            String projectName = project.getName();

            if( projectName.equals( WorkflowSupportManager.SUPPORT_PROJECT_NAME ) )
            {
                return false;
            }
        }

        return true;
    }

}
