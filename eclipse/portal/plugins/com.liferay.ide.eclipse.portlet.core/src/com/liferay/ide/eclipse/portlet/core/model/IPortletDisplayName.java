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
public interface IPortletDisplayName extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IPortletDisplayName.class );

	// *** DisplayName ***

	@Label( standard = "Display Name" )
	@Whitespace( trim = true )
	@XmlBinding( path = "display-name" )
	@PossibleValues( property = "/Portlets/DisplayName" )
	@Service( impl = PortletReferenceService.class, params = { "display-name" } )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "display-name" } )
	ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" );

	Value<String> getDisplayName();

	void setDisplayName( String value );

}
