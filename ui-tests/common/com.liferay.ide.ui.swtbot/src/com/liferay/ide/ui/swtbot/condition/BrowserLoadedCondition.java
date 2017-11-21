package com.liferay.ide.ui.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

public class BrowserLoadedCondition implements ICondition {

	public BrowserLoadedCondition(SWTBot bot) {
		_bot = bot;
	}

	@Override
	public String getFailureMessage() {
		return "Broswer is still loading";
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public boolean test() throws Exception {
		return _bot.browser().isPageLoaded();
	}

	private final SWTBot _bot;

}