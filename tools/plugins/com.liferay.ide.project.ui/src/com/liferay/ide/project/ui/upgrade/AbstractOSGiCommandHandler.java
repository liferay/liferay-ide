package com.liferay.ide.project.ui.upgrade;

import java.util.Collection;

import org.apache.felix.service.command.CommandProcessor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.liferay.blade.api.Command;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractOSGiCommandHandler extends AbstractHandler
{

    private final String _function;

    public AbstractOSGiCommandHandler( String osgiCommandFunction )
    {
        _function = osgiCommandFunction;
    }

    protected Command getCommand()
    {
        try
        {
            final BundleContext bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();

            final Collection<ServiceReference<Command>> refs =
                bundleContext.getServiceReferences( Command.class, "(" +CommandProcessor.COMMAND_FUNCTION + "=" + _function + ")" );

            if( refs != null && refs.size() > 0 )
            {
                final ServiceReference<Command> ref = refs.iterator().next();

                return bundleContext.getService( ref );
            }
        }
        catch( InvalidSyntaxException e )
        {
        }

        return null;
    }

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        return execute( event, getCommand() );
    }

    protected abstract Object execute( ExecutionEvent event, Command command ) throws ExecutionException;

}
