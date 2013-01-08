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
 *******************************************************************************/
package com.liferay.ide.portlet.core.display.model.internal;

import com.liferay.ide.portlet.core.display.model.Display6xx;
import com.liferay.ide.project.core.util.VersionedDTDRootElementController;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
public class DisplayRootElementController extends VersionedDTDRootElementController
{
    static final String xmlBindingPath = Display6xx.class.getAnnotation( XmlBinding.class ).path();
    static final String publicIdTemplate = "-//Liferay//DTD Display {0}//EN"; //$NON-NLS-1$
    static final String systemIdTemplate = "http://www.liferay.com/dtd/liferay-display_{0}.dtd"; //$NON-NLS-1$
    static final Pattern publicIdPattern = Pattern.compile( "^-//Liferay//DTD Display (.*)//EN$" ); //$NON-NLS-1$
    static final Pattern systemIdPattern = Pattern.compile("^http://www.liferay.com/dtd/liferay-display_(.*).dtd$"); //$NON-NLS-1$

    public DisplayRootElementController()
    {
        super( xmlBindingPath, publicIdTemplate, systemIdTemplate, publicIdPattern, systemIdPattern );
    }

}
