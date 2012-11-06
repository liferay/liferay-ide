/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model;

import com.liferay.ide.portlet.core.model.internal.WindowStateImageService;
import com.liferay.ide.portlet.core.model.internal.WindowStatesPossibleValueService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Service( impl = WindowStateImageService.class )
public interface CustomWindowState extends IModelElement, Describeable, Identifiable
{

    ModelElementType TYPE = new ModelElementType( CustomWindowState.class );

    // *** WindowState ***
    @Label( standard = "Window State" )
    @NoDuplicates
    @XmlBinding( path = "window-state" )
    @Required
    @Service( impl = WindowStatesPossibleValueService.class )
    ValueProperty PROP_WINDOW_STATE = new ValueProperty( TYPE, "WindowState" );

    Value<String> getWindowState();

    void setWindowState( String value );

}
