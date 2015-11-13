
package com.liferay.ide.project.ui.upgrade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Model;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.MavenModelManager;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import com.liferay.blade.api.Command;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Andy Wu
 */
public class ConvertJspHookHandler extends AbstractOSGiCommandHandler
{

    public ConvertJspHookHandler()
    {
        super( "ConvertJspHook" );
    }

    /**
     * This executes on a job/worker thread
     */
    public static final Status execute( final ConvertJspHookOp op, final ProgressMonitor pm )
    {
        Status retval = null;

        final Command command = new ConvertJspHookHandler().getCommand();

        final Map<String, String> parameters = new HashMap<>();

        parameters.put( "sourcePath", op.getSourceJspHookLocation().content().toString() );
        parameters.put( "targetPath", Platform.getLocation().toString() );

        try
        {
            String targetPath = (String) command.execute( parameters );
            IPath project = new Path( targetPath + "/pom.xml" );

            // import maven project
            doCreateNewProject( project, new NullProgressMonitor() );

            retval = Status.createOkStatus();
        }
        catch( Exception e )
        {
            retval = Status.createErrorStatus( e );
        }

        return retval;
    }

    /**
     * This executes on UI thread
     *
     * @throws ExecutionException
     */
    @Override
    protected Object execute( ExecutionEvent event, Command command ) throws ExecutionException
    {
        final ConvertJspHookWizard wizard = new ConvertJspHookWizard();

        int retval = new WizardDialog( UIUtil.getActiveShell(), wizard ).open();

        if( retval == Window.OK )
        {
            MessageDialog.openInformation( UIUtil.getActiveShell(), "Convert Jsp Hooks", "Convert successful." );

            return null;
        }
        else
        {
            throw new ExecutionException( "Convert Jsp Hook command failed" );
        }
    }

    public static void doCreateNewProject( IPath pomFilePath, IProgressMonitor monitor ) throws CoreException
    {
        IPath pomPath = pomFilePath;

        if( pomPath != null && pomPath.toFile().exists() )
        {
            File pomFile = new File( pomPath.toPortableString() );
            MavenModelManager mavenModelManager = MavenPlugin.getMavenModelManager();
            final ResolverConfiguration resolverConfig = new ResolverConfiguration();
            final ArrayList<MavenProjectInfo> projectInfos = new ArrayList<MavenProjectInfo>();

            Model model = mavenModelManager.readMavenModel( pomFile );
            MavenProjectInfo projectInfo = new MavenProjectInfo( pomFile.getName(), pomFile, model, null );
            // setBasedirRename( projectInfo );

            projectInfos.add( projectInfo );

            ProjectImportConfiguration importConfiguration = new ProjectImportConfiguration( resolverConfig );

            final IProjectConfigurationManager projectConfigurationManager =
                MavenPlugin.getProjectConfigurationManager();

            projectConfigurationManager.importProjects( projectInfos, importConfiguration, monitor );
        }
    }

}
