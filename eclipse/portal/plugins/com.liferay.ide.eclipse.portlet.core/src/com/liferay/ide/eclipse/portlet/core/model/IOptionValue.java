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
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface IOptionValue extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IOptionValue.class );

	// *** RuntimeOptionValue ***

	@Label( standard = "Option Value" )
	@Whitespace( trim = true )
	@XmlBinding( path = "" )
	// @CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "value" } )
	ValueProperty PROP_OPTION_VALUE = new ValueProperty( TYPE, "OptionValue" );

	Value<String> getOptionValue();

	void setOptionValue( String value );

}
