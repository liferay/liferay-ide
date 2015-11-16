package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * @author Gregory Amerson
 */
public class OpenUpgradeAssistantHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        UIUtil.async( new Runnable()
        {
            public void run()
            {
                UIUtil.showView( UpgradeAssistantView.ID );
            }
        });

        return null;
    }


}
