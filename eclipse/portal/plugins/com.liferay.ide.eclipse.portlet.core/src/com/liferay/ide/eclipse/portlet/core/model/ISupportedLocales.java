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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface ISupportedLocales extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ISupportedLocales.class );

	// *** SupportedLocale ***

	@Label( standard = "Supported Locale" )
	@Whitespace( trim = true )
	@XmlBinding( path = "supported-locale" )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = "supported-locale" )
	ValueProperty PROP_SUPPORTED_LOCALE = new ValueProperty( TYPE, "SupportedLocale" );

	Value<String> getSupportedLocale();

	void setSupportedLocale( String value );

}
