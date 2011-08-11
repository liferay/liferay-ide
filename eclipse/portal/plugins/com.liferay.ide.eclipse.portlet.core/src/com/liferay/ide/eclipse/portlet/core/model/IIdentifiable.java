/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IIdentifiable extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IIdentifiable.class );

	// *** Id ***

	@Label( standard = "Id" )
	@XmlBinding( path = "@id" )
	@Whitespace( trim = true )
	@DefaultValue( text = "ID" )
	ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );

	Value<String> getId();

	void setId( String id );

}
