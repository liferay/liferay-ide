package com.liferay.ide.project.ui.cmd;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


public class SelectPortletProjectsToUpgrade extends AbstractHandler
{

    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        MessageDialog.openInformation( Display.getDefault().getActiveShell(), "foo", "bar" );
        return null;
    }


}
