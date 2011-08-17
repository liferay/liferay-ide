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
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/elcl16/filter.gif" )
public interface IFilter extends IModelElement, IDescribeable {

	ModelElementType TYPE = new ModelElementType( IFilter.class );

	// *** DisplayName ***

	@Label( standard = "display name" )
	@XmlBinding( path = "display-name" )
	ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" );

	Value<String> getDisplayName();

	void setDisplayName( String value );

	// *** Name ***

	@Label( standard = "name" )
	@Required
	@MustExist
	@XmlBinding( path = "filter-name" )
	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();

	void setName( String value );

	// *** Implementation ***

	@Type( base = JavaTypeName.class )
	@Reference( target = JavaType.class )
	@Label( standard = "implementation class", full = "filter implementation class" )
	@Required
	@MustExist
	@JavaTypeConstraint( kind = JavaTypeKind.CLASS, type = "javax.portlet.Filter" )
	@XmlBinding( path = "filter-class" )
	ValueProperty PROP_IMPLEMENTATION = new ValueProperty( TYPE, "Implementation" );

	ReferenceValue<JavaTypeName, JavaType> getImplementation();

	void setImplementation( String value );

	void setImplementation( JavaTypeName value );

	// *** LifeCycle ***

	@Type( base = ILifeCycle.class )
	@Label( standard = "lifecycle" )
	@CountConstraint( min = 1 )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "lifecycle", type = ILifeCycle.class ) )
	ListProperty PROP_LIFE_CYCLE = new ListProperty( TYPE, "LifeCycle" );

	ModelElementList<ILifeCycle> getLifeCycle();

	// *** InitParams ***

	@Type( base = IParam.class )
	@Label( standard = "initialization parameters" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "init-param", type = IParam.class ) )
	ListProperty PROP_INIT_PARAMS = new ListProperty( TYPE, "InitParams" );

	ModelElementList<IParam> getInitParams();

}
