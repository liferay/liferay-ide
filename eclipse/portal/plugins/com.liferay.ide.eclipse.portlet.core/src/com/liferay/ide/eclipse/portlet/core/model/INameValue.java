/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface INameValue extends IModelElement {

	ModelElementType TYPE = new ModelElementType( INameValue.class );

	/*
	 * Name Element
	 */

	@Label( standard = "Name" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String name );

	/*
	 * Value Element
	 */

	@Label( standard = "Value" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "value" )
	@Whitespace( trim = true )
	ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

	Value<String> getValue();

	void setValue( String value );
}
