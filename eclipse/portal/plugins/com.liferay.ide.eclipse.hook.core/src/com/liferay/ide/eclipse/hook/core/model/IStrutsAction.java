/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/action_url_16x16.png" )
public interface IStrutsAction extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IStrutsAction.class );

	// TODO:Leading Slash validation

	// *** StrutsActionPath ***

	@Label( standard = "Struts Action Path" )
	@XmlBinding( path = "struts-action-path" )
	ValueProperty PROP_STRUTS_ACTION_PATH = new ValueProperty( TYPE, "StrutsActionPath" );

	Value<String> getStrutsActionPath();

	void setStrutsActionPath( String value );

	// ** StrutsActionImpl
	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@Label( standard = "Struts Action Impl" )
	@JavaTypeConstraint( kind = { JavaTypeKind.CLASS, JavaTypeKind.ABSTRACT_CLASS }, type = {
		"com.liferay.portal.kernel.struts.BaseStrutsAction", "com.liferay.portal.kernel.struts.BaseStrutsPortletAction" } )
	@MustExist
	@Required
	@XmlBinding( path = "struts-action-impl" )
	ValueProperty PROP_STRUTS_ACTION_IMPL = new ValueProperty( TYPE, "StrutsActionImpl" );

	ReferenceValue<JavaTypeName, JavaType> getStrutsActionImpl();

	void setStrutsActionImpl( String value );

	void setStrutsActionImpl( JavaTypeName value );

}
