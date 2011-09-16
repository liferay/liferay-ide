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
@Label( standard = "Window State" )
@GenerateImpl
public interface IWindowState extends IModelElement {

	ModelElementType TYPE = new ModelElementType( IWindowState.class );

	/*
	 * state
	 */

	@Label( standard = "state", full = "Window State" )
	@NoDuplicates
	@Whitespace( trim = true )
	@XmlBinding( path = "" )
	// @CustomXmlValueBinding( impl = TextNodeValueBinding.class, params = "window-state" )
	ValueProperty PROP_WINDOW_STATE = new ValueProperty( TYPE, "WindowState" );

	Value<String> getWindowState();

	void setWindowState( String state );
}
