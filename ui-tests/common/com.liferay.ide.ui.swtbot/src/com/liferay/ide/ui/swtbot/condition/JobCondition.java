package com.liferay.ide.ui.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

public abstract class JobCondition implements ICondition {

	public JobCondition(Object family, String readableJobFamily) {
		this.family = family;

		this.readableJobFamily = readableJobFamily;
	}

	@Override
	public abstract boolean test();

	protected final Object family;
	protected final String readableJobFamily;

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public abstract String getFailureMessage();

}
