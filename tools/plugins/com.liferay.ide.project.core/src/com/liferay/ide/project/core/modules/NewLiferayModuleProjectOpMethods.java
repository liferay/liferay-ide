/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Simon Jiang
 * @author Lovett Li
 */
public class NewLiferayModuleProjectOpMethods
{
    public static final Status execute( final NewLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayModuleProjectOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );

            final String projectName = op.getProjectName().content();
            final String className = op.getComponentName().content();
            final String packageName = op.getPackageName().content();
            final String projectTemplateName = op.getProjectTemplateName().content();

            IPath location = PathBridge.create( op.getLocation().content() );
            IPath projectLocation = location;

            final String lastSegment = location.lastSegment();

            if( location != null && location.segmentCount() > 0 )
            {
                if( !lastSegment.equals( projectName ) )
                {
                    projectLocation = location.append( projectName );
                }
            }


            final List<IPath> finalClassPaths = getClassFilePath( projectName, className, packageName, projectTemplateName, projectLocation );

            for( IPath classFilePath : finalClassPaths )
            {
                final File finalClassFile = classFilePath.toFile();

                if( finalClassFile.exists() )
                {
                    ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

                    final List<String> properties = new ArrayList<String>();

                    for( PropertyKey propertyKey : propertyKeys )
                    {
                        properties.add( propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) );
                    }

                    NewLiferayModuleProjectOpMethods.addProperties( finalClassFile, properties );

                    CoreUtil.getProject( op.getProjectName().content() ).refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
            }

            if( retval.ok() )
            {
                ProjectUtil.updateProjectBuildTypePrefs(
                    op.getProjectProvider().text(), ProjectCore.PREF_DEFAULT_MODULE_PROJECT_BUILD_TYPE_OPTION );
            }
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " " + e.getMessage(), e );
        }

        return retval;
    }

    private static void getClassFile( File packageRoot, List<IPath> classFiles )
    {
        File[] children = packageRoot.listFiles();

        if( children != null && children.length > 0 )
        {
            for( File child : children )
            {
                if( child.isDirectory() )
                {
                    getClassFile( child, classFiles );
                }
                else
                {
                    try
                    {
                        boolean hasComponentAnnotation = checkComponentAnnotation( child.getAbsoluteFile() );

                        if( hasComponentAnnotation )
                        {
                            classFiles.add( new Path( child.getAbsolutePath() ) );
                        }
                    }
                    catch( Exception e )
                    {
                        ProjectCore.logError( e );;
                    }
                }
            }
        }
    }

    private static List<IPath> getClassFilePath(
        final String projectName, String className, final String packageName, final String projectTemplateName,
        IPath projecLocation )
    {
        IPath packageNamePath = projecLocation.append( "src/main/java" );

        File packageRoot = packageNamePath.toFile();

        List<IPath> classFiles = new ArrayList<IPath>();
        getClassFile( packageRoot, classFiles );

        return classFiles;
    }


    public static String getMavenParentPomGroupId( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( groupId ) )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !CoreUtil.isNullOrEmpty( version ) )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    private static class CheckComponentAnnotationVistor extends ASTVisitor
    {

        public CheckComponentAnnotationVistor()
        {
            super();
        }

        private boolean hasComponentAnnotation = false;

        @Override
        public boolean visit( NormalAnnotation node )
        {
            if( node.getTypeName().getFullyQualifiedName().equals( "Component" ) )
            {
                hasComponentAnnotation = true;
            }

            return super.visit( node );
        }

        public boolean hasComponentAnnotation()
        {
            return this.hasComponentAnnotation;
        }
    }

    public static boolean checkComponentAnnotation( File dest ) throws Exception
    {
        try
        {
            ASTParser parser = ASTParser.newParser( AST.JLS8 );
            String readContents = FileUtil.readContents( dest, true );
            parser.setSource( readContents.toCharArray() );
            parser.setKind( ASTParser.K_COMPILATION_UNIT );
            parser.setResolveBindings( true );
            final CompilationUnit cu = (CompilationUnit) parser.createAST( new NullProgressMonitor() );
            CheckComponentAnnotationVistor componentAnnotationVistor = new CheckComponentAnnotationVistor();
            cu.accept( componentAnnotationVistor );

            return componentAnnotationVistor.hasComponentAnnotation();
        }
        catch( Exception e )
        {
            ProjectCore.logError( "error when adding properties to " + dest.getAbsolutePath(), e );
        }

        return false;
    }

    @SuppressWarnings( "unchecked" )
    public static void addProperties( File dest, List<String> properties ) throws Exception
    {
        try
        {
            if( properties == null || properties.size() < 1 )
            {
                return;
            }

            ASTParser parser = ASTParser.newParser( AST.JLS8 );
            String readContents = FileUtil.readContents( dest, true );
            parser.setSource( readContents.toCharArray() );
            parser.setKind( ASTParser.K_COMPILATION_UNIT );
            parser.setResolveBindings( true );
            final CompilationUnit cu = (CompilationUnit) parser.createAST( new NullProgressMonitor() );
            cu.recordModifications();
            Document document = new Document( new String( readContents ) );
            cu.accept( new ASTVisitor()
            {

                @Override
                public boolean visit( NormalAnnotation node )
                {
                    if( node.getTypeName().getFullyQualifiedName().equals( "Component" ) )
                    {
                        ASTRewrite rewrite = ASTRewrite.create( cu.getAST() );
                        AST ast = cu.getAST();
                        List<ASTNode> values = node.values();
                        boolean hasProperty = false;

                        for( ASTNode astNode : values )
                        {
                            if( astNode instanceof MemberValuePair )
                            {
                                MemberValuePair pairNode = (MemberValuePair) astNode;

                                if( pairNode.getName().getFullyQualifiedName().equals( "property" ) )
                                {
                                    Expression express = pairNode.getValue();

                                    if( express instanceof ArrayInitializer )
                                    {
                                        ListRewrite lrw =
                                            rewrite.getListRewrite( express, ArrayInitializer.EXPRESSIONS_PROPERTY );
                                        ArrayInitializer initializer = (ArrayInitializer) express;
                                        List<ASTNode> expressions = (List<ASTNode>) initializer.expressions();
                                        ASTNode propertyNode = null;

                                        for( int i = properties.size() - 1; i >= 0; i-- )
                                        {
                                            StringLiteral stringLiteral = ast.newStringLiteral();
                                            stringLiteral.setLiteralValue( properties.get( i ) );

                                            if( expressions.size() > 0 )
                                            {
                                                propertyNode = expressions.get( expressions.size() - 1 );
                                                lrw.insertAfter( stringLiteral, propertyNode, null );
                                            }
                                            else
                                            {
                                                lrw.insertFirst( stringLiteral, null );
                                            }
                                        }
                                    }
                                    hasProperty = true;
                                }
                            }
                        }

                        if( hasProperty == false )
                        {
                            ListRewrite clrw = rewrite.getListRewrite( node, NormalAnnotation.VALUES_PROPERTY );
                            ASTNode lastNode = values.get( values.size() - 1 );

                            ArrayInitializer newArrayInitializer = ast.newArrayInitializer();
                            MemberValuePair propertyMemberValuePair = ast.newMemberValuePair();

                            propertyMemberValuePair.setName( ast.newSimpleName( "property" ) );
                            propertyMemberValuePair.setValue( newArrayInitializer );

                            clrw.insertBefore( propertyMemberValuePair, lastNode, null );
                            ListRewrite newLrw =
                                rewrite.getListRewrite( newArrayInitializer, ArrayInitializer.EXPRESSIONS_PROPERTY );

                            for( String property : properties )
                            {
                                StringLiteral stringLiteral = ast.newStringLiteral();
                                stringLiteral.setLiteralValue( property );
                                newLrw.insertAt( stringLiteral, 0, null );
                            }
                        }
                        try(FileOutputStream fos = new FileOutputStream( dest ))
                        {
                            TextEdit edits = rewrite.rewriteAST( document, null );
                            edits.apply( document );
                            fos.write( document.get().getBytes() );
                            fos.flush();
                        }
                        catch( Exception e )
                        {
                            ProjectCore.logError( e );
                        }
                    }
                    return super.visit( node );
                }
            } );
        }
        catch( Exception e )
        {
            ProjectCore.logError( "error when adding properties to " + dest.getAbsolutePath(), e );
        }
    }
}
