package com.liferay.ide.eclipse.service.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/jrtexception_obj_16x16.gif")
public interface IException extends IModelElement {

    ModelElementType TYPE = new ModelElementType( IException.class );
    
	// *** Exception ***

	@XmlBinding(path = "")
	@Label(standard = "&exception")
	ValueProperty PROP_EXCEPTION = new ValueProperty(TYPE, "Exception");

	Value<String> getException();

	void setException(String value);

}
