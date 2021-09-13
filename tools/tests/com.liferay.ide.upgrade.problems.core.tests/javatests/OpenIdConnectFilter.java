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

package com.liferay.portal.security.sso.openid.connect.internal.service.filter;

import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectFlowState;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceException;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceHandler;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectConstants;
import com.liferay.portal.security.sso.openid.connect.constants.OpenIdConnectWebKeys;
import com.liferay.portal.security.sso.openid.connect.internal.exception.StrangersNotAllowedException;
import com.liferay.portal.security.sso.openid.connect.provider.OpenIdConnectSessionProvider;

import java.net.URI;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Edward C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.openid.connect.configuration.OpenIdConnectConfiguration",
	immediate = true,
	property = {
		"before-filter=Auto Login Filter", "servlet-context-name=",
		"servlet-filter-name=SSO OpenId Connect Filter",
		"url-pattern=" + OpenIdConnectConstants.REDIRECT_URL_PATTERN
	},
	service = {Filter.class, OpenIdConnectFilter.class}
)
public class OpenIdConnectFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		return _openIdConnect.isEnabled(
			_portal.getCompanyId(httpServletRequest));
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	protected void processAuthenticationResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession(false);

		if (httpSession == null) {
			return;
		}

		try {
			try {
				if (!_openIdConnectServiceHandler.hasValidOpenIdConnectSession(
						httpSession)) {

					endSession = true;
				}
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to validate OpenId Connect session: " +
						portalException.getMessage(),
					portalException);

				endSession = true;
			}
			
			OpenIdConnectSession openIdConnectSession =
				_openIdConnectSessionProvider.getOpenIdConnectSession(
					httpSession);

			if (openIdConnectSession == null) {
				return;
			}

			OpenIdConnectFlowState openIdConnectFlowState =
				openIdConnectSession.getOpenIdConnectFlowState();

			if (OpenIdConnectFlowState.INITIALIZED.equals(
					openIdConnectFlowState)) {

				throw new OpenIdConnectServiceException.AuthenticationException(
					"OpenId Connect authentication flow not started");
			}
			else if (OpenIdConnectFlowState.AUTH_COMPLETE.equals(
						openIdConnectFlowState) ||
					 OpenIdConnectFlowState.PORTAL_AUTH_COMPLETE.equals(
						 openIdConnectFlowState)) {

				if (_log.isDebugEnabled()) {
					_log.debug("User has already been logged in");
				}
			}
			else {
				_openIdConnectServiceHandler.processAuthenticationResponse(
					httpServletRequest, httpServletResponse);

				String actionURL = (String)httpSession.getAttribute(
					OpenIdConnectWebKeys.OPEN_ID_CONNECT_ACTION_URL);

				if (actionURL != null) {
					httpServletResponse.sendRedirect(actionURL);
				}
			}
		}
		catch (StrangersNotAllowedException |
			   UserEmailAddressException.MustNotUseCompanyMx exception) {

			Class<?> clazz = exception.getClass();

			httpSession.removeAttribute(
				OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION);

			sendError(
				clazz.getSimpleName(), httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to process OpenID Connect authentication response: " +
					exception.getMessage(),
				exception);

			httpSession.removeAttribute(
				OpenIdConnectWebKeys.OPEN_ID_CONNECT_SESSION);

			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		processAuthenticationResponse(httpServletRequest, httpServletResponse);

		processFilter(
			OpenIdConnectFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	protected void sendError(
			String error, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		HttpSession session = httpServletRequest.getSession(false);

		if (session == null) {
			return;
		}

		String actionURL = (String)session.getAttribute(
			OpenIdConnectWebKeys.OPEN_ID_CONNECT_ACTION_URL);

		if (actionURL == null) {
			return;
		}

		actionURL = _http.addParameter(actionURL, "error", error);

		httpServletResponse.sendRedirect(actionURL);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OpenIdConnectFilter.class);

	@Reference
	private Http _http;

	@Reference
	private OpenIdConnect _openIdConnect;

	@Reference
	private OpenIdConnectServiceHandler _openIdConnectServiceHandler;

	@Reference
	private OpenIdConnectSessionProvider _openIdConnectSessionProvider;

	@Reference
	private Portal _portal;

	public class OpenIdConnectServiceHandlerTest implements OpenIdConnectServiceHandler {

		@Override
		public boolean hasValidOpenIdConnectSession(HttpSession httpSession)
			throws OpenIdConnectServiceException.NoOpenIdConnectSessionException {
	
			OpenIdConnectSessionImpl openIdConnectSessionImpl =
				getOpenIdConnectSessionImpl(httpSession);
	
			if (!hasValidAccessToken(openIdConnectSessionImpl)) {
				try {
					return refreshAuthToken(openIdConnectSessionImpl);
				}
				catch (OpenIdConnectServiceException
							openIdConnectServiceException) {
	
					_log.error(
						"Unable to refresh auth token: " +
							openIdConnectServiceException.getMessage(),
						openIdConnectServiceException);
	
					return false;
				}
			}
	
			return true;
		}
	
		@Override
		public void processAuthenticationResponse(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
			throws PortalException {
	
			AuthenticationSuccessResponse authenticationSuccessResponse =
				getAuthenticationSuccessResponse(httpServletRequest);
	
			HttpSession httpSession = httpServletRequest.getSession();
	
			OpenIdConnectSessionImpl openIdConnectSessionImpl =
				getOpenIdConnectSessionImpl(httpSession);
	
			if (!OpenIdConnectFlowState.AUTH_REQUESTED.equals(
					openIdConnectSessionImpl.getOpenIdConnectFlowState())) {
	
				throw new OpenIdConnectServiceException.AuthenticationException(
					StringBundler.concat(
						"OpenId Connect login flow is not in the ",
						OpenIdConnectFlowState.AUTH_REQUESTED, " state: ",
						openIdConnectSessionImpl.getOpenIdConnectFlowState()));
			}
	
			validateState(
				openIdConnectSessionImpl.getState(),
				authenticationSuccessResponse.getState());
	
			OpenIdConnectProvider<OIDCClientMetadata, OIDCProviderMetadata>
				openIdConnectProvider =
					_openIdConnectProviderRegistry.findOpenIdConnectProvider(
						_portal.getCompanyId(httpServletRequest),
						openIdConnectSessionImpl.getOpenIdProviderName());
	
			OIDCProviderMetadata oidcProviderMetadata =
				openIdConnectProvider.getOIDCProviderMetadata();
	
			OIDCClientInformation oidcClientInformation = getOIDCClientInformation(
				openIdConnectProvider);
	
			URI redirectURI = getLoginRedirectURI(httpServletRequest);
	
			Tokens tokens = requestIdToken(
				authenticationSuccessResponse, oidcClientInformation,
				oidcProviderMetadata, redirectURI,
				openIdConnectSessionImpl.getNonce(),
				openIdConnectProvider.geTokenConnectionTimeout());
	
			updateSessionTokens(
				openIdConnectSessionImpl, tokens, System.currentTimeMillis());
	
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);
	
			processUserInfo(
				_portal.getCompanyId(httpServletRequest), openIdConnectSessionImpl,
				oidcProviderMetadata, serviceContext.getPathMain(),
				serviceContext.getPortalURL());
	
			openIdConnectSessionImpl.setOpenIdConnectFlowState(
				OpenIdConnectFlowState.AUTH_COMPLETE);
	
			_openIdConnectSessionProviderImpl.setOpenIdConnectSession(
				httpSession, openIdConnectSessionImpl);
		}
	}
	
}