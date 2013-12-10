package com.liferay.ide.project.ui.cheatsheet;

import org.eclipse.ui.cheatsheets.CheatSheetListener;
import org.eclipse.ui.cheatsheets.ICheatSheetEvent;


public class TestCheatSheetListener extends CheatSheetListener
{

    @Override
    public void cheatSheetEvent( ICheatSheetEvent event )
    {
        System.out.println(event);
    }

}
