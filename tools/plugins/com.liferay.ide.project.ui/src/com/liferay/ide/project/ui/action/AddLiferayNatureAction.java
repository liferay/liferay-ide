package com.liferay.ide.project.ui.action;


import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Simon Jiang
 */
public class AddLiferayNatureAction extends AbstractObjectAction
{

    @Override
    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IProject project = null;

            Object elem = elems[0];

            if( elem instanceof IProject )
            {
                project = (IProject) elem;

                try
                {
                    LiferayNature.addLiferayNature( project, new NullProgressMonitor() );
                }
                catch( CoreException e )
                {
                    ProjectUI.logError( "Failed to add Liferay Project Nature", e );
                }
            }
        }
    }

}