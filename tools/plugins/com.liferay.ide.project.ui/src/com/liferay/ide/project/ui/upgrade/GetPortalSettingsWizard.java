package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.PortalSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class GetPortalSettingsWizard extends SapphireWizard<GetPortalSettingsOp>
{

    public GetPortalSettingsWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( GetPortalSettingsWizard.class ).wizard() );
    }

    private static GetPortalSettingsOp createDefaultOp()
    {
        return GetPortalSettingsOp.TYPE.instantiate();
    }

    public static final Status execute( final GetPortalSettingsOp op, final ProgressMonitor pm )
    {
        final File previousLiferayLocationDir = op.getPreviousLiferayLocation().content().toFile();
        final File newLiferayLocationDir = op.getNewLiferayLocation().content().toFile();
        final String newName = op.getNewLiferayName().content();

        try
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

            UpgradeAssistantSettingsUtil.setObjectToStore( Liferay7UpgradeAssistantSettings.class, settings );
        }
        catch( IOException e )
        {
            return Status.createErrorStatus( e );
        }

        return Status.createOkStatus();
    }
}
