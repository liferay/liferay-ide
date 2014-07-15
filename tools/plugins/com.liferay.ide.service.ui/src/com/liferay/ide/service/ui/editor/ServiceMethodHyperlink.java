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

package com.liferay.ide.service.ui.editor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

/**
 * @author Gregory Amerson
 */
public class ServiceMethodHyperlink implements IHyperlink
{
    private final IMethod serviceMethod;
    private final SelectionDispatchAction openAction;
    private final boolean qualify;
    private final IRegion word;

    public ServiceMethodHyperlink(
        final IRegion word, final SelectionDispatchAction openAction, final IMethod serviceMethod, final boolean qualify )
    {
        this.word = word;
        this.openAction = openAction;
        this.serviceMethod = serviceMethod;
        this.qualify = qualify;
    }

    public IRegion getHyperlinkRegion()
    {
        return word;
    }

    public String getHyperlinkText()
    {
        final String methodLabel =
            JavaElementLabels.getElementLabel( serviceMethod, JavaElementLabels.ALL_FULLY_QUALIFIED );

        return getStaticText() + ( qualify ? ( " " + methodLabel ) : "" );
    }

    protected String getStaticText()
    {
        return "Open Service";
    }

    public String getTypeLabel()
    {
        return null;
    }

    public IMethod method()
    {
        return this.serviceMethod;
    }

    public void open()
    {
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                ServiceMethodHyperlink.this.openAction.run( new StructuredSelection( serviceMethod ) );
            }
        };

        BusyIndicator.showWhile( Display.getDefault(), runnable );
    }

    public IRegion wordRegion()
    {
        return this.word;
    }
}
