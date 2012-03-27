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


public class LibraryMacroDirective extends MacroDirective {

	private String contents;
	private String namespace;
	private int offset;
	private int length;

	public static void main (String[] args) {
		try {
			String content = "#macro entries startIndex=1\r\n" + //$NON-NLS-1$
			"data=\"data\" headerUrls=[] sortIndex=-1"; //$NON-NLS-1$
			
			LibraryMacroDirective lmd = new LibraryMacroDirective("lib", content, 0, content.length()); //$NON-NLS-1$
			String[] attributes = lmd.getAttributes();
			for (int i=0; i<attributes.length; i++) {
				System.out.println(attributes[i]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LibraryMacroDirective (String namespace, String contents, int offset, int length) {
		this.contents = contents;
		this.namespace = namespace;
		this.offset = offset;
		this.length = length;
	}

	private String name;
	public String getName() {
		if (null == name)
			name = namespace + "." + super.getName(); //$NON-NLS-1$
		return name;
	}

	public String getContents() {
		return contents;
	}

	public String getFullContents() {
		return contents;
	}

	protected int getCursorPosition(int offset) {
		return 1;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}
}