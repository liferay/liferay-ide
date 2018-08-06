/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.maven.core.aether;

import java.io.PrintStream;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

/**
 * A simplistic repository listener that logs events to the console.
 * @author Gregory Amerson
 */
public class ConsoleRepositoryListener extends AbstractRepositoryListener {

	public ConsoleRepositoryListener() {
		this(null);
	}

	public ConsoleRepositoryListener(PrintStream out) {
		_out = (out != null) ? out : System.out;
	}

	public void artifactDeployed(RepositoryEvent event) {
		_out.println("Deployed " + event.getArtifact() + " to " + event.getRepository());
	}

	public void artifactDeploying(RepositoryEvent event) {
		_out.println("Deploying " + event.getArtifact() + " to " + event.getRepository());
	}

	public void artifactDescriptorInvalid(RepositoryEvent event) {
		Exception exception = event.getException();

		_out.println("Invalid artifact descriptor for " + event.getArtifact() + ": " + exception.getMessage());
	}

	public void artifactDescriptorMissing(RepositoryEvent event) {
		_out.println("Missing artifact descriptor for " + event.getArtifact());
	}

	public void artifactDownloaded(RepositoryEvent event) {
		_out.println("Downloaded artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	public void artifactDownloading(RepositoryEvent event) {
		_out.println("Downloading artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	public void artifactInstalled(RepositoryEvent event) {
		_out.println("Installed " + event.getArtifact() + " to " + event.getFile());
	}

	public void artifactInstalling(RepositoryEvent event) {
		_out.println("Installing " + event.getArtifact() + " to " + event.getFile());
	}

	public void artifactResolved(RepositoryEvent event) {
		_out.println("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
	}

	public void artifactResolving(RepositoryEvent event) {
		_out.println("Resolving artifact " + event.getArtifact());
	}

	public void metadataDeployed(RepositoryEvent event) {
		_out.println("Deployed " + event.getMetadata() + " to " + event.getRepository());
	}

	public void metadataDeploying(RepositoryEvent event) {
		_out.println("Deploying " + event.getMetadata() + " to " + event.getRepository());
	}

	public void metadataInstalled(RepositoryEvent event) {
		_out.println("Installed " + event.getMetadata() + " to " + event.getFile());
	}

	public void metadataInstalling(RepositoryEvent event) {
		_out.println("Installing " + event.getMetadata() + " to " + event.getFile());
	}

	public void metadataInvalid(RepositoryEvent event) {
		_out.println("Invalid metadata " + event.getMetadata());
	}

	public void metadataResolved(RepositoryEvent event) {
		_out.println("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
	}

	public void metadataResolving(RepositoryEvent event) {
		_out.println("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
	}

	private PrintStream _out;

}