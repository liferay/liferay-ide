package com.liferay.ide.project.core.modules;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.service.command.CommandProcessor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.blade.api.ProjectBuild;
import com.liferay.blade.api.ProjectTemplate;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Gregory Amerson
 */
public class ModulesUtil
{
    public static ProjectTemplate[] getProjectTemplates()
    {
        final ServiceTracker<ProjectTemplate, ProjectTemplate> tracker = new ServiceTracker<>(
            FrameworkUtil.getBundle( ModulesUtil.class ).getBundleContext(), ProjectTemplate.class, null );

        tracker.open();

        final ProjectTemplate[] templates = tracker.getServices( new ProjectTemplate[0] );

        return templates != null ? templates : new ProjectTemplate[0];
    }

    public static IStatus createModuleProject( File workDir, ProjectTemplate template, ProjectBuild build, String name,
        String className, String serviceName, String packageName )
    {
        IStatus retVal = Status.OK_STATUS;

        final BundleContext bundleContext = FrameworkUtil.getBundle( ModulesUtil.class ).getBundleContext();

        final Collection<ServiceReference<Command>> refs;
        try
        {
            refs = bundleContext.getServiceReferences( Command.class, "(" + CommandProcessor.COMMAND_FUNCTION + "=createProject)" );

            if( refs != null && refs.size() > 0 )
            {
                final Command command = bundleContext.getService( refs.iterator().next() );

                final Map<String, Object> parameters = new HashMap<>();

                parameters.put("workDir", workDir);
                parameters.put("projectTemplate", template);
                parameters.put("buildValue", build.toString());
                parameters.put("name", name);
                parameters.put("classname", className );
                parameters.put("service", serviceName );
                parameters.put("packageName", packageName );

                final Object errors = command.execute( parameters );

                if( errors != null )
                {
                    retVal = ProjectCore.createErrorStatus( "Create this Project error " );
                }
            }
            else
            {
                retVal = ProjectCore.createErrorStatus( "Unable to create this Project " );
            }
        }
        catch( InvalidSyntaxException | CommandException e )
        {
            retVal = ProjectCore.createErrorStatus( "Can not invoke blade tools to create project " );
        }

        return retVal;
    }

}
