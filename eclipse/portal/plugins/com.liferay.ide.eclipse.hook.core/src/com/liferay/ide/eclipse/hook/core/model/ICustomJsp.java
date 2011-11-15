/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.hook.core.model.internal.CustomJspPossibleValuesService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface ICustomJsp extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( ICustomJsp.class );
    
    // *** Item ***
    
	@Label( standard = "Liferay Portal JSP" )
    @XmlBinding( path = "" )
    @NoDuplicates
	@Service( impl = CustomJspPossibleValuesService.class )
	ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );
    
	Value<String> getValue();

	void setValue( String value );
    
}
