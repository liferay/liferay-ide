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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/
/**
 * 
 */

package com.liferay.ide.portlet.core.model.lfrportlet.common;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface ITrigger extends Element {

	ElementType TYPE = new ElementType( ITrigger.class );

	// *** Cron ***

	@Type( base = ICron.class )
	@Label( standard = "Cron" )
	@XmlBinding( path = "cron" )
	ValueProperty PROP_CRON = new ValueProperty( TYPE, "Cron" );

	Value<ICron> getCron();

	void setCron( String value );

	void setCron( ICron value );

	// *** Simple ***

	@Type( base = ISimple.class )
	@Label( standard = "Simple" )
	@XmlBinding( path = "simple" )
	ValueProperty PROP_SIMPLE = new ValueProperty( TYPE, "Simple" );

	Value<ISimple> getSimple();

	void setSimple( String value );

	void setSimple( ISimple value );

}
