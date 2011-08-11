/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.NameAndQNameChoiceValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "/images/obj16/pub_obj.gif" )
public interface IPublicRenderParameter extends IModelElement, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( IPublicRenderParameter.class );

	// *** Qname ***
	@Label( standard = "Qname" )
	@Whitespace( trim = true )
	@XmlBinding( path = "qname" )
	@DefaultValue( text = "Q_NAME" )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "qname" } )
	ValueProperty PROP_Q_NAME = new ValueProperty( TYPE, "Qname" );

	Value<String> getQname();

	void setQname( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@DefaultValue( text = "PARAM_NAME" )
	@Enablement( expr = "${Qname == 'Q_NAME'}" )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

	// *** Aliases ***

	@Type( base = IAlias.class )
	@Label( standard = "Aliases" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "alias", type = IAlias.class ) } )
	ListProperty PROP_ALIASES = new ListProperty( TYPE, "Aliases" );

	ModelElementList<IAlias> getAliases();

}
