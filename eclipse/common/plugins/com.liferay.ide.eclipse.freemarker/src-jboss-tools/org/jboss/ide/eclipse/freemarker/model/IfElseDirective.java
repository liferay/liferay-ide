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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class IfElseDirective extends AbstractDirective {
	private IfDirective ifDirective;

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	public void relateItem(Item directive) {
		if (directive instanceof IfDirective)
			ifDirective = (IfDirective) directive;
		else if (null == ifDirective) {
			if (directive instanceof ElseIfDirective) ifDirective = ((ElseIfDirective) directive).getIfDirective();
			if (directive instanceof IfElseDirective) ifDirective = ((IfElseDirective) directive).getIfDirective();
			if (directive instanceof IfEndDirective) ifDirective = ((IfEndDirective) directive).getIfDirective();
		}
		if (directive instanceof IfEndDirective && null != ifDirective) {
			ifDirective.relateItem(directive);
		}
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof IfDirective
				|| directive instanceof IfElseDirective
				|| directive instanceof ElseIfDirective
				|| directive instanceof IfEndDirective);
	}

	public boolean isNestable() {
		return true;
	}

	public IfDirective getIfDirective() {
		return ifDirective;
	}

	public Item[] getRelatedItems() {
		return (null == getIfDirective()) ?
				null : getIfDirective().getRelatedItems();
	}

	public Item getStartItem () {
		return getIfDirective();
	}

	public String getTreeImage() {
		return "else.png"; //$NON-NLS-1$
	}

	public boolean isStartAndEndItem() {
		return true;
	}
}