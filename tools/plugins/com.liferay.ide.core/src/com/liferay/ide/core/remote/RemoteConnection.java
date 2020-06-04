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

package com.liferay.ide.core.remote;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 * @author Terry Jia
 */
public class RemoteConnection implements IRemoteConnection {

	public String getHost() {
		return _hostname;
	}

	public int getHttpPort() {
		return _httpPort;
	}

	public String getPassword() {
		return _password;
	}

	public String getUsername() {
		return _username;
	}

	public void releaseHttpClient() {
		if (_httpClient == null) {
			return;
		}

		ClientConnectionManager manager = _httpClient.getConnectionManager();

		manager.shutdown();

		_httpClient = null;
	}

	public void setHost(String host) {
		_hostname = host;

		releaseHttpClient();
	}

	public void setHttpPort(String httpPort) {
		if (httpPort != null) {
			try {
				_httpPort = Integer.parseInt(httpPort);
			}
			catch (NumberFormatException nfe) {
				_httpPort = -1;
			}
		}
		else {
			_httpPort = -1;
		}

		releaseHttpClient();
	}

	public void setPassword(String password) {
		_password = password;

		releaseHttpClient();
	}

	public void setUsername(String username) {
		_username = username;

		releaseHttpClient();
	}

	protected Object deleteJSONAPI(Object... args) throws APIException {
		if (!(args[0] instanceof String)) {
			throw new IllegalArgumentException("First argument must be a string");
		}

		HttpDelete deleteAPIMethod = new HttpDelete();

		return httpJSONAPI(deleteAPIMethod, args);
	}

	protected String getHttpResponse(HttpUriRequest request) throws Exception {
		if (!CoreUtil.isNullOrEmpty(getUsername()) && !CoreUtil.isNullOrEmpty(getPassword())) {
			String encoding = getUsername() + ":" + getPassword();

			request.setHeader("Authorization", "Basic " + Base64.encodeBase64String(encoding.getBytes()));
		}

		HttpResponse response = _getHttpClient().execute(request);

		StatusLine statusLine = response.getStatusLine();

		int statusCode = statusLine.getStatusCode();

		if (statusCode == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();

			String body = CoreUtil.readStreamToString(entity.getContent(), false);

			EntityUtils.consume(entity);

			return body;
		}
		else {
			return statusLine.getReasonPhrase();
		}
	}

	protected Object getJSONAPI(Object... args) throws APIException {
		if (!(args[0] instanceof String)) {
			throw new IllegalArgumentException("First argument must be a string");
		}

		HttpGet getAPIMethod = new HttpGet();

		return httpJSONAPI(getAPIMethod, args);
	}

	protected Object httpJSONAPI(Object... args) throws APIException {
		if (!(args[0] instanceof HttpRequestBase)) {
			throw new IllegalArgumentException("First argument must be a HttpRequestBase");
		}

		Object retval = null;

		String api = null;

		Object[] params = new Object[0];

		HttpRequestBase request = (HttpRequestBase)args[0];

		boolean isPostRequest = request instanceof HttpPost;

		if (args[1] instanceof String) {
			api = args[1].toString();
		}
		else if (args[1] instanceof Object[]) {
			params = (Object[])args[1];

			api = params[0].toString();
		}
		else {
			throw new IllegalArgumentException("2nd argument must be either String or Object[]");
		}

		try {
			URIBuilder builder = new URIBuilder();

			builder.setScheme("http");

			builder.setHost(getHost());

			builder.setPort(getHttpPort());

			builder.setPath(api);

			List<NameValuePair> postParams = new ArrayList<>();

			if (params.length >= 3) {
				for (int i = 1; i < params.length; i += 2) {
					String name = null;

					String value = StringPool.EMPTY;

					if (params[i] != null) {
						name = params[i].toString();
					}

					if (params[i + 1] != null) {
						value = params[i + 1].toString();
					}

					if (isPostRequest) {
						postParams.add(new BasicNameValuePair(name, value));
					}
					else {
						builder.setParameter(name, value);
					}
				}
			}

			if (isPostRequest) {
				HttpPost postRequest = (HttpPost)request;

				if (postRequest.getEntity() == null) {
					postRequest.setEntity(new UrlEncodedFormEntity(postParams));
				}
			}

			request.setURI(builder.build());

			String response = getHttpResponse(request);

			if ((response != null) && (response.length() > 0)) {
				Object jsonResponse = _getJSONResponse(response);

				if (jsonResponse == null) {
					throw new APIException(api, "Unable to get response: " + response);
				}
				else {
					retval = jsonResponse;
				}
			}
		}
		catch (APIException apie) {
			throw apie;
		}
		catch (Exception e) {
			throw new APIException(api, e);
		}
		finally {
			try {
				request.releaseConnection();
			}
			finally {

				// no need to log error

			}
		}

		return retval;
	}

	protected Object postJSONAPI(Object... args) throws APIException {
		if (!(args[0] instanceof String)) {
			throw new IllegalArgumentException("First argument must be a string");
		}

		HttpPost post = new HttpPost();

		return httpJSONAPI(post, args);
	}

	private HttpClient _getHttpClient() {
		if (_httpClient != null) {
			return _httpClient;
		}

		DefaultHttpClient newDefaultHttpClient = null;

		if ((getUsername() != null) || (getPassword() != null)) {
			try {
				IProxyService proxyService = LiferayCore.getProxyService();

				URI uri = new URI("http://" + getHost() + ":" + getHttpPort());

				IProxyData[] proxyDataForHost = proxyService.select(uri);

				for (IProxyData data : proxyDataForHost) {
					if ((data.getHost() == null) || (data.getPort() == 0)) {
						continue;
					}

					SchemeRegistry schemeRegistry = new SchemeRegistry();

					schemeRegistry.register(new Scheme("http", data.getPort(), PlainSocketFactory.getSocketFactory()));

					PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);

					cm.setMaxTotal(200);

					cm.setDefaultMaxPerRoute(20);

					DefaultHttpClient newHttpClient = new DefaultHttpClient(cm);

					HttpHost proxy = new HttpHost(data.getHost(), data.getPort());

					HttpParams params = newHttpClient.getParams();

					params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

					newDefaultHttpClient = newHttpClient;

					break;
				}

				if (newDefaultHttpClient == null) {
					uri = new URI("SOCKS://" + getHost() + ":" + getHttpPort());

					proxyDataForHost = proxyService.select(uri);

					for (IProxyData data : proxyDataForHost) {
						if (data.getHost() == null) {
							continue;
						}

						DefaultHttpClient newHttpClient = new DefaultHttpClient();

						HttpParams params = newHttpClient.getParams();

						params.setParameter("socks.host", data.getHost());
						params.setParameter("socks.port", data.getPort());

						ClientConnectionManager manager = newHttpClient.getConnectionManager();

						SchemeRegistry registry = manager.getSchemeRegistry();

						Scheme scheme = new Scheme("socks", data.getPort(), PlainSocketFactory.getSocketFactory());

						registry.register(scheme);

						newDefaultHttpClient = newHttpClient;

						break;
					}
				}
			}
			catch (URISyntaxException urise) {
				LiferayCore.logError("Unable to read proxy data", urise);
			}

			if (newDefaultHttpClient == null) {
				newDefaultHttpClient = new DefaultHttpClient();
			}

			_httpClient = newDefaultHttpClient;
		}
		else {
			_httpClient = new DefaultHttpClient();
		}

		return _httpClient;
	}

	private Object _getJSONResponse(String response) {
		Object retval = null;

		try {
			retval = new JSONObject(response);
		}
		catch (JSONException e) {
			try {
				retval = new JSONArray(response);
			}
			catch (JSONException e1) {
			}
		}

		return retval;
	}

	private String _hostname;
	private HttpClient _httpClient;
	private int _httpPort;
	private String _password;
	private String _username;

}