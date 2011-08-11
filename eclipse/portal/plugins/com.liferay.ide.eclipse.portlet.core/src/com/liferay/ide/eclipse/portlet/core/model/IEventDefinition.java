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
import org.eclipse.sapphire.modeling.annotations.DerivedValue;
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
import com.liferay.ide.eclipse.portlet.core.model.internal.QNameDerivedValueService;

/**
 * @author kamesh.sampath TODO:Qname validation
 */
@GenerateImpl
@Image( path = "images/elcl16/event.gif" )
public interface IEventDefinition extends IModelElement, IIdentifiable, IDescribeable, IQNamed {

	ModelElementType TYPE = new ModelElementType( IEventDefinition.class );

	// *** Qname ***

	@Label( standard = "Qname" )
	@Whitespace( trim = true )
	@XmlBinding( path = "qname" )
	@DefaultValue( text = "Q_NAME" )
	@DerivedValue( service = QNameDerivedValueService.class )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "qname" } )
	ValueProperty PROP_QnAME = new ValueProperty( TYPE, "Qname" );

	Value<String> getQname();

	void setQname( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@DefaultValue( text = "EVENT_NAME" )
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
