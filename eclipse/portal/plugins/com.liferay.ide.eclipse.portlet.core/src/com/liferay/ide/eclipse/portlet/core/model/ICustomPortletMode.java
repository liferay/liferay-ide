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
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.InvertingBooleanXmlValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.PortletModeImageService;

/**
 * @author kamesh
 */
@GenerateImpl
// @Image( path = "images/portlet_item.png" )
@Service( impl = PortletModeImageService.class )
public interface ICustomPortletMode extends IModelElement, IDescribeable, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( ICustomPortletMode.class );

	// *** PortletMode ***

	@Type( base = IPortletMode.class )
	@Required
	@NoDuplicates
	@DefaultValue( text = "VIEW" )
	@Label( standard = "Portlet Mode" )
	@XmlBinding( path = "portlet-mode" )
	ValueProperty PROP_PORTLET_MODE = new ValueProperty( TYPE, "PortletMode" );

	Value<IPortletMode> getPortletMode();

	void setPortletMode( String value );

	void setPortletMode( IPortletMode value );

	/*
	 * Portlet Managed
	 */

	@Type( base = Boolean.class )
	@Label( standard = "Portlet managed" )
	@CustomXmlValueBinding( impl = InvertingBooleanXmlValueBinding.class, params = "portal-managed" )
	ValueProperty PROP_PORTLET_MANAGED = new ValueProperty( TYPE, "PortletManaged" );

	Value<Boolean> getPortletManaged();

	void setPortletManaged( String value );

	void setPortletManaged( Boolean value );
}
