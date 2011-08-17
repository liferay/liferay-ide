/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/obj16/info_obj.gif" )
public interface IPortletInfo extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( IPortletInfo.class );

	// *** Title ***

	@Label( standard = "Title" )
	@XmlBinding( path = "title" )
	@CountConstraint( min = 0, max = 1 )
	@Whitespace( trim = true )
	ValueProperty PROP_TITLE = new ValueProperty( TYPE, "Title" );

	Value<String> getTitle();

	void setTitle( String value );

	// *** ShortTitle ***

	@Label( standard = "Short Title" )
	@XmlBinding( path = "short-title" )
	@CountConstraint( min = 0, max = 1 )
	@Whitespace( trim = true )
	ValueProperty PROP_SHORT_TITLE = new ValueProperty( TYPE, "ShortTitle" );

	Value<String> getShortTitle();

	void setShortTitle( String value );

	// *** Keywords ***

	@Label( standard = "Keywords" )
	@XmlBinding( path = "keywords" )
	@CountConstraint( min = 0, max = 1 )
	@Whitespace( trim = true )
	ValueProperty PROP_KEYWORDS = new ValueProperty( TYPE, "Keywords" );

	Value<String> getKeywords();

	void setKeywords( String value );

}
