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
@Label( standard = "Portlet Mode" )
@GenerateImpl
public interface IPortletMode extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IPortletMode.class );

	/*
	 * mode
	 */

	@Label( standard = "Portlet Mode" )
	@Required
	@NoDuplicates
	@Whitespace( trim = true )
	@XmlBinding( path = "" )
	//@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = "portlet-mode" )
	ValueProperty PROP_PORTLET_MODE = new ValueProperty( TYPE, "PortletMode" );

	void setPortletMode( String mode );

	Value<String> getPortletMode();

}
