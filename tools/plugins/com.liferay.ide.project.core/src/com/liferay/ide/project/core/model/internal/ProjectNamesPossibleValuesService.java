package com.liferay.ide.project.core.model.internal;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class ProjectNamesPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( Set<String> values )
    {
        for ( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
        {
            if ( project != null && project.isAccessible() )
            {
                values.add( project.getName() );
            }
        }
    }

}
