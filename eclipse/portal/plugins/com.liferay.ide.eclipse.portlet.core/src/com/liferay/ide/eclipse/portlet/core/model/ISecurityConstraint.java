/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh
 */
@GenerateImpl
@Image( path = "images/lock.png" )
public interface ISecurityConstraint extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( ISecurityConstraint.class );

	// *** PortletDisplayNames ***

	@Type( base = IPortletDisplayName.class )
	@Label( standard = "Portlet Display Names" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "display-name", type = IPortletDisplayName.class ) } )
	ListProperty PROP_PORTLET_DISPLAY_NAMES = new ListProperty( TYPE, "PortletDisplayNames" );

	ModelElementList<IPortletDisplayName> getPortletDisplayNames();

	// *** PortletCollection ***

	@Type( base = IPortletCollection.class )
	@Label( standard = "Portlet Collection" )
	@XmlBinding( path = "portlet-collection" )
	@Required
	@MustExist
	ImpliedElementProperty PROP_PORTLET_COLLECTION = new ImpliedElementProperty( TYPE, "PortletCollection" );

	IPortletCollection getPortletCollection();

	// *** UserDataConstraint ***

	@Type( base = IUserDataConstraint.class )
	@Label( standard = "User Data Constraint" )
	@XmlBinding( path = "user-data-constraint" )
	@MustExist
	@Required
	ImpliedElementProperty PROP_USER_DATA_CONSTRAINT = new ImpliedElementProperty( TYPE, "UserDataConstraint" );

	IUserDataConstraint getUserDataConstraint();

}
