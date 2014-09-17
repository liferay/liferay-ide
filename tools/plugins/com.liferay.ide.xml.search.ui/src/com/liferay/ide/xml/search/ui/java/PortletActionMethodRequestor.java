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
package com.liferay.ide.xml.search.ui.java;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.requestor.AbstractJavaMethodRequestor;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.requestor.IJavaMethodRequestor;


/**
 * @author Gregory Amerson
 */
public class PortletActionMethodRequestor extends AbstractJavaMethodRequestor
{
    public static final IJavaMethodRequestor INSTANCE = new PortletActionMethodRequestor();

    @Override
    protected IStatus doValidate( IMethod method )
    {
        final String[] parameterTypes = method.getParameterTypes();

        boolean valid =
            parameterTypes != null && parameterTypes.length == 2 &&
            parameterTypes[0].equals( "QActionRequest;" ) &&
            parameterTypes[1].equals( "QActionResponse;" );

        return valid ? Status.OK_STATUS : null;
    }

    @Override
    protected String formatMethodName( Object selectedNode, IMethod method )
    {
        return method.getElementName();
    }

}
