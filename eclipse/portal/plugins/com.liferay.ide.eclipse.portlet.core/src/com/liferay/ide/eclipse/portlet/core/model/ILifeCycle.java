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
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.TextNodeValueBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface ILifeCycle extends IModelElement {

	ModelElementType TYPE = new ModelElementType( ILifeCycle.class );

	// *** LifeCycle ***

	@Type( base = LifeCycleType.class )
	@Label( standard = "life cyle name" )
	@CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = { "lifecycle" } )
	@XmlBinding( path = "lifecycle" )
	@DefaultValue( text = "ACTION_PHASE" )
	ValueProperty PROP_LIFE_CYCLE = new ValueProperty( TYPE, "LifeCycle" );

	Value<LifeCycleType> getLifeCycle();

	void setLifeCycle( String value );

	void setLifeCycle( LifeCycleType value );

}
