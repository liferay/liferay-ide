/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface INameable extends IModelElement {

	ModelElementType TYPE = new ModelElementType( INameable.class );

	/*
	 * Name Element
	 */

	@Label( standard = "Name" )
	@Required
	@NoDuplicates
	@XmlBinding( path = "name" )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String name );

}
