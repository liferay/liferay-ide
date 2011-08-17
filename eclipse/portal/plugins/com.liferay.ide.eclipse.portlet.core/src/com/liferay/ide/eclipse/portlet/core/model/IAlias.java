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
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface IAlias extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IAlias.class );

	// *** Alias ***

	@Label( standard = "Alias" )
	@Whitespace( trim = true )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "alias" } )
	ValueProperty PROP_ALIAS = new ValueProperty( TYPE, "Alias" );

	Value<String> getAlias();

	void setAlias( String value );

}
