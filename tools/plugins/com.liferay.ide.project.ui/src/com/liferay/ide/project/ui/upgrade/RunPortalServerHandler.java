package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;


/**
 * @author Gregory Amerson
 */
public class RunPortalServerHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        try
        {
            Liferay7UpgradeAssistantSettings settings = UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            final String portalName = settings.getPortalSettings().getNewName();

            IServer server = ServerCore.findServer( portalName );

            server.start( "run", new NullProgressMonitor() );
        }
        catch( IOException | CoreException e )
        {
            e.printStackTrace();
        }

        return null;
    }

}
