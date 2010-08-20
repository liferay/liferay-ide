package com.liferay.ide.eclipse.ui.snippets;

import com.liferay.ide.eclipse.core.util.CoreUtil;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.VariableInsertionDialog;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;


@SuppressWarnings("restriction")
public class TaglibVariableInsertionDialog extends VariableInsertionDialog {

	public TaglibVariableInsertionDialog(Shell parentShell, boolean clearModality) {
		super(parentShell, clearModality);
	}

	@Override
	protected void prepareText() {
		// this could be horribly inefficient
		String text = fItem.getContentString();
		ISnippetVariable[] variables = fItem.getVariables();
		for (int i = 0; i < variables.length; i++) {
			String value = (String) fTableViewer.getColumnData()[1].get(((SnippetVariable) variables[i]).getId());

			if (!CoreUtil.isNullOrEmpty(value)) {
				value = variables[i].getName() + "=\"" + value + "\" ";
			}

			text = StringUtils.replace(text, "${" + variables[i].getName() + "}", value); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// remove all cursor markers
		text = StringUtils.replace(text, "${cursor}", ""); //$NON-NLS-1$ //$NON-NLS-2$

		// Update EOLs (bug 80231)
		String systemEOL = System.getProperty("line.separator"); //$NON-NLS-1$
		text = StringUtils.replace(text, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		text = StringUtils.replace(text, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!"\n".equals(systemEOL) && systemEOL != null) { //$NON-NLS-1$
			text = StringUtils.replace(text, "\n", systemEOL); //$NON-NLS-1$
		}

		setPreparedText(text);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		if (control instanceof Composite) {
			Composite composite = (Composite) control;
			replaceUIText(composite, "variable", "attribute");
			replaceUIText(composite, "Variable", "Attribute");
		}

		fTableViewer.getTable().getColumns()[0].setText("Attribute Name");
		fTableViewer.getTable().redraw();

		return control;
	}

	protected void replaceUIText(Composite parent, String search, String replace) {
		if (parent == null) {
			return;
		}

		if (parent.getChildren() == null || parent.getChildren().length == 0) {
			return;
		}

		for (Control child : parent.getChildren()) {
			if (child instanceof Label) {
				Label label = (Label) child;
				if (label.getText() != null) {
					label.setText(label.getText().replaceAll(search, replace));
				}
			}
			else if (child instanceof Text) {
				Text text = (Text) child;
				if (text.getText() != null) {
					text.setText(text.getText().replaceAll(search, replace));
				}
			}
			else if (child instanceof Composite) {
				replaceUIText((Composite) child, search, replace);
			}
		}
	}

}
