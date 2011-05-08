package com.liferay.ide.eclipse.ui.snippets;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;


@SuppressWarnings("restriction")
public class TaglibSnippetInsertion extends AbstractSnippetInsertion {

	public TaglibSnippetInsertion() {
		super();
	}

	/**
	 * Copied from DefaultSnippetInsertion.getInsertString() version 1.7 (WTP 3.2.1)
	 */
	@Override
	protected String getResolvedString(Shell host) {
		String insertString = null;
		if (fItem.getVariables().length > 0) {
			insertString = TaglibVariableItemHelper.getInsertString(host, fEditorPart, fItem);
		}
		else {
			insertString = StringUtils.replace(fItem.getContentString(), "${cursor}", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return insertString;
	}

}
