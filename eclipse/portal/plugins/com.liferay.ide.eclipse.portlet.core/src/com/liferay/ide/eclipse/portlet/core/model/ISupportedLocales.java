/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.LocaleBundleValidationService;
import com.liferay.ide.eclipse.portlet.core.model.internal.LocalePossibleValueService;
import com.liferay.ide.eclipse.portlet.core.model.internal.LocaleTextNodeValueBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
@GenerateImpl
@Image( path = "images/elcl16/locale_16x16.gif" )
public interface ISupportedLocales extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ISupportedLocales.class );

	// *** SupportedLocale ***

	@Label( standard = "Locale" )
	@Whitespace( trim = true )
	@NoDuplicates
	@XmlBinding( path = "" )
	@Services( value = { @Service( impl = LocalePossibleValueService.class ),
		@Service( impl = LocaleBundleValidationService.class ) } )
	@CustomXmlValueBinding( impl = LocaleTextNodeValueBinding.class )
	@DependsOn( { "/Portlets/ResourceBundle" } )
	ValueProperty PROP_SUPPORTED_LOCALE = new ValueProperty( TYPE, "SupportedLocale" );

	Value<String> getSupportedLocale();

	void setSupportedLocale( String value );

}
