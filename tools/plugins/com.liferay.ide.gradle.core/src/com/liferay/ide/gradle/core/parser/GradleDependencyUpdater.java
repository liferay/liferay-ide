/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.gradle.core.parser;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

/**
 * @author Lovett Li
 */
public class GradleDependencyUpdater
{

    private List<ASTNode> nodes;
    private File file;
    private List<String> gradleFileContents;

    public GradleDependencyUpdater( File inputfile ) throws MultipleCompilationErrorsException, IOException
    {
        this( IOUtils.toString( Files.newInputStream( inputfile.toPath() ), "UTF-8" ) );
        this.file = inputfile;
    }

    public GradleDependencyUpdater( String scriptContents ) throws MultipleCompilationErrorsException
    {
        AstBuilder builder = new AstBuilder();
        nodes = builder.buildFromString( scriptContents );
    }

    public FindDependenciesVisitor insertDependency( GradleDependency gradleDependency ) throws IOException
    {
        String dependency = "compile group: \"" + gradleDependency.getGroup() + "\", name:\"" +
            gradleDependency.getName() + "\", version:\"" + gradleDependency.getVersion() + "\"";

        return insertDependency( dependency );
    }

    public FindDependenciesVisitor insertDependency( String dependency ) throws IOException
    {
        FindDependenciesVisitor visitor = new FindDependenciesVisitor();
        walkScript( visitor );
        gradleFileContents = FileUtils.readLines( file );

        if( !dependency.startsWith( "\t" ) )
        {
            dependency = "\t" + dependency;;
        }

        if( visitor.getDependenceLineNum() == -1 )
        {
            gradleFileContents.add( "" );
            gradleFileContents.add( "dependencies {" );
            gradleFileContents.add( dependency );
            gradleFileContents.add( "}" );
        }
        else
        {
            if( visitor.getColumnNum() != -1 )
            {
                gradleFileContents = Files.readAllLines( Paths.get( file.toURI() ), StandardCharsets.UTF_8 );
                StringBuilder builder =
                    new StringBuilder( gradleFileContents.get( visitor.getDependenceLineNum() - 1 ) );
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

                gradleFileContents.remove( visitor.getDependenceLineNum() - 1 );
                gradleFileContents.add( visitor.getDependenceLineNum() - 1, dep );
            }
            else
            {
                gradleFileContents.add( visitor.getDependenceLineNum() - 1, dependency );
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

    public List<String> getGradleFileContents()
    {
        return gradleFileContents;
    }

}

