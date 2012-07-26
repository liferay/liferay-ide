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


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.model.MacroInstance;

public class MacroHyperlink  implements IHyperlink {
	
	private MacroInstance macroInstance;
	private IFile file;
	private int offset;
	private int length;

	public MacroHyperlink(MacroInstance macroInstance, IFile file, int offset, int length) {
		this.macroInstance = macroInstance;
		this.file = file;
		this.offset = offset;
		this.length = length;
	}

	public void open() {
		try {
			IEditorPart editorPart = Plugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(new FileEditorInput(file), FreemarkerMultiPageEditor.ID);
			if (offset >= 0 && length > 0 && editorPart instanceof FreemarkerMultiPageEditor) {
				((FreemarkerMultiPageEditor) editorPart).selectAndReveal(offset, length);
			}
		}
		catch (PartInitException e) {
			Plugin.error(e);
		}
	}

	public IRegion getHyperlinkRegion() {
		return macroInstance.getRegion();
	}

	public String getHyperlinkText() {
		return macroInstance.getName();
	}

	public String getTypeLabel() {
		return Messages.MacroHyperlink_TYPELABEL_MACRO_DEFINITION;
	}
}