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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.action;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.dialogs.TypeSelectionExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.BrowseActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Simon Jiang
 */
public final class HierarchyBrowseActionHandler extends BrowseActionHandler
{

    public static final String ID = "Hierarchy.Browse.Java.Type"; //$NON-NLS-1$

    @Override
    public String browse( final Presentation context )
    {
        final Element element = getModelElement();
        final Property property = property();
        final IProject project = element.adapt( IProject.class );

        try
        {
            IJavaSearchScope scope = null;

            TypeSelectionExtension extension = null;

            final String javaType = getClassReferenceType( property );

            if( javaType != null )
            {
                scope = SearchEngine.createHierarchyScope( JavaCore.create( project ).findType( javaType ) );
            }
            else
            {
                MessageDialog.openInformation(
                    ( (SwtPresentation) context ).shell(), Msgs.browseImplementation, Msgs.validClassImplProperty );

                return null;
            }

            final SelectionDialog dlg =
                JavaUI.createTypeDialog(
                    ( (SwtPresentation) context ).shell(), null, scope, IJavaElementSearchConstants.CONSIDER_CLASSES,
                    false, StringPool.DOUBLE_ASTERISK, extension );

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
            PortletUIPlugin.logError( e );
        }

        return null;
    }

    private String getClassReferenceType( Property property )
    {
        JavaTypeConstraint typeConstraint = property.definition().getAnnotation( JavaTypeConstraint.class );

        final String retval = Arrays.toString( typeConstraint.type() ).replaceAll( "[\\[\\]\\s,]", "" );

        return retval;
    }

    @Override
    public void init( final SapphireAction action, final ActionHandlerDef def )
    {
        super.init( action, def );

        setId( ID );

    }

    private static class Msgs extends NLS
    {

        public static String browseImplementation;
        public static String select;
        public static String validClassImplProperty;

        static
        {
            initializeMessages( HierarchyBrowseActionHandler.class.getName(), Msgs.class );
        }
    }
}
