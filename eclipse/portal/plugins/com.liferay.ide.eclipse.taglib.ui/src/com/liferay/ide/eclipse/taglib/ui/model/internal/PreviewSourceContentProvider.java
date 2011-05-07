
package com.liferay.ide.eclipse.taglib.ui.model.internal;

import com.liferay.ide.eclipse.taglib.ui.model.IAttribute;
import com.liferay.ide.eclipse.taglib.ui.model.ITag;

import org.eclipse.sapphire.modeling.DerivedValueService;


public class PreviewSourceContentProvider extends DerivedValueService {

	@Override
	public String getDerivedValue() {
		boolean preview = "Preview".equals(this.property().getName());

		ITag tag = (ITag) this.element();

		StringBuffer buffer = new StringBuffer();

		String tagName = tag.getName().getContent();

		String prefix = tag.getPrefix().getContent();

		if (preview) {
			buffer.append("<span style='color:RGB(64,128,128)'>&lt;");
		}
		else {
			buffer.append("<");
		}

		buffer.append(prefix + ":" + tagName);

		if (preview) {
			buffer.append("</span>");
		}

		for (IAttribute attr : tag.getRequiredAttributes()) {
			appendAttr(attr, buffer, preview);
		}

		for (IAttribute attr : tag.getOtherAttributes()) {
			appendAttr(attr, buffer, preview);
		}

		for (IAttribute attr : tag.getEvents()) {
			appendAttr(attr, buffer, preview);
		}

		if (preview) {
			buffer.append("<span style='color:RGB(64,128,128)'>&gt;&lt;");
		}
		else {
			buffer.append("><");
		}

		buffer.append("/" + prefix + ":" + tagName);

		if (preview) {
			buffer.append("&gt;</span>");
		}
		else {
			buffer.append(">");
		}

		return buffer.toString();
	}

	protected void appendAttr(IAttribute attr, StringBuffer buffer, boolean preview) {
		String content = attr.getValue().getContent();

		if (content != null) {
			buffer.append(" ");

			if (preview) {
				buffer.append("<span style='color:RGB(127,0,127)'>");
			}

			buffer.append(attr.getName().getContent());

			if (preview) {
				buffer.append("</span>");
			}

			buffer.append("=");

			if (preview) {
				buffer.append("<span style='color:RGB(42,0,255);font-style:italic'>&quot;");
			}
			else {
				buffer.append("\"");
			}

			buffer.append(content);

			if (preview) {
				buffer.append("&quot;</span>");
			}
			else {
				buffer.append("\"");
			}
		}
	}

}
