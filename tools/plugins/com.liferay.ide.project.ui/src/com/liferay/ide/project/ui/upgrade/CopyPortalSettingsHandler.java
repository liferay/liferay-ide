package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;


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
     * This executes on UI thread
     * @throws ExecutionException
     */
    @Override
    protected Object execute( ExecutionEvent event, final Command command ) throws ExecutionException
    {
        try
        {
            Liferay7UpgradeAssistantSettings settings =
                UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

            if( settings == null || settings.getPortalSettings() == null )
            {
                final GetPortalSettingsWizard wizard = new GetPortalSettingsWizard();
                int retcode = new WizardDialog( UIUtil.getActiveShell(), wizard ).open();

                if( retcode == Window.OK )
                {
                    final String previousLocation = wizard.element().getPreviousLiferayLocation().content().toOSString();
                    final String newName = wizard.element().getNewLiferayName().content();
                    final String newLocation = wizard.element().getNewLiferayLocation().content().toOSString();

                    PortalSettings portalSettings = settings.getPortalSettings();

                    if( portalSettings == null )
                    {
                        portalSettings = new PortalSettings();
                    }

                    portalSettings.setPreviousLiferayPortalLocation( previousLocation );
                    portalSettings.setNewName( newName );
                    portalSettings.setNewLiferayPortalLocation( newLocation );

                    settings.setPortalSettings( portalSettings );
                }
            }

            if( settings != null )
            {
                PortalSettings portalSettings = settings.getPortalSettings();

                final File sourceLiferayLocationDir = new File( portalSettings.getPreviousLiferayPortalLocation() );
                final File destLiferayLocationDir = new File( portalSettings.getNewLiferayPortalLocation() );

                final Map<String, File> parameters = new HashMap<>();
                parameters.put( "source", sourceLiferayLocationDir );
                parameters.put( "dest", destLiferayLocationDir );

                new Job("Copy portal settings")
                {
                    @Override
                    protected IStatus run( IProgressMonitor monitor )
                    {
                        try
                        {
                            command.execute( parameters );
                        }
                        catch( CommandException e )
                        {
                            return ProjectUI.createErrorStatus( "Command failed", e );
                        }

                        return org.eclipse.core.runtime.Status.OK_STATUS;
                    }

                }.schedule();

                UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
            }
        }
        catch( IOException e )
        {
            return e;
        }

        return null;
    }

}
