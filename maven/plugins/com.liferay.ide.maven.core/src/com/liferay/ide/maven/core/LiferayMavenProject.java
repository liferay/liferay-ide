package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayProject;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


public class LiferayMavenProject implements ILiferayProject
{

    private IProject project;
    private IMavenProjectFacade mavenProject;

    public LiferayMavenProject( IProject project, IMavenProjectFacade mavenProjectFacade )
    {
        this.project = project;
        this.mavenProject = mavenProjectFacade;
    }

    public IPath getAppServerPortalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerLibGlobalDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public IPath getAppServerDeployDir()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getHookSupportedProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPortalVersion()
    {
        // TODO Auto-generated method stub
        return null;
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
