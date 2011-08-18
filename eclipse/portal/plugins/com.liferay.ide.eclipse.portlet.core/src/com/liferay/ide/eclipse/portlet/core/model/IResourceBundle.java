/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath TODO: Change to browse type
 */
@GenerateImpl
@Image( path = "images/resource_bundle.png" )
public interface IResourceBundle extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IResourceBundle.class );

	// *** ResourceBundle ***

	@Label( standard = "Resource Bundle" )
	@Whitespace( trim = true )
	@XmlBinding( path = "resource-bundle" )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = "resource-bundle" )
	ValueProperty PROP_RESOURCE_BUNDLE = new ValueProperty( TYPE, "ResourceBundle" );

	Value<String> getResourceBundle();

	void setResourceBundle( String value );

}
