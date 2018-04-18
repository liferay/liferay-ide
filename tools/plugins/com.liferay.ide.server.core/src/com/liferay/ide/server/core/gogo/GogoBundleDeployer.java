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

package com.liferay.ide.server.core.gogo;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.portal.BundleDTOWithStatus;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Terry Jia
 */
public class GogoBundleDeployer {

	private static int _getState(String state) {
		if ("Active".equals(state)) {
			return Bundle.ACTIVE;
		}
		else if ("Starting".equals(state)) {
			return Bundle.STARTING;
		}
		else if ("Resolved".equals(state)) {
			return Bundle.RESOLVED;
		}
		else if ("Stopping".equals(state)) {
			return Bundle.STOPPING;
		}
		else if ("Installed".equals(state)) {
			return Bundle.INSTALLED;
		}
		else if ("Uninstalled".equals(state)) {
			return Bundle.UNINSTALLED;
		}

		return -1;
	}

	/**
	 * Return the string array with trim
	 */
	private static String[] _split(String string, String regex) {
		String[] lines = string.split(regex);

		String[] newLines = new String[lines.length];

		for (int i = 0; i < lines.length; i++) {
			newLines[i] = lines[i].trim();
		}

		return newLines;
	}

	/**
	 * The content should be the result from GogoShell by "lb -s"
	 * The format should be:
	 * START LEVEL 20\r\n
	 *    ID|State      |Level|Symbolic name\r\n
	 *     0|Active     |    0|org.eclipse.osgi (3.10.200.v20150831-0856)\r\n
	 *     1|Active     |    6|com.liferay.portal.startup.monitor (1.0.2)\r\n
	 *     ......
	 *   146|Active     |   10|com.liferay.asset.tags.service (2.0.2)
	 * We will get id, state, symbolicName, version for bundles by parsing:
	 * 146|Active     |   10|com.liferay.asset.tags.service (2.0.2)
	 */
	private static BundleDTO[] _parseBundleInfos(String content) {
		String[] lines = _split(content, "\r\n");

		if (lines.length < 3) {
			return new BundleDTO[0];
		}

		String[] newLines = new String[lines.length - 2];

		System.arraycopy(lines, 2, newLines, 0, newLines.length);

		BundleDTO[] bundles = new BundleDTO[newLines.length];

		for( int i = 0;i < bundles.length; i++) {
			BundleDTO bundle = new BundleDTO();

			String line = newLines[i];

			String[] infos = _split(line, "\\|");

			bundle.id = Long.parseLong(infos[0]);
			bundle.state = _getState(infos[1]);
			bundle.symbolicName = infos[3].substring(0, infos[3].indexOf("(")).trim();
			bundle.version = infos[3].substring(infos[3].indexOf("(") + 1, infos[3].indexOf(")")).trim();

			bundles[i] = bundle;
		}

		return bundles;
	}

	private String _host;

	private int _port;

	public GogoBundleDeployer() {
		_host = "localhost";
		_port = 11311;
	}

	public GogoBundleDeployer(String host, int port) {
		_host = host;
		_port = port;
	}

	public long getBundleId(String bsn) throws IOException {
		String result = run("lb -s " + bsn, true);

		if ("No matching bundles found".equals(result)) {
			return -1;
		}

		BundleDTO[] bundles = _parseBundleInfos(result);

		for (BundleDTO bundle : bundles) {
			if (bundle.symbolicName.equals(bsn)) {
				return bundle.id;
			}
		}

		return -1;
	}

	public BundleDTO install(File bundle) throws IOException {
		URL url = bundle.toURI().toURL();

		return install(url.toExternalForm());
	}

	public BundleDTO install(String url) throws IOException {
		String result = run("install " + url, true);

		String[] lines = _split(result, "\r\n");

		BundleDTO bundle = new BundleDTO();

		bundle.id = -1;

		for (String line : lines) {
			if (line.startsWith("Bundle ID")) {
				bundle.id = Long.parseLong(line.split(":")[1].trim());

				break;
			}
		}

		return bundle;
	}

	public BundleDTO[] listBundles() throws IOException {
		String result = run("lb -s", true);

		BundleDTO[] bundles = _parseBundleInfos(result);

		return bundles;
	}

	public String refresh(long id) throws IOException {
		return run("refresh " + id);
	}

	public String refresh(String bsn) throws IOException {
		return run("refresh " + bsn);
	}

	public String run(String cmd) throws IOException {
		return run(cmd, false);
	}

	public String run(String cmd, boolean successResult) throws IOException {
		GogoTelnetClient client = new GogoTelnetClient(_host, _port);

		String result = client.send(cmd);

		client.close();

		String retval = result;

		if (successResult) {
			if (result.startsWith(cmd)) {
				retval = result.substring(result.indexOf(cmd) + cmd.length()).trim();

				if ("".equals(retval)) {
					retval = null;
				}
			}
		}
		else {
			if (cmd.equals(result)) {
				retval = null;
			}
		}

		return retval;
	}

	public String start(long id) throws IOException {
		return run("start " + id, true);
	}

	public String stop(long id) throws IOException {
		return run("stop " + id);
	}

	public String uninstall(long id) throws IOException {
		return run("uninstall " + id);
	}

	public String uninstall(String bsn) throws IOException {
		return run("uninstall " + bsn);
	}

	public String update(long id, File bundle) throws IOException {
		URL url = bundle.toURI().toURL();

		return update(id, url.toExternalForm());
	}

	public String update(long id, String url) throws IOException {
		return run("update " + id + " " + url, true);
	}

	public BundleDTO deploy( final String bsn, final File bundleFile, final String bundleUrl ) throws Exception
    {
        BundleDTO retval = null;

        boolean isFragment = false;
        String fragmentHostName = null;

        if( !bundleUrl.contains( "webbundle:" ) )
        {
            fragmentHostName = ServerUtil.getFragemtHostName( bundleFile );

            isFragment = ( fragmentHostName != null );
        }

        long bundleId = getBundleId( bsn );

        if( bundleId > 0 )
        {
            if( !isFragment )
            {
                stop( bundleId );
            }

            if( bundleUrl.contains( "webbundle:" ) )
            {
                update( bundleId, bundleUrl );
            }
            else
            {
                update( bundleId, bundleFile );

                if( isFragment )
                {
                    refresh( bundleId );
                }
            }

            if( !isFragment )
            {
                String startStatus = start( bundleId );

                if( startStatus != null )
                {
                    retval = new BundleDTO();

                    retval.id = bundleId;

                    retval = new BundleDTOWithStatus( retval, startStatus );
                }
            }

            if( retval == null )
            {
                retval = new BundleDTO();

                retval.id = bundleId;
            }
        }
        else
        {
            if( bundleUrl.contains( "webbundle:" ) )
            {
                retval = install( bundleUrl );
            }
            else
            {
                retval = install( bundleFile );
            }

            if( !isFragment )
            {
                String startStatus = start( retval.id );

                if( startStatus != null )
                {
                    retval = new BundleDTOWithStatus( retval, startStatus );
                }
            }
            else
            {
                refresh( fragmentHostName );
            }
        }

        return retval;
    }

    public String uninstall( IBundleProject bundleProject ) throws Exception
    {
        String retVal = null;

        boolean isFragment = bundleProject.isFragmentBundle();

        final String symbolicName = bundleProject.getSymbolicName();

        if( symbolicName != null )
        {
            long bundleId = getBundleId( symbolicName );

            if( bundleId > 0 )
            {
                retVal = uninstall( bundleId );

                if( isFragment )
                {
                    IProject _bundleProject = bundleProject.getProject();

                    String fragmentName = ServerUtil.getBundleFragmentHostNameFromBND(_bundleProject);

                    if ( !CoreUtil.isNullOrEmpty(fragmentName))
                    {
                        refresh( fragmentName );
                    }
                }
            }
        }

        return retVal;
    }
}
