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
package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;


/**
 * @author Gregory Amerson
 */
public class BasicJavaElementHyperlink implements IHyperlink
{

    private final IRegion region;
    private final IJavaElement javaElement;

    public BasicJavaElementHyperlink( IRegion region, IJavaElement javaElement )
    {
        this.region = region;
        this.javaElement = javaElement;
    }

    public IRegion getHyperlinkRegion()
    {
        return this.region;
    }

    public String getTypeLabel()
    {
        return null;
    }

    public String getHyperlinkText()
    {
        String elementLabel = JavaElementLabels.getElementLabel( this.javaElement, JavaElementLabels.ALL_POST_QUALIFIED);
        return "Open " + elementLabel;
    }

    public void open()
    {
        try
        {
            JavaUI.openInEditor( this.javaElement );
        }
        catch( Exception e )
        {
            PortletUIPlugin.logError( "Unable to open java editor for element " + this.javaElement, e );
        }
    }

}
