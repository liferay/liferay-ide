package formatter;


File workspaceDir = new File("..")
File formatterSettingsXml = new File("bin/formatter/liferay_code_formatter_settings.xml")
File formatterSettingsJava = new File(workspaceDir, "com.liferay.ide.ui/src/com/liferay/ide/eclipse/ui/LiferayDefaultCodeFormatterSettings.java")

XmlParser xmlParser = new XmlParser()

Node settingsRootNode = xmlParser.parse(formatterSettingsXml)

def settings = []

settingsRootNode.profile.setting.findAll {
	def key = it.'@id'
	def value = it.'@value'

	key = key.replaceAll("org.eclipse.jdt.core.", "")

	def settingsKey
	if (key.startsWith("formatter.")) {
		def constant = key.substring("formatter.".length(), key.length())
		constant = constant.replaceAll("\\.", "_")
		settingsKey = "FORMATTER_${constant.toString().toUpperCase()}"

		settings << "\t\tsettings.put(${settingsKey}, \"${value}\");\n"
	}
}

def template = '''
/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.ui;

import java.util.Map;

import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({
	"restriction", "rawtypes", "unchecked", "deprecation"
})
public class LiferayDefaultCodeFormatterSettings extends DefaultCodeFormatterConstants {

	public final static Map settings = DefaultCodeFormatterOptions.getEclipseDefaultSettings().getMap();
	static {
__settings__
	}

}
'''

def settingsBlock = new StringBuffer()

settings.each {  settingsBlock.append(it) }

formatterSettingsJava.setText(template.replaceAll("__settings__", settingsBlock.toString()))