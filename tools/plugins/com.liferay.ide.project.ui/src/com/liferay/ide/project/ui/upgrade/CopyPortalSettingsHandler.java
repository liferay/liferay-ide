package com.liferay.ide.project.ui.upgrade;

import com.liferay.blade.api.Command;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
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

        final Map<String, File> parameters = new HashMap<>();

        parameters.put( "source", op.getSourceLiferayLocation().content().toFile() );
        parameters.put( "dest", op.getDestinationLiferayLocation().content().toFile() );

        Object error = command.execute( parameters );

        if( error != null )
        {
            retval = Status.createErrorStatus( error.toString() );
        }
        else
        {
            retval = Status.createOkStatus();

            // TODO save source/dest locations into preferences so they are used the next time by default value services
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
