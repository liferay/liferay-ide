/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.FilterReferenceService;
import com.liferay.ide.eclipse.portlet.core.model.internal.PortletReferenceService;

/**
 * @author kamesh.sampath
 */
@Label( standard = "filter mapping" )
@GenerateImpl
@Image( path = "images/elcl16/filter_mapping.gif" )
public interface IFilterMapping extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IFilterMapping.class );

	// *** IFilter ***

	@Reference( target = IFilter.class )
	@Service( impl = FilterReferenceService.class )
	@Label( standard = "filter" )
	@Required
	@MustExist
	@PossibleValues( property = "/Filters/Name" )
	@XmlBinding( path = "filter-name" )
	@DefaultValue( text = "FILTER" )
	ValueProperty PROP_FILTER = new ValueProperty( TYPE, "Filter" );

	ReferenceValue<String, IFilter> getFilter();

	void setFilter( String value );

	// *** Portlet ***

	@Reference( target = IPortlet.class )
	@Service( impl = PortletReferenceService.class )
	@Label( standard = "portlet" )
	@Required
	@MustExist
	@PossibleValues( property = "/Portlets/PortletName" )
	@XmlValueBinding( path = "portlet-name", removeNodeOnSetIfNull = false )
	ValueProperty PROP_PORTLET = new ValueProperty( TYPE, "Portlet" );

	ReferenceValue<String, IPortlet> getPortlet();

	void setPortlet( String value );

}
