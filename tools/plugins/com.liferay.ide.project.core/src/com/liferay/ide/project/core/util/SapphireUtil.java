package com.liferay.ide.project.core.util;

import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Value;

public class SapphireUtil {

	public static final <T> T getValue( final Value<T> valueProperty ) {
		if ( valueProperty != null) {
			return valueProperty.content();	
		}
		return null;
	}
	
	public static void propertyAttachListener( final Value<?> valueProperty, Listener listener ) {
		if ( valueProperty != null) {
			valueProperty.attach(listener);
		}
	}
}
