package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;

import java.util.Properties;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


/**
 * @author Gregory Amerson
 */
public class LiferayMavenProject implements ILiferayProject
{

    private IProject project;
    private IMavenProjectFacade mavenProjectFacade;

    public LiferayMavenProject( IProject project, IMavenProjectFacade mavenProjectFacade )
    {
        this.project = project;
        this.mavenProjectFacade = mavenProjectFacade;
    }

    public IPath getAppServerPortalDir()
    {
        IPath retval = null;

        final MavenProject mavenProject = this.mavenProjectFacade.getMavenProject();

        final String appServerPortalDir =
            LiferayMavenUtil.getLiferayMavenPluginConfig( mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR );

        if( ! CoreUtil.isNullOrEmpty( appServerPortalDir ) )
        {
            retval = new Path( appServerPortalDir );
        }

        return retval;
    }

    public IPath getAppServerLibGlobalDir()
    {
        IPath retval = null;

        final MavenProject mavenProject = this.mavenProjectFacade.getMavenProject();

        final String appServerPortalDir =
            LiferayMavenUtil.getLiferayMavenPluginConfig( mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_LIB_GLOBAL_DIR );

        if( ! CoreUtil.isNullOrEmpty( appServerPortalDir ) )
        {
            retval = new Path( appServerPortalDir );
        }

        return retval;
    }

    public IPath getAppServerDeployDir()
    {
        IPath retval = null;

        final MavenProject mavenProject = this.mavenProjectFacade.getMavenProject();

        final String appServerPortalDir =
            LiferayMavenUtil.getLiferayMavenPluginConfig( mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_DEPLOY_DIR );

        if( ! CoreUtil.isNullOrEmpty( appServerPortalDir ) )
        {
            retval = new Path( appServerPortalDir );
        }

        return retval;
    }

    public String[] getHookSupportedProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPortalVersion()
    {
        MavenProject mavenProject = this.mavenProjectFacade.getMavenProject();

        return LiferayMavenUtil.getLiferayMavenPluginConfig( mavenProject, ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION );
    }

    public Properties getPortletCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getPortletEntryCategories()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getLibraryPath( String string )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath[] getUserLibs()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
