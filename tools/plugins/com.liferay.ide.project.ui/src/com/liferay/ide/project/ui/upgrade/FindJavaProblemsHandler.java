package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.migration.MigrateProjectHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * @author Gregory Amerson
 */
public class FindJavaProblemsHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        try
        {
            Liferay7UpgradeAssistantSettings settings = UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            String[] projectLocations = settings.getJavaProjectLocations();

            List<IPath> locations = new ArrayList<>();

            for( String projectLocation : projectLocations )
            {
                locations.add( new Path( projectLocation) );
            }

            new MigrateProjectHandler().findMigrationProblems( locations.toArray( new IPath[0] ) );
        }
        catch( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return null;
    }

}
