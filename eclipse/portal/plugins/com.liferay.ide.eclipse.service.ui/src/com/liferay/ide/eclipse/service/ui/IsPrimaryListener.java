package com.liferay.ide.eclipse.service.ui;

import org.eclipse.sapphire.ui.listeners.ValuePropertyEditorListener;


public class IsPrimaryListener extends ValuePropertyEditorListener {

	@Override
	public void handleValueChanged() {
		System.out.println(this.getModelElement());
	}
}
