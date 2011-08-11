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
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import com.liferay.ide.eclipse.portlet.core.model.internal.WindowStateImageService;

/**
 * @author kamesh
 */
@GenerateImpl
// @Image( path = "images/window_states.png" )
@Service( impl = WindowStateImageService.class )
public interface ICustomWindowState extends IModelElement, IDescribeable, IIdentifiable {

	ModelElementType TYPE = new ModelElementType( ICustomWindowState.class );

	// *** WindowState ***

	@Type( base = IWindowState.class )
	@Label( standard = "Window State" )
	@XmlBinding( path = "window-state" )
	ValueProperty PROP_WINDOW_STATE = new ValueProperty( TYPE, "WindowState" );

	Value<IWindowState> getWindowState();

	void setWindowState( String value );

	void setWindowState( IWindowState value );

}
