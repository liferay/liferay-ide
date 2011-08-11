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
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.PortletReferenceService;
import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface IPortletName extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IPortletName.class );

	// *** Name ***

	@Label( standard = "Name" )
	@Whitespace( trim = true )
	@XmlBinding( path = "portlet-name" )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "portlet-name" } )
	@PossibleValues( property = "/Portlets/PortletName" )
	@Service( impl = PortletReferenceService.class, params = { "portlet-name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

}
