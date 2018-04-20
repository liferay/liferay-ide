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

package com.liferay.roster.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.TransactionalTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;

import com.liferay.roster.exception.NoSuchClubException;
import com.liferay.roster.model.Club;
import com.liferay.roster.service.ClubLocalServiceUtil;
import com.liferay.roster.service.persistence.ClubPersistence;
import com.liferay.roster.service.persistence.ClubUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class ClubPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ClubUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Club> iterator = _clubs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Club club = _persistence.create(pk);

		Assert.assertNotNull(club);

		Assert.assertEquals(club.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Club newClub = addClub();

		_persistence.remove(newClub);

		Club existingClub = _persistence.fetchByPrimaryKey(newClub.getPrimaryKey());

		Assert.assertNull(existingClub);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addClub();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Club newClub = _persistence.create(pk);

		newClub.setUuid(RandomTestUtil.randomString());

		newClub.setCreateDate(RandomTestUtil.nextDate());

		newClub.setModifiedDate(RandomTestUtil.nextDate());

		newClub.setName(RandomTestUtil.randomString());

		_clubs.add(_persistence.update(newClub));

		Club existingClub = _persistence.findByPrimaryKey(newClub.getPrimaryKey());

		Assert.assertEquals(existingClub.getUuid(), newClub.getUuid());
		Assert.assertEquals(existingClub.getClubId(), newClub.getClubId());
		Assert.assertEquals(Time.getShortTimestamp(existingClub.getCreateDate()),
			Time.getShortTimestamp(newClub.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingClub.getModifiedDate()),
			Time.getShortTimestamp(newClub.getModifiedDate()));
		Assert.assertEquals(existingClub.getName(), newClub.getName());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid(StringPool.BLANK);

		_persistence.countByUuid(StringPool.NULL);

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Club newClub = addClub();

		Club existingClub = _persistence.findByPrimaryKey(newClub.getPrimaryKey());

		Assert.assertEquals(existingClub, newClub);
	}

	@Test(expected = NoSuchClubException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<Club> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("ROSTER_Club", "uuid", true,
			"clubId", true, "createDate", true, "modifiedDate", true, "name",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Club newClub = addClub();

		Club existingClub = _persistence.fetchByPrimaryKey(newClub.getPrimaryKey());

		Assert.assertEquals(existingClub, newClub);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Club missingClub = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingClub);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		Club newClub1 = addClub();
		Club newClub2 = addClub();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClub1.getPrimaryKey());
		primaryKeys.add(newClub2.getPrimaryKey());

		Map<Serializable, Club> clubs = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, clubs.size());
		Assert.assertEquals(newClub1, clubs.get(newClub1.getPrimaryKey()));
		Assert.assertEquals(newClub2, clubs.get(newClub2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Club> clubs = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(clubs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		Club newClub = addClub();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClub.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Club> clubs = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, clubs.size());
		Assert.assertEquals(newClub, clubs.get(newClub.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Club> clubs = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(clubs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		Club newClub = addClub();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClub.getPrimaryKey());

		Map<Serializable, Club> clubs = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, clubs.size());
		Assert.assertEquals(newClub, clubs.get(newClub.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = ClubLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Club>() {
				@Override
				public void performAction(Club club) {
					Assert.assertNotNull(club);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		Club newClub = addClub();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Club.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("clubId",
				newClub.getClubId()));

		List<Club> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Club existingClub = result.get(0);

		Assert.assertEquals(existingClub, newClub);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Club.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("clubId",
				RandomTestUtil.nextLong()));

		List<Club> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		Club newClub = addClub();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Club.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("clubId"));

		Object newClubId = newClub.getClubId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("clubId",
				new Object[] { newClubId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingClubId = result.get(0);

		Assert.assertEquals(existingClubId, newClubId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Club.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("clubId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("clubId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected Club addClub() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Club club = _persistence.create(pk);

		club.setUuid(RandomTestUtil.randomString());

		club.setCreateDate(RandomTestUtil.nextDate());

		club.setModifiedDate(RandomTestUtil.nextDate());

		club.setName(RandomTestUtil.randomString());

		_clubs.add(_persistence.update(club));

		return club;
	}

	private List<Club> _clubs = new ArrayList<Club>();
	private ClubPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}