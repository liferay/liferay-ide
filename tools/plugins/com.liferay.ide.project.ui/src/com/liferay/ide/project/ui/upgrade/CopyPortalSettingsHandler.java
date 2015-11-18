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
    public static final Status execute( final CopyPortalSettingsOp op, final ProgressMonitor pm )
    {
        Status retval = null;

        final Command command = new CopyPortalSettingsHandler().getCommand();

        if( op.getSourceLiferayLocation() == null || op.getSourceLiferayLocation().empty() )
        {
            return Status.createErrorStatus( "Previous Liferay Location can not be empty" );
        }

        if( op.getDestinationLiferayLocation() == null || op.getDestinationLiferayLocation().empty() )
        {
            return Status.createErrorStatus( "New Liferay Location can not be empty" );
        }

        final File sourceLiferayLocationDir = op.getSourceLiferayLocation().content().toFile();
        final File destLiferayLocationDir = op.getDestinationLiferayLocation().content().toFile();
        final String sourceName = op.getSourceLiferayName().content();
        final String destName = op.getDestinationLiferayName().content();

        final Map<String, File> parameters = new HashMap<>();
        parameters.put( "source", sourceLiferayLocationDir );
        parameters.put( "dest", destLiferayLocationDir );

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

                portalSettings.setPreviousName( sourceName );
                portalSettings.setPreviousLiferayPortalLocation( sourceLiferayLocationDir.getPath() );
                portalSettings.setNewName( destName );
                portalSettings.setNewLiferayPortalLocation( destLiferayLocationDir.getPath() );

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
        final CopyPortalSettingsWizard wizard = new CopyPortalSettingsWizard();

        try
        {
            Liferay7UpgradeAssistantSettings settings =
                UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            if( settings != null )
            {
                PortalSettings portalSettings = settings.getPortalSettings();

                wizard.element().setSourceLiferayName( portalSettings.getPreviousName() );
                wizard.element().setSourceLiferayLocation( portalSettings.getPreviousLiferayPortalLocation() );
                wizard.element().setDestinationLiferayName( portalSettings.getNewName() );
                wizard.element().setDestinationLiferayLocation( portalSettings.getNewLiferayPortalLocation() );
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
