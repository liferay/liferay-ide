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
package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.CoreUtil;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;


/**
 * @author Gregory Amerson
 */
public class AddJSRPortletActionMethodMarkerResolution extends CommonWorkbenchMarkerResolution
{

    private final String CODE = "@ProcessAction(name = \"{0}\")\npublic void {0}(ActionRequest actionRequest, ActionResponse actionResponse) '{\n}'";
    private final String[] IMPORTS = new String[]
    {
        "javax.portlet.ActionRequest",
        "javax.portlet.ActionResponse",
        "javax.portlet.ProcessAction"
    };

    protected final IType type;

    public AddJSRPortletActionMethodMarkerResolution( IMarker marker, IType type  )
    {
        super( marker );
        this.type = type;
    }

    @Override
    public IMarker[] findOtherMarkers( IMarker[] markers )
    {
        return new IMarker[0];
    }

    protected String getCode()
    {
        return CODE;
    }

    @Override
    public String getDescription()
    {
        return getLabel();
    }

    @Override
    public Image getImage()
    {
        return LiferayXMLSearchUI.getDefault().getImageRegistry().get( LiferayXMLSearchUI.PORTLET_IMG );
    }

    protected String[] getImports()
    {
        return IMPORTS;
    }

    @Override
    public String getLabel()
    {
        return "add new @ProcessAction method \"" + getTextContent( marker ) + "\" to " + type.getElementName();
    }

    protected String getTextContent( IMarker marker )
    {
        return marker.getAttribute( XMLSearchConstants.TEXT_CONTENT, "" );
    }

    @Override
    protected void resolve( IMarker marker )
    {
        try
        {
            final IProgressMonitor npm = new NullProgressMonitor();

            final IMethod newMethod = this.type.createMethod(
                MessageFormat.format( getCode(), getTextContent( marker ) ), null, true, npm );

            for( String importName : getImports() )
            {
                type.getCompilationUnit().createImport( importName, null, npm );
            }

            type.getCompilationUnit().save( npm, false );

            try
            {
                JavaUI.revealInEditor( JavaUI.openInEditor( newMethod ), (IJavaElement) newMethod );
            }
            catch( PartInitException e )
            {
                LiferayXMLSearchUI.logError( "Unable to open java editor on action method", e );
            }

            if( marker.getResource() instanceof IFile )
            {
                CoreUtil.validateFile( (IFile) marker.getResource(), npm );
            }
        }
        catch( JavaModelException e )
        {
            LiferayXMLSearchUI.logError( "Unable to add JSR process action method", e );
        }
    }

}
