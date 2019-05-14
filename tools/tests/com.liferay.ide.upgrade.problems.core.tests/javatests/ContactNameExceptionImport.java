/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.service.impl;

import com.liferay.portal.CompanyMaxUsersException;
import com.liferay.portal.ContactBirthdayException;
import com.liferay.portal.ContactFirstNameException;
import com.liferay.portal.ContactFullNameException;
import com.liferay.portal.ContactLastNameException;
import com.liferay.portal.DuplicateOpenIdException;
import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.DuplicateUserScreenNameException;
import com.liferay.portal.GroupFriendlyURLException;
import com.liferay.portal.ModelListenerException;
import com.liferay.portal.NoSuchImageException;
import com.liferay.portal.NoSuchOrganizationException;
import com.liferay.portal.NoSuchRoleException;
import com.liferay.portal.NoSuchTicketException;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.NoSuchUserGroupException;
import com.liferay.portal.PasswordExpiredException;
import com.liferay.portal.RequiredUserException;
import com.liferay.portal.ReservedUserEmailAddressException;
import com.liferay.portal.ReservedUserScreenNameException;
import com.liferay.portal.UserEmailAddressException;
import com.liferay.portal.UserIdException;
import com.liferay.portal.UserLockoutException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.UserPortraitSizeException;
import com.liferay.portal.UserPortraitTypeException;
import com.liferay.portal.UserReminderQueryException;
import com.liferay.portal.UserScreenNameException;
import com.liferay.portal.UserSmsException;
import com.liferay.portal.kernel.dao.shard.ShardCallable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.image.ImageToolUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.spring.aop.Skip;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackRegistryUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.model.Account;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.ContactConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.Ticket;
import com.liferay.portal.model.TicketConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.security.auth.AuthPipeline;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.security.auth.EmailAddressGenerator;
import com.liferay.portal.security.auth.EmailAddressGeneratorFactory;
import com.liferay.portal.security.auth.EmailAddressValidator;
import com.liferay.portal.security.auth.EmailAddressValidatorFactory;
import com.liferay.portal.security.auth.FullNameGenerator;
import com.liferay.portal.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.security.auth.FullNameValidator;
import com.liferay.portal.security.auth.FullNameValidatorFactory;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.auth.ScreenNameGenerator;
import com.liferay.portal.security.auth.ScreenNameGeneratorFactory;
import com.liferay.portal.security.auth.ScreenNameValidator;
import com.liferay.portal.security.auth.ScreenNameValidatorFactory;
import com.liferay.portal.security.ldap.LDAPSettingsUtil;
import com.liferay.portal.security.permission.PermissionCacheUtil;
import com.liferay.portal.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.security.pwd.PwdAuthenticator;
import com.liferay.portal.security.pwd.PwdToolkitUtil;
import com.liferay.portal.security.pwd.RegExpToolkit;
import com.liferay.portal.service.BaseServiceImpl;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.base.UserLocalServiceBaseImpl;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.SubscriptionSender;
import com.liferay.portlet.documentlibrary.ImageSizeException;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.util.Encryptor;
import com.liferay.util.EncryptorException;
import com.liferay.util.PwdGenerator;

import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the local service for accessing, adding, authenticating, deleting,
 * and updating users.
 *
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 * @author Raymond Augé
 * @author Jorge Ferrer
 * @author Julio Camarero
 * @author Wesley Gong
 * @author Zsigmond Rab
 */
public class UserLocalServiceImpl extends UserLocalServiceBaseImpl {

	/**
	 * Adds a default admin user for the company.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @return the new default admin user
	 * @throws PortalException n if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User addDefaultAdminUser(
			long companyId, String screenName, String emailAddress,
			Locale locale, String firstName, String middleName, String lastName)
		throws PortalException, SystemException {

		long creatorUserId = 0;
		boolean autoPassword = false;
		String password1 = PropsValues.DEFAULT_ADMIN_PASSWORD;
		String password2 = password1;
		boolean autoScreenName = false;

		screenName = getLogin(screenName);

		for (int i = 1;; i++) {
			User screenNameUser = userPersistence.fetchByC_SN(
				companyId, screenName);

			if (screenNameUser == null) {
				break;
			}

			screenName = screenName + i;
		}

		long facebookId = 0;
		String openId = StringPool.BLANK;
		int prefixId = 0;
		int suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;

		Group guestGroup = groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		long[] groupIds = {guestGroup.getGroupId()};

		long[] organizationIds = null;

		Role adminRole = roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		Role powerUserRole = roleLocalService.getRole(
			companyId, RoleConstants.POWER_USER);

		long[] roleIds = {adminRole.getRoleId(), powerUserRole.getRoleId()};

		long[] userGroupIds = null;
		boolean sendEmail = false;
		ServiceContext serviceContext = new ServiceContext();

		User defaultAdminUser = addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, facebookId, openId,
			locale, firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);

		updateEmailAddressVerified(defaultAdminUser.getUserId(), true);

		updateLastLogin(
			defaultAdminUser.getUserId(), defaultAdminUser.getLoginIP());

		updatePasswordReset(defaultAdminUser.getUserId(), false);

		return defaultAdminUser;
	}

	/**
	 * Adds the user to the default groups, unless the user is already in these
	 * groups. The default groups can be specified in
	 * <code>portal.properties</code> with the key
	 * <code>admin.default.group.names</code>.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addDefaultGroups(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		Set<Long> groupIdsSet = new HashSet<Long>();

		String[] defaultGroupNames = PrefsPropsUtil.getStringArray(
			user.getCompanyId(), PropsKeys.ADMIN_DEFAULT_GROUP_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_DEFAULT_GROUP_NAMES);

		for (String defaultGroupName : defaultGroupNames) {
			Company company = companyPersistence.findByPrimaryKey(
				user.getCompanyId());

			Account account = company.getAccount();

			if (StringUtil.equalsIgnoreCase(
					defaultGroupName, account.getName())) {

				defaultGroupName = GroupConstants.GUEST;
			}

			Group group = groupPersistence.fetchByC_N(
				user.getCompanyId(), defaultGroupName);

			if ((group != null) &&
				!userPersistence.containsGroup(
					userId, group.getGroupId())) {

				groupIdsSet.add(group.getGroupId());
			}
		}

		String[] defaultOrganizationGroupNames = PrefsPropsUtil.getStringArray(
			user.getCompanyId(),
			PropsKeys.ADMIN_DEFAULT_ORGANIZATION_GROUP_NAMES,
			StringPool.NEW_LINE,
			PropsValues.ADMIN_DEFAULT_ORGANIZATION_GROUP_NAMES);

		for (String defaultOrganizationGroupName :
				defaultOrganizationGroupNames) {

			defaultOrganizationGroupName +=
				GroupLocalServiceImpl.ORGANIZATION_NAME_SUFFIX;

			Group group = groupPersistence.fetchByC_N(
				user.getCompanyId(), defaultOrganizationGroupName);

			if ((group != null) &&
				!userPersistence.containsGroup(
					userId, group.getGroupId())) {

				groupIdsSet.add(group.getGroupId());
			}
		}

		long[] groupIds = ArrayUtil.toArray(
			groupIdsSet.toArray(new Long[groupIdsSet.size()]));

		groupLocalService.addUserGroups(userId, groupIds);
	}

	/**
	 * Adds the user to the default roles, unless the user already has these
	 * roles. The default roles can be specified in
	 * <code>portal.properties</code> with the key
	 * <code>admin.default.role.names</code>.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addDefaultRoles(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		Set<Long> roleIdSet = new HashSet<Long>();

		String[] defaultRoleNames = PrefsPropsUtil.getStringArray(
			user.getCompanyId(), PropsKeys.ADMIN_DEFAULT_ROLE_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_DEFAULT_ROLE_NAMES);

		for (String defaultRoleName : defaultRoleNames) {
			try {
				Role role = rolePersistence.findByC_N(
					user.getCompanyId(), defaultRoleName);

				if (!userPersistence.containsRole(userId, role.getRoleId())) {
					roleIdSet.add(role.getRoleId());
				}
			}
			catch (NoSuchRoleException nsre) {
			}
		}

		long[] roleIds = ArrayUtil.toArray(
			roleIdSet.toArray(new Long[roleIdSet.size()]));

		roleIds = UsersAdminUtil.addRequiredRoles(user, roleIds);

		userPersistence.addRoles(userId, roleIds);
	}

	/**
	 * Adds the user to the default user groups, unless the user is already in
	 * these user groups. The default user groups can be specified in
	 * <code>portal.properties</code> with the property
	 * <code>admin.default.user.group.names</code>.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void addDefaultUserGroups(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		Set<Long> userGroupIdSet = new HashSet<Long>();

		String[] defaultUserGroupNames = PrefsPropsUtil.getStringArray(
			user.getCompanyId(), PropsKeys.ADMIN_DEFAULT_USER_GROUP_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_DEFAULT_USER_GROUP_NAMES);

		for (String defaultUserGroupName : defaultUserGroupNames) {
			try {
				UserGroup userGroup = userGroupPersistence.findByC_N(
					user.getCompanyId(), defaultUserGroupName);

				if (!userPersistence.containsUserGroup(
						userId, userGroup.getUserGroupId())) {

					userGroupIdSet.add(userGroup.getUserGroupId());
				}
			}
			catch (NoSuchUserGroupException nsuge) {
			}
		}

		long[] userGroupIds = ArrayUtil.toArray(
			userGroupIdSet.toArray(new Long[userGroupIdSet.size()]));

		if (PropsValues.USER_GROUPS_COPY_LAYOUTS_TO_USER_PERSONAL_SITE) {
			for (long userGroupId : userGroupIds) {
				userGroupLocalService.copyUserGroupLayouts(userGroupId, userId);
			}
		}

		userPersistence.addUserGroups(userId, userGroupIds);
	}

	/**
	 * Adds the users to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a group or user with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addGroupUsers(long groupId, long[] userIds)
		throws PortalException, SystemException {

		groupPersistence.addUsers(groupId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();

		addDefaultRolesAndTeams(groupId, userIds);
	}

	/**
	 * Adds the users to the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if an organization or user with the primary key
	 *         could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException, SystemException {

		organizationPersistence.addUsers(organizationId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Assigns the password policy to the users, removing any other currently
	 * assigned password policies.
	 *
	 * @param  passwordPolicyId the primary key of the password policy
	 * @param  userIds the primary keys of the users
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws SystemException {

		passwordPolicyRelLocalService.addPasswordPolicyRels(
			passwordPolicyId, User.class.getName(), userIds);
	}

	/**
	 * Adds the users to the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a role or user with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addRoleUsers(long roleId, long[] userIds)
		throws PortalException, SystemException {

		rolePersistence.addUsers(roleId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Adds the users to the team.
	 *
	 * @param  teamId the primary key of the team
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a team or user with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void addTeamUsers(long teamId, long[] userIds)
		throws PortalException, SystemException {

		teamPersistence.addUsers(teamId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Adds a user.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  creatorUserId the primary key of the creator
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  facebookId the user's facebook ID
	 * @param  openId the user's OpenID
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixId the user's name prefix ID
	 * @param  suffixId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 * @throws PortalException if the user's information was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User addUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName,
			String lastName, int prefixId, int suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			if (serviceContext == null) {
				serviceContext = new ServiceContext();
			}

			if (serviceContext.getWorkflowAction() !=
					WorkflowConstants.ACTION_PUBLISH) {

				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}

			return addUserWithWorkflow(
				creatorUserId, companyId, autoPassword, password1, password2,
				autoScreenName, screenName, emailAddress, facebookId, openId,
				locale, firstName, middleName, lastName, prefixId, suffixId,
				male, birthdayMonth, birthdayDay, birthdayYear, jobTitle,
				groupIds, organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Adds the users to the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a user group or user with the primary could
	 *         could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void addUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException, SystemException {

		if (PropsValues.USER_GROUPS_COPY_LAYOUTS_TO_USER_PERSONAL_SITE) {
			userGroupLocalService.copyUserGroupLayouts(userGroupId, userIds);
		}

		userGroupPersistence.addUsers(userGroupId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Adds a user with workflow.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  creatorUserId the primary key of the creator
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  facebookId the user's facebook ID
	 * @param  openId the user's OpenID
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixId the user's name prefix ID
	 * @param  suffixId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 * @throws PortalException if the user's information was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@SuppressWarnings("deprecation")
	public User addUserWithWorkflow(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName,
			String lastName, int prefixId, int suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		// User

		Company company = companyPersistence.findByPrimaryKey(companyId);
		screenName = getLogin(screenName);
		openId = StringUtil.trim(openId);
		Date now = new Date();

		if (PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

			autoScreenName = true;
		}

		// PLACEHOLDER 01

		long userId = counterLocalService.increment();

		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		if ((emailAddress == null) ||
			emailAddressGenerator.isGenerated(emailAddress)) {

			emailAddress = StringPool.BLANK;
		}
		else {
			emailAddress = StringUtil.toLowerCase(emailAddress.trim());
		}

		if (!PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) &&
			Validator.isNull(emailAddress)) {

			emailAddress = emailAddressGenerator.generate(companyId, userId);
		}

		validate(
			companyId, userId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, openId, firstName,
			middleName, lastName, organizationIds);

		if (!autoPassword) {
			if (Validator.isNull(password1) || Validator.isNull(password2)) {
				throw new UserPasswordException(
					UserPasswordException.PASSWORD_INVALID);
			}
		}

		if (autoScreenName) {
			ScreenNameGenerator screenNameGenerator =
				ScreenNameGeneratorFactory.getInstance();

			try {
				screenName = screenNameGenerator.generate(
					companyId, userId, emailAddress);
			}
			catch (Exception e) {
				throw new SystemException(e);
			}
		}

		User defaultUser = getDefaultUser(companyId);

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		String fullName = fullNameGenerator.getFullName(
			firstName, middleName, lastName);

		String greeting = LanguageUtil.format(
			locale, "welcome-x", " " + fullName, false);

		User user = userPersistence.create(userId);

		if (serviceContext != null) {
			String uuid = serviceContext.getUuid();

			if (Validator.isNotNull(uuid)) {
				user.setUuid(uuid);
			}
		}

		user.setCompanyId(companyId);
		user.setCreateDate(now);
		user.setModifiedDate(now);
		user.setDefaultUser(false);
		user.setContactId(counterLocalService.increment());

		if (Validator.isNotNull(password1)) {
			user.setPassword(PasswordEncryptorUtil.encrypt(password1));
			user.setPasswordUnencrypted(password1);
		}

		user.setPasswordEncrypted(true);

		PasswordPolicy passwordPolicy = defaultUser.getPasswordPolicy();

		boolean passwordReset = false;

		if (passwordPolicy != null) {
			if (passwordPolicy.isChangeable() &&
				passwordPolicy.isChangeRequired()) {

				passwordReset = true;
			}

			addPasswordPolicyUsers(
				passwordPolicy.getPasswordPolicyId(), new long[] {userId});
		}

		user.setPasswordReset(passwordReset);

		user.setDigest(StringPool.BLANK);
		user.setScreenName(screenName);
		user.setEmailAddress(emailAddress);
		user.setFacebookId(facebookId);

		Long ldapServerId = (Long)serviceContext.getAttribute("ldapServerId");

		if (ldapServerId != null) {
			user.setLdapServerId(ldapServerId);
		}
		else {
			user.setLdapServerId(-1);
		}

		user.setOpenId(openId);
		user.setLanguageId(LocaleUtil.toLanguageId(locale));
		user.setTimeZoneId(defaultUser.getTimeZoneId());
		user.setGreeting(greeting);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(jobTitle);
		user.setStatus(WorkflowConstants.STATUS_DRAFT);
		user.setExpandoBridgeAttributes(serviceContext);

		userPersistence.update(user, serviceContext);

		// Contact

		String creatorUserName = StringPool.BLANK;

		if (creatorUserId <= 0) {
			creatorUserId = user.getUserId();

			// Don't grab the full name from the User object because it doesn't
			// have a corresponding Contact object yet

			//creatorUserName = user.getFullName();
		}
		else {
			User creatorUser = userPersistence.findByPrimaryKey(creatorUserId);

			creatorUserName = creatorUser.getFullName();
		}

		Date birthday = getBirthday(birthdayMonth, birthdayDay, birthdayYear);

		Contact contact = contactPersistence.create(user.getContactId());

		contact.setCompanyId(user.getCompanyId());
		contact.setUserId(creatorUserId);
		contact.setUserName(creatorUserName);
		contact.setCreateDate(now);
		contact.setModifiedDate(now);
		contact.setClassName(User.class.getName());
		contact.setClassPK(user.getUserId());
		contact.setAccountId(company.getAccountId());
		contact.setParentContactId(ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(firstName);
		contact.setMiddleName(middleName);
		contact.setLastName(lastName);
		contact.setPrefixId(prefixId);
		contact.setSuffixId(suffixId);
		contact.setMale(male);
		contact.setBirthday(birthday);
		contact.setJobTitle(jobTitle);

		contactPersistence.update(contact, serviceContext);

		// Group

		groupLocalService.addGroup(
			user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			User.class.getName(), user.getUserId(), null, null, 0,
			StringPool.SLASH + screenName, false, true, null);

		// Groups

		if (groupIds != null) {
			List<Group> groups = new ArrayList<Group>();

			for (long groupId : groupIds) {
				Group group = groupLocalService.fetchGroup(groupId);

				if (group != null) {
					groups.add(group);
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn("Group " + groupId + " does not exist");
					}
				}
			}

			groupLocalService.addUserGroups(userId, groups);
		}

		addDefaultGroups(userId);

		// Organizations

		updateOrganizations(userId, organizationIds, false);

		// Roles

		if (roleIds != null) {
			roleIds = UsersAdminUtil.addRequiredRoles(user, roleIds);

			userPersistence.setRoles(userId, roleIds);
		}

		addDefaultRoles(userId);

		// User groups

		if (userGroupIds != null) {
			if (PropsValues.USER_GROUPS_COPY_LAYOUTS_TO_USER_PERSONAL_SITE) {
				for (long userGroupId : userGroupIds) {
					userGroupLocalService.copyUserGroupLayouts(
						userGroupId, new long[] {userId});
				}
			}

			userPersistence.setUserGroups(userId, userGroupIds);
		}

		addDefaultUserGroups(userId);

		// Resources

		resourceLocalService.addResources(
			companyId, 0, creatorUserId, User.class.getName(), user.getUserId(),
			false, false, false);

		// Asset

		if (serviceContext != null) {
			updateAsset(
				creatorUserId, user, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());
		}

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			reindex(user);
		}

		// Workflow

		long workflowUserId = creatorUserId;

		if (workflowUserId == userId) {
			workflowUserId = defaultUser.getUserId();
		}

		ServiceContext workflowServiceContext = serviceContext;

		if (workflowServiceContext == null) {
			workflowServiceContext = new ServiceContext();
		}

		workflowServiceContext.setAttribute("autoPassword", autoPassword);
		workflowServiceContext.setAttribute("passwordUnencrypted", password1);
		workflowServiceContext.setAttribute("sendEmail", sendEmail);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			companyId, workflowUserId, User.class.getName(), userId, user,
			workflowServiceContext);

		if (serviceContext != null) {
			String passwordUnencrypted = (String)serviceContext.getAttribute(
				"passwordUnencrypted");

			if (Validator.isNotNull(passwordUnencrypted)) {
				user.setPasswordUnencrypted(passwordUnencrypted);
			}
		}

		return user;
	}

	/**
	 * Attempts to authenticate the user by their email address and password,
	 * while using the AuthPipeline.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @param  password the user's password
	 * @param  headerMap the header map from the authentication request
	 * @param  parameterMap the parameter map from the authentication request
	 * @param  resultsMap the map of authentication results (may be nil). After
	 *         a succesful authentication the user's primary key will be placed
	 *         under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 *         com.liferay.portal.security.auth.Authenticator#FAILURE}
	 *         indicating that the user's credentials are invalid, {@link
	 *         com.liferay.portal.security.auth.Authenticator#SUCCESS}
	 *         indicating a successful login, or {@link
	 *         com.liferay.portal.security.auth.Authenticator#DNE} indicating
	 *         that a user with that login does not exist.
	 * @throws PortalException if <code>emailAddress</code> or
	 *         <code>password</code> was <code>null</code>
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.security.auth.AuthPipeline
	 */
	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException, SystemException {

		return authenticate(
			companyId, emailAddress, password, CompanyConstants.AUTH_TYPE_EA,
			headerMap, parameterMap, resultsMap);
	}

	/**
	 * Attempts to authenticate the user by their screen name and password,
	 * while using the AuthPipeline.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @param  password the user's password
	 * @param  headerMap the header map from the authentication request
	 * @param  parameterMap the parameter map from the authentication request
	 * @param  resultsMap the map of authentication results (may be nil). After
	 *         a succesful authentication the user's primary key will be placed
	 *         under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 *         com.liferay.portal.security.auth.Authenticator#FAILURE}
	 *         indicating that the user's credentials are invalid, {@link
	 *         com.liferay.portal.security.auth.Authenticator#SUCCESS}
	 *         indicating a successful login, or {@link
	 *         com.liferay.portal.security.auth.Authenticator#DNE} indicating
	 *         that a user with that login does not exist.
	 * @throws PortalException if <code>screenName</code> or
	 *         <code>password</code> was <code>null</code>
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.security.auth.AuthPipeline
	 */
	@Override
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException, SystemException {

		return authenticate(
			companyId, screenName, password, CompanyConstants.AUTH_TYPE_SN,
			headerMap, parameterMap, resultsMap);
	}

	/**
	 * Attempts to authenticate the user by their primary key and password,
	 * while using the AuthPipeline.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  userId the user's primary key
	 * @param  password the user's password
	 * @param  headerMap the header map from the authentication request
	 * @param  parameterMap the parameter map from the authentication request
	 * @param  resultsMap the map of authentication results (may be nil). After
	 *         a succesful authentication the user's primary key will be placed
	 *         under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 *         com.liferay.portal.security.auth.Authenticator#FAILURE}
	 *         indicating that the user's credentials are invalid, {@link
	 *         com.liferay.portal.security.auth.Authenticator#SUCCESS}
	 *         indicating a successful login, or {@link
	 *         com.liferay.portal.security.auth.Authenticator#DNE} indicating
	 *         that a user with that login does not exist.
	 * @throws PortalException if <code>userId</code> or <code>password</code>
	 *         was <code>null</code>
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.security.auth.AuthPipeline
	 */
	@Override
	public int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException, SystemException {

		return authenticate(
			companyId, String.valueOf(userId), password,
			CompanyConstants.AUTH_TYPE_ID, headerMap, parameterMap, resultsMap);
	}

	/**
	 * Attempts to authenticate the user using HTTP basic access authentication,
	 * without using the AuthPipeline. Primarily used for authenticating users
	 * of <code>tunnel-web</code>.
	 *
	 * <p>
	 * Authentication type specifies what <code>login</code> contains.The valid
	 * values are:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_EA</code> - <code>login</code> is the
	 * user's email address
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_SN</code> - <code>login</code> is the
	 * user's screen name
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_ID</code> - <code>login</code> is the
	 * user's primary key
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  authType the type of authentication to perform
	 * @param  login either the user's email address, screen name, or primary
	 *         key depending on the value of <code>authType</code>
	 * @param  password the user's password
	 * @return the authentication status. This can be {@link
	 *         com.liferay.portal.security.auth.Authenticator#FAILURE}
	 *         indicating that the user's credentials are invalid, {@link
	 *         com.liferay.portal.security.auth.Authenticator#SUCCESS}
	 *         indicating a successful login, or {@link
	 *         com.liferay.portal.security.auth.Authenticator#DNE} indicating
	 *         that a user with that login does not exist.
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long authenticateForBasic(
			long companyId, String authType, String login, String password)
		throws PortalException, SystemException {

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			return 0;
		}

		User user = null;

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			user = fetchUserByEmailAddress(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			user = fetchUserByScreenName(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			user = userPersistence.fetchByPrimaryKey(GetterUtil.getLong(login));
		}

		if (user == null) {
			return 0;
		}

		if (user.isDefaultUser()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Basic authentication is disabled for the default " +
						"user");
			}

			return 0;
		}
		else if (!user.isActive()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Basic authentication is disabled for inactive user " +
						user.getUserId());
			}

			return 0;
		}

		if (!PropsValues.BASIC_AUTH_PASSWORD_REQUIRED) {
			return user.getUserId();
		}

		String userPassword = user.getPassword();

		if (!user.isPasswordEncrypted()) {
			userPassword = PasswordEncryptorUtil.encrypt(userPassword);
		}

		String encPassword = PasswordEncryptorUtil.encrypt(
			password, userPassword);

		if (userPassword.equals(password) || userPassword.equals(encPassword)) {
			return user.getUserId();
		}

		return 0;
	}

	/**
	 * Attempts to authenticate the user using HTTP digest access
	 * authentication, without using the AuthPipeline. Primarily used for
	 * authenticating users of <code>tunnel-web</code>.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  username either the user's email address, screen name, or primary
	 *         key
	 * @param  realm unused
	 * @param  nonce the number used once
	 * @param  method the request method
	 * @param  uri the request URI
	 * @param  response the authentication response hash
	 * @return the user's primary key if authentication is succesful;
	 *         <code>0</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long authenticateForDigest(
			long companyId, String username, String realm, String nonce,
			String method, String uri, String response)
		throws PortalException, SystemException {

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			return 0;
		}

		// Get User

		User user = fetchUserByEmailAddress(companyId, username);

		if (user == null) {
			user = fetchUserByScreenName(companyId, username);
		}

		if (user == null) {
			user = userPersistence.fetchByPrimaryKey(
				GetterUtil.getLong(username));
		}

		if (user == null) {
			return 0;
		}

		if (user.isDefaultUser()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Digest authentication is disabled for the default user");
			}

			return 0;
		}
		else if (!user.isActive()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Digest authentication is disabled for inactive user " +
						user.getUserId());
			}

			return 0;
		}

		// Verify digest

		String digest = user.getDigest();

		if (Validator.isNull(digest)) {
			_log.error(
				"User must first login through the portal " + user.getUserId());

			return 0;
		}

		String[] digestArray = StringUtil.split(user.getDigest());

		for (String ha1 : digestArray) {
			String ha2 = DigesterUtil.digestHex(Digester.MD5, method, uri);

			String curResponse = DigesterUtil.digestHex(
				Digester.MD5, ha1, nonce, ha2);

			if (response.equals(curResponse)) {
				return user.getUserId();
			}
		}

		return 0;
	}

	/**
	 * Attempts to authenticate the user using JAAS credentials, without using
	 * the AuthPipeline.
	 *
	 * @param  userId the primary key of the user
	 * @param  encPassword the encrypted password
	 * @return <code>true</code> if authentication is successful;
	 *         <code>false</code> otherwise
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean authenticateForJAAS(long userId, String encPassword) {
		if (PropsValues.AUTH_LOGIN_DISABLED) {
			return false;
		}

		try {
			User user = userPersistence.findByPrimaryKey(userId);

			if (user.isDefaultUser()) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"JAAS authentication is disabled for the default user");
				}

				return false;
			}
			else if (!user.isActive()) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"JAAS authentication is disabled for inactive user " +
							userId);
				}

				return false;
			}

			String userPassword = user.getPassword();

			if (user.isPasswordEncrypted()) {
				if (userPassword.equals(encPassword)) {
					return true;
				}

				if (!PropsValues.PORTAL_JAAS_STRICT_PASSWORD) {
					encPassword = PasswordEncryptorUtil.encrypt(
						encPassword, userPassword);

					if (userPassword.equals(encPassword)) {
						return true;
					}
				}
			}
			else {
				if (!PropsValues.PORTAL_JAAS_STRICT_PASSWORD) {
					if (userPassword.equals(encPassword)) {
						return true;
					}
				}

				userPassword = PasswordEncryptorUtil.encrypt(
					userPassword, encPassword);

				if (userPassword.equals(encPassword)) {
					return true;
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		return false;
	}

	/**
	 * Checks if the user is currently locked out based on the password policy,
	 * and performs maintenance on the user's lockout and failed login data.
	 *
	 * @param  user the user
	 * @throws PortalException if the user was determined to still be locked out
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkLockout(User user)
		throws PortalException, SystemException {

		if (LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId())) {
			return;
		}

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if (!passwordPolicy.isLockout()) {
			return;
		}

		// Reset failure count

		Date now = new Date();
		int failedLoginAttempts = user.getFailedLoginAttempts();

		if (failedLoginAttempts > 0) {
			long failedLoginTime = user.getLastFailedLoginDate().getTime();
			long elapsedTime = now.getTime() - failedLoginTime;
			long requiredElapsedTime =
				passwordPolicy.getResetFailureCount() * 1000;

			if ((requiredElapsedTime != 0) &&
				(elapsedTime > requiredElapsedTime)) {

				user.setFailedLoginAttempts(0);

				userPersistence.update(user);
			}
		}

		// Reset lockout

		if (user.isLockout()) {
			long lockoutTime = user.getLockoutDate().getTime();
			long elapsedTime = now.getTime() - lockoutTime;
			long requiredElapsedTime =
				passwordPolicy.getLockoutDuration() * 1000;

			if ((requiredElapsedTime != 0) &&
				(elapsedTime > requiredElapsedTime)) {

				user.setLockout(false);
				user.setLockoutDate(null);

				userPersistence.update(user);
			}
		}

		if (user.isLockout()) {
			throw new UserLockoutException();
		}
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param  user the user
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkLoginFailure(User user) throws SystemException {
		Date now = new Date();

		int failedLoginAttempts = user.getFailedLoginAttempts();

		user.setLastFailedLoginDate(now);
		user.setFailedLoginAttempts(++failedLoginAttempts);

		userPersistence.update(user);
	}

	/**
	 * Adds a failed login attempt to the user with the email address and
	 * updates the user's last failed login date.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @throws PortalException if a user with the email address could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkLoginFailureByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException, SystemException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		checkLoginFailure(user);
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkLoginFailureById(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		checkLoginFailure(user);
	}

	/**
	 * Adds a failed login attempt to the user with the screen name and updates
	 * the user's last failed login date.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @throws PortalException if a user with the screen name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkLoginFailureByScreenName(long companyId, String screenName)
		throws PortalException, SystemException {

		User user = getUserByScreenName(companyId, screenName);

		checkLoginFailure(user);
	}

	/**
	 * Checks if the user's password is expired based on the password policy,
	 * and performs maintenance on the user's grace login and password reset
	 * data.
	 *
	 * @param  user the user
	 * @throws PortalException if the user's password has expired and the grace
	 *         login limit has been exceeded
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void checkPasswordExpired(User user)
		throws PortalException, SystemException {

		if (LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId())) {
			return;
		}

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		// Check if password has expired

		if (isPasswordExpired(user)) {
			int graceLoginCount = user.getGraceLoginCount();

			if (graceLoginCount < passwordPolicy.getGraceLimit()) {
				user.setGraceLoginCount(++graceLoginCount);

				userPersistence.update(user);
			}
			else {
				user.setDigest(StringPool.BLANK);

				userPersistence.update(user);

				throw new PasswordExpiredException();
			}
		}

		// Check if user should be forced to change password on first login

		if (passwordPolicy.isChangeable() &&
			passwordPolicy.isChangeRequired()) {

			if (user.getLastLoginDate() == null) {
				user.setPasswordReset(true);

				userPersistence.update(user);
			}
		}
	}

	/**
	 * Removes all the users from the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearOrganizationUsers(long organizationId)
		throws SystemException {

		organizationPersistence.clearUsers(organizationId);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes all the users from the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void clearUserGroupUsers(long userGroupId) throws SystemException {
		userGroupPersistence.clearUsers(userGroupId);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Completes the user's registration by generating a password and sending
	 * the confirmation email.
	 *
	 * @param  user the user
	 * @param  serviceContext the service context to be applied. You can specify
	 *         an unencrypted custom password for the user via attribute
	 *         <code>passwordUnencrypted</code>. You automatically generate a
	 *         password for the user by setting attribute
	 *         <code>autoPassword</code> to <code>true</code>. You can send a
	 *         confirmation email to the user by setting attribute
	 *         <code>sendEmail</code> to <code>true</code>.
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void completeUserRegistration(
			User user, ServiceContext serviceContext)
		throws PortalException, SystemException {

		boolean autoPassword = ParamUtil.getBoolean(
			serviceContext, "autoPassword");

		String password = (String)serviceContext.getAttribute(
			"passwordUnencrypted");

		if (autoPassword) {
			if (LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId())) {
				if (_log.isWarnEnabled()) {
					StringBundler sb = new StringBundler(4);

					sb.append("When LDAP password policy is enabled, it is ");
					sb.append("possible that portal generated passwords will ");
					sb.append("not match the LDAP policy. Using ");
					sb.append("RegExpToolkit to generate new password.");

					_log.warn(sb.toString());
				}

				RegExpToolkit regExpToolkit = new RegExpToolkit();

				password = regExpToolkit.generate(null);
			}
			else {
				PasswordPolicy passwordPolicy =
					passwordPolicyLocalService.getPasswordPolicy(
						user.getCompanyId(), user.getOrganizationIds());

				password = PwdToolkitUtil.generate(passwordPolicy);
			}

			serviceContext.setAttribute("passwordUnencrypted", password);

			user.setPassword(PasswordEncryptorUtil.encrypt(password));
			user.setPasswordUnencrypted(password);
			user.setPasswordEncrypted(true);
			user.setPasswordModified(true);
			user.setPasswordModifiedDate(new Date());

			userPersistence.update(user);

			user.setPasswordModified(false);
		}

		if (user.hasCompanyMx()) {
			mailService.addUser(
				user.getCompanyId(), user.getUserId(), password,
				user.getFirstName(), user.getMiddleName(), user.getLastName(),
				user.getEmailAddress());
		}

		boolean sendEmail = ParamUtil.getBoolean(serviceContext, "sendEmail");

		if (sendEmail) {
			sendEmail(user, password, serviceContext);
		}

		Company company = companyPersistence.findByPrimaryKey(
			user.getCompanyId());

		if (company.isStrangersVerify()) {
			sendEmailAddressVerification(
				user, user.getEmailAddress(), serviceContext);
		}
	}

	/**
	 * Decrypts the user's primary key and password from their encrypted forms.
	 * Used for decrypting a user's credentials from the values stored in an
	 * automatic login cookie.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  name the encrypted primary key of the user
	 * @param  password the encrypted password of the user
	 * @return the user's primary key and password
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the user's password was incorrect
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public KeyValuePair decryptUserId(
			long companyId, String name, String password)
		throws PortalException, SystemException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		try {
			name = Encryptor.decrypt(company.getKeyObj(), name);
		}
		catch (EncryptorException ee) {
			throw new SystemException(ee);
		}

		long userId = GetterUtil.getLong(name);

		User user = userPersistence.findByPrimaryKey(userId);

		try {
			password = Encryptor.decrypt(company.getKeyObj(), password);
		}
		catch (EncryptorException ee) {
			throw new SystemException(ee);
		}

		String userPassword = user.getPassword();
		String encPassword = PasswordEncryptorUtil.encrypt(
			password, userPassword);

		if (userPassword.equals(encPassword)) {
			if (isPasswordExpired(user)) {
				user.setPasswordReset(true);

				userPersistence.update(user);
			}

			return new KeyValuePair(name, password);
		}
		else {
			throw new PrincipalException();
		}
	}

	/**
	 * Deletes the user's portrait image.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the user's portrait could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void deletePortrait(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		long portraitId = user.getPortraitId();

		if (portraitId > 0) {
			user.setPortraitId(0);

			userPersistence.update(user);

			imageLocalService.deleteImage(portraitId);
		}
	}

	/**
	 * Removes the user from the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  userId the primary key of the user
	 * @throws PortalException if a role or user with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void deleteRoleUser(long roleId, long userId)
		throws PortalException, SystemException {

		rolePersistence.removeUser(roleId, userId);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userId);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Deletes the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the deleted user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User deleteUser(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		return deleteUser(user);
	}

	/**
	 * Deletes the user.
	 *
	 * @param  user the user
	 * @return the deleted user
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User deleteUser(User user) throws PortalException, SystemException {
		if (!PropsValues.USERS_DELETE) {
			throw new RequiredUserException();
		}

		// Browser tracker

		browserTrackerLocalService.deleteUserBrowserTracker(user.getUserId());

		// Group

		Group group = null;

		if (!user.isDefaultUser()) {
			group = user.getGroup();
		}

		if (group != null) {
			groupLocalService.deleteGroup(group);
		}

		// Portrait

		try {
			imageLocalService.deleteImage(user.getPortraitId());
		}
		catch (NoSuchImageException nsie) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to delete image " + user.getPortraitId());
			}
		}

		// Password policy relation

		passwordPolicyRelLocalService.deletePasswordPolicyRel(
			User.class.getName(), user.getUserId());

		// Old passwords

		passwordTrackerLocalService.deletePasswordTrackers(user.getUserId());

		// Subscriptions

		subscriptionLocalService.deleteSubscriptions(user.getUserId());

		// External user ids

		userIdMapperLocalService.deleteUserIdMappers(user.getUserId());

		// Announcements

		announcementsDeliveryLocalService.deleteDeliveries(user.getUserId());

		// Asset

		assetEntryLocalService.deleteEntry(
			User.class.getName(), user.getUserId());

		// Blogs

		blogsStatsUserLocalService.deleteStatsUserByUserId(user.getUserId());

		// Document library

		dlFileRankLocalService.deleteFileRanksByUserId(user.getUserId());

		// Expando

		expandoRowLocalService.deleteRows(user.getUserId());

		// Message boards

		mbBanLocalService.deleteBansByBanUserId(user.getUserId());
		mbStatsUserLocalService.deleteStatsUsersByUserId(user.getUserId());
		mbThreadFlagLocalService.deleteThreadFlagsByUserId(user.getUserId());

		// Membership requests

		membershipRequestLocalService.deleteMembershipRequestsByUserId(
			user.getUserId());

		// Shopping cart

		shoppingCartLocalService.deleteUserCarts(user.getUserId());

		// Social

		socialActivityLocalService.deleteUserActivities(user.getUserId());
		socialRequestLocalService.deleteReceiverUserRequests(user.getUserId());
		socialRequestLocalService.deleteUserRequests(user.getUserId());

		// Mail

		mailService.deleteUser(user.getCompanyId(), user.getUserId());

		// Contact

		Contact contact = contactLocalService.fetchContact(user.getContactId());

		if (contact != null) {
			contactLocalService.deleteContact(contact);
		}

		// Group roles

		userGroupRoleLocalService.deleteUserGroupRolesByUserId(
			user.getUserId());

		// Resources

		resourceLocalService.deleteResource(
			user.getCompanyId(), User.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, user.getUserId());

		// User

		userPersistence.remove(user);

		// Permission cache

		PermissionCacheUtil.clearCache();

		// Workflow

		workflowInstanceLinkLocalService.deleteWorkflowInstanceLinks(
			user.getCompanyId(), 0, User.class.getName(), user.getUserId());

		return user;
	}

	/**
	 * Removes the user from the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @param  userId the primary key of the user
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void deleteUserGroupUser(long userGroupId, long userId)
		throws PortalException, SystemException {

		userGroupPersistence.removeUser(userGroupId, userId);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userId);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Encrypts the primary key of the user. Used when encrypting the user's
	 * credentials for storage in an automatic login cookie.
	 *
	 * @param  name the primary key of the user
	 * @return the user's encrypted primary key
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String encryptUserId(String name)
		throws PortalException, SystemException {

		long userId = GetterUtil.getLong(name);

		User user = userPersistence.findByPrimaryKey(userId);

		Company company = companyPersistence.findByPrimaryKey(
			user.getCompanyId());

		try {
			return Encryptor.encrypt(company.getKeyObj(), name);
		}
		catch (EncryptorException ee) {
			throw new SystemException(ee);
		}
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return the user with the email address, or <code>null</code> if a user
	 *         with the email address could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User fetchUserByEmailAddress(long companyId, String emailAddress)
		throws SystemException {

		emailAddress = getLogin(emailAddress);

		return userPersistence.fetchByC_EA(companyId, emailAddress);
	}

	/**
	 * Returns the user with the Facebook ID.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  facebookId the user's Facebook ID
	 * @return the user with the Facebook ID, or <code>null</code> if a user
	 *         with the Facebook ID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User fetchUserByFacebookId(long companyId, long facebookId)
		throws SystemException {

		return userPersistence.fetchByC_FID(companyId, facebookId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param  userId the primary key of the user
	 * @return the user with the primary key, or <code>null</code> if a user
	 *         with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User fetchUserById(long userId) throws SystemException {
		return userPersistence.fetchByPrimaryKey(userId);
	}

	/**
	 * Returns the user with the OpenID.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  openId the user's OpenID
	 * @return the user with the OpenID, or <code>null</code> if a user with the
	 *         OpenID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User fetchUserByOpenId(long companyId, String openId)
		throws SystemException {

		return userPersistence.fetchByC_O(companyId, openId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return the user with the screen name, or <code>null</code> if a user
	 *         with the screen name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User fetchUserByScreenName(long companyId, String screenName)
		throws SystemException {

		screenName = getLogin(screenName);

		return userPersistence.fetchByC_SN(companyId, screenName);
	}

	/**
	 * Returns a range of all the users belonging to the company.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the company
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @return the range of users belonging to the company
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getCompanyUsers(long companyId, int start, int end)
		throws SystemException {

		return userPersistence.findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns the number of users belonging to the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the number of users belonging to the company
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getCompanyUsersCount(long companyId) throws SystemException {
		return userPersistence.countByCompanyId(companyId);
	}

	/**
	 * Returns the default user for the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the default user for the company
	 * @throws PortalException if a default user for the company could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@Skip
	public User getDefaultUser(long companyId)
		throws PortalException, SystemException {

		User userModel = _defaultUsers.get(companyId);

		if (userModel == null) {
			userModel = userLocalService.loadGetDefaultUser(companyId);

			_defaultUsers.put(companyId, userModel);
		}

		return userModel;
	}

	/**
	 * Returns the primary key of the default user for the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the primary key of the default user for the company
	 * @throws PortalException if a default user for the company could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@Skip
	public long getDefaultUserId(long companyId)
		throws PortalException, SystemException {

		User user = getDefaultUser(companyId);

		return user.getUserId();
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public long[] getGroupUserIds(long groupId) throws SystemException {
		return getUserIds(getGroupUsers(groupId));
	}

	/**
	 * Returns the number of users with the status belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the group
	 * @throws PortalException if a group with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getGroupUsersCount(long groupId, int status)
		throws PortalException, SystemException {

		Group group = groupPersistence.findByPrimaryKey(groupId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("usersGroups", new Long(groupId));

		return searchCount(group.getCompanyId(), null, status, params);
	}

	@Override
	public List<User> getInheritedRoleUsers(
			long roleId, int start, int end, OrderByComparator obc)
		throws PortalException, SystemException {

		Role role = rolePersistence.findByPrimaryKey(roleId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("inherit", Boolean.TRUE);
		params.put("usersRoles", roleId);

		return search(
			role.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params, start, end, obc);
	}

	/**
	 * Returns all the users who have not had any announcements of the type
	 * delivered, excluding the default user.
	 *
	 * @param  type the type of announcement
	 * @return the users who have not had any annoucements of the type delivered
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getNoAnnouncementsDeliveries(String type)
		throws SystemException {

		return userFinder.findByNoAnnouncementsDeliveries(type);
	}

	/**
	 * Returns all the users who do not have any contacts.
	 *
	 * @return the users who do not have any contacts
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getNoContacts() throws SystemException {
		return userFinder.findByNoContacts();
	}

	/**
	 * Returns all the users who do not belong to any groups, excluding the
	 * default user.
	 *
	 * @return the users who do not belong to any groups
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getNoGroups() throws SystemException {
		return userFinder.findByNoGroups();
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public long[] getOrganizationUserIds(long organizationId)
		throws SystemException {

		return getUserIds(getOrganizationUsers(organizationId));
	}

	/**
	 * Returns the number of users with the status belonging to the
	 * organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the organization
	 * @throws PortalException if an organization with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getOrganizationUsersCount(long organizationId, int status)
		throws PortalException, SystemException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("usersOrgs", new Long(organizationId));

		return searchCount(organization.getCompanyId(), null, status, params);
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param  roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public long[] getRoleUserIds(long roleId) throws SystemException {
		return getUserIds(getRoleUsers(roleId));
	}

	/**
	 * Returns the number of users with the status belonging to the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the role
	 * @throws PortalException if an role with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getRoleUsersCount(long roleId, int status)
		throws PortalException, SystemException {

		Role role = rolePersistence.findByPrimaryKey(roleId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("usersRoles", new Long(roleId));

		return searchCount(role.getCompanyId(), null, status, params);
	}

	/**
	 * Returns an ordered range of all the users with a social relation of the
	 * type with the user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  userId the primary key of the user
	 * @param  type the type of social relation. The possible types can be found
	 *         in {@link
	 *         com.liferay.portlet.social.model.SocialRelationConstants}.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the ordered range of users with a social relation of the type
	 *         with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getSocialUsers(
			long userId, int type, int start, int end, OrderByComparator obc)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialRelationType", new Long[] {userId, new Long(type)});

		return search(
			user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the users with a social relation with the
	 * user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  userId the primary key of the user
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the ordered range of users with a social relation with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getSocialUsers(
			long userId, int start, int end, OrderByComparator obc)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialRelation", new Long[] {userId});

		return search(
			user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the users with a mutual social relation
	 * of the type with both of the given users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  userId1 the primary key of the first user
	 * @param  userId2 the primary key of the second user
	 * @param  type the type of social relation. The possible types can be found
	 *         in {@link
	 *         com.liferay.portlet.social.model.SocialRelationConstants}.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the ordered range of users with a mutual social relation of the
	 *         type with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getSocialUsers(
			long userId1, long userId2, int type, int start, int end,
			OrderByComparator obc)
		throws PortalException, SystemException {

		User user1 = userPersistence.findByPrimaryKey(userId1);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put(
			"socialMutualRelationType",
			new Long[] {userId1, new Long(type), userId2, new Long(type)});

		return search(
			user1.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the users with a mutual social relation
	 * with both of the given users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  userId1 the primary key of the first user
	 * @param  userId2 the primary key of the second user
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the ordered range of users with a mutual social relation with the
	 *         user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public List<User> getSocialUsers(
			long userId1, long userId2, int start, int end,
			OrderByComparator obc)
		throws PortalException, SystemException {

		User user1 = userPersistence.findByPrimaryKey(userId1);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialMutualRelation", new Long[] {userId1, userId2});

		return search(
			user1.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params, start, end, obc);
	}

	/**
	 * Returns the number of users with a social relation with the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of users with a social relation with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getSocialUsersCount(long userId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialRelation", new Long[] {userId});

		return searchCount(
			user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params);
	}

	/**
	 * Returns the number of users with a social relation of the type with the
	 * user.
	 *
	 * @param  userId the primary key of the user
	 * @param  type the type of social relation. The possible types can be found
	 *         in {@link
	 *         com.liferay.portlet.social.model.SocialRelationConstants}.
	 * @return the number of users with a social relation of the type with the
	 *         user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getSocialUsersCount(long userId, int type)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialRelationType", new Long[] {userId, new Long(type)});

		return searchCount(
			user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params);
	}

	/**
	 * Returns the number of users with a mutual social relation with both of
	 * the given users.
	 *
	 * @param  userId1 the primary key of the first user
	 * @param  userId2 the primary key of the second user
	 * @return the number of users with a mutual social relation with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getSocialUsersCount(long userId1, long userId2)
		throws PortalException, SystemException {

		User user1 = userPersistence.findByPrimaryKey(userId1);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("socialMutualRelation", new Long[] {userId1, userId2});

		return searchCount(
			user1.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params);
	}

	/**
	 * Returns the number of users with a mutual social relation of the type
	 * with both of the given users.
	 *
	 * @param  userId1 the primary key of the first user
	 * @param  userId2 the primary key of the second user
	 * @param  type the type of social relation. The possible types can be found
	 *         in {@link
	 *         com.liferay.portlet.social.model.SocialRelationConstants}.
	 * @return the number of users with a mutual social relation of the type
	 *         with the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getSocialUsersCount(long userId1, long userId2, int type)
		throws PortalException, SystemException {

		User user1 = userPersistence.findByPrimaryKey(userId1);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put(
			"socialMutualRelationType",
			new Long[] {userId1, new Long(type), userId2, new Long(type)});

		return searchCount(
			user1.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			params);
	}

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param  contactId the user's contact ID
	 * @return the user with the contact ID
	 * @throws PortalException if a user with the contact ID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByContactId(long contactId)
		throws PortalException, SystemException {

		return userPersistence.findByContactId(contactId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return the user with the email address
	 * @throws PortalException if a user with the email address could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByEmailAddress(long companyId, String emailAddress)
		throws PortalException, SystemException {

		emailAddress = getLogin(emailAddress);

		return userPersistence.findByC_EA(companyId, emailAddress);
	}

	/**
	 * Returns the user with the Facebook ID.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  facebookId the user's Facebook ID
	 * @return the user with the Facebook ID
	 * @throws PortalException if a user with the Facebook ID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByFacebookId(long companyId, long facebookId)
		throws PortalException, SystemException {

		return userPersistence.findByC_FID(companyId, facebookId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param  userId the primary key of the user
	 * @return the user with the primary key
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserById(long userId)
		throws PortalException, SystemException {

		return userPersistence.findByPrimaryKey(userId);
	}

	/**
	 * Returns the user with the primary key from the company.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  userId the primary key of the user
	 * @return the user with the primary key
	 * @throws PortalException if a user with the primary key from the company
	 *         could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserById(long companyId, long userId)
		throws PortalException, SystemException {

		return userPersistence.findByC_U(companyId, userId);
	}

	/**
	 * Returns the user with the OpenID.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  openId the user's OpenID
	 * @return the user with the OpenID
	 * @throws PortalException if a user with the OpenID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByOpenId(long companyId, String openId)
		throws PortalException, SystemException {

		return userPersistence.findByC_O(companyId, openId);
	}

	/**
	 * Returns the user with the portrait ID.
	 *
	 * @param  portraitId the user's portrait ID
	 * @return the user with the portrait ID
	 * @throws PortalException if a user with the portrait ID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByPortraitId(long portraitId)
		throws PortalException, SystemException {

		return userPersistence.findByPortraitId(portraitId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return the user with the screen name
	 * @throws PortalException if a user with the screen name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByScreenName(long companyId, String screenName)
		throws PortalException, SystemException {

		screenName = getLogin(screenName);

		return userPersistence.findByC_SN(companyId, screenName);
	}

	/**
	 * Returns the user with the UUID.
	 *
	 * @param      uuid the user's UUID
	 * @return     the user with the UUID
	 * @throws     PortalException if a user with the UUID could not be found
	 * @throws     SystemException if a system exception occurred
	 * @deprecated As of 6.2.0, replaced by {@link
	 *             #getUserByUuidAndCompanyId(String, long)}
	 */
	@Override
	public User getUserByUuid(String uuid)
		throws PortalException, SystemException {

		List<User> users = userPersistence.findByUuid(uuid);

		if (users.isEmpty()) {
			throw new NoSuchUserException("{uuid=" + uuid + "}");
		}
		else {
			return users.get(0);
		}
	}

	/**
	 * Returns the user with the UUID.
	 *
	 * @param  uuid the user's UUID
	 * @param  companyId the primary key of the user's company
	 * @return the user with the UUID
	 * @throws PortalException if a user with the UUID could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User getUserByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException, SystemException {

		List<User> users = userPersistence.findByUuid_C(uuid, companyId);

		if (users.isEmpty()) {
			StringBundler sb = new StringBundler(5);

			sb.append("{uuid=");
			sb.append(uuid);
			sb.append(", companyId=");
			sb.append(companyId);
			sb.append("}");

			throw new NoSuchUserException(sb.toString());
		}
		else {
			return users.get(0);
		}
	}

	/**
	 * Returns the number of users with the status belonging to the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the user group
	 * @throws PortalException if a user group with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int getUserGroupUsersCount(long userGroupId, int status)
		throws PortalException, SystemException {

		UserGroup userGroup = userGroupPersistence.findByPrimaryKey(
			userGroupId);

		LinkedHashMap<String, Object> params =
			new LinkedHashMap<String, Object>();

		params.put("usersUserGroups", new Long(userGroupId));

		return searchCount(userGroup.getCompanyId(), null, status, params);
	}

	/**
	 * Returns the primary key of the user with the email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return the primary key of the user with the email address
	 * @throws PortalException if a user with the email address could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public long getUserIdByEmailAddress(long companyId, String emailAddress)
		throws PortalException, SystemException {

		emailAddress = StringUtil.toLowerCase(emailAddress.trim());

		User user = userPersistence.findByC_EA(companyId, emailAddress);

		return user.getUserId();
	}

	/**
	 * Returns the primary key of the user with the screen name.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return the primary key of the user with the screen name
	 * @throws PortalException if a user with the screen name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public long getUserIdByScreenName(long companyId, String screenName)
		throws PortalException, SystemException {

		screenName = getLogin(screenName);

		User user = userPersistence.findByC_SN(companyId, screenName);

		return user.getUserId();
	}

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * user.
	 *
	 * @param  passwordPolicyId the primary key of the password policy
	 * @param  userId the primary key of the user
	 * @return <code>true</code> if the password policy is assigned to the user;
	 *         <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean hasPasswordPolicyUser(long passwordPolicyId, long userId)
		throws SystemException {

		return passwordPolicyRelLocalService.hasPasswordPolicyRel(
			passwordPolicyId, User.class.getName(), userId);
	}

	/**
	 * Returns <code>true</code> if the user has the role with the name,
	 * optionally through inheritance.
	 *
	 * @param  companyId the primary key of the role's company
	 * @param  name the name of the role (must be a regular role, not an
	 *         organization, site or provider role)
	 * @param  userId the primary key of the user
	 * @param  inherited whether to include roles inherited from organizations,
	 *         sites, etc.
	 * @return <code>true</code> if the user has the role; <code>false</code>
	 *         otherwise
	 * @throws PortalException if a role with the name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean hasRoleUser(
			long companyId, String name, long userId, boolean inherited)
		throws PortalException, SystemException {

		return roleLocalService.hasUserRole(userId, companyId, name, inherited);
	}

	/**
	 * Returns <code>true</code> if the user's password is expired.
	 *
	 * @param  user the user
	 * @return <code>true</code> if the user's password is expired;
	 *         <code>false</code> otherwise
	 * @throws PortalException if the password policy for the user could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean isPasswordExpired(User user)
		throws PortalException, SystemException {

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if ((passwordPolicy != null) && passwordPolicy.getExpireable()) {
			Date now = new Date();

			if (user.getPasswordModifiedDate() == null) {
				user.setPasswordModifiedDate(now);

				userLocalService.updateUser(user);
			}

			long passwordStartTime = user.getPasswordModifiedDate().getTime();
			long elapsedTime = now.getTime() - passwordStartTime;

			if (elapsedTime > (passwordPolicy.getMaxAge() * 1000)) {
				return true;
			}
			else {
				return false;
			}
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the password policy is configured to warn
	 * the user that his password is expiring and the remaining time until
	 * expiration is equal or less than the configured warning time.
	 *
	 * @param  user the user
	 * @return <code>true</code> if the user's password is expiring soon;
	 *         <code>false</code> otherwise
	 * @throws PortalException if the password policy for the user could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public boolean isPasswordExpiringSoon(User user)
		throws PortalException, SystemException {

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if ((passwordPolicy != null) && passwordPolicy.isExpireable() &&
			(passwordPolicy.getWarningTime() > 0)) {

			Date now = new Date();

			if (user.getPasswordModifiedDate() == null) {
				user.setPasswordModifiedDate(now);

				userLocalService.updateUser(user);
			}

			long timeModified = user.getPasswordModifiedDate().getTime();
			long passwordExpiresOn =
				(passwordPolicy.getMaxAge() * 1000) + timeModified;

			long timeStartWarning =
				passwordExpiresOn - (passwordPolicy.getWarningTime() * 1000);

			if (now.getTime() > timeStartWarning) {
				return true;
			}
			else {
				return false;
			}
		}

		return false;
	}

	/**
	 * Returns the default user for the company.
	 *
	 * @param  companyId the primary key of the company
	 * @return the default user for the company
	 * @throws PortalException if the user could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User loadGetDefaultUser(long companyId)
		throws PortalException, SystemException {

		return userPersistence.findByC_DU(companyId, true);
	}

	/**
	 * Returns an ordered range of all the users who match the keywords and
	 * status, without using the indexer. It is preferable to use the indexed
	 * version {@link #search(long, String, int, LinkedHashMap, int, int, Sort)}
	 * instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user's first name, middle name, last name, screen name, or email
	 *         address
	 * @param  status the workflow status
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.service.persistence.UserFinder}.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the matching users
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.service.persistence.UserFinder
	 */
	@Override
	public List<User> search(
			long companyId, String keywords, int status,
			LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator obc)
		throws SystemException {

		return userFinder.findByKeywords(
			companyId, keywords, status, params, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the users who match the keywords and
	 * status, using the indexer. It is preferable to use this method instead of
	 * the non-indexed version whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user's first name, middle name, last name, screen name, or email
	 *         address
	 * @param  status the workflow status
	 * @param  params the indexer parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portlet.usersadmin.util.UserIndexer}.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  sort the field and direction to sort by (optionally
	 *         <code>null</code>)
	 * @return the matching users
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portlet.usersadmin.util.UserIndexer
	 */
	@Override
	public Hits search(
			long companyId, String keywords, int status,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws SystemException {

		String firstName = null;
		String middleName = null;
		String lastName = null;
		String fullName = null;
		String screenName = null;
		String emailAddress = null;
		String street = null;
		String city = null;
		String zip = null;
		String region = null;
		String country = null;
		boolean andOperator = false;

		if (Validator.isNotNull(keywords)) {
			firstName = keywords;
			middleName = keywords;
			lastName = keywords;
			fullName = keywords;
			screenName = keywords;
			emailAddress = keywords;
			street = keywords;
			city = keywords;
			zip = keywords;
			region = keywords;
			country = keywords;
		}
		else {
			andOperator = true;
		}

		if (params != null) {
			params.put("keywords", keywords);
		}

		return search(
			companyId, firstName, middleName, lastName, fullName, screenName,
			emailAddress, street, city, zip, region, country, status, params,
			andOperator, start, end, sort);
	}

	/**
	 * Returns an ordered range of all the users with the status, and whose
	 * first name, middle name, last name, screen name, and email address match
	 * the keywords specified for them, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, String,
	 * String, String, String, String, int, LinkedHashMap, boolean, int, int,
	 * Sort)} instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  firstName the first name keywords (space separated)
	 * @param  middleName the middle name keywords
	 * @param  lastName the last name keywords
	 * @param  screenName the screen name keywords
	 * @param  emailAddress the email address keywords
	 * @param  status the workflow status
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.service.persistence.UserFinder}.
	 * @param  andSearch whether every field must match its keywords, or just
	 *         one field. For example, &quot;users with the first name 'bob' and
	 *         last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 *         or the last name 'smith'&quot;.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  obc the comparator to order the users by (optionally
	 *         <code>null</code>)
	 * @return the matching users
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.service.persistence.UserFinder
	 */
	@Override
	public List<User> search(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, OrderByComparator obc)
		throws SystemException {

		return userFinder.findByC_FN_MN_LN_SN_EA_S(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the users with the status, and whose
	 * first name, middle name, last name, screen name, and email address match
	 * the keywords specified for them, using the indexer. It is preferable to
	 * use this method instead of the non-indexed version whenever possible for
	 * performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  firstName the first name keywords (space separated)
	 * @param  middleName the middle name keywords
	 * @param  lastName the last name keywords
	 * @param  screenName the screen name keywords
	 * @param  emailAddress the email address keywords
	 * @param  status the workflow status
	 * @param  params the indexer parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portlet.usersadmin.util.UserIndexer}.
	 * @param  andSearch whether every field must match its keywords, or just
	 *         one field. For example, &quot;users with the first name 'bob' and
	 *         last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 *         or the last name 'smith'&quot;.
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  sort the field and direction to sort by (optionally
	 *         <code>null</code>)
	 * @return the matching users
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portlet.usersadmin.util.UserIndexer
	 */
	@Override
	public Hits search(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort sort)
		throws SystemException {

		return search(
			companyId, firstName, middleName, lastName, null, screenName,
			emailAddress, null, null, null, null, null, status, params,
			andSearch, start, end, sort);
	}

	/**
	 * Returns the number of users who match the keywords and status.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  keywords the keywords (space separated), which may occur in the
	 *         user's first name, middle name, last name, screen name, or email
	 *         address
	 * @param  status the workflow status
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.service.persistence.UserFinder}.
	 * @return the number matching users
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int searchCount(
			long companyId, String keywords, int status,
			LinkedHashMap<String, Object> params)
		throws SystemException {

		return userFinder.countByKeywords(companyId, keywords, status, params);
	}

	/**
	 * Returns the number of users with the status, and whose first name, middle
	 * name, last name, screen name, and email address match the keywords
	 * specified for them.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  firstName the first name keywords (space separated)
	 * @param  middleName the middle name keywords
	 * @param  lastName the last name keywords
	 * @param  screenName the screen name keywords
	 * @param  emailAddress the email address keywords
	 * @param  status the workflow status
	 * @param  params the finder parameters (optionally <code>null</code>). For
	 *         more information see {@link
	 *         com.liferay.portal.service.persistence.UserFinder}.
	 * @param  andSearch whether every field must match its keywords, or just
	 *         one field. For example, &quot;users with the first name 'bob' and
	 *         last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 *         or the last name 'smith'&quot;.
	 * @return the number of matching users
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public int searchCount(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			LinkedHashMap<String, Object> params, boolean andSearch)
		throws SystemException {

		return userFinder.countByC_FN_MN_LN_SN_EA_S(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch);
	}

	/**
	 * Sends an email address verification to the user.
	 *
	 * @param  user the verification email recipient
	 * @param  emailAddress the recipient's email address
	 * @param  serviceContext the service context to be applied. Must set the
	 *         portal URL, main path, primary key of the layout, remote address,
	 *         remote host, and agent for the user.
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void sendEmailAddressVerification(
			User user, String emailAddress, ServiceContext serviceContext)
		throws PortalException, SystemException {

		if (user.isEmailAddressVerified() &&
			StringUtil.equalsIgnoreCase(emailAddress, user.getEmailAddress())) {

			return;
		}

		Ticket ticket = ticketLocalService.addTicket(
			user.getCompanyId(), User.class.getName(), user.getUserId(),
			TicketConstants.TYPE_EMAIL_ADDRESS, emailAddress, null,
			serviceContext);

		String verifyEmailAddressURL =
			serviceContext.getPortalURL() + serviceContext.getPathMain() +
				"/portal/verify_email_address?ticketKey=" + ticket.getKey();

		long plid = serviceContext.getPlid();

		if (plid > 0) {
			Layout layout = layoutLocalService.fetchLayout(plid);

			if (layout != null) {
				Group group = layout.getGroup();

				if (!layout.isPrivateLayout() && !group.isUser()) {
					verifyEmailAddressURL +=
						"&p_l_id=" + serviceContext.getPlid();
				}
			}
		}

		String fromName = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);
		String fromAddress = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		String toName = user.getFullName();
		String toAddress = emailAddress;

		String subject = PrefsPropsUtil.getContent(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_VERIFICATION_SUBJECT);

		String body = PrefsPropsUtil.getContent(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_VERIFICATION_BODY);

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(body);
		subscriptionSender.setCompanyId(user.getCompanyId());
		subscriptionSender.setContextAttributes(
			"[$EMAIL_VERIFICATION_CODE$]", ticket.getKey(),
			"[$EMAIL_VERIFICATION_URL$]", verifyEmailAddressURL,
			"[$REMOTE_ADDRESS$]", serviceContext.getRemoteAddr(),
			"[$REMOTE_HOST$]", serviceContext.getRemoteHost(), "[$USER_ID$]",
			user.getUserId(), "[$USER_SCREENNAME$]", user.getScreenName());
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId(
			"user", user.getUserId(), System.currentTimeMillis(),
			PwdGenerator.getPassword());
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setSubject(subject);
		subscriptionSender.setUserId(user.getUserId());

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	/**
	 * Sends the password email to the user with the email address. The content
	 * of this email can be specified in <code>portal.properties</code> with the
	 * <code>admin.email.password</code> keys.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @param  fromName the name of the individual that the email should be from
	 * @param  fromAddress the address of the individual that the email should
	 *         be from
	 * @param  subject the email subject. If <code>null</code>, the subject
	 *         specified in <code>portal.properties</code> will be used.
	 * @param  body the email body. If <code>null</code>, the body specified in
	 *         <code>portal.properties</code> will be used.
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a user with the email address could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void sendPassword(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		if (!company.isSendPassword() && !company.isSendPasswordResetLink()) {
			return;
		}

		emailAddress = StringUtil.toLowerCase(emailAddress.trim());

		if (Validator.isNull(emailAddress)) {
			throw new UserEmailAddressException();
		}

		User user = userPersistence.findByC_EA(companyId, emailAddress);

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		String newPassword = StringPool.BLANK;
		String passwordResetURL = StringPool.BLANK;

		if (company.isSendPasswordResetLink()) {
			Date expirationDate = null;

			if ((passwordPolicy != null) &&
				(passwordPolicy.getResetTicketMaxAge() > 0)) {

				expirationDate = new Date(
					System.currentTimeMillis() +
						(passwordPolicy.getResetTicketMaxAge() * 1000));
			}

			Ticket ticket = ticketLocalService.addTicket(
				companyId, User.class.getName(), user.getUserId(),
				TicketConstants.TYPE_PASSWORD, null, expirationDate,
				serviceContext);

			passwordResetURL =
				serviceContext.getPortalURL() + serviceContext.getPathMain() +
					"/portal/update_password?p_l_id="+
						serviceContext.getPlid() +
							"&ticketKey=" + ticket.getKey();
		}
		else {
			if (!PasswordEncryptorUtil.PASSWORDS_ENCRYPTION_ALGORITHM.equals(
					PasswordEncryptorUtil.TYPE_NONE)) {

				if (LDAPSettingsUtil.isPasswordPolicyEnabled(
						user.getCompanyId())) {

					if (_log.isWarnEnabled()) {
						StringBundler sb = new StringBundler(5);

						sb.append("When LDAP password policy is enabled, ");
						sb.append("it is possible that portal generated ");
						sb.append("passwords will not match the LDAP policy.");
						sb.append("Using RegExpToolkit to generate new ");
						sb.append("password.");

						_log.warn(sb.toString());
					}

					RegExpToolkit regExpToolkit = new RegExpToolkit();

					newPassword = regExpToolkit.generate(null);
				}
				else {
					newPassword = PwdToolkitUtil.generate(passwordPolicy);
				}

				boolean passwordReset = false;

				if (passwordPolicy.getChangeable() &&
					passwordPolicy.getChangeRequired()) {

					passwordReset = true;
				}

				user.setPassword(PasswordEncryptorUtil.encrypt(newPassword));
				user.setPasswordUnencrypted(newPassword);
				user.setPasswordEncrypted(true);
				user.setPasswordReset(passwordReset);
				user.setPasswordModified(true);
				user.setPasswordModifiedDate(new Date());

				userPersistence.update(user);

				user.setPasswordModified(false);
			}
			else {
				newPassword = user.getPassword();
			}
		}

		if (Validator.isNull(fromName)) {
			fromName = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_NAME);
		}

		if (Validator.isNull(fromAddress)) {
			fromAddress = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		}

		String toName = user.getFullName();
		String toAddress = user.getEmailAddress();

		if (Validator.isNull(subject)) {
			if (company.isSendPasswordResetLink()) {
				subject = PrefsPropsUtil.getContent(
					companyId, PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_SUBJECT);
			}
			else {
				subject = PrefsPropsUtil.getContent(
					companyId, PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_SUBJECT);
			}
		}

		if (Validator.isNull(body)) {
			if (company.isSendPasswordResetLink()) {
				body = PrefsPropsUtil.getContent(
					companyId, PropsKeys.ADMIN_EMAIL_PASSWORD_RESET_BODY);
			}
			else {
				body = PrefsPropsUtil.getContent(
					companyId, PropsKeys.ADMIN_EMAIL_PASSWORD_SENT_BODY);
			}
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(body);
		subscriptionSender.setCompanyId(companyId);
		subscriptionSender.setContextAttributes(
			"[$PASSWORD_RESET_URL$]", passwordResetURL, "[$REMOTE_ADDRESS$]",
			serviceContext.getRemoteAddr(), "[$REMOTE_HOST$]",
			serviceContext.getRemoteHost(), "[$USER_ID$]", user.getUserId(),
			"[$USER_PASSWORD$]", newPassword, "[$USER_SCREENNAME$]",
			user.getScreenName());
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId(
			"user", user.getUserId(), System.currentTimeMillis(),
			PwdGenerator.getPassword());
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setSubject(subject);
		subscriptionSender.setUserId(user.getUserId());

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	/**
	 * Sets the users in the role, removing and adding users to the role as
	 * necessary.
	 *
	 * @param  roleId the primary key of the role
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void setRoleUsers(long roleId, long[] userIds)
		throws PortalException, SystemException {

		rolePersistence.setUsers(roleId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Sets the users in the user group, removing and adding users to the user
	 * group as necessary.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void setUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException, SystemException {

		if (PropsValues.USER_GROUPS_COPY_LAYOUTS_TO_USER_PERSONAL_SITE) {
			userGroupLocalService.copyUserGroupLayouts(userGroupId, userIds);
		}

		userGroupPersistence.setUsers(userGroupId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes the users from the teams of a group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetGroupTeamsUsers(long groupId, long[] userIds)
		throws PortalException, SystemException {

		List<Team> teams = teamPersistence.findByGroupId(groupId);

		for (Team team : teams) {
			unsetTeamUsers(team.getTeamId(), userIds);
		}

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes the users from the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userIds the primary keys of the users
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>)
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetGroupUsers(
			final long groupId, final long[] userIds,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		userGroupRoleLocalService.deleteUserGroupRoles(
			userIds, groupId, RoleConstants.TYPE_SITE);

		userLocalService.unsetGroupTeamsUsers(groupId, userIds);

		groupPersistence.removeUsers(groupId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();

		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Message message = new Message();

				message.put("groupId", groupId);
				message.put("userIds", userIds);

				MessageBusUtil.sendMessage(
					DestinationNames.SUBSCRIPTION_CLEAN_UP, message);

				return null;
			}

		};

		TransactionCommitCallbackRegistryUtil.registerCallback(callable);
	}

	/**
	 * Removes the users from the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetOrganizationUsers(
			long organizationId, final long[] userIds)
		throws PortalException, SystemException {

		Organization organization = organizationPersistence.findByPrimaryKey(
			organizationId);

		final Group group = organization.getGroup();

		userGroupRoleLocalService.deleteUserGroupRoles(
			userIds, group.getGroupId());

		organizationPersistence.removeUsers(organizationId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();

		Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Message message = new Message();

				message.put("groupId", group.getGroupId());
				message.put("userIds", userIds);

				MessageBusUtil.sendMessage(
					DestinationNames.SUBSCRIPTION_CLEAN_UP, message);

				return null;
			}

		};

		TransactionCommitCallbackRegistryUtil.registerCallback(callable);
	}

	/**
	 * Removes the users from the password policy.
	 *
	 * @param  passwordPolicyId the primary key of the password policy
	 * @param  userIds the primary keys of the users
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws SystemException {

		passwordPolicyRelLocalService.deletePasswordPolicyRels(
			passwordPolicyId, User.class.getName(), userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  users the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetRoleUsers(long roleId, List<User> users)
		throws PortalException, SystemException {

		Role role = rolePersistence.findByPrimaryKey(roleId);

		String roleName = role.getName();

		if ((roleName.equals(RoleConstants.ADMINISTRATOR) &&
			 (getRoleUsersCount(role.getRoleId()) <= 1)) ||
			roleName.equals(RoleConstants.USER)) {

			return;
		}

		rolePersistence.removeUsers(roleId, users);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(users);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetRoleUsers(long roleId, long[] userIds)
		throws PortalException, SystemException {

		Role role = rolePersistence.findByPrimaryKey(roleId);

		String roleName = role.getName();

		if (roleName.equals(RoleConstants.USER) ||
			(roleName.equals(RoleConstants.ADMINISTRATOR) &&
			 getRoleUsersCount(role.getRoleId()) <= 1)) {

			return;
		}

		rolePersistence.removeUsers(roleId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes the users from the team.
	 *
	 * @param  teamId the primary key of the team
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetTeamUsers(long teamId, long[] userIds)
		throws PortalException, SystemException {

		teamPersistence.removeUsers(teamId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Removes the users from the user group.
	 *
	 * @param  userGroupId the primary key of the user group
	 * @param  userIds the primary keys of the users
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void unsetUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException, SystemException {

		userGroupPersistence.removeUsers(userGroupId, userIds);

		Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(User.class);

		indexer.reindex(userIds);

		PermissionCacheUtil.clearCache();
	}

	/**
	 * Updates whether the user has agreed to the terms of use.
	 *
	 * @param  userId the primary key of the user
	 * @param  agreedToTermsOfUse whether the user has agreet to the terms of
	 *         use
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setAgreedToTermsOfUse(agreedToTermsOfUse);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's asset with the new asset categories and tag names,
	 * removing and adding asset categories and tag names as necessary.
	 *
	 * @param  userId the primary key of the user
	 * @param  user ID the primary key of the user
	 * @param  assetCategoryIds the primary key's of the new asset categories
	 * @param  assetTagNames the new asset tag names
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void updateAsset(
			long userId, User user, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException, SystemException {

		User owner = userPersistence.findByPrimaryKey(userId);

		Company company = companyPersistence.findByPrimaryKey(
			owner.getCompanyId());

		Group companyGroup = company.getGroup();

		assetEntryLocalService.updateEntry(
			userId, companyGroup.getGroupId(), user.getCreateDate(),
			user.getModifiedDate(), User.class.getName(), user.getUserId(),
			user.getUuid(), 0, assetCategoryIds, assetTagNames, false, null,
			null, null, null, user.getFullName(), null, null, null, null, 0, 0,
			null, false);
	}

	/**
	 * Updates the user's creation date.
	 *
	 * @param  userId the primary key of the user
	 * @param  createDate the new creation date
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateCreateDate(long userId, Date createDate)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setCreateDate(createDate);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param  userId the primary key of the user
	 * @param  password the user's password
	 * @param  emailAddress1 the user's new email address
	 * @param  emailAddress2 the user's new email address confirmation
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2)
		throws PortalException, SystemException {

		emailAddress1 = StringUtil.toLowerCase(emailAddress1.trim());
		emailAddress2 = StringUtil.toLowerCase(emailAddress2.trim());

		User user = userPersistence.findByPrimaryKey(userId);

		validateEmailAddress(user, emailAddress1, emailAddress2);

		setEmailAddress(
			user, password, user.getFirstName(), user.getMiddleName(),
			user.getLastName(), emailAddress1);

		userPersistence.update(user);

		Contact contact = user.getContact();

		contact.setEmailAddress(user.getEmailAddress());

		contactPersistence.update(contact);

		return user;
	}

	/**
	 * Updates the user's email address or sends verification email.
	 *
	 * @param  userId the primary key of the user
	 * @param  password the user's password
	 * @param  emailAddress1 the user's new email address
	 * @param  emailAddress2 the user's new email address confirmation
	 * @param  serviceContext the service context to be applied. Must set the
	 *         portal URL, main path, primary key of the layout, remote address,
	 *         remote host, and agent for the user.
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws PortalException, SystemException {

		emailAddress1 = StringUtil.toLowerCase(emailAddress1.trim());
		emailAddress2 = StringUtil.toLowerCase(emailAddress2.trim());

		User user = userPersistence.findByPrimaryKey(userId);

		validateEmailAddress(user, emailAddress1, emailAddress2);

		Company company = companyPersistence.findByPrimaryKey(
			user.getCompanyId());

		if (!company.isStrangersVerify()) {
			setEmailAddress(
				user, password, user.getFirstName(), user.getMiddleName(),
				user.getLastName(), emailAddress1);

			userPersistence.update(user);

			Contact contact = user.getContact();

			contact.setEmailAddress(user.getEmailAddress());

			contactPersistence.update(contact);
		}
		else {
			sendEmailAddressVerification(user, emailAddress1, serviceContext);
		}

		return user;
	}

	/**
	 * Updates whether the user has verified email address.
	 *
	 * @param  userId the primary key of the user
	 * @param  emailAddressVerified whether the user has verified email address
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateEmailAddressVerified(
			long userId, boolean emailAddressVerified)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setEmailAddressVerified(emailAddressVerified);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's Facebook ID.
	 *
	 * @param  userId the primary key of the user
	 * @param  facebookId the user's new Facebook ID
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateFacebookId(long userId, long facebookId)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setFacebookId(facebookId);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Sets the groups the user is in, removing and adding groups as necessary.
	 *
	 * @param  userId the primary key of the user
	 * @param  newGroupIds the primary keys of the groups
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>)
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void updateGroups(
			long userId, long[] newGroupIds, ServiceContext serviceContext)
		throws PortalException, SystemException {

		updateGroups(
			userId, newGroupIds, serviceContext,
			serviceContext.isIndexingEnabled());
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param  creatorUserId the primary key of the creator
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  facebookId the user's facebook ID
	 * @param  openId the user's OpenID
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixId the user's name prefix ID
	 * @param  suffixId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  updateUserInformation whether to update the user's information
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set expando bridge attributes for the
	 *         user.
	 * @return the user
	 * @throws PortalException if the user's information was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateIncompleteUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, long facebookId,
			String openId, Locale locale, String firstName, String middleName,
			String lastName, int prefixId, int suffixId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		if (user.getStatus() != WorkflowConstants.STATUS_INCOMPLETE) {
			throw new PortalException("Invalid user status");
		}

		User defaultUser = getDefaultUser(companyId);

		if (facebookId > 0) {
			autoPassword = false;

			if ((password1 == null) || (password2 == null)) {
				password1 = PwdGenerator.getPassword();
				password2 = password1;
			}

			sendEmail = false;
		}

		if (updateUserInformation) {
			autoScreenName = false;

			if (PrefsPropsUtil.getBoolean(
					companyId,
					PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE)) {

				autoScreenName = true;
			}

			validate(
				companyId, user.getUserId(), autoPassword, password1, password2,
				autoScreenName, screenName, emailAddress, openId, firstName,
				middleName, lastName, null);

			if (!autoPassword) {
				if (Validator.isNull(password1) ||
					Validator.isNull(password2)) {
						throw new UserPasswordException(
							UserPasswordException.PASSWORD_INVALID);
				}
			}

			if (autoScreenName) {
				ScreenNameGenerator screenNameGenerator =
					ScreenNameGeneratorFactory.getInstance();

				try {
					screenName = screenNameGenerator.generate(
						companyId, user.getUserId(), emailAddress);
				}
				catch (Exception e) {
					throw new SystemException(e);
				}
			}

			FullNameGenerator fullNameGenerator =
				FullNameGeneratorFactory.getInstance();

			String fullName = fullNameGenerator.getFullName(
				firstName, middleName, lastName);

			String greeting = LanguageUtil.format(
				locale, "welcome-x", " " + fullName, false);

			if (Validator.isNotNull(password1)) {
				user.setPassword(PasswordEncryptorUtil.encrypt(password1));
				user.setPasswordUnencrypted(password1);
			}

			user.setPasswordEncrypted(true);

			PasswordPolicy passwordPolicy = defaultUser.getPasswordPolicy();

			if ((passwordPolicy != null) && passwordPolicy.isChangeable() &&
				passwordPolicy.isChangeRequired()) {

				user.setPasswordReset(true);
			}
			else {
				user.setPasswordReset(false);
			}

			user.setScreenName(screenName);
			user.setFacebookId(facebookId);
			user.setOpenId(openId);
			user.setLanguageId(locale.toString());
			user.setTimeZoneId(defaultUser.getTimeZoneId());
			user.setGreeting(greeting);
			user.setFirstName(firstName);
			user.setMiddleName(middleName);
			user.setLastName(lastName);
			user.setJobTitle(jobTitle);
			user.setExpandoBridgeAttributes(serviceContext);

			Date birthday = getBirthday(
				birthdayMonth, birthdayDay, birthdayYear);

			Contact contact = user.getContact();

			contact.setFirstName(firstName);
			contact.setMiddleName(middleName);
			contact.setLastName(lastName);
			contact.setPrefixId(prefixId);
			contact.setSuffixId(suffixId);
			contact.setMale(male);
			contact.setBirthday(birthday);
			contact.setJobTitle(jobTitle);

			contactPersistence.update(contact, serviceContext);

			// Indexer

			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(user);
		}

		user.setStatus(WorkflowConstants.STATUS_DRAFT);

		userPersistence.update(user, serviceContext);

		// Workflow

		long workflowUserId = creatorUserId;

		if (workflowUserId == user.getUserId()) {
			workflowUserId = defaultUser.getUserId();
		}

		ServiceContext workflowServiceContext = serviceContext;

		if (workflowServiceContext == null) {
			workflowServiceContext = new ServiceContext();
		}

		workflowServiceContext.setAttribute("autoPassword", autoPassword);
		workflowServiceContext.setAttribute("passwordUnencrypted", password1);
		workflowServiceContext.setAttribute("sendEmail", sendEmail);

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			companyId, workflowUserId, User.class.getName(), user.getUserId(),
			user, workflowServiceContext);

		return getUserByEmailAddress(companyId, emailAddress);
	}

	/**
	 * Updates the user's job title.
	 *
	 * @param  userId the primary key of the user
	 * @param  jobTitle the user's job title
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if a contact could not be found matching the user's contact ID
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateJobTitle(long userId, String jobTitle)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setJobTitle(jobTitle);

		userPersistence.update(user);

		Contact contact = contactPersistence.findByPrimaryKey(
			user.getContactId());

		contact.setJobTitle(jobTitle);

		contactPersistence.update(contact);

		return user;
	}

	/**
	 * Updates the user's last login with the current time and the IP address.
	 *
	 * @param  userId the primary key of the user
	 * @param  loginIP the IP address the user logged in from
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateLastLogin(long userId, String loginIP)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		Date lastLoginDate = user.getLoginDate();

		if (lastLoginDate == null) {
			lastLoginDate = new Date();
		}

		user.setLoginDate(new Date());
		user.setLoginIP(loginIP);
		user.setLastLoginDate(lastLoginDate);
		user.setLastLoginIP(user.getLoginIP());
		user.setFailedLoginAttempts(0);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param  user the user
	 * @param  lockout whether the user is locked out
	 * @return the user
	 * @throws PortalException if a portal exception occurred
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateLockout(User user, boolean lockout)
		throws PortalException, SystemException {

		PasswordPolicy passwordPolicy = user.getPasswordPolicy();

		if ((passwordPolicy == null) || !passwordPolicy.isLockout()) {
			return user;
		}

		Date lockoutDate = null;

		if (lockout) {
			lockoutDate = new Date();
		}

		user.setLockout(lockout);
		user.setLockoutDate(lockoutDate);

		if (!lockout) {
			user.setLastFailedLoginDate(lockoutDate);
			user.setFailedLoginAttempts(0);
		}

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @param  lockout whether the user is locked out
	 * @return the user
	 * @throws PortalException if a user with the email address could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateLockoutByEmailAddress(
			long companyId, String emailAddress, boolean lockout)
		throws PortalException, SystemException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		return updateLockout(user, lockout);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param  userId the primary key of the user
	 * @param  lockout whether the user is locked out
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateLockoutById(long userId, boolean lockout)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		return updateLockout(user, lockout);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @param  lockout whether the user is locked out
	 * @return the user
	 * @throws PortalException if a user with the screen name could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateLockoutByScreenName(
			long companyId, String screenName, boolean lockout)
		throws PortalException, SystemException {

		User user = getUserByScreenName(companyId, screenName);

		return updateLockout(user, lockout);
	}

	/**
	 * Updates the user's modified date.
	 *
	 * @param  userId the primary key of the user
	 * @param  modifiedDate the new modified date
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateModifiedDate(long userId, Date modifiedDate)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setModifiedDate(modifiedDate);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's OpenID.
	 *
	 * @param  userId the primary key of the user
	 * @param  openId the new OpenID
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateOpenId(long userId, String openId)
		throws PortalException, SystemException {

		openId = openId.trim();

		User user = userPersistence.findByPrimaryKey(userId);

		user.setOpenId(openId);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Sets the organizations that the user is in, removing and adding
	 * organizations as necessary.
	 *
	 * @param  userId the primary key of the user
	 * @param  newOrganizationIds the primary keys of the organizations
	 * @param  serviceContext the service context to be applied. Must set
	 *         whether user indexing is enabled.
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void updateOrganizations(
			long userId, long[] newOrganizationIds,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		updateOrganizations(
			userId, newOrganizationIds, serviceContext.isIndexingEnabled());
	}

	/**
	 * Updates the user's password without tracking or validation of the change.
	 *
	 * @param  userId the primary key of the user
	 * @param  password1 the user's new password
	 * @param  password2 the user's new password confirmation
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they log in
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws PortalException, SystemException {

		return updatePassword(
			userId, password1, password2, passwordReset, false);
	}

	/**
	 * Updates the user's password, optionally with tracking and validation of
	 * the change.
	 *
	 * @param  userId the primary key of the user
	 * @param  password1 the user's new password
	 * @param  password2 the user's new password confirmation
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @param  silentUpdate whether the password should be updated without being
	 *         tracked, or validated. Primarily used for password imports.
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset, boolean silentUpdate)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		if (!silentUpdate) {
			validatePassword(user.getCompanyId(), userId, password1, password2);
		}

		String oldEncPwd = user.getPassword();

		if (!user.isPasswordEncrypted()) {
			oldEncPwd = PasswordEncryptorUtil.encrypt(user.getPassword());
		}

		String newEncPwd = PasswordEncryptorUtil.encrypt(password1);

		if (user.hasCompanyMx()) {
			mailService.updatePassword(user.getCompanyId(), userId, password1);
		}

		user.setPassword(newEncPwd);
		user.setPasswordUnencrypted(password1);
		user.setPasswordEncrypted(true);
		user.setPasswordReset(passwordReset);
		user.setPasswordModifiedDate(new Date());
		user.setDigest(StringPool.BLANK);
		user.setGraceLoginCount(0);

		if (!silentUpdate) {
			user.setPasswordModified(true);
		}

		try {
			userPersistence.update(user);
		}
		catch (ModelListenerException mle) {
			String msg = GetterUtil.getString(mle.getCause().getMessage());

			if (LDAPSettingsUtil.isPasswordPolicyEnabled(user.getCompanyId())) {
				String passwordHistory = PrefsPropsUtil.getString(
					user.getCompanyId(), PropsKeys.LDAP_ERROR_PASSWORD_HISTORY);

				if (msg.contains(passwordHistory)) {
					throw new UserPasswordException(
						UserPasswordException.PASSWORD_ALREADY_USED);
				}
			}

			throw new UserPasswordException(
				UserPasswordException.PASSWORD_INVALID);
		}

		if (!silentUpdate) {
			user.setPasswordModified(false);

			passwordTrackerLocalService.trackPassword(userId, oldEncPwd);
		}

		return user;
	}

	/**
	 * Updates the user's password with manually input information. This method
	 * should only be used when performing maintenance.
	 *
	 * @param  userId the primary key of the user
	 * @param  password the user's new password
	 * @param  passwordEncrypted the user's new encrypted password
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @param  passwordModifiedDate the new password modified date
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updatePasswordManually(
			long userId, String password, boolean passwordEncrypted,
			boolean passwordReset, Date passwordModifiedDate)
		throws PortalException, SystemException {

		// This method should only be used to manually massage data

		User user = userPersistence.findByPrimaryKey(userId);

		user.setPassword(password);
		user.setPasswordEncrypted(passwordEncrypted);
		user.setPasswordReset(passwordReset);
		user.setPasswordModifiedDate(passwordModifiedDate);
		user.setDigest(StringPool.BLANK);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates whether the user should be asked to reset their password the next
	 * time they login.
	 *
	 * @param  userId the primary key of the user
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updatePasswordReset(long userId, boolean passwordReset)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		user.setPasswordReset(passwordReset);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's portrait image.
	 *
	 * @param  userId the primary key of the user
	 * @param  bytes the new portrait image data
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the new portrait was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updatePortrait(long userId, byte[] bytes)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		long imageMaxSize = PrefsPropsUtil.getLong(
			PropsKeys.USERS_IMAGE_MAX_SIZE);

		if ((imageMaxSize > 0) &&
			((bytes == null) || (bytes.length > imageMaxSize))) {

			throw new UserPortraitSizeException();
		}

		long portraitId = user.getPortraitId();

		if (portraitId <= 0) {
			portraitId = counterLocalService.increment();

			user.setPortraitId(portraitId);
		}

		try {
			ImageBag imageBag = ImageToolUtil.read(bytes);

			RenderedImage renderedImage = imageBag.getRenderedImage();

			if (renderedImage == null) {
				throw new UserPortraitTypeException();
			}

			renderedImage = ImageToolUtil.scale(
				renderedImage, PropsValues.USERS_IMAGE_MAX_HEIGHT,
				PropsValues.USERS_IMAGE_MAX_WIDTH);

			String contentType = imageBag.getType();

			imageLocalService.updateImage(
				portraitId,
				ImageToolUtil.getBytes(renderedImage, contentType));
		}
		catch (IOException ioe) {
			throw new ImageSizeException(ioe);
		}

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's password reset question and answer.
	 *
	 * @param  userId the primary key of the user
	 * @param  question the user's new password reset question
	 * @param  answer the user's new password reset answer
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the new question or answer were invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateReminderQuery(long userId, String question, String answer)
		throws PortalException, SystemException {

		validateReminderQuery(question, answer);

		User user = userPersistence.findByPrimaryKey(userId);

		user.setReminderQueryQuestion(question);
		user.setReminderQueryAnswer(answer);

		userPersistence.update(user);

		return user;
	}

	/**
	 * Updates the user's screen name.
	 *
	 * @param  userId the primary key of the user
	 * @param  screenName the user's new screen name
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the new screen name was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateScreenName(long userId, String screenName)
		throws PortalException, SystemException {

		// User

		User user = userPersistence.findByPrimaryKey(userId);

		screenName = getLogin(screenName);

		validateScreenName(user.getCompanyId(), userId, screenName);

		if (!StringUtil.equalsIgnoreCase(user.getScreenName(), screenName)) {
			user.setDigest(StringPool.BLANK);
		}

		user.setScreenName(screenName);

		userPersistence.update(user);

		// Group

		Group group = groupLocalService.getUserGroup(
			user.getCompanyId(), userId);

		group.setFriendlyURL(StringPool.SLASH + screenName);

		groupPersistence.update(group);

		return user;
	}

	/**
	 * Updates the user's workflow status.
	 *
	 * @param      userId the primary key of the user
	 * @param      status the user's new workflow status
	 * @return     the user
	 * @throws     PortalException if a user with the primary key could not be
	 *             found
	 * @throws     SystemException if a system exception occurred
	 * @deprecated As of 7.0.0, replaced by {@link #updateStatus(long, int,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public User updateStatus(long userId, int status)
		throws PortalException, SystemException {

		return updateStatus(userId, status, new ServiceContext());
	}

	/**
	 * Updates the user's workflow status.
	 *
	 * @param  userId the primary key of the user
	 * @param  status the user's new workflow status
	 * @param  serviceContext the service context to be applied. You can specify
	 *         an unencrypted custom password (used by an LDAP listener) for the
	 *         user via attribute <code>passwordUnencrypted</code>.
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public User updateStatus(
			long userId, int status, ServiceContext serviceContext)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			(user.getStatus() != WorkflowConstants.STATUS_APPROVED)) {

			validateCompanyMaxUsers(user.getCompanyId());
		}

		String passwordUnencrypted = (String)serviceContext.getAttribute(
			"passwordUnencrypted");

		if (Validator.isNotNull(passwordUnencrypted)) {
			user.setPasswordUnencrypted(passwordUnencrypted);
		}

		user.setStatus(status);

		userPersistence.update(user);

		reindex(user);

		return user;
	}

	/**
	 * Updates the user.
	 *
	 * @param  userId the primary key of the user
	 * @param  oldPassword the user's old password
	 * @param  newPassword1 the user's new password (optionally
	 *         <code>null</code>)
	 * @param  newPassword2 the user's new password confirmation (optionally
	 *         <code>null</code>)
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @param  reminderQueryQuestion the user's new password reset question
	 * @param  reminderQueryAnswer the user's new password reset answer
	 * @param  screenName the user's new screen name
	 * @param  emailAddress the user's new email address
	 * @param  facebookId the user's new Facebook ID
	 * @param  openId the user's new OpenID
	 * @param  languageId the user's new language ID
	 * @param  timeZoneId the user's new time zone ID
	 * @param  greeting the user's new greeting
	 * @param  comments the user's new comments
	 * @param  firstName the user's new first name
	 * @param  middleName the user's new middle name
	 * @param  lastName the user's new last name
	 * @param  prefixId the user's new name prefix ID
	 * @param  suffixId the user's new name suffix ID
	 * @param  male whether user is male
	 * @param  birthdayMonth the user's new birthday month (0-based, meaning 0
	 *         for January)
	 * @param  birthdayDay the user's new birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  smsSn the user's new SMS screen name
	 * @param  aimSn the user's new AIM screen name
	 * @param  facebookSn the user's new Facebook screen name
	 * @param  icqSn the user's new ICQ screen name
	 * @param  jabberSn the user's new Jabber screen name
	 * @param  msnSn the user's new MSN screen name
	 * @param  mySpaceSn the user's new MySpace screen name
	 * @param  skypeSn the user's new Skype screen name
	 * @param  twitterSn the user's new Twitter screen name
	 * @param  ymSn the user's new Yahoo! Messenger screen name
	 * @param  jobTitle the user's new job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the user's roles
	 * @param  userGroupRoles the user user's group roles
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 *         or if the new information was invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	@SuppressWarnings("deprecation")
	public User updateUser(
			long userId, String oldPassword, String newPassword1,
			String newPassword2, boolean passwordReset,
			String reminderQueryQuestion, String reminderQueryAnswer,
			String screenName, String emailAddress, long facebookId,
			String openId, String languageId, String timeZoneId,
			String greeting, String comments, String firstName,
			String middleName, String lastName, int prefixId, int suffixId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String smsSn, String aimSn, String facebookSn, String icqSn,
			String jabberSn, String msnSn, String mySpaceSn, String skypeSn,
			String twitterSn, String ymSn, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		// User

		User user = userPersistence.findByPrimaryKey(userId);
		Company company = companyPersistence.findByPrimaryKey(
			user.getCompanyId());
		String password = oldPassword;
		screenName = getLogin(screenName);
		emailAddress = StringUtil.toLowerCase(emailAddress.trim());
		openId = openId.trim();
		String oldFullName = user.getFullName();
		aimSn = StringUtil.toLowerCase(aimSn.trim());
		facebookSn = StringUtil.toLowerCase(facebookSn.trim());
		icqSn = StringUtil.toLowerCase(icqSn.trim());
		jabberSn = StringUtil.toLowerCase(jabberSn.trim());
		msnSn = StringUtil.toLowerCase(msnSn.trim());
		mySpaceSn = StringUtil.toLowerCase(mySpaceSn.trim());
		skypeSn = StringUtil.toLowerCase(skypeSn.trim());
		twitterSn = StringUtil.toLowerCase(twitterSn.trim());
		ymSn = StringUtil.toLowerCase(ymSn.trim());
		Date now = new Date();

		EmailAddressGenerator emailAddressGenerator =
			EmailAddressGeneratorFactory.getInstance();

		if (emailAddressGenerator.isGenerated(emailAddress)) {
			emailAddress = StringPool.BLANK;
		}

		if (!PropsValues.USERS_EMAIL_ADDRESS_REQUIRED &&
			Validator.isNull(emailAddress)) {

			emailAddress = emailAddressGenerator.generate(
				user.getCompanyId(), userId);
		}

		validate(
			userId, screenName, emailAddress, openId, firstName, middleName,
			lastName, smsSn);

		if (Validator.isNotNull(newPassword1) ||
			Validator.isNotNull(newPassword2)) {

			user = updatePassword(
				userId, newPassword1, newPassword2, passwordReset);

			password = newPassword1;

			user.setDigest(StringPool.BLANK);
		}

		user.setModifiedDate(now);

		if (user.getContactId() <= 0) {
			user.setContactId(counterLocalService.increment());
		}

		user.setPasswordReset(passwordReset);

		if (Validator.isNotNull(reminderQueryQuestion) &&
			Validator.isNotNull(reminderQueryAnswer)) {

			user.setReminderQueryQuestion(reminderQueryQuestion);
			user.setReminderQueryAnswer(reminderQueryAnswer);
		}

		if (!StringUtil.equalsIgnoreCase(user.getScreenName(), screenName)) {
			user.setScreenName(screenName);

			user.setDigest(StringPool.BLANK);
		}

		boolean sendEmailAddressVerification = false;

		if (!company.isStrangersVerify()) {
			setEmailAddress(
				user, password, firstName, middleName, lastName, emailAddress);
		}
		else {
			sendEmailAddressVerification = true;
		}

		if (serviceContext != null) {
			String uuid = serviceContext.getUuid();

			if (Validator.isNotNull(uuid)) {
				user.setUuid(uuid);
			}
		}

		user.setFacebookId(facebookId);

		Long ldapServerId = (Long)serviceContext.getAttribute("ldapServerId");

		if (ldapServerId != null) {
			user.setLdapServerId(ldapServerId);
		}

		user.setOpenId(openId);
		user.setLanguageId(languageId);
		user.setTimeZoneId(timeZoneId);
		user.setGreeting(greeting);
		user.setComments(comments);
		user.setFirstName(firstName);
		user.setMiddleName(middleName);
		user.setLastName(lastName);
		user.setJobTitle(jobTitle);
		user.setExpandoBridgeAttributes(serviceContext);

		userPersistence.update(user, serviceContext);

		// Contact

		Date birthday = getBirthday(birthdayMonth, birthdayDay, birthdayYear);

		long contactId = user.getContactId();

		Contact contact = contactPersistence.fetchByPrimaryKey(contactId);

		if (contact == null) {
			contact = contactPersistence.create(contactId);

			contact.setCompanyId(user.getCompanyId());
			contact.setUserName(StringPool.BLANK);
			contact.setCreateDate(now);
			contact.setClassName(User.class.getName());
			contact.setClassPK(user.getUserId());
			contact.setAccountId(company.getAccountId());
			contact.setParentContactId(
				ContactConstants.DEFAULT_PARENT_CONTACT_ID);
		}

		contact.setModifiedDate(now);
		contact.setEmailAddress(user.getEmailAddress());
		contact.setFirstName(firstName);
		contact.setMiddleName(middleName);
		contact.setLastName(lastName);
		contact.setPrefixId(prefixId);
		contact.setSuffixId(suffixId);
		contact.setMale(male);
		contact.setBirthday(birthday);
		contact.setSmsSn(smsSn);
		contact.setAimSn(aimSn);
		contact.setFacebookSn(facebookSn);
		contact.setIcqSn(icqSn);
		contact.setJabberSn(jabberSn);
		contact.setMsnSn(msnSn);
		contact.setMySpaceSn(mySpaceSn);
		contact.setSkypeSn(skypeSn);
		contact.setTwitterSn(twitterSn);
		contact.setYmSn(ymSn);
		contact.setJobTitle(jobTitle);

		contactPersistence.update(contact, serviceContext);

		// Group

		Group group = groupLocalService.getUserGroup(
			user.getCompanyId(), userId);

		group.setFriendlyURL(StringPool.SLASH + screenName);

		groupPersistence.update(group);

		// Groups and organizations

		// See LPS-33205. Cache the user's list of user group roles because
		// adding or removing groups may add or remove user group roles
		// depending on the site default user associations.

		List<UserGroupRole> previousUserGroupRoles =
			userGroupRolePersistence.findByUserId(userId);

		updateGroups(userId, groupIds, serviceContext, false);
		updateOrganizations(userId, organizationIds, false);

		// Roles

		if (roleIds != null) {
			roleIds = UsersAdminUtil.addRequiredRoles(user, roleIds);

			userPersistence.setRoles(userId, roleIds);
		}

		// User group roles

		updateUserGroupRoles(
			user, groupIds, organizationIds, userGroupRoles,
			previousUserGroupRoles);

		// User groups

		if (userGroupIds != null) {
			if (PropsValues.USER_GROUPS_COPY_LAYOUTS_TO_USER_PERSONAL_SITE) {
				userGroupLocalService.copyUserGroupLayouts(
					userGroupIds, userId);
			}

			userPersistence.setUserGroups(userId, userGroupIds);
		}

		// Announcements

		announcementsDeliveryLocalService.getUserDeliveries(user.getUserId());

		// Asset

		if (serviceContext != null) {
			updateAsset(
				userId, user, serviceContext.getAssetCategoryIds(),
				serviceContext.getAssetTagNames());
		}

		// Message boards

		if (GetterUtil.getBoolean(
				PropsKeys.USERS_UPDATE_USER_NAME + MBMessage.class.getName()) &&
			!oldFullName.equals(user.getFullName())) {

			mbMessageLocalService.updateUserName(userId, user.getFullName());
		}

		// Indexer

		if ((serviceContext == null) || serviceContext.isIndexingEnabled()) {
			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(user);
		}

		// Email address verification

		if ((serviceContext != null) && sendEmailAddressVerification) {
			sendEmailAddressVerification(user, emailAddress, serviceContext);
		}

		// Permission cache

		PermissionCacheUtil.clearCache();

		return user;
	}

	/**
	 * Verifies the email address of the ticket.
	 *
	 * @param  ticketKey the ticket key
	 * @throws PortalException if a ticket matching the ticket key could not be
	 *         found, if the ticket has expired, if the ticket is an email
	 *         address ticket, or if the email address is invalid
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public void verifyEmailAddress(String ticketKey)
		throws PortalException, SystemException {

		Ticket ticket = ticketLocalService.getTicket(ticketKey);

		if (ticket.isExpired() ||
			(ticket.getType() != TicketConstants.TYPE_EMAIL_ADDRESS)) {

			throw new NoSuchTicketException("{ticketKey=" + ticketKey + "}");
		}

		User user = userPersistence.findByPrimaryKey(ticket.getClassPK());

		String emailAddress = ticket.getExtraInfo();

		emailAddress = StringUtil.toLowerCase(emailAddress).trim();

		if (!emailAddress.equals(user.getEmailAddress())) {
			if (userPersistence.fetchByC_EA(
					user.getCompanyId(), emailAddress) != null) {

				throw new DuplicateUserEmailAddressException(
					"{userId=" + user.getUserId() + "}");
			}

			setEmailAddress(
				user, StringPool.BLANK, user.getFirstName(),
				user.getMiddleName(), user.getLastName(), emailAddress);

			Contact contact = user.getContact();

			contact.setEmailAddress(user.getEmailAddress());

			contactPersistence.update(contact);
		}

		user.setEmailAddressVerified(true);

		userPersistence.update(user);

		ticketLocalService.deleteTicket(ticket);
	}

	protected void addDefaultRolesAndTeams(long groupId, long[] userIds)
		throws PortalException, SystemException {

		List<Role> defaultSiteRoles = new ArrayList<Role>();

		Group group = groupLocalService.getGroup(groupId);

		UnicodeProperties typeSettingsProperties =
			group.getTypeSettingsProperties();

		long[] defaultSiteRoleIds = StringUtil.split(
			typeSettingsProperties.getProperty("defaultSiteRoleIds"), 0L);

		for (long defaultSiteRoleId : defaultSiteRoleIds) {
			Role defaultSiteRole = rolePersistence.fetchByPrimaryKey(
				defaultSiteRoleId);

			if (defaultSiteRole == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find role " + defaultSiteRoleId);
				}

				continue;
			}

			defaultSiteRoles.add(defaultSiteRole);
		}

		List<Team> defaultTeams = new ArrayList<Team>();

		long[] defaultTeamIds = StringUtil.split(
			typeSettingsProperties.getProperty("defaultTeamIds"), 0L);

		for (long defaultTeamId : defaultTeamIds) {
			Team defaultTeam = teamPersistence.findByPrimaryKey(defaultTeamId);

			if (defaultTeam == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to find team " + defaultTeamId);
				}

				continue;
			}

			defaultTeams.add(defaultTeam);
		}

		for (long userId : userIds) {
			Set<Long> userRoleIdsSet = new HashSet<Long>();

			for (Role role : defaultSiteRoles) {
				if (!userPersistence.containsRole(userId, role.getRoleId())) {
					userRoleIdsSet.add(role.getRoleId());
				}
			}

			long[] userRoleIds = ArrayUtil.toArray(
				userRoleIdsSet.toArray(new Long[userRoleIdsSet.size()]));

			userGroupRoleLocalService.addUserGroupRoles(
				userId, groupId, userRoleIds);

			Set<Long> userTeamIdsSet = new HashSet<Long>();

			for (Team team : defaultTeams) {
				if (!userPersistence.containsTeam(userId, team.getTeamId())) {
					userTeamIdsSet.add(team.getTeamId());
				}
			}

			long[] userTeamIds = ArrayUtil.toArray(
				userTeamIdsSet.toArray(new Long[userTeamIdsSet.size()]));

			userPersistence.addTeams(userId, userTeamIds);
		}
	}

	/**
	 * Attempts to authenticate the user by their login and password, while
	 * using the AuthPipeline.
	 *
	 * <p>
	 * Authentication type specifies what <code>login</code> contains.The valid
	 * values are:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_EA</code> - <code>login</code> is the
	 * user's email address
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_SN</code> - <code>login</code> is the
	 * user's screen name
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_ID</code> - <code>login</code> is the
	 * user's primary key
	 * </li>
	 * </ul>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  login either the user's email address, screen name, or primary
	 *         key depending on the value of <code>authType</code>
	 * @param  password the user's password
	 * @param  authType the type of authentication to perform
	 * @param  headerMap the header map from the authentication request
	 * @param  parameterMap the parameter map from the authentication request
	 * @param  resultsMap the map of authentication results (may be nil). After
	 *         a succesful authentication the user's primary key will be placed
	 *         under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 *         com.liferay.portal.security.auth.Authenticator#FAILURE}
	 *         indicating that the user's credentials are invalid, {@link
	 *         com.liferay.portal.security.auth.Authenticator#SUCCESS}
	 *         indicating a successful login, or {@link
	 *         com.liferay.portal.security.auth.Authenticator#DNE} indicating
	 *         that a user with that login does not exist.
	 * @throws PortalException if <code>login</code> or <code>password</code>
	 *         was <code>null</code>
	 * @throws SystemException if a system exception occurred
	 * @see    com.liferay.portal.security.auth.AuthPipeline
	 */
	protected int authenticate(
			long companyId, String login, String password, String authType,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException, SystemException {

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			return Authenticator.FAILURE;
		}

		login = StringUtil.toLowerCase(login.trim());

		long userId = GetterUtil.getLong(login);

		// User input validation

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			if (Validator.isNull(login)) {
				throw new UserEmailAddressException();
			}
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			if (Validator.isNull(login)) {
				throw new UserScreenNameException();
			}
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			if (Validator.isNull(login)) {
				throw new UserIdException();
			}
		}

		if (Validator.isNull(password)) {
			throw new UserPasswordException(
				UserPasswordException.PASSWORD_INVALID);
		}

		int authResult = Authenticator.FAILURE;

		// Pre-authentication pipeline

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			authResult = AuthPipeline.authenticateByEmailAddress(
				PropsKeys.AUTH_PIPELINE_PRE, companyId, login, password,
				headerMap, parameterMap);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			authResult = AuthPipeline.authenticateByScreenName(
				PropsKeys.AUTH_PIPELINE_PRE, companyId, login, password,
				headerMap, parameterMap);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			authResult = AuthPipeline.authenticateByUserId(
				PropsKeys.AUTH_PIPELINE_PRE, companyId, userId, password,
				headerMap, parameterMap);
		}

		// Get user

		User user = null;

		if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			user = fetchUserByEmailAddress(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			user = fetchUserByScreenName(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
			user = userPersistence.fetchByPrimaryKey(GetterUtil.getLong(login));
		}

		if (user == null) {
			return Authenticator.DNE;
		}

		if (user.isDefaultUser()) {
			if (_log.isInfoEnabled()) {
				_log.info("Authentication is disabled for the default user");
			}

			return Authenticator.DNE;
		}
		else if (!user.isActive()) {
			if (_log.isInfoEnabled()) {
				_log.info(
					"Authentication is disabled for inactive user " +
						user.getUserId());
			}

			return Authenticator.FAILURE;
		}

		if (!user.isPasswordEncrypted()) {
			user.setPassword(PasswordEncryptorUtil.encrypt(user.getPassword()));
			user.setPasswordEncrypted(true);

			userPersistence.update(user);
		}

		// Check password policy to see if the is account locked out or if the
		// password is expired

		checkLockout(user);

		checkPasswordExpired(user);

		// Authenticate against the User_ table

		boolean skipLiferayCheck = false;

		if (authResult == Authenticator.SKIP_LIFERAY_CHECK) {
			authResult = Authenticator.SUCCESS;

			skipLiferayCheck = true;
		}
		else if ((authResult == Authenticator.SUCCESS) &&
				 PropsValues.AUTH_PIPELINE_ENABLE_LIFERAY_CHECK) {

			boolean authenticated = PwdAuthenticator.authenticate(
				login, password, user.getPassword());

			if (authenticated) {
				authResult = Authenticator.SUCCESS;
			}
			else {
				authResult = Authenticator.FAILURE;
			}
		}

		// Post-authentication pipeline

		if (authResult == Authenticator.SUCCESS) {
			if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
				authResult = AuthPipeline.authenticateByEmailAddress(
					PropsKeys.AUTH_PIPELINE_POST, companyId, login, password,
					headerMap, parameterMap);
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
				authResult = AuthPipeline.authenticateByScreenName(
					PropsKeys.AUTH_PIPELINE_POST, companyId, login, password,
					headerMap, parameterMap);
			}
			else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
				authResult = AuthPipeline.authenticateByUserId(
					PropsKeys.AUTH_PIPELINE_POST, companyId, userId, password,
					headerMap, parameterMap);
			}
		}

		if (authResult == Authenticator.SUCCESS) {
			if (resultsMap != null) {
				resultsMap.put("userId", user.getUserId());
			}

			// Update digest

			if (skipLiferayCheck ||
				!PropsValues.AUTH_PIPELINE_ENABLE_LIFERAY_CHECK ||
				Validator.isNull(user.getDigest())) {

				String digest = user.getDigest(password);

				user.setDigest(digest);

				userPersistence.update(user);
			}
		}

		// Execute code triggered by authentication failure

		if (authResult == Authenticator.FAILURE) {
			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					AuthPipeline.onFailureByEmailAddress(
						PropsKeys.AUTH_FAILURE, companyId, login, headerMap,
						parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					AuthPipeline.onFailureByScreenName(
						PropsKeys.AUTH_FAILURE, companyId, login, headerMap,
						parameterMap);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
					AuthPipeline.onFailureByUserId(
						PropsKeys.AUTH_FAILURE, companyId, userId, headerMap,
						parameterMap);
				}

				user = userPersistence.fetchByPrimaryKey(user.getUserId());

				if (user == null) {
					return Authenticator.DNE;
				}

				// Let LDAP handle max failure event

				if (!LDAPSettingsUtil.isPasswordPolicyEnabled(
						user.getCompanyId())) {

					PasswordPolicy passwordPolicy = user.getPasswordPolicy();

					user = userPersistence.fetchByPrimaryKey(user.getUserId());

					int failedLoginAttempts = user.getFailedLoginAttempts();
					int maxFailures = passwordPolicy.getMaxFailure();

					if ((failedLoginAttempts >= maxFailures) &&
						(maxFailures != 0)) {

						if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
							AuthPipeline.onMaxFailuresByEmailAddress(
								PropsKeys.AUTH_MAX_FAILURES, companyId, login,
								headerMap, parameterMap);
						}
						else if (authType.equals(
									CompanyConstants.AUTH_TYPE_SN)) {

							AuthPipeline.onMaxFailuresByScreenName(
								PropsKeys.AUTH_MAX_FAILURES, companyId, login,
								headerMap, parameterMap);
						}
						else if (authType.equals(
									CompanyConstants.AUTH_TYPE_ID)) {

							AuthPipeline.onMaxFailuresByUserId(
								PropsKeys.AUTH_MAX_FAILURES, companyId, userId,
								headerMap, parameterMap);
						}
					}
				}
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		// PLACEHOLDER 02

		return authResult;
	}

	protected Date getBirthday(
			int birthdayMonth, int birthdayDay, int birthdayYear)
		throws PortalException {

		Date birthday = PortalUtil.getDate(
			birthdayMonth, birthdayDay, birthdayYear,
			ContactBirthdayException.class);

		Date now = new Date();

		if (birthday.after(now)) {
			throw new ContactBirthdayException();
		}

		return birthday;
	}

	protected String getLogin(String login) {
		return StringUtil.lowerCase(StringUtil.trim(login));
	}

	protected long[] getUserIds(List<User> users) {
		long[] userIds = new long[users.size()];

		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);

			userIds[i] = user.getUserId();
		}

		return userIds;
	}

	protected void reindex(final User user) {
		final Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			User.class);

		Callable<Void> callable = new ShardCallable<Void>(
			user.getCompanyId()) {

			@Override
			protected Void doCall() throws Exception {
				indexer.reindex(user);

				return null;
			}

		};

		TransactionCommitCallbackRegistryUtil.registerCallback(callable);
	}

	protected Hits search(
			long companyId, String firstName, String middleName,
			String lastName, String fullName, String screenName,
			String emailAddress, String street, String city, String zip,
			String region, String country, int status,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort sort)
		throws SystemException {

		try {
			SearchContext searchContext = new SearchContext();

			searchContext.setAndSearch(andSearch);

			Map<String, Serializable> attributes =
				new HashMap<String, Serializable>();

			attributes.put("city", city);
			attributes.put("country", country);
			attributes.put("emailAddress", emailAddress);
			attributes.put("firstName", firstName);
			attributes.put("fullName", fullName);
			attributes.put("lastName", lastName);
			attributes.put("middleName", middleName);
			attributes.put("params", params);
			attributes.put("region", region);
			attributes.put("screenName", screenName);
			attributes.put("street", street);
			attributes.put("status", status);
			attributes.put("zip", zip);

			searchContext.setAttributes(attributes);

			searchContext.setCompanyId(companyId);
			searchContext.setEnd(end);

			if (params != null) {
				String keywords = (String)params.remove("keywords");

				if (Validator.isNotNull(keywords)) {
					searchContext.setKeywords(keywords);
				}
			}

			QueryConfig queryConfig = new QueryConfig();

			queryConfig.setHighlightEnabled(false);
			queryConfig.setScoreEnabled(false);

			searchContext.setQueryConfig(queryConfig);

			if (sort != null) {
				searchContext.setSorts(sort);
			}

			searchContext.setStart(start);

			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			return indexer.search(searchContext);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected void sendEmail(
			User user, String password, ServiceContext serviceContext)
		throws SystemException {

		if (!PrefsPropsUtil.getBoolean(
				user.getCompanyId(),
				PropsKeys.ADMIN_EMAIL_USER_ADDED_ENABLED)) {

			return;
		}

		String fromName = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_NAME);
		String fromAddress = PrefsPropsUtil.getString(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);

		String toName = user.getFullName();
		String toAddress = user.getEmailAddress();

		String subject = PrefsPropsUtil.getContent(
			user.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_SUBJECT);

		String body = null;

		if (Validator.isNotNull(password)) {
			body = PrefsPropsUtil.getContent(
				user.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_BODY);
		}
		else {
			body = PrefsPropsUtil.getContent(
				user.getCompanyId(),
				PropsKeys.ADMIN_EMAIL_USER_ADDED_NO_PASSWORD_BODY);
		}

		SubscriptionSender subscriptionSender = new SubscriptionSender();

		subscriptionSender.setBody(body);
		subscriptionSender.setCompanyId(user.getCompanyId());
		subscriptionSender.setContextAttributes(
			"[$USER_ID$]", user.getUserId(), "[$USER_PASSWORD$]", password,
			"[$USER_SCREENNAME$]", user.getScreenName());
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);
		subscriptionSender.setMailId(
			"user", user.getUserId(), System.currentTimeMillis(),
			PwdGenerator.getPassword());
		subscriptionSender.setServiceContext(serviceContext);
		subscriptionSender.setSubject(subject);
		subscriptionSender.setUserId(user.getUserId());

		subscriptionSender.addRuntimeSubscribers(toAddress, toName);

		subscriptionSender.flushNotificationsAsync();
	}

	protected void setEmailAddress(
			User user, String password, String firstName, String middleName,
			String lastName, String emailAddress)
		throws PortalException, SystemException {

		if (StringUtil.equalsIgnoreCase(emailAddress, user.getEmailAddress())) {
			return;
		}

		long userId = user.getUserId();

		// test@test.com -> test@liferay.com

		if (!user.hasCompanyMx() && user.hasCompanyMx(emailAddress) &&
			Validator.isNotNull(password)) {

			mailService.addUser(
				user.getCompanyId(), userId, password, firstName, middleName,
				lastName, emailAddress);
		}

		// test@liferay.com -> bob@liferay.com

		else if (user.hasCompanyMx() && user.hasCompanyMx(emailAddress)) {
			mailService.updateEmailAddress(
				user.getCompanyId(), userId, emailAddress);
		}

		// test@liferay.com -> test@test.com

		else if (user.hasCompanyMx() && !user.hasCompanyMx(emailAddress)) {
			mailService.deleteEmailAddress(user.getCompanyId(), userId);
		}

		user.setEmailAddress(emailAddress);
		user.setDigest(StringPool.BLANK);
	}

	protected void updateGroups(
			long userId, long[] newGroupIds, ServiceContext serviceContext,
			boolean indexingEnabled)
		throws PortalException, SystemException {

		if (newGroupIds == null) {
			return;
		}

		List<Group> oldGroups = userPersistence.getGroups(userId);

		Set<Long> oldGroupIds = new HashSet<Long>(oldGroups.size());

		for (Group oldGroup : oldGroups) {
			long oldGroupId = oldGroup.getGroupId();

			oldGroupIds.add(oldGroupId);

			if (!ArrayUtil.contains(newGroupIds, oldGroupId)) {
				unsetGroupUsers(
					oldGroupId, new long[] {userId}, serviceContext);
			}
		}

		for (long newGroupId : newGroupIds) {
			if (!oldGroupIds.contains(newGroupId)) {
				addGroupUsers(newGroupId, new long[] {userId});
			}
		}

		if (indexingEnabled) {
			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(new long[] {userId});
		}

		PermissionCacheUtil.clearCache();
	}

	protected void updateOrganizations(
			long userId, long[] newOrganizationIds, boolean indexingEnabled)
		throws PortalException, SystemException {

		if (newOrganizationIds == null) {
			return;
		}

		List<Organization> oldOrganizations = userPersistence.getOrganizations(
			userId);

		Set<Long> oldOrganizationIds = new HashSet<Long>(
			oldOrganizations.size());

		for (Organization oldOrganization : oldOrganizations) {
			long oldOrganizationId = oldOrganization.getOrganizationId();

			oldOrganizationIds.add(oldOrganizationId);

			if (!ArrayUtil.contains(newOrganizationIds, oldOrganizationId)) {
				unsetOrganizationUsers(oldOrganizationId, new long[] {userId});
			}
		}

		for (long newOrganizationId : newOrganizationIds) {
			if (!oldOrganizationIds.contains(newOrganizationId)) {
				addOrganizationUsers(newOrganizationId, new long[] {userId});
			}
		}

		if (indexingEnabled) {
			Indexer indexer = IndexerRegistryUtil.nullSafeGetIndexer(
				User.class);

			indexer.reindex(new long[] {userId});
		}

		PermissionCacheUtil.clearCache();
	}

	protected void updateUserGroupRoles(
			User user, long[] groupIds, long[] organizationIds,
			List<UserGroupRole> userGroupRoles,
			List<UserGroupRole> previousUserGroupRoles)
		throws PortalException, SystemException {

		if (userGroupRoles == null) {
			return;
		}

		userGroupRoles = new ArrayList<UserGroupRole>(userGroupRoles);

		for (UserGroupRole userGroupRole : previousUserGroupRoles) {
			if (userGroupRoles.contains(userGroupRole)) {
				userGroupRoles.remove(userGroupRole);
			}
			else {
				userGroupRoleLocalService.deleteUserGroupRole(userGroupRole);
			}
		}

		if (userGroupRoles.isEmpty()) {
			return;
		}

		long[] validGroupIds = null;

		if (groupIds != null) {
			validGroupIds = ArrayUtil.clone(groupIds);
		}
		else {
			validGroupIds = user.getGroupIds();
		}

		if (organizationIds == null) {
			organizationIds = user.getOrganizationIds();
		}

		long[] organizationGroupIds = new long[organizationIds.length];

		for (int i = 0; i < organizationIds.length; i++) {
			long organizationId = organizationIds[i];

			Organization organization =
				organizationPersistence.findByPrimaryKey(organizationId);

			organizationGroupIds[i] = organization.getGroupId();
		}

		validGroupIds = ArrayUtil.append(validGroupIds, organizationGroupIds);

		Arrays.sort(validGroupIds);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			if (Arrays.binarySearch(
					validGroupIds, userGroupRole.getGroupId()) >= 0) {

				userGroupRoleLocalService.addUserGroupRole(userGroupRole);
			}
		}
	}

	protected void validate(
			long companyId, long userId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, String openId, String firstName,
			String middleName, String lastName, long[] organizationIds)
		throws PortalException, SystemException {

		validateCompanyMaxUsers(companyId);

		if (!autoScreenName) {
			validateScreenName(companyId, userId, screenName);
		}

		if (!autoPassword) {
			PasswordPolicy passwordPolicy =
				passwordPolicyLocalService.getDefaultPasswordPolicy(companyId);

			PwdToolkitUtil.validate(
				companyId, 0, password1, password2, passwordPolicy);
		}

		validateEmailAddress(companyId, emailAddress);

		if (Validator.isNotNull(emailAddress)) {
			User user = userPersistence.fetchByC_EA(companyId, emailAddress);

			if ((user != null) && (user.getUserId() != userId)) {
				throw new DuplicateUserEmailAddressException(
					"{userId=" + userId + "}");
			}
		}

		validateOpenId(companyId, userId, openId);

		validateFullName(companyId, firstName, middleName, lastName);

		if (organizationIds != null) {
			for (long organizationId : organizationIds) {
				Organization organization =
					organizationPersistence.fetchByPrimaryKey(organizationId);

				if (organization == null) {
					throw new NoSuchOrganizationException(
						"{organizationId=" + organizationId + "}");
				}
			}
		}
	}

	protected void validate(
			long userId, String screenName, String emailAddress, String openId,
			String firstName, String middleName, String lastName, String smsSn)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		if (!StringUtil.equalsIgnoreCase(user.getScreenName(), screenName)) {
			validateScreenName(user.getCompanyId(), userId, screenName);
		}

		validateEmailAddress(user.getCompanyId(), emailAddress);

		validateOpenId(user.getCompanyId(), userId, openId);

		if (!user.isDefaultUser()) {
			if (Validator.isNotNull(emailAddress) &&
				!StringUtil.equalsIgnoreCase(
					user.getEmailAddress(), emailAddress)) {

				if (userPersistence.fetchByC_EA(
						user.getCompanyId(), emailAddress) != null) {

					throw new DuplicateUserEmailAddressException(
						"{userId=" + userId + "}");
				}
			}

			validateFullName(
				user.getCompanyId(), firstName, middleName, lastName);
		}

		if (Validator.isNotNull(smsSn) && !Validator.isEmailAddress(smsSn)) {
			throw new UserSmsException();
		}
	}

	protected void validateCompanyMaxUsers(long companyId)
		throws PortalException, SystemException {

		Company company = companyPersistence.findByPrimaryKey(companyId);

		if (company.isSystem() || (company.getMaxUsers() == 0)) {
			return;
		}

		int userCount = searchCount(
			companyId, null, WorkflowConstants.STATUS_APPROVED, null);

		if (userCount >= company.getMaxUsers()) {
			throw new CompanyMaxUsersException();
		}
	}

	protected void validateEmailAddress(long companyId, String emailAddress)
		throws PortalException, SystemException {

		if (Validator.isNull(emailAddress) &&
			!PropsValues.USERS_EMAIL_ADDRESS_REQUIRED) {

			return;
		}

		EmailAddressValidator emailAddressValidator =
			EmailAddressValidatorFactory.getInstance();

		if (!emailAddressValidator.validate(companyId, emailAddress)) {
			throw new UserEmailAddressException();
		}

		String pop3User = PrefsPropsUtil.getString(
			PropsKeys.MAIL_SESSION_MAIL_POP3_USER,
			PropsValues.MAIL_SESSION_MAIL_POP3_USER);

		if (StringUtil.equalsIgnoreCase(emailAddress, pop3User)) {
			throw new ReservedUserEmailAddressException();
		}

		String[] reservedEmailAddresses = PrefsPropsUtil.getStringArray(
			companyId, PropsKeys.ADMIN_RESERVED_EMAIL_ADDRESSES,
			StringPool.NEW_LINE, PropsValues.ADMIN_RESERVED_EMAIL_ADDRESSES);

		for (String reservedEmailAddress : reservedEmailAddresses) {
			if (StringUtil.equalsIgnoreCase(
					emailAddress, reservedEmailAddress)) {

				throw new ReservedUserEmailAddressException();
			}
		}
	}

	protected void validateEmailAddress(
			User user, String emailAddress1, String emailAddress2)
		throws PortalException, SystemException {

		if (!emailAddress1.equals(emailAddress2)) {
			throw new UserEmailAddressException();
		}

		validateEmailAddress(user.getCompanyId(), emailAddress1);
		validateEmailAddress(user.getCompanyId(), emailAddress2);

		if (!StringUtil.equalsIgnoreCase(
				emailAddress1, user.getEmailAddress())) {

			if (userPersistence.fetchByC_EA(
					user.getCompanyId(), emailAddress1) != null) {

				throw new DuplicateUserEmailAddressException(
					"{userId=" + user.getUserId() + "}");
			}
		}
	}

	protected void validateFullName(
			long companyId, String firstName, String middleName,
			String lastName)
		throws PortalException, SystemException {

		if (Validator.isNull(firstName)) {
			throw new ContactFirstNameException();
		}
		else if (Validator.isNull(lastName) &&
				 PrefsPropsUtil.getBoolean(
					 companyId, PropsKeys.USERS_LAST_NAME_REQUIRED,
					 PropsValues.USERS_LAST_NAME_REQUIRED)) {

			throw new ContactLastNameException();
		}

		FullNameValidator fullNameValidator =
			FullNameValidatorFactory.getInstance();

		if (!fullNameValidator.validate(
				companyId, firstName, middleName, lastName)) {

			throw new ContactFullNameException();
		}
	}

	protected void validateOpenId(long companyId, long userId, String openId)
		throws PortalException, SystemException {

		if (Validator.isNull(openId)) {
			return;
		}

		User user = userPersistence.fetchByC_O(companyId, openId);

		if ((user != null) && (user.getUserId() != userId)) {
			throw new DuplicateOpenIdException("{userId=" + userId + "}");
		}
	}

	protected void validatePassword(
			long companyId, long userId, String password1, String password2)
		throws PortalException, SystemException {

		if (Validator.isNull(password1) || Validator.isNull(password2)) {
			throw new UserPasswordException(
				UserPasswordException.PASSWORD_INVALID);
		}

		if (!password1.equals(password2)) {
			throw new UserPasswordException(
				UserPasswordException.PASSWORDS_DO_NOT_MATCH);
		}

		PasswordPolicy passwordPolicy =
			passwordPolicyLocalService.getPasswordPolicyByUserId(userId);

		PwdToolkitUtil.validate(
			companyId, userId, password1, password2, passwordPolicy);
	}

	protected void validateReminderQuery(String question, String answer)
		throws PortalException {

		if (!PropsValues.USERS_REMINDER_QUERIES_ENABLED) {
			return;
		}

		if (Validator.isNull(question)) {
			throw new UserReminderQueryException("Question is null");
		}

		if (Validator.isNull(answer)) {
			throw new UserReminderQueryException("Answer is null");
		}
	}

	protected void validateScreenName(
			long companyId, long userId, String screenName)
		throws PortalException, SystemException {

		if (Validator.isNull(screenName)) {
			throw new UserScreenNameException();
		}

		ScreenNameValidator screenNameValidator =
			ScreenNameValidatorFactory.getInstance();

		if (!screenNameValidator.validate(companyId, screenName)) {
			throw new UserScreenNameException();
		}

		if (Validator.isNumber(screenName)) {
			if (!PropsValues.USERS_SCREEN_NAME_ALLOW_NUMERIC) {
				throw new UserScreenNameException();
			}

			if (!screenName.equals(String.valueOf(userId))) {
				Group group = groupPersistence.fetchByPrimaryKey(
					GetterUtil.getLong(screenName));

				if (group != null) {
					throw new UserScreenNameException();
				}
			}
		}

		for (char c : screenName.toCharArray()) {
			if (!Validator.isChar(c) && !Validator.isDigit(c) &&
				(c != CharPool.DASH) && (c != CharPool.PERIOD) &&
				(c != CharPool.UNDERLINE)) {

				throw new UserScreenNameException();
			}
		}

		String[] anonymousNames = BaseServiceImpl.ANONYMOUS_NAMES;

		for (String anonymousName : anonymousNames) {
			if (StringUtil.equalsIgnoreCase(screenName, anonymousName)) {
				throw new UserScreenNameException();
			}
		}

		User user = userPersistence.fetchByC_SN(companyId, screenName);

		if ((user != null) && (user.getUserId() != userId)) {
			throw new DuplicateUserScreenNameException(
				"{userId=" + userId + "}");
		}

		String friendlyURL = StringPool.SLASH + screenName;

		Group group = groupPersistence.fetchByC_F(companyId, friendlyURL);

		if ((group != null) && (group.getClassPK() != userId)) {
			throw new GroupFriendlyURLException(
				GroupFriendlyURLException.DUPLICATE);
		}

		int exceptionType = LayoutImpl.validateFriendlyURL(friendlyURL);

		if (exceptionType != -1) {
			throw new UserScreenNameException(
				new GroupFriendlyURLException(exceptionType));
		}

		String[] reservedScreenNames = PrefsPropsUtil.getStringArray(
			companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
			StringPool.NEW_LINE, PropsValues.ADMIN_RESERVED_SCREEN_NAMES);

		for (String reservedScreenName : reservedScreenNames) {
			if (StringUtil.equalsIgnoreCase(screenName, reservedScreenName)) {
				throw new ReservedUserScreenNameException();
			}
		}
	}

	private static Log _log = LogFactoryUtil.getLog(UserLocalServiceImpl.class);

	private Map<Long, User> _defaultUsers = new ConcurrentHashMap<Long, User>();

}