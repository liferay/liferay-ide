/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;

/**
 * @author kamesh
 */
@GenerateImpl
@Image( path = "images/user_icon.png" )
public interface IUserAttribute extends IModelElement, IIdentifiable, IDescribeable, INameable {

	ModelElementType TYPE = new ModelElementType( IUserAttribute.class );

}
