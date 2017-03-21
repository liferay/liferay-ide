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
package org.jboss.ide.eclipse.freemarker.editor;

import java.util.Iterator;
import java.util.List;


import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.MacroLibrary;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroInstance;

public class MacroHyperlinkDetector implements IHyperlinkDetector {

	private Editor editor;
	public MacroHyperlinkDetector(ITextViewer textViewer, Editor editor) {
		this.editor = editor;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		Item item = editor.getItemSet().getItem(region.getOffset());
		if (null != item && item instanceof MacroInstance) {
			MacroInstance instance = (MacroInstance) item;
			int index = instance.getName().indexOf('.');
			if (index > 0) {
				// it is from a macro library
				String namespace = instance.getName().substring(0, index);
				MacroLibrary macroLibrary = ConfigurationManager.getInstance(editor.getProject()).getMacroLibrary(namespace);
				if (null != macroLibrary) {
					for (int i=0; i<macroLibrary.getMacros().length; i++) {
						if (macroLibrary.getMacros()[i].getName().equals(instance.getName())) {
							// we have a match
							return new IHyperlink[]{new MacroHyperlink(
									instance, macroLibrary.getFile(),
									macroLibrary.getMacros()[i].getOffset(), macroLibrary.getMacros()[i].getLength())};
						}
					}
				}
				if (null != macroLibrary)
					return new IHyperlink[]{new MacroHyperlink(instance, macroLibrary.getFile(), -1, -1)};
			}
			else {
				List macroDefinitions = instance.getItemSet().getMacroDefinitions();
				for (Iterator i=macroDefinitions.iterator(); i.hasNext(); ) {
					MacroDirective macroDefinition = (MacroDirective) i.next();
					if (macroDefinition.getName().equals(instance.getName())) {
						return new IHyperlink[]{new MacroHyperlink(
								instance, editor.getFile(),
								macroDefinition.getOffset(), macroDefinition.getLength())};
					}
				}
			}
		}
		return null;
	}

	public void init (ITextViewer viewer) {
	}
}