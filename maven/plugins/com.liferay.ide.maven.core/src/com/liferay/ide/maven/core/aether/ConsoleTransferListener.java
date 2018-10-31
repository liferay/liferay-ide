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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

/**
 * A simplistic transfer listener that logs uploads/downloads to the console.
 * @author Gregory Amerson
 */
public class ConsoleTransferListener extends AbstractTransferListener {

	public ConsoleTransferListener() {
		this(null);
	}

	public ConsoleTransferListener(PrintStream out) {
		_out = (out != null) ? out : System.out;
	}

	public void transferCorrupted(TransferEvent event) {
		Exception exception = event.getException();

		exception.printStackTrace(_out);
	}

	@Override
	public void transferFailed(TransferEvent event) {
		_transferCompleted(event);

		Exception exception = event.getException();

		if (!(exception instanceof MetadataNotFoundException)) {
			exception.printStackTrace(_out);
		}
	}

	@Override
	public void transferInitiated(TransferEvent event) {
		String message = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploading" : "Downloading";

		TransferResource resource = event.getResource();

		_out.println(message + ": " + resource.getRepositoryUrl() + resource.getResourceName());
	}

	@Override
	public void transferProgressed(TransferEvent event) {
		TransferResource resource = event.getResource();

		_downloads.put(resource, Long.valueOf(event.getTransferredBytes()));

		StringBuilder buffer = new StringBuilder(64);

		for (Map.Entry<TransferResource, Long> entry : _downloads.entrySet()) {
			TransferResource transferResource = entry.getKey();

			buffer.append(_getStatus(entry.getValue(), transferResource.getContentLength()));

			buffer.append("  ");
		}

		int pad = _lastLength - buffer.length();
		_lastLength = buffer.length();
		_pad(buffer, pad);
		buffer.append('\r');

		_out.print(buffer);
	}

	@Override
	public void transferSucceeded(TransferEvent event) {
		_transferCompleted(event);

		TransferResource resource = event.getResource();
		long contentLength = event.getTransferredBytes();

		if (contentLength >= 0) {
			String type = event.getRequestType() == TransferEvent.RequestType.PUT ? "Uploaded" : "Downloaded";
			String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B";

			String throughput = "";

			long duration = System.currentTimeMillis() - resource.getTransferStartTime();

			if (duration > 0) {

				// long bytes = contentLength - resource.getResumeOffset();
				// DecimalFormat format = new DecimalFormat( "0.0", new DecimalFormatSymbols(
				// Locale.ENGLISH ) );
				// double kbPerSec = ( bytes / 1024.0 ) / ( duration / 1000.0 );
				// throughput = " at " + format.format( kbPerSec ) + " KB/sec";

			}

			String messeage =
				type + ": " + resource.getRepositoryUrl() + resource.getResourceName() + " (" + len + throughput + ")";

			_out.println(messeage);
		}
	}

	protected long toKB(long bytes) {
		return (bytes + 1023) / 1024;
	}

	private String _getStatus(long complete, long total) {
		if (total >= 1024) {
			return toKB(complete) + "/" + toKB(total) + " KB ";
		}
		else if (total >= 0) {
			return complete + "/" + total + " B ";
		}
		else if (complete >= 1024) {
			return toKB(complete) + " KB ";
		}
		else {
			return complete + " B ";
		}
	}

	private void _pad(StringBuilder buffer, int spaces) {
		String block = "                                        ";

		while (spaces > 0) {
			int n = Math.min(spaces, block.length());

			buffer.append(block, 0, n);

			spaces -= n;
		}
	}

	private void _transferCompleted(TransferEvent event) {
		_downloads.remove(event.getResource());

		StringBuilder buffer = new StringBuilder(64);

		_pad(buffer, _lastLength);

		buffer.append('\r');

		_out.print(buffer);
	}

	private Map<TransferResource, Long> _downloads = new ConcurrentHashMap<>();
	private int _lastLength;
	private PrintStream _out;

}