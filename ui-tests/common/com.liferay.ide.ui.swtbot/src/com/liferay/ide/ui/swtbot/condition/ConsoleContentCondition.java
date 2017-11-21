package com.liferay.ide.ui.swtbot.condition;

import com.liferay.ide.ui.swtbot.eclipse.page.ConsoleView;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

public class ConsoleContentCondition implements ICondition{

	public ConsoleContentCondition(SWTWorkbenchBot bot, String content) {
		_bot = bot;

		_content = content;
	}

	@Override
	public boolean test() throws Exception {
		ConsoleView console = new ConsoleView(_bot);

		String consoleContent = console.getLog().getText();

		return consoleContent.contains(_content);
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public String getFailureMessage() {
		return "Console doesn't contain " + _content;
	}

	private SWTWorkbenchBot _bot;
	private String _content;
}
