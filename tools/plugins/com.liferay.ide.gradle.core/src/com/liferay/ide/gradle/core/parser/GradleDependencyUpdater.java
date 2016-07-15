package com.liferay.ide.gradle.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import com.liferay.ide.core.util.CoreUtil;

/**
 * @author Lovett Li
 */
public class GradleDependencyUpdater
{

    private List<ASTNode> nodes;
    private File file;
    private List<String> scripts;

    public GradleDependencyUpdater( File inputfile ) throws MultipleCompilationErrorsException, IOException
    {
        this( IOUtils.toString( new FileInputStream( inputfile ), "UTF-8" ) );
        this.file = inputfile;
    }

    public GradleDependencyUpdater( String script ) throws MultipleCompilationErrorsException
    {
        AstBuilder builder = new AstBuilder();
        nodes = builder.buildFromString( script );
    }

    public FindDependenciesVisitor updateDependency( String dependency ) throws IOException
    {
        FindDependenciesVisitor visitor = new FindDependenciesVisitor();
        walkScript( visitor );
        scripts = Files.readAllLines( Paths.get( file.toURI() ) );

        if( visitor.getDependenceLineNum() == -1 )
        {
            if( !dependency.startsWith( "\t" ) )
            {
                dependency = "\t" + dependency;;
            }

            scripts.add( "" );
            scripts.add( "dependencies {" );
            scripts.add( dependency );
            scripts.add( "}" );
        }
        else
        {
            if( visitor.getColumnNum() != -1 )
            {
                scripts = Files.readAllLines( Paths.get( file.toURI() ) );
                StringBuilder builder = new StringBuilder( scripts.get( visitor.getDependenceLineNum() - 1 ) );
                builder.insert( visitor.getColumnNum() - 2, "\n" + dependency + "\n" );
                String dep = builder.toString();

                if( CoreUtil.isWindows() )
                {
                    dep.replace( "\n", "\r\n" );
                }
                else if( CoreUtil.isMac() )
                {
                    dep.replace( "\n", "\r" );
                }

                scripts.remove( visitor.getDependenceLineNum() - 1 );
                scripts.add( visitor.getDependenceLineNum() - 1, dep );
            }
            else
            {
                scripts.add( visitor.getDependenceLineNum() - 1, dependency );
            }
        }

        return visitor;
    }

    public List<GradleDependency> getAllDependencies()
    {
        FindDependenciesVisitor visitor = new FindDependenciesVisitor();
        walkScript( visitor );

        return visitor.getDependencies();
    }

    public void walkScript( GroovyCodeVisitor visitor )
    {
        for( ASTNode node : nodes )
        {
            node.visit( visitor );
        }
    }

    public List<String> getScripts()
    {
        return scripts;
    }

}
