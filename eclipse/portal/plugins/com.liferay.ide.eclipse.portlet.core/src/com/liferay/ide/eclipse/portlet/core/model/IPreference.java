
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.InvertingBooleanXmlValueBinding;

@GenerateImpl
public interface IPreference extends IModelElement, IIdentifiable, IDescribeable, INameValue {

	ModelElementType TYPE = new ModelElementType( IPreference.class );

	// *** ReadOnly ***

	@Type( base = Boolean.class )
	@Label( standard = "Read Only" )
	@CustomXmlValueBinding( impl = InvertingBooleanXmlValueBinding.class, params = "read-only" )
	ValueProperty PROP_READ_ONLY = new ValueProperty( TYPE, "ReadOnly" );

	Value<Boolean> getReadOnly();

	void setReadOnly( String value );

	void setReadOnly( Boolean value );

}
