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
package org.jboss.ide.eclipse.freemarker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class MacroDirective extends AbstractDirective implements Comparable {

	private MacroEndDirective endDirective;
	private String name;

	public MacroDirective () {}

	public MacroDirective (String contents) {
		this.contents = contents;
	}
	
	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}
	
	public boolean isStartItem() {
		return true;
	}

	public void relateItem(Item directive) {
		if (directive instanceof MacroEndDirective)
			endDirective = (MacroEndDirective) directive;
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof MacroEndDirective);
	}

	public boolean isNestable() {
		return true;
	}

	public MacroEndDirective getEndDirective() {
		return endDirective;
	}

	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			ArrayList l = new ArrayList();
			if (null != getEndDirective())
				l.add(getEndDirective());
			relatedItems = (Item[]) l.toArray(new Item[l.size()]);
		}
		return relatedItems;
	}
	private Item[] relatedItems;

	public String getTreeImage() {
		return "macro.png"; //$NON-NLS-1$
	}

	public String getTreeDisplay() {
		return getSplitValue(1);
	}

	private String[] attributes;
	public String[] getAttributes () {
		if (null == attributes) {
			List l = new ArrayList();
			String[] contents = splitContents();
			int i = 2;
			while (i<contents.length) {
				String att = contents[i];
				int index = att.indexOf("="); //$NON-NLS-1$
				if (index < 0) {
					att = att.trim();
					if (att.endsWith("]") || att.endsWith(">")) att = att.substring(0, att.length()-1); //$NON-NLS-1$ //$NON-NLS-2$
					l.add(att);
					i++;
				}
				else {
					i+=2;
				}
			}
			attributes = (String[]) l.toArray(new String[l.size()]);
		}
		return attributes;
	}

	public String getName() {
		if (null == name) {
			name = getSplitValue(1);
		}
		return name;
	}

	public Item getEndItem() {
		return endDirective;
	}

	private static final char[] descriptorTokens = new char[]{'/','#','@','<','>'};
	public char[] getDescriptors () {
		return descriptorTokens;
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof MacroDirective)
			return (getName().compareTo(((MacroDirective) arg0).getName()));
		else
			return 0;
	}

	public void addToContext(Map context) {
		for (int i=0; i<getAttributes().length; i++) {
			if (null == context.get(getAttributes()[i]))
				context.put(getAttributes()[i], Object.class);
		}
	}

	public void removeFromContext(Map context) {
		for (int i=0; i<getAttributes().length; i++) {
			Object obj = context.get(getAttributes()[i]);
			if (null != obj && obj.equals(Object.class))
				context.remove(getAttributes()[i]);
		}
	}
}