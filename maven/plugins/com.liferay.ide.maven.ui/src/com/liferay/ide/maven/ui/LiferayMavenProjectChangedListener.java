package com.liferay.ide.maven.ui;

import com.liferay.ide.project.core.util.ProjectUtil;

import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValManager.UseProjectPreferences;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValidatorMutable;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;


/**
 * @author Simon Jiang
 */

@SuppressWarnings( { "restriction" } )
public class LiferayMavenProjectChangedListener implements IMavenProjectChangedListener
{

    public void mavenProjectChanged( MavenProjectChangedEvent[] events, IProgressMonitor monitor )
    {
        if ( events[0].getKind() == MavenProjectChangedEvent.KIND_CHANGED )
        {
            try
            {
                IMavenProjectFacade  projectFacade = events[0].getMavenProject();
                if ( projectFacade != null )
                {
                    MavenProject mavenProject = projectFacade.getMavenProject( new NullProgressMonitor() );
                    if ( mavenProject!= null )
                    {
                        final IProject project = ProjectUtil.getProject( projectFacade.getProject().getName() );
                        project.getWorkspace().getRoot().accept(
                            new IResourceProxyVisitor() 
                            {
                                public boolean visit( IResourceProxy proxy ) 
                                {
                                    switch( proxy.getType() ) 
                                    {
                                        case IResource.FOLDER :
                                            if (proxy.getName().equals( "custom_jsps" ) ) 
                                            {
                                                addJSPSyntaxValidationExclude(
                                                    project, (IFolder) project.getWorkspace().getRoot().getFolder(
                                                        new Path( proxy.requestFullPath().toString() ) ) );
                                                return false;
                                            }
                                    }
                                    return true;
                                }
                            },
                            IResource.NONE
                        );
                    }
                    
                }
                
            }
            catch(Exception e)
            {
                LiferayMavenUI.logError( "Unable to prepare to remove jsp syntax validation folder exclude rule.", e ); //$NON-NLS-1$
            }
        }

    }

    private void addJSPSyntaxValidationExclude( IProject project, IFolder customFolder )
    {
        try
        {
            Validator[] vals =
                ValManager.getDefault().getValidatorsConfiguredForProject( project, UseProjectPreferences.MustUse );

            ValidatorMutable[] validators = new ValidatorMutable[vals.length];

            for( int i = 0; i < vals.length; i++ )
            {
                validators[i] = new ValidatorMutable( vals[i] );

                if( "org.eclipse.jst.jsp.core.JSPBatchValidator".equals( validators[i].getId() ) ) //$NON-NLS-1$
                {
                    // check for exclude group
                    FilterGroup excludeGroup = null;

                    for( FilterGroup group : validators[i].getGroups() )
                    {
                        if( group.isExclude() )
                        {
                            excludeGroup = group;
                            break;
                        }
                    }

                    String customJSPFolderPattern =
                        customFolder.getFullPath().makeRelativeTo( customFolder.getProject().getFullPath() ).toPortableString();

                    if( excludeGroup != null )
                    {
                        for( FilterRule rule : excludeGroup.getRules() )
                        {
                            if( customJSPFolderPattern.equals( rule.getPattern() ) )
                            {
                                FilterGroup newExcludeGroup = FilterGroup.removeRule( excludeGroup, rule );
                                validators[i].replaceFilterGroup( excludeGroup, newExcludeGroup );
                                break;
                            }
                        }
                    }

                }
            }

            ProjectConfiguration pc = ConfigurationManager.getManager().getProjectConfiguration( project );
            pc.setDoesProjectOverride( true );

            ProjectPreferences pp = new ProjectPreferences( project, true, false, null );

            ValPrefManagerProject vpm = new ValPrefManagerProject( project );
            vpm.savePreferences( pp, validators );
        }
        catch( Exception e )
        {
            LiferayMavenUI.logError( "Unable to remove jsp syntax validation folder exclude rule.", e ); //$NON-NLS-1$
        }
    }

}
