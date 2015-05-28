
package com.liferay.ide.portlet.ui.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public class PortletUITestBase extends ProjectCoreBase
{

    public IProject createProject( String projectName, PluginType pluginType, String portletFramework )
        throws Exception
    {
        IProject[] projects = CoreUtil.getWorkspaceRoot().getProjects();
        if( projects != null )
        {
            for( IProject project : projects )
            {
                if( project.exists() && project.getName().contains( projectName ) )
                {
                    return project;
                }
            }
        }

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( pluginType );
        op.setPortletFramework( portletFramework );
        op.setIncludeSampleCode( false );
        return createAntProject( op );
    }

    public boolean checkFileExist( IProject project, String fileName, String path )
    {
        IFolder docroot = CoreUtil.getDefaultDocrootFolder( project );

        if( docroot.getFolder( path ).exists() )
        {
            return docroot.getFolder( path ).getFile( fileName ).exists();
        }

        return docroot.getFile( fileName ).exists();
    }
}
