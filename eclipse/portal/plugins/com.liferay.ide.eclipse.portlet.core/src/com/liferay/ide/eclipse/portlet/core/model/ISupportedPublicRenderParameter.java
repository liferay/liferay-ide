
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.PublicRenderParameterValuesService;
import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface ISupportedPublicRenderParameter extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ISupportedPublicRenderParameter.class );

	// *** RenderParameter ***

	@Label( standard = "Render Parameter" )
	@Whitespace( trim = true )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "supported-public-render-parameter" } )
	@XmlBinding( path = "supported-public-render-parameter" )
	@Service( impl = PublicRenderParameterValuesService.class )
	ValueProperty PROP_RENDER_PARAMETER = new ValueProperty( TYPE, "RenderParameter" );

	Value<String> getRenderParameter();

	void setRenderParameter( String value );

}
