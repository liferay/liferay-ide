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

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;


public class FunctionDirective extends AbstractDirective {

	private FunctionEndDirective endDirective;

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	public boolean isStartItem() {
		return super.isNestable();
	}

	public void relateItem(Item directive) {
		if (directive instanceof FunctionEndDirective)
			endDirective = (FunctionEndDirective) directive;
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof FunctionEndDirective);
	}

	public FunctionEndDirective getEndDirective() {
		return endDirective;
	}

	public Item getRelatedItem() {
		return getEndDirective();
	}

	public String getTreeImage() {
		return "element.png"; //$NON-NLS-1$
	}

	public void addToContext(Map context) {
		String[] contents = splitContents();
		if (contents.length >= 2) {
			String key = contents[1];
			context.put(key, Object.class);
		}
	}

	public Item getEndItem() {
		return endDirective;
	}
}