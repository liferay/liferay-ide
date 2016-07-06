package com.liferay.ide.gradle.core.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class GradleDependenceParser
{

    private List<ASTNode> nodes;
    private File file;
    private List<String> scripts;

    public GradleDependenceParser( File inputfile ) throws MultipleCompilationErrorsException, IOException
    {
        this( IOUtils.toString( new FileInputStream( inputfile ), "UTF-8" ) );
        this.file = inputfile;
    }

    public GradleDependenceParser( String script ) throws MultipleCompilationErrorsException
    {
        AstBuilder builder = new AstBuilder();
        nodes = builder.buildFromString( script );
    }

    public FindDependenceVisitor updateDependence( String dependence ) throws IOException
    {
        FindDependenceVisitor visitor = new FindDependenceVisitor();
        walkScript( visitor );
        scripts = Files.readAllLines( Paths.get( file.toURI() ) );

        if( visitor.getDependenceLineNum() == -1 )
        {
            if( !dependence.startsWith( "\t" ) )
            {
                dependence = "\t" + dependence;;
            }

            scripts.add( "" );
            scripts.add( "dependencies {" );
            scripts.add( dependence );
            scripts.add( "}" );
        }
        else
        {
            if( visitor.getColumnNum() != -1 )
            {
                scripts = Files.readAllLines( Paths.get( file.toURI() ) );
                StringBuilder builder = new StringBuilder( scripts.get( visitor.getDependenceLineNum() - 1 ) );
                builder.insert( visitor.getColumnNum() - 2, "\n" + dependence + "\n" );
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
                scripts.add( visitor.getDependenceLineNum() - 1, dependence );
            }

        }

        return visitor;
    }

    public List<GradleDependence> getAllDependence()
    {
        FindDependenceVisitor visitor = new FindDependenceVisitor();
        walkScript( visitor );
        return visitor.getDependences();
    }

    public void walkScript( GroovyCodeVisitor visitor )
    {
        for( ASTNode node : nodes )
        {
            node.visit( visitor );
        }
    }

    public void insertDependence() throws IOException
    {
        Files.write( file.toPath(), scripts, StandardCharsets.UTF_8 );
    }

    public List<String> getScripts()
    {
        return scripts;
    }

}
