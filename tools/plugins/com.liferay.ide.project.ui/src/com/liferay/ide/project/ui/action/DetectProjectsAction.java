package com.liferay.ide.project.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;


public class DetectProjectsAction extends Action implements ICheatSheetAction
{

    public void run( String[] params, ICheatSheetManager manager )
    {
        String data = manager.getData( "projectsToUpgrade" );

        System.out.println(data);
    }

}
