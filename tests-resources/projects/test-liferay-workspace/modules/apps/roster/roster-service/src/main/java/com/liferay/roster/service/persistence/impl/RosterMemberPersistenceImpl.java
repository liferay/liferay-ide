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

package com.liferay.roster.service.persistence.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;

import com.liferay.roster.exception.NoSuchRosterMemberException;
import com.liferay.roster.model.RosterMember;
import com.liferay.roster.model.impl.RosterMemberImpl;
import com.liferay.roster.model.impl.RosterMemberModelImpl;
import com.liferay.roster.service.persistence.RosterMemberPersistence;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the roster member service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterMemberPersistence
 * @see com.liferay.roster.service.persistence.RosterMemberUtil
 * @generated
 */
@ProviderType
public class RosterMemberPersistenceImpl extends BasePersistenceImpl<RosterMember>
	implements RosterMemberPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link RosterMemberUtil} to access the roster member persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = RosterMemberImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] { String.class.getName() },
			RosterMemberModelImpl.UUID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_UUID = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] { String.class.getName() });

	/**
	 * Returns all the roster members where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching roster members
	 */
	@Override
	public List<RosterMember> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the roster members where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @return the range of matching roster members
	 */
	@Override
	public List<RosterMember> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the roster members where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roster members
	 */
	@Override
	public List<RosterMember> findByUuid(String uuid, int start, int end,
		OrderByComparator<RosterMember> orderByComparator) {
		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the roster members where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching roster members
	 */
	@Override
	public List<RosterMember> findByUuid(String uuid, int start, int end,
		OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID;
			finderArgs = new Object[] { uuid };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID;
			finderArgs = new Object[] { uuid, start, end, orderByComparator };
		}

		List<RosterMember> list = null;

		if (retrieveFromCache) {
			list = (List<RosterMember>)finderCache.getResult(finderPath,
					finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RosterMember rosterMember : list) {
					if (!Validator.equals(uuid, rosterMember.getUuid())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(3 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_ROSTERMEMBER_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_UUID_1);
			}
			else if (uuid.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(RosterMemberModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				if (!pagination) {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first roster member in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster member
	 * @throws NoSuchRosterMemberException if a matching roster member could not be found
	 */
	@Override
	public RosterMember findByUuid_First(String uuid,
		OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = fetchByUuid_First(uuid, orderByComparator);

		if (rosterMember != null) {
			return rosterMember;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterMemberException(msg.toString());
	}

	/**
	 * Returns the first roster member in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	 */
	@Override
	public RosterMember fetchByUuid_First(String uuid,
		OrderByComparator<RosterMember> orderByComparator) {
		List<RosterMember> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last roster member in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster member
	 * @throws NoSuchRosterMemberException if a matching roster member could not be found
	 */
	@Override
	public RosterMember findByUuid_Last(String uuid,
		OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = fetchByUuid_Last(uuid, orderByComparator);

		if (rosterMember != null) {
			return rosterMember;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterMemberException(msg.toString());
	}

	/**
	 * Returns the last roster member in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	 */
	@Override
	public RosterMember fetchByUuid_Last(String uuid,
		OrderByComparator<RosterMember> orderByComparator) {
		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<RosterMember> list = findByUuid(uuid, count - 1, count,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the roster members before and after the current roster member in the ordered set where uuid = &#63;.
	 *
	 * @param rosterMemberId the primary key of the current roster member
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next roster member
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember[] findByUuid_PrevAndNext(long rosterMemberId,
		String uuid, OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = findByPrimaryKey(rosterMemberId);

		Session session = null;

		try {
			session = openSession();

			RosterMember[] array = new RosterMemberImpl[3];

			array[0] = getByUuid_PrevAndNext(session, rosterMember, uuid,
					orderByComparator, true);

			array[1] = rosterMember;

			array[2] = getByUuid_PrevAndNext(session, rosterMember, uuid,
					orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected RosterMember getByUuid_PrevAndNext(Session session,
		RosterMember rosterMember, String uuid,
		OrderByComparator<RosterMember> orderByComparator, boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_ROSTERMEMBER_WHERE);

		boolean bindUuid = false;

		if (uuid == null) {
			query.append(_FINDER_COLUMN_UUID_UUID_1);
		}
		else if (uuid.equals(StringPool.BLANK)) {
			query.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			query.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(RosterMemberModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		if (bindUuid) {
			qPos.add(uuid);
		}

		if (orderByComparator != null) {
			Object[] values = orderByComparator.getOrderByConditionValues(rosterMember);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<RosterMember> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the roster members where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (RosterMember rosterMember : findByUuid(uuid, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)) {
			remove(rosterMember);
		}
	}

	/**
	 * Returns the number of roster members where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching roster members
	 */
	@Override
	public int countByUuid(String uuid) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID;

		Object[] finderArgs = new Object[] { uuid };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_ROSTERMEMBER_WHERE);

			boolean bindUuid = false;

			if (uuid == null) {
				query.append(_FINDER_COLUMN_UUID_UUID_1);
			}
			else if (uuid.equals(StringPool.BLANK)) {
				query.append(_FINDER_COLUMN_UUID_UUID_3);
			}
			else {
				bindUuid = true;

				query.append(_FINDER_COLUMN_UUID_UUID_2);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (bindUuid) {
					qPos.add(uuid);
				}

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_UUID_UUID_1 = "rosterMember.uuid IS NULL";
	private static final String _FINDER_COLUMN_UUID_UUID_2 = "rosterMember.uuid = ?";
	private static final String _FINDER_COLUMN_UUID_UUID_3 = "(rosterMember.uuid IS NULL OR rosterMember.uuid = '')";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_ROSTERID = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRosterId",
			new String[] {
				Long.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_ROSTERID =
		new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, RosterMemberImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRosterId",
			new String[] { Long.class.getName() },
			RosterMemberModelImpl.ROSTERID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_ROSTERID = new FinderPath(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRosterId",
			new String[] { Long.class.getName() });

	/**
	 * Returns all the roster members where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @return the matching roster members
	 */
	@Override
	public List<RosterMember> findByRosterId(long rosterId) {
		return findByRosterId(rosterId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the roster members where rosterId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param rosterId the roster ID
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @return the range of matching roster members
	 */
	@Override
	public List<RosterMember> findByRosterId(long rosterId, int start, int end) {
		return findByRosterId(rosterId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the roster members where rosterId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param rosterId the roster ID
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roster members
	 */
	@Override
	public List<RosterMember> findByRosterId(long rosterId, int start, int end,
		OrderByComparator<RosterMember> orderByComparator) {
		return findByRosterId(rosterId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the roster members where rosterId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param rosterId the roster ID
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching roster members
	 */
	@Override
	public List<RosterMember> findByRosterId(long rosterId, int start, int end,
		OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_ROSTERID;
			finderArgs = new Object[] { rosterId };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_ROSTERID;
			finderArgs = new Object[] { rosterId, start, end, orderByComparator };
		}

		List<RosterMember> list = null;

		if (retrieveFromCache) {
			list = (List<RosterMember>)finderCache.getResult(finderPath,
					finderArgs, this);

			if ((list != null) && !list.isEmpty()) {
				for (RosterMember rosterMember : list) {
					if ((rosterId != rosterMember.getRosterId())) {
						list = null;

						break;
					}
				}
			}
		}

		if (list == null) {
			StringBundler query = null;

			if (orderByComparator != null) {
				query = new StringBundler(3 +
						(orderByComparator.getOrderByFields().length * 2));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_ROSTERMEMBER_WHERE);

			query.append(_FINDER_COLUMN_ROSTERID_ROSTERID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(RosterMemberModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(rosterId);

				if (!pagination) {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Returns the first roster member in the ordered set where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster member
	 * @throws NoSuchRosterMemberException if a matching roster member could not be found
	 */
	@Override
	public RosterMember findByRosterId_First(long rosterId,
		OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = fetchByRosterId_First(rosterId,
				orderByComparator);

		if (rosterMember != null) {
			return rosterMember;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("rosterId=");
		msg.append(rosterId);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterMemberException(msg.toString());
	}

	/**
	 * Returns the first roster member in the ordered set where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster member, or <code>null</code> if a matching roster member could not be found
	 */
	@Override
	public RosterMember fetchByRosterId_First(long rosterId,
		OrderByComparator<RosterMember> orderByComparator) {
		List<RosterMember> list = findByRosterId(rosterId, 0, 1,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last roster member in the ordered set where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster member
	 * @throws NoSuchRosterMemberException if a matching roster member could not be found
	 */
	@Override
	public RosterMember findByRosterId_Last(long rosterId,
		OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = fetchByRosterId_Last(rosterId,
				orderByComparator);

		if (rosterMember != null) {
			return rosterMember;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("rosterId=");
		msg.append(rosterId);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterMemberException(msg.toString());
	}

	/**
	 * Returns the last roster member in the ordered set where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster member, or <code>null</code> if a matching roster member could not be found
	 */
	@Override
	public RosterMember fetchByRosterId_Last(long rosterId,
		OrderByComparator<RosterMember> orderByComparator) {
		int count = countByRosterId(rosterId);

		if (count == 0) {
			return null;
		}

		List<RosterMember> list = findByRosterId(rosterId, count - 1, count,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the roster members before and after the current roster member in the ordered set where rosterId = &#63;.
	 *
	 * @param rosterMemberId the primary key of the current roster member
	 * @param rosterId the roster ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next roster member
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember[] findByRosterId_PrevAndNext(long rosterMemberId,
		long rosterId, OrderByComparator<RosterMember> orderByComparator)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = findByPrimaryKey(rosterMemberId);

		Session session = null;

		try {
			session = openSession();

			RosterMember[] array = new RosterMemberImpl[3];

			array[0] = getByRosterId_PrevAndNext(session, rosterMember,
					rosterId, orderByComparator, true);

			array[1] = rosterMember;

			array[2] = getByRosterId_PrevAndNext(session, rosterMember,
					rosterId, orderByComparator, false);

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected RosterMember getByRosterId_PrevAndNext(Session session,
		RosterMember rosterMember, long rosterId,
		OrderByComparator<RosterMember> orderByComparator, boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_ROSTERMEMBER_WHERE);

		query.append(_FINDER_COLUMN_ROSTERID_ROSTERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields = orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				query.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						query.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(WHERE_GREATER_THAN);
					}
					else {
						query.append(WHERE_LESSER_THAN);
					}
				}
			}

			query.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				query.append(_ORDER_BY_ENTITY_ALIAS);
				query.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						query.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						query.append(ORDER_BY_ASC);
					}
					else {
						query.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			query.append(RosterMemberModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		qPos.add(rosterId);

		if (orderByComparator != null) {
			Object[] values = orderByComparator.getOrderByConditionValues(rosterMember);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<RosterMember> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the roster members where rosterId = &#63; from the database.
	 *
	 * @param rosterId the roster ID
	 */
	@Override
	public void removeByRosterId(long rosterId) {
		for (RosterMember rosterMember : findByRosterId(rosterId,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {
			remove(rosterMember);
		}
	}

	/**
	 * Returns the number of roster members where rosterId = &#63;.
	 *
	 * @param rosterId the roster ID
	 * @return the number of matching roster members
	 */
	@Override
	public int countByRosterId(long rosterId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_ROSTERID;

		Object[] finderArgs = new Object[] { rosterId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_ROSTERMEMBER_WHERE);

			query.append(_FINDER_COLUMN_ROSTERID_ROSTERID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(rosterId);

				count = (Long)q.uniqueResult();

				finderCache.putResult(finderPath, finderArgs, count);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_ROSTERID_ROSTERID_2 = "rosterMember.rosterId = ?";

	public RosterMemberPersistenceImpl() {
		setModelClass(RosterMember.class);
	}

	/**
	 * Caches the roster member in the entity cache if it is enabled.
	 *
	 * @param rosterMember the roster member
	 */
	@Override
	public void cacheResult(RosterMember rosterMember) {
		entityCache.putResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberImpl.class, rosterMember.getPrimaryKey(), rosterMember);

		rosterMember.resetOriginalValues();
	}

	/**
	 * Caches the roster members in the entity cache if it is enabled.
	 *
	 * @param rosterMembers the roster members
	 */
	@Override
	public void cacheResult(List<RosterMember> rosterMembers) {
		for (RosterMember rosterMember : rosterMembers) {
			if (entityCache.getResult(
						RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
						RosterMemberImpl.class, rosterMember.getPrimaryKey()) == null) {
				cacheResult(rosterMember);
			}
			else {
				rosterMember.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all roster members.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(RosterMemberImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the roster member.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(RosterMember rosterMember) {
		entityCache.removeResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberImpl.class, rosterMember.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<RosterMember> rosterMembers) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (RosterMember rosterMember : rosterMembers) {
			entityCache.removeResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
				RosterMemberImpl.class, rosterMember.getPrimaryKey());
		}
	}

	/**
	 * Creates a new roster member with the primary key. Does not add the roster member to the database.
	 *
	 * @param rosterMemberId the primary key for the new roster member
	 * @return the new roster member
	 */
	@Override
	public RosterMember create(long rosterMemberId) {
		RosterMember rosterMember = new RosterMemberImpl();

		rosterMember.setNew(true);
		rosterMember.setPrimaryKey(rosterMemberId);

		String uuid = PortalUUIDUtil.generate();

		rosterMember.setUuid(uuid);

		return rosterMember;
	}

	/**
	 * Removes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rosterMemberId the primary key of the roster member
	 * @return the roster member that was removed
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember remove(long rosterMemberId)
		throws NoSuchRosterMemberException {
		return remove((Serializable)rosterMemberId);
	}

	/**
	 * Removes the roster member with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the roster member
	 * @return the roster member that was removed
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember remove(Serializable primaryKey)
		throws NoSuchRosterMemberException {
		Session session = null;

		try {
			session = openSession();

			RosterMember rosterMember = (RosterMember)session.get(RosterMemberImpl.class,
					primaryKey);

			if (rosterMember == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchRosterMemberException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(rosterMember);
		}
		catch (NoSuchRosterMemberException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected RosterMember removeImpl(RosterMember rosterMember) {
		rosterMember = toUnwrappedModel(rosterMember);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(rosterMember)) {
				rosterMember = (RosterMember)session.get(RosterMemberImpl.class,
						rosterMember.getPrimaryKeyObj());
			}

			if (rosterMember != null) {
				session.delete(rosterMember);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (rosterMember != null) {
			clearCache(rosterMember);
		}

		return rosterMember;
	}

	@Override
	public RosterMember updateImpl(RosterMember rosterMember) {
		rosterMember = toUnwrappedModel(rosterMember);

		boolean isNew = rosterMember.isNew();

		RosterMemberModelImpl rosterMemberModelImpl = (RosterMemberModelImpl)rosterMember;

		if (Validator.isNull(rosterMember.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			rosterMember.setUuid(uuid);
		}

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (rosterMember.getCreateDate() == null)) {
			if (serviceContext == null) {
				rosterMember.setCreateDate(now);
			}
			else {
				rosterMember.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!rosterMemberModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				rosterMember.setModifiedDate(now);
			}
			else {
				rosterMember.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (rosterMember.isNew()) {
				session.save(rosterMember);

				rosterMember.setNew(false);
			}
			else {
				rosterMember = (RosterMember)session.merge(rosterMember);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !RosterMemberModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		else {
			if ((rosterMemberModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						rosterMemberModelImpl.getOriginalUuid()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);

				args = new Object[] { rosterMemberModelImpl.getUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);
			}

			if ((rosterMemberModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_ROSTERID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] {
						rosterMemberModelImpl.getOriginalRosterId()
					};

				finderCache.removeResult(FINDER_PATH_COUNT_BY_ROSTERID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_ROSTERID,
					args);

				args = new Object[] { rosterMemberModelImpl.getRosterId() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_ROSTERID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_ROSTERID,
					args);
			}
		}

		entityCache.putResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
			RosterMemberImpl.class, rosterMember.getPrimaryKey(), rosterMember,
			false);

		rosterMember.resetOriginalValues();

		return rosterMember;
	}

	protected RosterMember toUnwrappedModel(RosterMember rosterMember) {
		if (rosterMember instanceof RosterMemberImpl) {
			return rosterMember;
		}

		RosterMemberImpl rosterMemberImpl = new RosterMemberImpl();

		rosterMemberImpl.setNew(rosterMember.isNew());
		rosterMemberImpl.setPrimaryKey(rosterMember.getPrimaryKey());

		rosterMemberImpl.setUuid(rosterMember.getUuid());
		rosterMemberImpl.setRosterMemberId(rosterMember.getRosterMemberId());
		rosterMemberImpl.setCreateDate(rosterMember.getCreateDate());
		rosterMemberImpl.setModifiedDate(rosterMember.getModifiedDate());
		rosterMemberImpl.setRosterId(rosterMember.getRosterId());
		rosterMemberImpl.setContactId(rosterMember.getContactId());

		return rosterMemberImpl;
	}

	/**
	 * Returns the roster member with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the roster member
	 * @return the roster member
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember findByPrimaryKey(Serializable primaryKey)
		throws NoSuchRosterMemberException {
		RosterMember rosterMember = fetchByPrimaryKey(primaryKey);

		if (rosterMember == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchRosterMemberException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return rosterMember;
	}

	/**
	 * Returns the roster member with the primary key or throws a {@link NoSuchRosterMemberException} if it could not be found.
	 *
	 * @param rosterMemberId the primary key of the roster member
	 * @return the roster member
	 * @throws NoSuchRosterMemberException if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember findByPrimaryKey(long rosterMemberId)
		throws NoSuchRosterMemberException {
		return findByPrimaryKey((Serializable)rosterMemberId);
	}

	/**
	 * Returns the roster member with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the roster member
	 * @return the roster member, or <code>null</code> if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember fetchByPrimaryKey(Serializable primaryKey) {
		RosterMember rosterMember = (RosterMember)entityCache.getResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
				RosterMemberImpl.class, primaryKey);

		if (rosterMember == _nullRosterMember) {
			return null;
		}

		if (rosterMember == null) {
			Session session = null;

			try {
				session = openSession();

				rosterMember = (RosterMember)session.get(RosterMemberImpl.class,
						primaryKey);

				if (rosterMember != null) {
					cacheResult(rosterMember);
				}
				else {
					entityCache.putResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
						RosterMemberImpl.class, primaryKey, _nullRosterMember);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
					RosterMemberImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return rosterMember;
	}

	/**
	 * Returns the roster member with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rosterMemberId the primary key of the roster member
	 * @return the roster member, or <code>null</code> if a roster member with the primary key could not be found
	 */
	@Override
	public RosterMember fetchByPrimaryKey(long rosterMemberId) {
		return fetchByPrimaryKey((Serializable)rosterMemberId);
	}

	@Override
	public Map<Serializable, RosterMember> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, RosterMember> map = new HashMap<Serializable, RosterMember>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			RosterMember rosterMember = fetchByPrimaryKey(primaryKey);

			if (rosterMember != null) {
				map.put(primaryKey, rosterMember);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			RosterMember rosterMember = (RosterMember)entityCache.getResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
					RosterMemberImpl.class, primaryKey);

			if (rosterMember == null) {
				if (uncachedPrimaryKeys == null) {
					uncachedPrimaryKeys = new HashSet<Serializable>();
				}

				uncachedPrimaryKeys.add(primaryKey);
			}
			else {
				map.put(primaryKey, rosterMember);
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_ROSTERMEMBER_WHERE_PKS_IN);

		for (Serializable primaryKey : uncachedPrimaryKeys) {
			query.append(String.valueOf(primaryKey));

			query.append(StringPool.COMMA);
		}

		query.setIndex(query.index() - 1);

		query.append(StringPool.CLOSE_PARENTHESIS);

		String sql = query.toString();

		Session session = null;

		try {
			session = openSession();

			Query q = session.createQuery(sql);

			for (RosterMember rosterMember : (List<RosterMember>)q.list()) {
				map.put(rosterMember.getPrimaryKeyObj(), rosterMember);

				cacheResult(rosterMember);

				uncachedPrimaryKeys.remove(rosterMember.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(RosterMemberModelImpl.ENTITY_CACHE_ENABLED,
					RosterMemberImpl.class, primaryKey, _nullRosterMember);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the roster members.
	 *
	 * @return the roster members
	 */
	@Override
	public List<RosterMember> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the roster members.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @return the range of roster members
	 */
	@Override
	public List<RosterMember> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the roster members.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of roster members
	 */
	@Override
	public List<RosterMember> findAll(int start, int end,
		OrderByComparator<RosterMember> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the roster members.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterMemberModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of roster members
	 * @param end the upper bound of the range of roster members (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of roster members
	 */
	@Override
	public List<RosterMember> findAll(int start, int end,
		OrderByComparator<RosterMember> orderByComparator,
		boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL;
			finderArgs = FINDER_ARGS_EMPTY;
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_ALL;
			finderArgs = new Object[] { start, end, orderByComparator };
		}

		List<RosterMember> list = null;

		if (retrieveFromCache) {
			list = (List<RosterMember>)finderCache.getResult(finderPath,
					finderArgs, this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_ROSTERMEMBER);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_ROSTERMEMBER;

				if (pagination) {
					sql = sql.concat(RosterMemberModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<RosterMember>)QueryUtil.list(q, getDialect(),
							start, end);
				}

				cacheResult(list);

				finderCache.putResult(finderPath, finderArgs, list);
			}
			catch (Exception e) {
				finderCache.removeResult(finderPath, finderArgs);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the roster members from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (RosterMember rosterMember : findAll()) {
			remove(rosterMember);
		}
	}

	/**
	 * Returns the number of roster members.
	 *
	 * @return the number of roster members
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_ROSTERMEMBER);

				count = (Long)q.uniqueResult();

				finderCache.putResult(FINDER_PATH_COUNT_ALL, FINDER_ARGS_EMPTY,
					count);
			}
			catch (Exception e) {
				finderCache.removeResult(FINDER_PATH_COUNT_ALL,
					FINDER_ARGS_EMPTY);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return RosterMemberModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the roster member persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(RosterMemberImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_ROSTERMEMBER = "SELECT rosterMember FROM RosterMember rosterMember";
	private static final String _SQL_SELECT_ROSTERMEMBER_WHERE_PKS_IN = "SELECT rosterMember FROM RosterMember rosterMember WHERE rosterMemberId IN (";
	private static final String _SQL_SELECT_ROSTERMEMBER_WHERE = "SELECT rosterMember FROM RosterMember rosterMember WHERE ";
	private static final String _SQL_COUNT_ROSTERMEMBER = "SELECT COUNT(rosterMember) FROM RosterMember rosterMember";
	private static final String _SQL_COUNT_ROSTERMEMBER_WHERE = "SELECT COUNT(rosterMember) FROM RosterMember rosterMember WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "rosterMember.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No RosterMember exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No RosterMember exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(RosterMemberPersistenceImpl.class);
	private static final Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"uuid"
			});
	private static final RosterMember _nullRosterMember = new RosterMemberImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<RosterMember> toCacheModel() {
				return _nullRosterMemberCacheModel;
			}
		};

	private static final CacheModel<RosterMember> _nullRosterMemberCacheModel = new CacheModel<RosterMember>() {
			@Override
			public RosterMember toEntityModel() {
				return _nullRosterMember;
			}
		};
}