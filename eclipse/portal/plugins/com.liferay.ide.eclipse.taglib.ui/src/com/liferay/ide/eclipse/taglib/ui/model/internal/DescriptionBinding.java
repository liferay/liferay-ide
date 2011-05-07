
package com.liferay.ide.eclipse.taglib.ui.model.internal;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;


public class DescriptionBinding extends XmlValueBindingImpl {

	@Override
	public String read() {
		String value = null;

		final XmlElement element = xml(false);

		XmlElement desc = element.getChildElement("description", false);

		if (desc != null) {
			value = desc.getText(true);

			// remove everything that is in a comment
			if (value != null) {
				value = value.replaceAll("<!--.*-->", "");
			}
		}

		return value;
	}

	@Override
	public void write(String value) {
	}

}
