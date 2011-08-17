/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh.sampath
 */
@GenerateImpl
@Image( path = "images/obj16/contianer_runtime.gif" )
public interface IContainerRuntimeOption extends IModelElement, INameable {

	ModelElementType TYPE = new ModelElementType( IContainerRuntimeOption.class );

	// *** Values ***

	@Type( base = IOptionValue.class )
	@Label( standard = "Options" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( type = IOptionValue.class, element = "value" ) } )
	ListProperty PROP_OPTIONS = new ListProperty( TYPE, "Options" );

	ModelElementList<IOptionValue> getOptions();

}
