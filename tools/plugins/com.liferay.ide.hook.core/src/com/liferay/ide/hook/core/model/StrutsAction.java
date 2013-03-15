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
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - IDE-355
 ******************************************************************************/

package com.liferay.ide.hook.core.model;

import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesService;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/action_url_16x16.png" )
public interface StrutsAction extends IModelElement
{

    ModelElementType TYPE = new ModelElementType( StrutsAction.class );

    // *** StrutsActionPath ***

    @Label( standard = "Struts Action Path" )
    @XmlBinding( path = "struts-action-path" )
    @Required
    // @Services( { @Service( impl = UrlPathValidationService.class ),
    @Service( impl = StrutsActionPathPossibleValuesService.class )
    ValueProperty PROP_STRUTS_ACTION_PATH = new ValueProperty( TYPE, "StrutsActionPath" ); //$NON-NLS-1$

    Value<String> getStrutsActionPath();

    void setStrutsActionPath( String value );

    // ** StrutsActionImpl
    @Type( base = JavaTypeName.class )
    @Reference( target = JavaType.class )
    @Label( standard = "Struts Action Impl" )
    @JavaTypeConstraint( kind = { JavaTypeKind.CLASS }, type = { "com.liferay.portal.kernel.struts.StrutsAction",
        "com.liferay.portal.kernel.struts.StrutsPortletAction" }, behavior = JavaTypeConstraintBehavior.AT_LEAST_ONE )
    @MustExist
    @Required
    @XmlBinding( path = "struts-action-impl" )
    ValueProperty PROP_STRUTS_ACTION_IMPL = new ValueProperty( TYPE, "StrutsActionImpl" ); //$NON-NLS-1$

    ReferenceValue<JavaTypeName, JavaType> getStrutsActionImpl();

    void setStrutsActionImpl( String value );

    void setStrutsActionImpl( JavaTypeName value );

}
