package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;


/**
 * @author Gregory Amerson
 * @author Lovett Li
 * @author Terry Jia
 */
public class CopyPortalSettingsHandler extends AbstractOSGiCommandHandler
{

    public CopyPortalSettingsHandler()
    {
        super( "copyPortalSettings" );
    }

    /**
     * This executes on a job/worker thread
     */
    public static final Status execute( final GetPortalSettingsOp op, final ProgressMonitor pm )
    {
        Status retval = null;

        final Command command = new CopyPortalSettingsHandler().getCommand();

        final Value<Path> previousLiferayLocation = op.getPreviousLiferayLocation();
        final Value<Path> newLiferayLocation = op.getNewLiferayLocation();

        if( previousLiferayLocation == null || previousLiferayLocation.empty() )
        {
            return Status.createErrorStatus( "Previous Liferay Location can not be null or empty." );
        }

        if( newLiferayLocation == null || newLiferayLocation.empty() )
        {
            return Status.createErrorStatus( "New Liferay Location can not be null or empty." );
        }

        final File previousLiferayLocationDir = previousLiferayLocation.content().toFile();
        final File newLiferayLocationDir = newLiferayLocation.content().toFile();
        final String newName = op.getNewLiferayName().content();

        final Map<String, File> parameters = new HashMap<>();
        parameters.put( "source", previousLiferayLocationDir );
        parameters.put( "dest", newLiferayLocationDir );

        try
        {
            synchronized( ProjectUI.getDefault() )
            {
                Liferay7UpgradeAssistantSettings settings =
                    UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

                if( settings == null )
                {
                    settings = new Liferay7UpgradeAssistantSettings();
                }

                PortalSettings portalSettings = settings.getPortalSettings();

                if( portalSettings == null )
                {
                    portalSettings = new PortalSettings();
                }

                portalSettings.setPreviousLiferayPortalLocation( previousLiferayLocationDir.getPath() );
                portalSettings.setNewName( newName );
                portalSettings.setNewLiferayPortalLocation( newLiferayLocationDir.getPath() );

                settings.setPortalSettings( portalSettings );

                try
                {
                    UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                }
            }

            command.execute( parameters );

            retval = Status.createOkStatus();
        }
        catch( Exception e )
        {
            retval = Status.createErrorStatus( e );
        }

        return retval;
    }

    /**
     * This executes on UI thread
     * @throws ExecutionException
     */
    @Override
    protected Object execute( ExecutionEvent event, Command command ) throws ExecutionException
    {
        final GetPortalSettingsWizard wizard = new GetPortalSettingsWizard();

        try
        {
            Liferay7UpgradeAssistantSettings settings =
                UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            if( settings != null )
            {
                PortalSettings portalSettings = settings.getPortalSettings();

                wizard.element().setPreviousLiferayLocation( portalSettings.getPreviousLiferayPortalLocation() );
                wizard.element().setNewLiferayName( portalSettings.getNewName() );
                wizard.element().setNewLiferayLocation( portalSettings.getNewLiferayPortalLocation() );
            }
        }
        catch( IOException e )
        {
        }

        int retval = new WizardDialog( UIUtil.getActiveShell(), wizard ).open();

        if( retval == Window.OK )
        {
            MessageDialog.openInformation( UIUtil.getActiveShell(), "Copy Portal Settings", "Copy successful." );

            return null;
        }
        else
        {
            throw new ExecutionException( "Copy portal settings command failed" );
        }
    }

}
