/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.EventDefinitionReferenceService;
import com.liferay.ide.eclipse.portlet.core.model.internal.NameAndQNameChoiceValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface IEventDefinitionRef extends IModelElement, IIdentifiable, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IEventDefinitionRef.class );

	// *** Qname ***

	@Label( standard = "Qname" )
	@XmlBinding( path = "qname" )
	@Whitespace( trim = true )
	@DefaultValue( text = "Q_NAME" )
	@NoDuplicates
	@PossibleValues( property = "/EventDefinitions/Qname" )
	@Service( impl = EventDefinitionReferenceService.class, params = { "qname" } )
	@CustomXmlValueBinding( impl = NameAndQNameChoiceValueBinding.class, params = { "qname" } )
	ValueProperty PROP_Q_NAME = new ValueProperty( TYPE, "Qname" );

	Value<String> getQname();

	void setQname( String value );

	// *** Name ***

	@Label( standard = "Name" )
	@XmlBinding( path = "name" )
	@Whitespace( trim = true )
	@NoDuplicates
	@DefaultValue( text = "EVENT_NAME" )
	@Enablement( expr = "${Qname == 'Q_NAME'}" )
	@PossibleValues( property = "/EventDefinitions/Name" )
	@Service( impl = EventDefinitionReferenceService.class, params = { "name" } )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

}
