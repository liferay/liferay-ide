package com.liferay.ide.project.core.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IServer;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Lovett Li
 */
public class ServiceWrapperCommand
{

    private final IServer _server;

    public ServiceWrapperCommand( IServer server )
    {
        _server = server;
    }

    public String[] getServiceWrapper() throws Exception
    {

        if( _server == null )
        {
            return getStaticServiceWrapper();
        }
        else
        {
            String[] wrappers = getDynamicServiceWrapper();
            updateServiceWrapperStaticFile( wrappers );

            return wrappers;
        }
    }

    @SuppressWarnings( "unchecked" )
    public String[] getStaticServiceWrapper() throws Exception
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/wrappers-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            final ObjectMapper mapper = new ObjectMapper();

            List<String> map = mapper.readValue( servicesFile, List.class );
            String[] wrappers = map.toArray( new String[0] );

            return wrappers;
        }

        throw new FileNotFoundException( "can't find static services file wrapper-static.json" );
    }

    public String[] getDynamicServiceWrapper()
    {
        final List<File> targetJarFile = new ArrayList<>();
        final IPath bundleLibPath = _server.getRuntime().getLocation();
        final List<String> wrapperList = new ArrayList<>();
        List<File> libFiles;

        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.append( "lib" ).toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( "portal-service.jar" ) )
                {
                    targetJarFile.add( lib );
                }
            }

            libFiles = FileListing.getFileListing( new File( bundleLibPath.append( "../osgi" ).toOSString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( "api.jar" ) )
                {
                    targetJarFile.add( lib );
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        for( File jarFile : targetJarFile )
        {
            try(JarFile jar = new JarFile( jarFile ))
            {
                Enumeration<JarEntry> enu = jar.entries();

                while( enu.hasMoreElements() )
                {
                    JarEntry entry = enu.nextElement();
                    String name = entry.getName();

                    if( name.endsWith( "Wrapper.class" ) && !( name.contains( "$" ) ) )
                    {
                        name = name.replaceAll( "\\\\", "." ).replaceAll( "/", "." );
                        name = name.substring( 0, name.lastIndexOf( "." ) );
                        wrapperList.add( name );
                    }
                }
            }
            catch( IOException e )
            {
            }
        }

        return wrapperList.toArray( new String[0] );
    }

    private void updateServiceWrapperStaticFile( final String[] wrapperList ) throws Exception
    {
        final File wrappersFile = checkStaticWrapperFile();
        final ObjectMapper mapper = new ObjectMapper();

        final Job job = new WorkspaceJob( "Update ServiceWrapper static file...")
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor )
            {
                try
                {
                    mapper.writeValue( wrappersFile, wrapperList );
                }
                catch( IOException e )
                {
                    return Status.CANCEL_STATUS;
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();

    }

    private File checkStaticWrapperFile() throws IOException
    {
        final URL url =
            FileLocator.toFileURL( ProjectCore.getDefault().getBundle().getEntry( "OSGI-INF/wrappers-static.json" ) );
        final File servicesFile = new File( url.getFile() );

        if( servicesFile.exists() )
        {
            return servicesFile;
        }

        throw new FileNotFoundException( "can't find static services file wrappers-static.json" );
    }
}
