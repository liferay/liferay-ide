package com.liferay.ide.ui.swtbot.eclipse.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

public class ShowViewDialog extends TextDialog {

	public ShowViewDialog(SWTWorkbenchBot bot) {
		super(bot, CANCEL, OPEN);
	}

}
