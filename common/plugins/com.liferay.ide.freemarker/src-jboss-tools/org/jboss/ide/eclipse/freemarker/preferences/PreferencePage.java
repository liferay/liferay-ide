/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.preferences;


import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.ide.eclipse.freemarker.Constants;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	public static final String ID = "com.liferay.ide.freemarker.preferences.OutlinePreferencePage";
	
	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Plugin.getDefault().getPreferenceStore());
		setDescription(Messages.PreferencePage_DESCRIPTION_FREEMARKER_SETTINGS);
	}

	public void createFieldEditors() {
		addField(new ColorFieldEditor(Constants.COLOR_DIRECTIVE,
				Messages.PreferencePage_FIELD_DIRECTIVE, getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_RELATED_ITEM,
                Messages.PreferencePage_FIELD_RELATED_DIRECTIVES, getFieldEditorParent()));
		addField(new BooleanFieldEditor(Constants.HIGHLIGHT_RELATED_ITEMS,
				Messages.PreferencePage_FIELD_HIGHLIGHT_RELATED_DIRECTIVES, getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_INTERPOLATION,
				Messages.PreferencePage_FIELD_INTERPOLATION, getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_TEXT,
				Messages.PreferencePage_FIELD_TEXT, getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_COMMENT,
				Messages.PreferencePage_FIELD_COMMENT, getFieldEditorParent()));
		addField(new ColorFieldEditor(Constants.COLOR_STRING,
				Messages.PreferencePage_FIELD_STRING, getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_XML_TAG,
                Messages.PreferencePage_FIELD_HTML_XML_TAG, getFieldEditorParent()));
        addField(new ColorFieldEditor(Constants.COLOR_XML_COMMENT,
                Messages.PreferencePage_FIELD_HTML_XML_COMMENT, getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
}