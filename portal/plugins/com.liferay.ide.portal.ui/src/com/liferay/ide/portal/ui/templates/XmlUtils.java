/*******************************************************************************
 * Copyright (c) 2008-2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.portal.ui.templates;

import java.io.File;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;

/**
 * @author mkleint
 */
public class XmlUtils
{

	/**
	 * what is this method supposed to do? for the sourceViewer find the associated file on disk and for that one find
	 * the IProject it belongs to. The required condition for the IProject instance is that project relative path of the
	 * file shall only be pom.xml (thus no nested, unopened maven pom). So that when
	 * MavenPlugin.getMavenProjectManager().getProject(prj); is called later on the instance, it actually returns the
	 * maven model facade for the pom.xml backing the sourceViewer.
	 *
	 * @param sourceViewer
	 * @return
	 */
	public static IProject extractProject( ITextViewer sourceViewer )
	{
		ITextFileBuffer buf = FileBuffers.getTextFileBufferManager().getTextFileBuffer( sourceViewer.getDocument() );
		if( buf == null )
		{
			// eg. for viewers of pom files in local repository
			return null;
		}
		IFileStore folder = buf.getFileStore();
		File file = new File( folder.toURI() );
		IPath path = Path.fromOSString( file.getAbsolutePath() );
		// Stack<IFile> stack = new Stack<IFile>();
		// here we need to find the most inner project to the path.
		// we do so by shortening the path and remembering all the resources identified.
		// at the end we pick the last one from the stack. is there a catch to it?
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( path );
		if( ifile != null )
		{
			return ifile.getProject();
			// stack.push(ifile);
		}
		// while(path.segmentCount() > 1) {
		// IResource ires = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		// if(ires != null && ires instanceof IFile) {
		// stack.push((IFile)ires);
		// }
		// path = path.removeFirstSegments(1);
		// }
		// IFile res = stack.empty() ? null : stack.pop();
		// if (res != null) {
		// IProject prj = res.getProject();
		// the project returned is in a way unrelated to nested child poms that don't have an opened project,
		// in that case we pass along a wrong parent/aggregator
		// if (res.getProjectRelativePath().segmentCount() != 1) {
		// if the project were the pom's project, the relative path would be just "pom.xml", if it's not just throw it
		// out of the window..
		// prj = null;
		// }
		// return prj;
		return null;
	}

}
