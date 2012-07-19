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
package org.jboss.ide.eclipse.freemarker.configuration;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.model.LibraryMacroDirective;
import org.jboss.ide.eclipse.freemarker.model.MacroDirective;
import org.jboss.ide.eclipse.freemarker.util.StringUtil;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class MacroLibrary {

	public static final String TYPE_FILE = "file"; //$NON-NLS-1$
	public static final String TYPE_JAR_ENTRY = "jarEntry"; //$NON-NLS-1$
	
	private long lastUpdatedTime;
	private IFile file;
	private String content;
	private String path;
	private String namespace;
	private String type;
	private MacroDirective[] macros;

	public MacroLibrary (String namespace, IFile file) throws IOException, CoreException {
		this.namespace = namespace;
		this.file = file;
		this.content = StringUtil.getStringFromStream(file.getContents(true));
		this.type = TYPE_FILE;
	}

	public MacroLibrary (String namespace, InputStream is, String path, String type) throws IOException {
		this.namespace = namespace;
		this.content = StringUtil.getStringFromStream(is);
		this.type = type;
		this.path = path;
		if (null == this.type) this.type = TYPE_FILE;
	}

	public synchronized MacroDirective[] getMacros() {
		if (null == macros
				|| isStale()) {
				load();
		}
		return macros;
	}

	public boolean isStale () {
		return (null != file && file.getModificationStamp() > lastUpdatedTime);
	}

	public IFile getFile () {
		return file;
	}

	private void load () {
		try {
			List macros = new ArrayList();
			String search = "#macro "; //$NON-NLS-1$
			int index = content.indexOf(search);
			int startIndex = index;
			char startChar = content.charAt(index-1);
			char endChar;
			if (startChar == '[') endChar = ']';
			else endChar = '>';
			while (startIndex > 0) {
				int stackCount = 0;
				boolean inString = false;
				// find the end
				int endIndex = Integer.MIN_VALUE;
				boolean escape = false;
				while (content.length() > index && index >= 0) {
					boolean doEscape = false;
					char c = content.charAt(index++);
					if (!escape) {
						if (c == '\"') inString = !inString;
						else if (c == '\\' && inString) doEscape = true;
						else if (c == startChar) stackCount ++;
						else if (c == endChar) {
							if (stackCount > 0) stackCount --;
							else {
								endIndex = index-1;
								break;
							}
						}
					}
					escape = doEscape;
				}
				if (endIndex > 0) {
					String sub = content.substring(startIndex, endIndex);
					MacroDirective macroDirective = 
						new LibraryMacroDirective(namespace, sub, startIndex-1, endIndex-index+2);
					macros.add(macroDirective);
					index = content.indexOf(startChar + search, endIndex);
					if (index >= 0) index++;
					startIndex = index;
					endIndex = Integer.MIN_VALUE;
				}
				else {
					break;
				}
			}
			this.macros = (MacroDirective[]) macros.toArray(
					new MacroDirective[macros.size()]);
			if (null != file)
				this.lastUpdatedTime = file.getModificationStamp();
		}
		catch (Exception e) {
			macros = new MacroDirective[0];
			Plugin.log(e);
		}
	}

	public String getNamespace() {
		return namespace;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toXML () {
		StringBuffer sb = new StringBuffer();
		sb.append("<entry namespace=\"" + getNamespace() + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append("path=\"" + getPath() + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
		if (null != file) {
			sb.append("project=\"" + file.getProject().getName() + "\" "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		sb.append("type=\"" + getType() + "\"/>"); //$NON-NLS-1$//$NON-NLS-2$
		return sb.toString();
	}

	public String getPath () {
		if (null != file)
			return file.getProjectRelativePath().toString();
		else
			return path;
	}

	public static MacroLibrary fromXML (IProject project, Element node, ClassLoader classLoader) throws CoreException, IOException {
        String namespace = node.getAttribute("namespace"); //$NON-NLS-1$
		String path = node.getAttribute("path"); //$NON-NLS-1$
		String projectName = node.getAttribute("project"); //$NON-NLS-1$
		String type = node.getAttribute("type"); //$NON-NLS-1$
        if (null == type || type.length() == 0 || type.equals(TYPE_FILE)) {
        	if (null != projectName && projectName.length() > 0)
        		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        	IFile file = project.getFile(new Path(path));
        	if (null == file || !file.exists()) return null;
        	else return new MacroLibrary(namespace, file);
        }
        else if (type.equals(TYPE_JAR_ENTRY)) {
        	InputStream is = classLoader.getResourceAsStream(path);
        	if (null != is) {
        		return new MacroLibrary(namespace, is, path, TYPE_JAR_ENTRY);
        	}
        	else return null;
        }
        else return null;
	}
}