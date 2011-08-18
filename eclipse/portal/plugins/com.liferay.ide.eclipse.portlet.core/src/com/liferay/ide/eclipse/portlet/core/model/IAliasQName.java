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
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNamespaceValueBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IAliasQName extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IAliasQName.class );

	// *** NamespaceURI ***

	@Label( standard = "Namespace URI" )
	@DefaultValue( text = "NAMESPACE_URI" )
	@XmlBinding( path = "alias" )
	@CustomXmlValueBinding( impl = QNamespaceValueBinding.class, params = { "alias" } )
	ValueProperty PROP_NAMESPACE_URI = new ValueProperty( TYPE, "NamespaceURI" );

	Value<String> getNamespaceURI();

	void setNamespaceURI( String value );

	// *** LocalPart ***

	@Label( standard = "Local Part" )
	@DefaultValue( text = "LOCAL_PART" )
	@XmlBinding( path = "alias" )
	@CustomXmlValueBinding( impl = QNameLocalPartValueBinding.class, params = { "alias" } )
	ValueProperty PROP_LOCAL_PART = new ValueProperty( TYPE, "LocalPart" );

	Value<String> getLocalPart();

	void setLocalPart( String value );
}
