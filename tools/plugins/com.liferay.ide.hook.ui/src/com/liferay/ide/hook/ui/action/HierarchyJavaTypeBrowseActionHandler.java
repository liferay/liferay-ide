/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.ui.action;

import java.util.EnumSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.java.JavaTypeConstraintService;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireBrowseActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Gregory Amerson
 */
public final class HierarchyJavaTypeBrowseActionHandler extends SapphireBrowseActionHandler
{
    public static final String ID = "Hierarchy.Browse.Java.Type"; //$NON-NLS-1$
    
    private String typeName;
    private String filter;

    @Override
    public void init( final SapphireAction action, final ActionHandlerDef def )
    {
        super.init( action, def );
        setId( ID );

        this.typeName = def.getParam( "type" ); //$NON-NLS-1$
        this.filter = def.getParam( "filter" ); //$NON-NLS-1$
    }

    @Override
    public String browse( final SapphireRenderingContext context )
    {
        final IModelElement element = getModelElement();
        final ModelProperty property = getProperty();
        final IProject project = element.adapt( IProject.class );

        try
        {
            JavaTypeConstraintService typeService = element.service( property, JavaTypeConstraintService.class );

            final EnumSet<JavaTypeKind> kinds = EnumSet.noneOf( JavaTypeKind.class );
            kinds.addAll( typeService.kinds() );

            int browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
            int count = kinds.size();

            if( count == 1 )
            {
                final JavaTypeKind kind = kinds.iterator().next();

                switch( kind )
                {
                    case CLASS:
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;
                        break;
                    case ABSTRACT_CLASS:
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;
                        break;
                    case INTERFACE:
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_INTERFACES;
                        break;
                    case ANNOTATION:
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
                        break;
                    case ENUM:
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_ENUMS;
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
            else if( count == 2 )
            {
                if( kinds.contains( JavaTypeKind.CLASS ) || kinds.contains( JavaTypeKind.ABSTRACT_CLASS ) )
                {
                    if( kinds.contains( JavaTypeKind.INTERFACE ) )
                    {
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
                    }
                    else if( kinds.contains( JavaTypeKind.ENUM ) )
                    {
                        browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES_AND_ENUMS;
                    }
                }
            }

            final IJavaSearchScope scope =
                SearchEngine.createHierarchyScope( JavaCore.create( project ).findType( this.typeName ) );

            final SelectionDialog dlg =
                JavaUI.createTypeDialog( context.getShell(), null, scope, browseDialogStyle, false, this.filter, null );

            final String title = property.getLabel( true, CapitalizationType.TITLE_STYLE, false );

            dlg.setTitle( Msgs.select + title );

            if( dlg.open() == SelectionDialog.OK )
            {
                Object results[] = dlg.getResult();
                assert results != null && results.length == 1;
                
                if( results[0] instanceof IType )
                {
                    return ( (IType) results[0] ).getFullyQualifiedName();
                }
            }
        }
        catch( JavaModelException e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }

        return null;
    }

    private static class Msgs extends NLS
    {
        public static String select;

        static
        {
            initializeMessages( HierarchyJavaTypeBrowseActionHandler.class.getName(), Msgs.class );
        }
    }
}
