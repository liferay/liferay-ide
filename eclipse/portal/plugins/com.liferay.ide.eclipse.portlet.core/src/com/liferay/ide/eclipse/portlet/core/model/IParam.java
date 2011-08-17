
package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;

/**
 * @author kamesh
 */
@GenerateImpl
@Image( path = "images/obj16/param_obj.gif" )
public interface IParam extends IModelElement, IIdentifiable, IDescribeable, INameValue {

	ModelElementType TYPE = new ModelElementType( IParam.class );

}
