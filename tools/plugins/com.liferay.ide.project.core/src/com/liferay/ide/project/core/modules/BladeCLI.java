package com.liferay.ide.project.core.modules;

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Processor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class BladeCLI
{
    static IPath cachedBladeCLIPath;
    static String[] projectTemplateNames;
    static File settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();
    static File repoCache = new File( settingsDir, "repoCache" );
    static String repoUrl = "https://liferay-test-01.ci.cloudbees.com/job/blade.tools/lastSuccessfulBuild/artifact/p2_build/generated/p2/index.xml.gz";

    public static String[] execute( String args ) throws BladeCLIException
    {
        final IPath bladeCLIPath = getBladeCLIPath();

        if( bladeCLIPath == null || !bladeCLIPath.toFile().exists() )
        {
            throw new BladeCLIException("Could not get blade cli jar.");
        }

        final Project project = new Project();
        final Java javaTask = new Java();

        javaTask.setProject( project );
        javaTask.setFork( true );
        javaTask.setFailonerror( true );
        javaTask.setJar( bladeCLIPath.toFile() );
        javaTask.setArgs( args );

        final DefaultLogger logger = new DefaultLogger();
        project.addBuildListener(logger);

        final StringBufferOutputStream out = new StringBufferOutputStream();

        logger.setOutputPrintStream( new PrintStream( out ) );
        logger.setMessageOutputLevel(Project.MSG_INFO);

        javaTask.executeJava();

        final List<String> lines = new ArrayList<>();
        final Scanner scanner = new Scanner( out.toString() );

        while( scanner.hasNextLine() )
        {
            lines.add( scanner.nextLine().replaceAll( ".*\\[null\\] ", "" ) );
        }

        scanner.close();

        return lines.toArray( new String[0] );
    }

    private static IPath getBladeCLIPath() throws BladeCLIException
    {
        if( cachedBladeCLIPath == null )
        {
            settingsDir.mkdirs();
            repoCache.mkdirs();

            Processor reporter = new Processor();
            FixedIndexedRepo repo = new FixedIndexedRepo();
            Map<String,String> props = new HashMap<String,String>();
            props.put("name", "index1");
            props.put("locations", repoUrl );
            props.put(FixedIndexedRepo.PROP_CACHE, repoCache.getAbsolutePath());

            repo.setProperties(props);
            repo.setReporter(reporter);

            try
            {
                File[] files = repo.get("com.liferay.blade.cli", "latest");

                File agentJar = files[0];

                cachedBladeCLIPath = new Path( agentJar.getCanonicalPath() );
            }
            catch( Exception e )
            {
                throw new BladeCLIException( "Could not get blade cli jar from repository.", e );
            }
        }

        return cachedBladeCLIPath;
    }

    public static String[] getProjectTemplates() throws BladeCLIException
    {
        if( projectTemplateNames == null )
        {
            Set<String> templateNames = new HashSet<>();
            IPath bladeJarPath = getBladeCLIPath();

            try(ZipFile zip = new ZipFile( bladeJarPath.toFile() ) )
            {
                File temp = File.createTempFile( "templates", ".zip" );
                FileUtil.writeFileFromStream( temp, zip.getInputStream( zip.getEntry( "templates.zip" ) ) );

                try( ZipFile templatesZipFile = new ZipFile( temp ) )
                {
                    Enumeration<? extends ZipEntry> entries = templatesZipFile.entries();

                    while( entries.hasMoreElements() )
                    {
                        ZipEntry entry = entries.nextElement();
                        IPath entryPath = new Path(entry.getName());

                        if( entryPath.segmentCount() > 1 )
                        {
                            templateNames.add( entryPath.segment( 1 ) );
                        }
                    }
                }
            }
            catch( IOException e )
            {
                throw new BladeCLIException( "Unable to open blade cli jar.", e );
            }

            projectTemplateNames = templateNames.toArray( new String[0] );
        }

        return projectTemplateNames;
    }

    public static void main(String[] args) throws Exception
    {
        String[] output = execute( "help" );

        for( String s : output )
        {
            System.out.println( s );
        }
    }

}
