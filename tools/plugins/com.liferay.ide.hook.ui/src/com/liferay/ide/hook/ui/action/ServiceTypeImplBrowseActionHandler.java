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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.core.model.ServiceWrapper;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.ITypeInfoFilterExtension;
import org.eclipse.jdt.ui.dialogs.ITypeInfoRequestor;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireBrowseActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Gregory Amerson
 */
public final class ServiceTypeImplBrowseActionHandler extends SapphireBrowseActionHandler
{

    public static final String ID = "ServiceTypeImpl.Browse.Java.Type"; //$NON-NLS-1$

    private int browseDialogStyle;

    private String kind;

    @Override
    public String browse( final SapphireRenderingContext context )
    {
        final Element element = getModelElement();
        final Property property = property();
        final IProject project = element.adapt( IProject.class );

        try
        {
            IJavaSearchScope scope = null;

            TypeSelectionExtension extension = null;

            if( "type".equals( kind ) ) //$NON-NLS-1$
            {
                scope = SearchEngine.createJavaSearchScope( new IJavaProject[] { JavaCore.create( project ) } );

                extension = new TypeSelectionExtension()
                {
                    @Override
                    public ITypeInfoFilterExtension getFilterExtension()
                    {
                        return new ITypeInfoFilterExtension()
                        {
                            public boolean select( ITypeInfoRequestor typeInfoRequestor )
                            {
                                return typeInfoRequestor.getPackageName().startsWith( "com.liferay" ) && //$NON-NLS-1$
                                    typeInfoRequestor.getTypeName().endsWith( "Service" ); //$NON-NLS-1$
                            }
                        };
                    }
                };
            }
            else if( "impl".equals( kind ) ) //$NON-NLS-1$
            {
                String serviceType = getServiceType( element, property );

                if( serviceType != null )
                {
                    String wrapperType = serviceType + "Wrapper"; //$NON-NLS-1$

                    scope = SearchEngine.createHierarchyScope( JavaCore.create( project ).findType( wrapperType ) );
                }
                else
                {
                    MessageDialog.openInformation(
                        context.getShell(), Msgs.serviceImplBrowse,
                        Msgs.validServiceTypeProperty );

                    return null;
                }
            }

            final SelectionDialog dlg =
                JavaUI.createTypeDialog(
                    context.getShell(), null, scope, this.browseDialogStyle, false, StringPool.DOUBLE_ASTERISK, extension );

            final String title = property.definition().getLabel( true, CapitalizationType.TITLE_STYLE, false );
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

    private String getServiceType( Element element, Property property )
    {
        String retval = null;

        ServiceWrapper service = element.nearest( ServiceWrapper.class );

        JavaTypeName javaTypeName = service.getServiceType().content( false );

        if( javaTypeName != null )
        {
            retval = javaTypeName.qualified();
        }

        return retval;
    }

    @Override
    public void init( final SapphireAction action, final ActionHandlerDef def )
    {
        super.init( action, def );

        setId( ID );

        this.kind = def.getParam( "kind" ); //$NON-NLS-1$

        if( "type".equals( kind ) ) //$NON-NLS-1$
        {
            this.browseDialogStyle = IJavaElementSearchConstants.CONSIDER_INTERFACES;
        }
        else if( "impl".equals( kind ) ) //$NON-NLS-1$
        {
            this.browseDialogStyle = IJavaElementSearchConstants.CONSIDER_CLASSES;
        }
    }

    private static class Msgs extends NLS
    {
        public static String select;
        public static String serviceImplBrowse;
        public static String validServiceTypeProperty;

        static
        {
            initializeMessages( ServiceTypeImplBrowseActionHandler.class.getName(), Msgs.class );
        }
    }
}
