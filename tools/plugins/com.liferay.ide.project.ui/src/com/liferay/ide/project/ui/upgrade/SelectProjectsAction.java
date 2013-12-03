package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;


public class SelectProjectsAction extends Action implements ICheatSheetAction
{

    public void run( String[] params, ICheatSheetManager manager )
    {
        System.out.println(params);
        System.out.println(manager);

        StringBuilder sb = new StringBuilder();

        for( final IProject project : CoreUtil.getAllProjects() )
        {
            if( project.getName().contains( "upgrade" ) )
            {
                sb.append( project.getName() ).append(',');
            }
        }

        manager.setData( "projectsToUpgrade", sb.toString() );
    }

}
