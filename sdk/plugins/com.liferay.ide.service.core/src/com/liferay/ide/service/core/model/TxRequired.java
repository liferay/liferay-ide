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
 *******************************************************************************/
package com.liferay.ide.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
@Image(path = "images/tx_requireds_16x16.gif")
public interface TxRequired extends IModelElement 
{
    ModelElementType TYPE = new ModelElementType( TxRequired.class );
    
	// *** TxRequired ***

	@XmlBinding(path = "")
	@Label(standard = "&tx required")
	ValueProperty PROP_TX_REQUIRED = new ValueProperty(TYPE, "TxRequired");

	Value<String> getTxRequired();

	void setTxRequired(String value);

}
