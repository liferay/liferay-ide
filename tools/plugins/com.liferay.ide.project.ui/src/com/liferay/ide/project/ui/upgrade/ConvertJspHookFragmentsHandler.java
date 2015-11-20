package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.ui.WorkspaceHelper;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * @author Gregory Amerson
 */
public class ConvertJspHookFragmentsHandler extends AbstractOSGiCommandHandler
{

    public ConvertJspHookFragmentsHandler()
    {
        super( "ConvertJspHook" );
    }

    @Override
    protected Object execute( ExecutionEvent event, Command command ) throws ExecutionException
    {
        ViewerFilter filter = new ViewerFilter()
        {
            @Override
            public boolean select( Viewer viewer, Object parentElement, Object element )
            {
                if( element instanceof IJavaProject )
                {
                    IJavaProject project = (IJavaProject) element;

                    if( CoreUtil.isLiferayProject( project.getProject() ) )
                    {
                        return true;
                    }
                }

                return false;
            }
        };

        LiferayProjectSelectionDialog dialog = new LiferayProjectSelectionDialog( UIUtil.getActiveShell(), filter );

        dialog.open();

        final Object[] projects = dialog.getResult();

        if( projects != null && projects.length > 0 )
        {
            IJavaProject javaProject = (IJavaProject) projects[0];

            File srcDir = javaProject.getProject().getLocation().toFile();

            File destDir = javaProject.getProject().getLocation().removeLastSegments( 2 ).append( "modules" ).append( srcDir.getName() + "-module" ).toFile();

            destDir.mkdirs();

            Map<String, File> parameters = new HashMap<>();
            parameters.put( "sourcePath", srcDir );
            parameters.put( "targetPath", destDir );

            try
            {
                command.execute( parameters );

                new WorkspaceHelper().openDir( destDir.getAbsolutePath() );
            }
            catch( CommandException e )
            {
                e.printStackTrace();
            }
        }

        return null;
    }

}
