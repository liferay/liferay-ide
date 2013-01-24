package com.liferay.ide.maven.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;


public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayMavenProjectProvider()
    {
        super(IProject.class);
    }

    public ILiferayProject provide( Object type )
    {
        if( type instanceof IProject )
        {
            final IProject project = (IProject) type;

            try
            {
                if( project.hasNature( IMavenConstants.NATURE_ID ) || project.getFile( "pom.xml" ).exists() )
                {
                    final IMavenProjectFacade mavenProjectFacade =
                        MavenPlugin.getMavenProjectRegistry().create( project, null );

                    return new LiferayMavenProject( project, mavenProjectFacade );
                }
            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError( "Unable to create ILiferayProject from maven project " + project.getName(), e );
            }
        }

        return null;
    }

}
