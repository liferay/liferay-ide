
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IPortletCollection extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IPortletCollection.class );

	// *** Portlet Name ***
	@Type( base = IPortletName.class )
	@Label( standard = "Portlet name" )
	@Required
	@CountConstraint( min = 1 )
	@NoDuplicates
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "portlet-name", type = IPortletName.class ) )
	ListProperty PROP_PORTLET_NAMES = new ListProperty( TYPE, "PortletNames" );

	ModelElementList<IPortletName> getPortletNames();
}
