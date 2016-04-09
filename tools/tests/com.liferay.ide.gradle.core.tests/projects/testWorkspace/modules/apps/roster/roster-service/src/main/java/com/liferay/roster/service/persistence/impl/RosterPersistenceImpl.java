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

import com.liferay.roster.exception.NoSuchRosterException;
import com.liferay.roster.model.Roster;
import com.liferay.roster.model.impl.RosterImpl;
import com.liferay.roster.model.impl.RosterModelImpl;
import com.liferay.roster.service.persistence.RosterPersistence;

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
 * The persistence implementation for the roster service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RosterPersistence
 * @see com.liferay.roster.service.persistence.RosterUtil
 * @generated
 */
@ProviderType
public class RosterPersistenceImpl extends BasePersistenceImpl<Roster>
	implements RosterPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link RosterUtil} to access the roster persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = RosterImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] { String.class.getName() },
			RosterModelImpl.UUID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_UUID = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] { String.class.getName() });

	/**
	 * Returns all the rosters where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching rosters
	 */
	@Override
	public List<Roster> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the rosters where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @return the range of matching rosters
	 */
	@Override
	public List<Roster> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the rosters where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rosters
	 */
	@Override
	public List<Roster> findByUuid(String uuid, int start, int end,
		OrderByComparator<Roster> orderByComparator) {
		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the rosters where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching rosters
	 */
	@Override
	public List<Roster> findByUuid(String uuid, int start, int end,
		OrderByComparator<Roster> orderByComparator, boolean retrieveFromCache) {
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

		List<Roster> list = null;

		if (retrieveFromCache) {
			list = (List<Roster>)finderCache.getResult(finderPath, finderArgs,
					this);

			if ((list != null) && !list.isEmpty()) {
				for (Roster roster : list) {
					if (!Validator.equals(uuid, roster.getUuid())) {
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

			query.append(_SQL_SELECT_ROSTER_WHERE);

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
				query.append(RosterModelImpl.ORDER_BY_JPQL);
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
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end);
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
	 * Returns the first roster in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster
	 * @throws NoSuchRosterException if a matching roster could not be found
	 */
	@Override
	public Roster findByUuid_First(String uuid,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = fetchByUuid_First(uuid, orderByComparator);

		if (roster != null) {
			return roster;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterException(msg.toString());
	}

	/**
	 * Returns the first roster in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster, or <code>null</code> if a matching roster could not be found
	 */
	@Override
	public Roster fetchByUuid_First(String uuid,
		OrderByComparator<Roster> orderByComparator) {
		List<Roster> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last roster in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster
	 * @throws NoSuchRosterException if a matching roster could not be found
	 */
	@Override
	public Roster findByUuid_Last(String uuid,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = fetchByUuid_Last(uuid, orderByComparator);

		if (roster != null) {
			return roster;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterException(msg.toString());
	}

	/**
	 * Returns the last roster in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster, or <code>null</code> if a matching roster could not be found
	 */
	@Override
	public Roster fetchByUuid_Last(String uuid,
		OrderByComparator<Roster> orderByComparator) {
		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Roster> list = findByUuid(uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the rosters before and after the current roster in the ordered set where uuid = &#63;.
	 *
	 * @param rosterId the primary key of the current roster
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next roster
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster[] findByUuid_PrevAndNext(long rosterId, String uuid,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = findByPrimaryKey(rosterId);

		Session session = null;

		try {
			session = openSession();

			Roster[] array = new RosterImpl[3];

			array[0] = getByUuid_PrevAndNext(session, roster, uuid,
					orderByComparator, true);

			array[1] = roster;

			array[2] = getByUuid_PrevAndNext(session, roster, uuid,
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

	protected Roster getByUuid_PrevAndNext(Session session, Roster roster,
		String uuid, OrderByComparator<Roster> orderByComparator,
		boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_ROSTER_WHERE);

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
			query.append(RosterModelImpl.ORDER_BY_JPQL);
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
			Object[] values = orderByComparator.getOrderByConditionValues(roster);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<Roster> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the rosters where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Roster roster : findByUuid(uuid, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)) {
			remove(roster);
		}
	}

	/**
	 * Returns the number of rosters where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching rosters
	 */
	@Override
	public int countByUuid(String uuid) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID;

		Object[] finderArgs = new Object[] { uuid };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_ROSTER_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_1 = "roster.uuid IS NULL";
	private static final String _FINDER_COLUMN_UUID_UUID_2 = "roster.uuid = ?";
	private static final String _FINDER_COLUMN_UUID_UUID_3 = "(roster.uuid IS NULL OR roster.uuid = '')";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_CLUBID = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClubId",
			new String[] {
				Long.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_CLUBID =
		new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, RosterImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByClubId",
			new String[] { Long.class.getName() },
			RosterModelImpl.CLUBID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_CLUBID = new FinderPath(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByClubId",
			new String[] { Long.class.getName() });

	/**
	 * Returns all the rosters where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @return the matching rosters
	 */
	@Override
	public List<Roster> findByClubId(long clubId) {
		return findByClubId(clubId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the rosters where clubId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param clubId the club ID
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @return the range of matching rosters
	 */
	@Override
	public List<Roster> findByClubId(long clubId, int start, int end) {
		return findByClubId(clubId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the rosters where clubId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param clubId the club ID
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rosters
	 */
	@Override
	public List<Roster> findByClubId(long clubId, int start, int end,
		OrderByComparator<Roster> orderByComparator) {
		return findByClubId(clubId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the rosters where clubId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param clubId the club ID
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching rosters
	 */
	@Override
	public List<Roster> findByClubId(long clubId, int start, int end,
		OrderByComparator<Roster> orderByComparator, boolean retrieveFromCache) {
		boolean pagination = true;
		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {
			pagination = false;
			finderPath = FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_CLUBID;
			finderArgs = new Object[] { clubId };
		}
		else {
			finderPath = FINDER_PATH_WITH_PAGINATION_FIND_BY_CLUBID;
			finderArgs = new Object[] { clubId, start, end, orderByComparator };
		}

		List<Roster> list = null;

		if (retrieveFromCache) {
			list = (List<Roster>)finderCache.getResult(finderPath, finderArgs,
					this);

			if ((list != null) && !list.isEmpty()) {
				for (Roster roster : list) {
					if ((clubId != roster.getClubId())) {
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

			query.append(_SQL_SELECT_ROSTER_WHERE);

			query.append(_FINDER_COLUMN_CLUBID_CLUBID_2);

			if (orderByComparator != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);
			}
			else
			 if (pagination) {
				query.append(RosterModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(clubId);

				if (!pagination) {
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end);
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
	 * Returns the first roster in the ordered set where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster
	 * @throws NoSuchRosterException if a matching roster could not be found
	 */
	@Override
	public Roster findByClubId_First(long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = fetchByClubId_First(clubId, orderByComparator);

		if (roster != null) {
			return roster;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("clubId=");
		msg.append(clubId);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterException(msg.toString());
	}

	/**
	 * Returns the first roster in the ordered set where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching roster, or <code>null</code> if a matching roster could not be found
	 */
	@Override
	public Roster fetchByClubId_First(long clubId,
		OrderByComparator<Roster> orderByComparator) {
		List<Roster> list = findByClubId(clubId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last roster in the ordered set where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster
	 * @throws NoSuchRosterException if a matching roster could not be found
	 */
	@Override
	public Roster findByClubId_Last(long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = fetchByClubId_Last(clubId, orderByComparator);

		if (roster != null) {
			return roster;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("clubId=");
		msg.append(clubId);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchRosterException(msg.toString());
	}

	/**
	 * Returns the last roster in the ordered set where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching roster, or <code>null</code> if a matching roster could not be found
	 */
	@Override
	public Roster fetchByClubId_Last(long clubId,
		OrderByComparator<Roster> orderByComparator) {
		int count = countByClubId(clubId);

		if (count == 0) {
			return null;
		}

		List<Roster> list = findByClubId(clubId, count - 1, count,
				orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the rosters before and after the current roster in the ordered set where clubId = &#63;.
	 *
	 * @param rosterId the primary key of the current roster
	 * @param clubId the club ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next roster
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster[] findByClubId_PrevAndNext(long rosterId, long clubId,
		OrderByComparator<Roster> orderByComparator)
		throws NoSuchRosterException {
		Roster roster = findByPrimaryKey(rosterId);

		Session session = null;

		try {
			session = openSession();

			Roster[] array = new RosterImpl[3];

			array[0] = getByClubId_PrevAndNext(session, roster, clubId,
					orderByComparator, true);

			array[1] = roster;

			array[2] = getByClubId_PrevAndNext(session, roster, clubId,
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

	protected Roster getByClubId_PrevAndNext(Session session, Roster roster,
		long clubId, OrderByComparator<Roster> orderByComparator,
		boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_ROSTER_WHERE);

		query.append(_FINDER_COLUMN_CLUBID_CLUBID_2);

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
			query.append(RosterModelImpl.ORDER_BY_JPQL);
		}

		String sql = query.toString();

		Query q = session.createQuery(sql);

		q.setFirstResult(0);
		q.setMaxResults(2);

		QueryPos qPos = QueryPos.getInstance(q);

		qPos.add(clubId);

		if (orderByComparator != null) {
			Object[] values = orderByComparator.getOrderByConditionValues(roster);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<Roster> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the rosters where clubId = &#63; from the database.
	 *
	 * @param clubId the club ID
	 */
	@Override
	public void removeByClubId(long clubId) {
		for (Roster roster : findByClubId(clubId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null)) {
			remove(roster);
		}
	}

	/**
	 * Returns the number of rosters where clubId = &#63;.
	 *
	 * @param clubId the club ID
	 * @return the number of matching rosters
	 */
	@Override
	public int countByClubId(long clubId) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_CLUBID;

		Object[] finderArgs = new Object[] { clubId };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_ROSTER_WHERE);

			query.append(_FINDER_COLUMN_CLUBID_CLUBID_2);

			String sql = query.toString();

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(clubId);

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

	private static final String _FINDER_COLUMN_CLUBID_CLUBID_2 = "roster.clubId = ?";

	public RosterPersistenceImpl() {
		setModelClass(Roster.class);
	}

	/**
	 * Caches the roster in the entity cache if it is enabled.
	 *
	 * @param roster the roster
	 */
	@Override
	public void cacheResult(Roster roster) {
		entityCache.putResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterImpl.class, roster.getPrimaryKey(), roster);

		roster.resetOriginalValues();
	}

	/**
	 * Caches the rosters in the entity cache if it is enabled.
	 *
	 * @param rosters the rosters
	 */
	@Override
	public void cacheResult(List<Roster> rosters) {
		for (Roster roster : rosters) {
			if (entityCache.getResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
						RosterImpl.class, roster.getPrimaryKey()) == null) {
				cacheResult(roster);
			}
			else {
				roster.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all rosters.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(RosterImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the roster.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Roster roster) {
		entityCache.removeResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterImpl.class, roster.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<Roster> rosters) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Roster roster : rosters) {
			entityCache.removeResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
				RosterImpl.class, roster.getPrimaryKey());
		}
	}

	/**
	 * Creates a new roster with the primary key. Does not add the roster to the database.
	 *
	 * @param rosterId the primary key for the new roster
	 * @return the new roster
	 */
	@Override
	public Roster create(long rosterId) {
		Roster roster = new RosterImpl();

		roster.setNew(true);
		roster.setPrimaryKey(rosterId);

		String uuid = PortalUUIDUtil.generate();

		roster.setUuid(uuid);

		return roster;
	}

	/**
	 * Removes the roster with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rosterId the primary key of the roster
	 * @return the roster that was removed
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster remove(long rosterId) throws NoSuchRosterException {
		return remove((Serializable)rosterId);
	}

	/**
	 * Removes the roster with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the roster
	 * @return the roster that was removed
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster remove(Serializable primaryKey) throws NoSuchRosterException {
		Session session = null;

		try {
			session = openSession();

			Roster roster = (Roster)session.get(RosterImpl.class, primaryKey);

			if (roster == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchRosterException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(roster);
		}
		catch (NoSuchRosterException nsee) {
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
	protected Roster removeImpl(Roster roster) {
		roster = toUnwrappedModel(roster);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(roster)) {
				roster = (Roster)session.get(RosterImpl.class,
						roster.getPrimaryKeyObj());
			}

			if (roster != null) {
				session.delete(roster);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (roster != null) {
			clearCache(roster);
		}

		return roster;
	}

	@Override
	public Roster updateImpl(Roster roster) {
		roster = toUnwrappedModel(roster);

		boolean isNew = roster.isNew();

		RosterModelImpl rosterModelImpl = (RosterModelImpl)roster;

		if (Validator.isNull(roster.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			roster.setUuid(uuid);
		}

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (roster.getCreateDate() == null)) {
			if (serviceContext == null) {
				roster.setCreateDate(now);
			}
			else {
				roster.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!rosterModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				roster.setModifiedDate(now);
			}
			else {
				roster.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (roster.isNew()) {
				session.save(roster);

				roster.setNew(false);
			}
			else {
				roster = (Roster)session.merge(roster);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !RosterModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		else {
			if ((rosterModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] { rosterModelImpl.getOriginalUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);

				args = new Object[] { rosterModelImpl.getUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);
			}

			if ((rosterModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_CLUBID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] { rosterModelImpl.getOriginalClubId() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_CLUBID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_CLUBID,
					args);

				args = new Object[] { rosterModelImpl.getClubId() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_CLUBID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_CLUBID,
					args);
			}
		}

		entityCache.putResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
			RosterImpl.class, roster.getPrimaryKey(), roster, false);

		roster.resetOriginalValues();

		return roster;
	}

	protected Roster toUnwrappedModel(Roster roster) {
		if (roster instanceof RosterImpl) {
			return roster;
		}

		RosterImpl rosterImpl = new RosterImpl();

		rosterImpl.setNew(roster.isNew());
		rosterImpl.setPrimaryKey(roster.getPrimaryKey());

		rosterImpl.setUuid(roster.getUuid());
		rosterImpl.setRosterId(roster.getRosterId());
		rosterImpl.setCreateDate(roster.getCreateDate());
		rosterImpl.setModifiedDate(roster.getModifiedDate());
		rosterImpl.setClubId(roster.getClubId());
		rosterImpl.setName(roster.getName());

		return rosterImpl;
	}

	/**
	 * Returns the roster with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the roster
	 * @return the roster
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster findByPrimaryKey(Serializable primaryKey)
		throws NoSuchRosterException {
		Roster roster = fetchByPrimaryKey(primaryKey);

		if (roster == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchRosterException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return roster;
	}

	/**
	 * Returns the roster with the primary key or throws a {@link NoSuchRosterException} if it could not be found.
	 *
	 * @param rosterId the primary key of the roster
	 * @return the roster
	 * @throws NoSuchRosterException if a roster with the primary key could not be found
	 */
	@Override
	public Roster findByPrimaryKey(long rosterId) throws NoSuchRosterException {
		return findByPrimaryKey((Serializable)rosterId);
	}

	/**
	 * Returns the roster with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the roster
	 * @return the roster, or <code>null</code> if a roster with the primary key could not be found
	 */
	@Override
	public Roster fetchByPrimaryKey(Serializable primaryKey) {
		Roster roster = (Roster)entityCache.getResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
				RosterImpl.class, primaryKey);

		if (roster == _nullRoster) {
			return null;
		}

		if (roster == null) {
			Session session = null;

			try {
				session = openSession();

				roster = (Roster)session.get(RosterImpl.class, primaryKey);

				if (roster != null) {
					cacheResult(roster);
				}
				else {
					entityCache.putResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
						RosterImpl.class, primaryKey, _nullRoster);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
					RosterImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return roster;
	}

	/**
	 * Returns the roster with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rosterId the primary key of the roster
	 * @return the roster, or <code>null</code> if a roster with the primary key could not be found
	 */
	@Override
	public Roster fetchByPrimaryKey(long rosterId) {
		return fetchByPrimaryKey((Serializable)rosterId);
	}

	@Override
	public Map<Serializable, Roster> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Roster> map = new HashMap<Serializable, Roster>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Roster roster = fetchByPrimaryKey(primaryKey);

			if (roster != null) {
				map.put(primaryKey, roster);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Roster roster = (Roster)entityCache.getResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
					RosterImpl.class, primaryKey);

			if (roster == null) {
				if (uncachedPrimaryKeys == null) {
					uncachedPrimaryKeys = new HashSet<Serializable>();
				}

				uncachedPrimaryKeys.add(primaryKey);
			}
			else {
				map.put(primaryKey, roster);
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_ROSTER_WHERE_PKS_IN);

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

			for (Roster roster : (List<Roster>)q.list()) {
				map.put(roster.getPrimaryKeyObj(), roster);

				cacheResult(roster);

				uncachedPrimaryKeys.remove(roster.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(RosterModelImpl.ENTITY_CACHE_ENABLED,
					RosterImpl.class, primaryKey, _nullRoster);
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
	 * Returns all the rosters.
	 *
	 * @return the rosters
	 */
	@Override
	public List<Roster> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the rosters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @return the range of rosters
	 */
	@Override
	public List<Roster> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the rosters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of rosters
	 */
	@Override
	public List<Roster> findAll(int start, int end,
		OrderByComparator<Roster> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the rosters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link RosterModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of rosters
	 * @param end the upper bound of the range of rosters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of rosters
	 */
	@Override
	public List<Roster> findAll(int start, int end,
		OrderByComparator<Roster> orderByComparator, boolean retrieveFromCache) {
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

		List<Roster> list = null;

		if (retrieveFromCache) {
			list = (List<Roster>)finderCache.getResult(finderPath, finderArgs,
					this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_ROSTER);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_ROSTER;

				if (pagination) {
					sql = sql.concat(RosterModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Roster>)QueryUtil.list(q, getDialect(), start,
							end);
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
	 * Removes all the rosters from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Roster roster : findAll()) {
			remove(roster);
		}
	}

	/**
	 * Returns the number of rosters.
	 *
	 * @return the number of rosters
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_ROSTER);

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
		return RosterModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the roster persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(RosterImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_ROSTER = "SELECT roster FROM Roster roster";
	private static final String _SQL_SELECT_ROSTER_WHERE_PKS_IN = "SELECT roster FROM Roster roster WHERE rosterId IN (";
	private static final String _SQL_SELECT_ROSTER_WHERE = "SELECT roster FROM Roster roster WHERE ";
	private static final String _SQL_COUNT_ROSTER = "SELECT COUNT(roster) FROM Roster roster";
	private static final String _SQL_COUNT_ROSTER_WHERE = "SELECT COUNT(roster) FROM Roster roster WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "roster.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No Roster exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No Roster exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(RosterPersistenceImpl.class);
	private static final Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"uuid"
			});
	private static final Roster _nullRoster = new RosterImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<Roster> toCacheModel() {
				return _nullRosterCacheModel;
			}
		};

	private static final CacheModel<Roster> _nullRosterCacheModel = new CacheModel<Roster>() {
			@Override
			public Roster toEntityModel() {
				return _nullRoster;
			}
		};
}