package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.ServerDelegate;


/**
 * @author Gregory Amerson
 */
public class SetupPortalServerHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        String newName = null;
        String newLocation = null;
        Liferay7UpgradeAssistantSettings settings = null;

        try
        {
            settings = UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            if( settings == null )
            {
                settings = new Liferay7UpgradeAssistantSettings();
            }
            else
            {
                final PortalSettings portalSettings = settings.getPortalSettings();
                newName = portalSettings.getNewName();
                newLocation = portalSettings.getNewLiferayPortalLocation();
            }
        }
        catch( IOException e )
        {
        }

        if( newLocation == null )
        {
            final GetPortalSettingsWizard wizard = new GetPortalSettingsWizard();
            int retcode = new WizardDialog( UIUtil.getActiveShell(), wizard ).open();

            if( retcode == Window.OK )
            {
                newName = wizard.element().getNewLiferayName().content();
                newLocation = wizard.element().getNewLiferayLocation().content().toOSString();

                PortalSettings portalSettings = settings.getPortalSettings();

                if( portalSettings == null )
                {
                    portalSettings = new PortalSettings();
                }

                portalSettings.setNewName( newName );
                portalSettings.setNewLiferayPortalLocation( newLocation );

                settings.setPortalSettings( portalSettings );
            }
        }

        if( newName != null && newLocation != null )
        {
            try
            {
                final IProgressMonitor npm = new NullProgressMonitor();

                IRuntimeWorkingCopy portalRuntimeWC = ServerCore.findRuntimeType( PortalRuntime.ID ).createRuntime( newName, npm );
                portalRuntimeWC.setLocation( new Path( newLocation ) );
                portalRuntimeWC.setName( newName );

                IRuntime portalRuntime = portalRuntimeWC.save( true, npm );

                IServerWorkingCopy serverWC = ServerCore.findServerType( PortalServer.ID ).createServer( newName, null, portalRuntime, npm );

                ServerDelegate delegate = (ServerDelegate) serverWC.loadAdapter( ServerDelegate.class, null );
                delegate.importRuntimeConfiguration( serverWC.getRuntime(), null );
                IServer server = serverWC.save( true, npm );

                if( server != null )
                {
                    UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
                }

                MessageDialog.openInformation( UIUtil.getActiveShell(), "Setup Portal Server", "Liferay 7 server setup complete." );
            }
            catch( CoreException | IOException e )
            {
                e.printStackTrace();
            }
        }

        return null;
    }

}
