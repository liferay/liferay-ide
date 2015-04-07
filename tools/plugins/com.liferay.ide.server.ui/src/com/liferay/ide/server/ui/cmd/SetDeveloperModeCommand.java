package com.liferay.ide.server.ui.cmd;

import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerDelegate;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.command.ServerCommand;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class SetDeveloperModeCommand extends ServerCommand
{
    protected boolean useDevelopmentServerMode;
    protected boolean oldUseDevelopmentServerMode;

    public SetDeveloperModeCommand( IServerWorkingCopy server, boolean useDevelopmentServerMode )
    {
        super( server, Messages.editorResourceModifiedTitle );
        this.useDevelopmentServerMode = useDevelopmentServerMode;
    }

    @Override
    public void execute()
    {
        oldUseDevelopmentServerMode =
            ( (PortalServer) server.loadAdapter( PortalServer.class, null ) ).getDeveloperMode();
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setDeveloperMode( useDevelopmentServerMode );
    }

    @Override
    public void undo()
    {
        ( (PortalServerDelegate) server.loadAdapter( PortalServer.class, null ) ).setDeveloperMode( oldUseDevelopmentServerMode );
    }

}
