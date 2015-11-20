package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * @author Gregory Amerson
 */
public class SelectJavaProjectsHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
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

            try
            {
                Liferay7UpgradeAssistantSettings settings = UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

                if ( settings == null )
                {
                    settings = new Liferay7UpgradeAssistantSettings();
                }

                List<String> locations = new ArrayList<>();

                for( Object project : projects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject javaProject = (IJavaProject) project;

                        locations.add( javaProject.getProject().getLocation().toOSString() );
                    }
                }

                settings.setJavaProjectLocations( locations.toArray( new String[0] ) );

                UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
            }
            catch( IOException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        return null;
    }

}
