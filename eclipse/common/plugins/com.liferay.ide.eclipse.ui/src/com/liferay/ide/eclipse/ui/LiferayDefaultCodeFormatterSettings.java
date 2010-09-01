package com.liferay.ide.eclipse.ui;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

@SuppressWarnings({
	"restriction", "rawtypes", "unchecked"
})
public class LiferayDefaultCodeFormatterSettings extends DefaultCodeFormatterConstants {

	public final static Map settings = DefaultCodeFormatterOptions.getEclipseDefaultSettings().getMap();
	{
		settings.put(FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_IF, JavaCore.DO_NOT_INSERT);
	}
	
}
