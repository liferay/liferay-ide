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

import com.liferay.roster.exception.NoSuchClubException;
import com.liferay.roster.model.Club;
import com.liferay.roster.model.impl.ClubImpl;
import com.liferay.roster.model.impl.ClubModelImpl;
import com.liferay.roster.service.persistence.ClubPersistence;

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
 * The persistence implementation for the club service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ClubPersistence
 * @see com.liferay.roster.service.persistence.ClubUtil
 * @generated
 */
@ProviderType
public class ClubPersistenceImpl extends BasePersistenceImpl<Club>
	implements ClubPersistence {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link ClubUtil} to access the club persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY = ClubImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List1";
	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION = FINDER_CLASS_NAME_ENTITY +
		".List2";
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_ALL = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, ClubImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_ALL = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, ClubImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll", new String[0]);
	public static final FinderPath FINDER_PATH_WITH_PAGINATION_FIND_BY_UUID = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, ClubImpl.class,
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(),
				
			Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			});
	public static final FinderPath FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, ClubImpl.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] { String.class.getName() },
			ClubModelImpl.UUID_COLUMN_BITMASK);
	public static final FinderPath FINDER_PATH_COUNT_BY_UUID = new FinderPath(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubModelImpl.FINDER_CACHE_ENABLED, Long.class,
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] { String.class.getName() });

	/**
	 * Returns all the clubs where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching clubs
	 */
	@Override
	public List<Club> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the clubs where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @return the range of matching clubs
	 */
	@Override
	public List<Club> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the clubs where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching clubs
	 */
	@Override
	public List<Club> findByUuid(String uuid, int start, int end,
		OrderByComparator<Club> orderByComparator) {
		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the clubs where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of matching clubs
	 */
	@Override
	public List<Club> findByUuid(String uuid, int start, int end,
		OrderByComparator<Club> orderByComparator, boolean retrieveFromCache) {
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

		List<Club> list = null;

		if (retrieveFromCache) {
			list = (List<Club>)finderCache.getResult(finderPath, finderArgs,
					this);

			if ((list != null) && !list.isEmpty()) {
				for (Club club : list) {
					if (!Validator.equals(uuid, club.getUuid())) {
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

			query.append(_SQL_SELECT_CLUB_WHERE);

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
				query.append(ClubModelImpl.ORDER_BY_JPQL);
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
					list = (List<Club>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Club>)QueryUtil.list(q, getDialect(), start,
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
	 * Returns the first club in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching club
	 * @throws NoSuchClubException if a matching club could not be found
	 */
	@Override
	public Club findByUuid_First(String uuid,
		OrderByComparator<Club> orderByComparator) throws NoSuchClubException {
		Club club = fetchByUuid_First(uuid, orderByComparator);

		if (club != null) {
			return club;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchClubException(msg.toString());
	}

	/**
	 * Returns the first club in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching club, or <code>null</code> if a matching club could not be found
	 */
	@Override
	public Club fetchByUuid_First(String uuid,
		OrderByComparator<Club> orderByComparator) {
		List<Club> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last club in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching club
	 * @throws NoSuchClubException if a matching club could not be found
	 */
	@Override
	public Club findByUuid_Last(String uuid,
		OrderByComparator<Club> orderByComparator) throws NoSuchClubException {
		Club club = fetchByUuid_Last(uuid, orderByComparator);

		if (club != null) {
			return club;
		}

		StringBundler msg = new StringBundler(4);

		msg.append(_NO_SUCH_ENTITY_WITH_KEY);

		msg.append("uuid=");
		msg.append(uuid);

		msg.append(StringPool.CLOSE_CURLY_BRACE);

		throw new NoSuchClubException(msg.toString());
	}

	/**
	 * Returns the last club in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching club, or <code>null</code> if a matching club could not be found
	 */
	@Override
	public Club fetchByUuid_Last(String uuid,
		OrderByComparator<Club> orderByComparator) {
		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<Club> list = findByUuid(uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the clubs before and after the current club in the ordered set where uuid = &#63;.
	 *
	 * @param clubId the primary key of the current club
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next club
	 * @throws NoSuchClubException if a club with the primary key could not be found
	 */
	@Override
	public Club[] findByUuid_PrevAndNext(long clubId, String uuid,
		OrderByComparator<Club> orderByComparator) throws NoSuchClubException {
		Club club = findByPrimaryKey(clubId);

		Session session = null;

		try {
			session = openSession();

			Club[] array = new ClubImpl[3];

			array[0] = getByUuid_PrevAndNext(session, club, uuid,
					orderByComparator, true);

			array[1] = club;

			array[2] = getByUuid_PrevAndNext(session, club, uuid,
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

	protected Club getByUuid_PrevAndNext(Session session, Club club,
		String uuid, OrderByComparator<Club> orderByComparator, boolean previous) {
		StringBundler query = null;

		if (orderByComparator != null) {
			query = new StringBundler(4 +
					(orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			query = new StringBundler(3);
		}

		query.append(_SQL_SELECT_CLUB_WHERE);

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
			query.append(ClubModelImpl.ORDER_BY_JPQL);
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
			Object[] values = orderByComparator.getOrderByConditionValues(club);

			for (Object value : values) {
				qPos.add(value);
			}
		}

		List<Club> list = q.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the clubs where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (Club club : findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null)) {
			remove(club);
		}
	}

	/**
	 * Returns the number of clubs where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching clubs
	 */
	@Override
	public int countByUuid(String uuid) {
		FinderPath finderPath = FINDER_PATH_COUNT_BY_UUID;

		Object[] finderArgs = new Object[] { uuid };

		Long count = (Long)finderCache.getResult(finderPath, finderArgs, this);

		if (count == null) {
			StringBundler query = new StringBundler(2);

			query.append(_SQL_COUNT_CLUB_WHERE);

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

	private static final String _FINDER_COLUMN_UUID_UUID_1 = "club.uuid IS NULL";
	private static final String _FINDER_COLUMN_UUID_UUID_2 = "club.uuid = ?";
	private static final String _FINDER_COLUMN_UUID_UUID_3 = "(club.uuid IS NULL OR club.uuid = '')";

	public ClubPersistenceImpl() {
		setModelClass(Club.class);
	}

	/**
	 * Caches the club in the entity cache if it is enabled.
	 *
	 * @param club the club
	 */
	@Override
	public void cacheResult(Club club) {
		entityCache.putResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubImpl.class, club.getPrimaryKey(), club);

		club.resetOriginalValues();
	}

	/**
	 * Caches the clubs in the entity cache if it is enabled.
	 *
	 * @param clubs the clubs
	 */
	@Override
	public void cacheResult(List<Club> clubs) {
		for (Club club : clubs) {
			if (entityCache.getResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
						ClubImpl.class, club.getPrimaryKey()) == null) {
				cacheResult(club);
			}
			else {
				club.resetOriginalValues();
			}
		}
	}

	/**
	 * Clears the cache for all clubs.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(ClubImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the club.
	 *
	 * <p>
	 * The {@link EntityCache} and {@link FinderCache} are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(Club club) {
		entityCache.removeResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubImpl.class, club.getPrimaryKey());

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@Override
	public void clearCache(List<Club> clubs) {
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Club club : clubs) {
			entityCache.removeResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
				ClubImpl.class, club.getPrimaryKey());
		}
	}

	/**
	 * Creates a new club with the primary key. Does not add the club to the database.
	 *
	 * @param clubId the primary key for the new club
	 * @return the new club
	 */
	@Override
	public Club create(long clubId) {
		Club club = new ClubImpl();

		club.setNew(true);
		club.setPrimaryKey(clubId);

		String uuid = PortalUUIDUtil.generate();

		club.setUuid(uuid);

		return club;
	}

	/**
	 * Removes the club with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param clubId the primary key of the club
	 * @return the club that was removed
	 * @throws NoSuchClubException if a club with the primary key could not be found
	 */
	@Override
	public Club remove(long clubId) throws NoSuchClubException {
		return remove((Serializable)clubId);
	}

	/**
	 * Removes the club with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the club
	 * @return the club that was removed
	 * @throws NoSuchClubException if a club with the primary key could not be found
	 */
	@Override
	public Club remove(Serializable primaryKey) throws NoSuchClubException {
		Session session = null;

		try {
			session = openSession();

			Club club = (Club)session.get(ClubImpl.class, primaryKey);

			if (club == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchClubException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					primaryKey);
			}

			return remove(club);
		}
		catch (NoSuchClubException nsee) {
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
	protected Club removeImpl(Club club) {
		club = toUnwrappedModel(club);

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(club)) {
				club = (Club)session.get(ClubImpl.class, club.getPrimaryKeyObj());
			}

			if (club != null) {
				session.delete(club);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		if (club != null) {
			clearCache(club);
		}

		return club;
	}

	@Override
	public Club updateImpl(Club club) {
		club = toUnwrappedModel(club);

		boolean isNew = club.isNew();

		ClubModelImpl clubModelImpl = (ClubModelImpl)club;

		if (Validator.isNull(club.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			club.setUuid(uuid);
		}

		ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();

		Date now = new Date();

		if (isNew && (club.getCreateDate() == null)) {
			if (serviceContext == null) {
				club.setCreateDate(now);
			}
			else {
				club.setCreateDate(serviceContext.getCreateDate(now));
			}
		}

		if (!clubModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				club.setModifiedDate(now);
			}
			else {
				club.setModifiedDate(serviceContext.getModifiedDate(now));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (club.isNew()) {
				session.save(club);

				club.setNew(false);
			}
			else {
				club = (Club)session.merge(club);
			}
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);

		if (isNew || !ClubModelImpl.COLUMN_BITMASK_ENABLED) {
			finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
		}

		else {
			if ((clubModelImpl.getColumnBitmask() &
					FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID.getColumnBitmask()) != 0) {
				Object[] args = new Object[] { clubModelImpl.getOriginalUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);

				args = new Object[] { clubModelImpl.getUuid() };

				finderCache.removeResult(FINDER_PATH_COUNT_BY_UUID, args);
				finderCache.removeResult(FINDER_PATH_WITHOUT_PAGINATION_FIND_BY_UUID,
					args);
			}
		}

		entityCache.putResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
			ClubImpl.class, club.getPrimaryKey(), club, false);

		club.resetOriginalValues();

		return club;
	}

	protected Club toUnwrappedModel(Club club) {
		if (club instanceof ClubImpl) {
			return club;
		}

		ClubImpl clubImpl = new ClubImpl();

		clubImpl.setNew(club.isNew());
		clubImpl.setPrimaryKey(club.getPrimaryKey());

		clubImpl.setUuid(club.getUuid());
		clubImpl.setClubId(club.getClubId());
		clubImpl.setCreateDate(club.getCreateDate());
		clubImpl.setModifiedDate(club.getModifiedDate());
		clubImpl.setName(club.getName());

		return clubImpl;
	}

	/**
	 * Returns the club with the primary key or throws a {@link com.liferay.portal.kernel.exception.NoSuchModelException} if it could not be found.
	 *
	 * @param primaryKey the primary key of the club
	 * @return the club
	 * @throws NoSuchClubException if a club with the primary key could not be found
	 */
	@Override
	public Club findByPrimaryKey(Serializable primaryKey)
		throws NoSuchClubException {
		Club club = fetchByPrimaryKey(primaryKey);

		if (club == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchClubException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				primaryKey);
		}

		return club;
	}

	/**
	 * Returns the club with the primary key or throws a {@link NoSuchClubException} if it could not be found.
	 *
	 * @param clubId the primary key of the club
	 * @return the club
	 * @throws NoSuchClubException if a club with the primary key could not be found
	 */
	@Override
	public Club findByPrimaryKey(long clubId) throws NoSuchClubException {
		return findByPrimaryKey((Serializable)clubId);
	}

	/**
	 * Returns the club with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the club
	 * @return the club, or <code>null</code> if a club with the primary key could not be found
	 */
	@Override
	public Club fetchByPrimaryKey(Serializable primaryKey) {
		Club club = (Club)entityCache.getResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
				ClubImpl.class, primaryKey);

		if (club == _nullClub) {
			return null;
		}

		if (club == null) {
			Session session = null;

			try {
				session = openSession();

				club = (Club)session.get(ClubImpl.class, primaryKey);

				if (club != null) {
					cacheResult(club);
				}
				else {
					entityCache.putResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
						ClubImpl.class, primaryKey, _nullClub);
				}
			}
			catch (Exception e) {
				entityCache.removeResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
					ClubImpl.class, primaryKey);

				throw processException(e);
			}
			finally {
				closeSession(session);
			}
		}

		return club;
	}

	/**
	 * Returns the club with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param clubId the primary key of the club
	 * @return the club, or <code>null</code> if a club with the primary key could not be found
	 */
	@Override
	public Club fetchByPrimaryKey(long clubId) {
		return fetchByPrimaryKey((Serializable)clubId);
	}

	@Override
	public Map<Serializable, Club> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {
		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, Club> map = new HashMap<Serializable, Club>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			Club club = fetchByPrimaryKey(primaryKey);

			if (club != null) {
				map.put(primaryKey, club);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			Club club = (Club)entityCache.getResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
					ClubImpl.class, primaryKey);

			if (club == null) {
				if (uncachedPrimaryKeys == null) {
					uncachedPrimaryKeys = new HashSet<Serializable>();
				}

				uncachedPrimaryKeys.add(primaryKey);
			}
			else {
				map.put(primaryKey, club);
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		StringBundler query = new StringBundler((uncachedPrimaryKeys.size() * 2) +
				1);

		query.append(_SQL_SELECT_CLUB_WHERE_PKS_IN);

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

			for (Club club : (List<Club>)q.list()) {
				map.put(club.getPrimaryKeyObj(), club);

				cacheResult(club);

				uncachedPrimaryKeys.remove(club.getPrimaryKeyObj());
			}

			for (Serializable primaryKey : uncachedPrimaryKeys) {
				entityCache.putResult(ClubModelImpl.ENTITY_CACHE_ENABLED,
					ClubImpl.class, primaryKey, _nullClub);
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
	 * Returns all the clubs.
	 *
	 * @return the clubs
	 */
	@Override
	public List<Club> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the clubs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @return the range of clubs
	 */
	@Override
	public List<Club> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the clubs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of clubs
	 */
	@Override
	public List<Club> findAll(int start, int end,
		OrderByComparator<Club> orderByComparator) {
		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the clubs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not {@link QueryUtil#ALL_POS}), then the query will include the default ORDER BY logic from {@link ClubModelImpl}. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of clubs
	 * @param end the upper bound of the range of clubs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param retrieveFromCache whether to retrieve from the finder cache
	 * @return the ordered range of clubs
	 */
	@Override
	public List<Club> findAll(int start, int end,
		OrderByComparator<Club> orderByComparator, boolean retrieveFromCache) {
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

		List<Club> list = null;

		if (retrieveFromCache) {
			list = (List<Club>)finderCache.getResult(finderPath, finderArgs,
					this);
		}

		if (list == null) {
			StringBundler query = null;
			String sql = null;

			if (orderByComparator != null) {
				query = new StringBundler(2 +
						(orderByComparator.getOrderByFields().length * 2));

				query.append(_SQL_SELECT_CLUB);

				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS,
					orderByComparator);

				sql = query.toString();
			}
			else {
				sql = _SQL_SELECT_CLUB;

				if (pagination) {
					sql = sql.concat(ClubModelImpl.ORDER_BY_JPQL);
				}
			}

			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(sql);

				if (!pagination) {
					list = (List<Club>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);

					list = Collections.unmodifiableList(list);
				}
				else {
					list = (List<Club>)QueryUtil.list(q, getDialect(), start,
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
	 * Removes all the clubs from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (Club club : findAll()) {
			remove(club);
		}
	}

	/**
	 * Returns the number of clubs.
	 *
	 * @return the number of clubs
	 */
	@Override
	public int countAll() {
		Long count = (Long)finderCache.getResult(FINDER_PATH_COUNT_ALL,
				FINDER_ARGS_EMPTY, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_CLUB);

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
		return ClubModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the club persistence.
	 */
	public void afterPropertiesSet() {
	}

	public void destroy() {
		entityCache.removeCache(ClubImpl.class.getName());
		finderCache.removeCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.removeCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;
	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;
	private static final String _SQL_SELECT_CLUB = "SELECT club FROM Club club";
	private static final String _SQL_SELECT_CLUB_WHERE_PKS_IN = "SELECT club FROM Club club WHERE clubId IN (";
	private static final String _SQL_SELECT_CLUB_WHERE = "SELECT club FROM Club club WHERE ";
	private static final String _SQL_COUNT_CLUB = "SELECT COUNT(club) FROM Club club";
	private static final String _SQL_COUNT_CLUB_WHERE = "SELECT COUNT(club) FROM Club club WHERE ";
	private static final String _ORDER_BY_ENTITY_ALIAS = "club.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No Club exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No Club exists with the key {";
	private static final Log _log = LogFactoryUtil.getLog(ClubPersistenceImpl.class);
	private static final Set<String> _badColumnNames = SetUtil.fromArray(new String[] {
				"uuid"
			});
	private static final Club _nullClub = new ClubImpl() {
			@Override
			public Object clone() {
				return this;
			}

			@Override
			public CacheModel<Club> toCacheModel() {
				return _nullClubCacheModel;
			}
		};

	private static final CacheModel<Club> _nullClubCacheModel = new CacheModel<Club>() {
			@Override
			public Club toEntityModel() {
				return _nullClub;
			}
		};
}