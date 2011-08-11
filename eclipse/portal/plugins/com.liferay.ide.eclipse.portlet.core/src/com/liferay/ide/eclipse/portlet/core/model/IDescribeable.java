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
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface IDescribeable extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IDescribeable.class );

	/*
	 * Description Element
	 */

	@Label( standard = "Description" )
	@NoDuplicates
	@XmlBinding( path = "description" )
	@Whitespace( trim = true )
	ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );

	Value<String> getDescription();

	void setDescription( String description );

}
