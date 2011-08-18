/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.NameAndQNameChoiceValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNameLocalPartValueBinding;
import com.liferay.ide.eclipse.portlet.core.model.internal.QNamespaceValueBinding;

/**
 * @author kamesh.sampath TODO:Qname validation
 */
@GenerateImpl
@Image( path = "images/elcl16/event.gif" )
public interface IEventDefinition extends IModelElement, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IEventDefinition.class );

	//
	// *** NamespaceURI ***

	@Label( standard = "Namespace URI" )
	@DefaultValue( text = "NAMESPACE_URI" )
	@XmlBinding( path = "event-definition/qname" )
	@CustomXmlValueBinding( impl = QNamespaceValueBinding.class, params = { "qname" } )
	ValueProperty PROP_NAMESPACE_URI = new ValueProperty( TYPE, "NamespaceURI" );

	Value<String> getNamespaceURI();

	void setNamespaceURI( String value );

	// *** LocalPart ***

	@Label( standard = "Local Part" )
	@DefaultValue( text = "LOCAL_PART" )
	@XmlBinding( path = "event-definition/qname" )
	@CustomXmlValueBinding( impl = QNameLocalPartValueBinding.class, params = { "qname" } )
	ValueProperty PROP_LOCAL_PART = new ValueProperty( TYPE, "LocalPart" );

	Value<String> getLocalPart();
	void setLocalPart( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@DefaultValue( text = "EVENT_NAME" )
	@Enablement( expr = "${NamespaceURI == 'NAMESPACE_URI' AND LocalPart=='LOCAL_PART'}" )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

	// *** Aliases ***

	@Type( base = IAliasQName.class )
	@Label( standard = "Aliases" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "alias", type = IAliasQName.class ) } )
	ListProperty PROP_ALIASES = new ListProperty( TYPE, "Aliases" );

	ModelElementList<IAliasQName> getAliases();

	// *** Event Value Type ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@JavaTypeConstraint( kind = { JavaTypeKind.CLASS, JavaTypeKind.INTERFACE }, type = { "java.io.Serializable" } )
	@Label( standard = "Value Type" )
	@XmlBinding( path = "value-type" )
	ValueProperty PROP_EVENT_VALUE_TYPE = new ValueProperty( TYPE, "EventValueType" );

	ReferenceValue<JavaTypeName, JavaType> getEventValueType();

	void setEventValueType( String eventValueType );

	void setEventValueType( JavaTypeName eventValueType );

}
