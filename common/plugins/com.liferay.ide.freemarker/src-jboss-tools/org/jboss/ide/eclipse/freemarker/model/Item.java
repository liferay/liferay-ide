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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;

public interface Item {

	public void load (ITypedRegion region, ISourceViewer viewer, IResource resource);

	public boolean isNestable ();

	public boolean isStartItem ();

	public boolean isEndItem ();
	
	public boolean isStartAndEndItem ();
	
	public Item getStartItem ();

	public Item getEndItem ();

	public boolean relatesToItem (Item directive);

	public void relateItem (Item directive);

	public ITypedRegion getRegion();

	public List getChildItems();

	public Item getParentItem();
	
	public void setParentItem(Item item);

	public void addSubDirective(Item directive);

	public Item[] getRelatedItems ();
	
	public String getContents();

	public String getTreeImage();
	
	public String getTreeDisplay();

	public ICompletionProposal[] getCompletionProposals(int offset, Map context);

	public void setItemSet (ItemSet itemSet);

	public String getFirstToken ();

	public void addToContext (Map context);

	public void removeFromContext (Map context);

	public String getName();
}
