/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
public interface IListener extends IModelElement, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IListener.class );

	// *** Implementation ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@Label( standard = "implementation class", full = "listener implementation class" )
	@Required
	@MustExist
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "javax.portlet.PortletContextListener" )
	@XmlBinding( path = "listener-class" )
	@Documentation( content = "The listener implementation class." )
	ValueProperty PROP_IMPLEMENTATION = new ValueProperty( TYPE, "Implementation" );

	ReferenceValue<JavaTypeName, JavaType> getImplementation();

	void setImplementation( String value );

	void setImplementation( JavaTypeName value );

}
