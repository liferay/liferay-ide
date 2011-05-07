
package com.liferay.ide.eclipse.taglib.ui.model.internal;

import org.eclipse.sapphire.modeling.ValueBindingImpl;


public class PreviewBinding extends ValueBindingImpl {

	@Override
	public String read() {
		return "preview string " + System.currentTimeMillis();
	}

	@Override
	public void write(String value) {
	}

}
