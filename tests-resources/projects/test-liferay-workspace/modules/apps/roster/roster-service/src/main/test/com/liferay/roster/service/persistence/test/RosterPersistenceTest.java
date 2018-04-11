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

import com.liferay.roster.exception.NoSuchRosterException;
import com.liferay.roster.model.Roster;
import com.liferay.roster.service.RosterLocalServiceUtil;
import com.liferay.roster.service.persistence.RosterPersistence;
import com.liferay.roster.service.persistence.RosterUtil;

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
public class RosterPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RosterUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Roster> iterator = _rosters.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Roster roster = _persistence.create(pk);

		Assert.assertNotNull(roster);

		Assert.assertEquals(roster.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Roster newRoster = addRoster();

		_persistence.remove(newRoster);

		Roster existingRoster = _persistence.fetchByPrimaryKey(newRoster.getPrimaryKey());

		Assert.assertNull(existingRoster);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRoster();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Roster newRoster = _persistence.create(pk);

		newRoster.setUuid(RandomTestUtil.randomString());

		newRoster.setCreateDate(RandomTestUtil.nextDate());

		newRoster.setModifiedDate(RandomTestUtil.nextDate());

		newRoster.setClubId(RandomTestUtil.nextLong());

		newRoster.setName(RandomTestUtil.randomString());

		_rosters.add(_persistence.update(newRoster));

		Roster existingRoster = _persistence.findByPrimaryKey(newRoster.getPrimaryKey());

		Assert.assertEquals(existingRoster.getUuid(), newRoster.getUuid());
		Assert.assertEquals(existingRoster.getRosterId(),
			newRoster.getRosterId());
		Assert.assertEquals(Time.getShortTimestamp(
				existingRoster.getCreateDate()),
			Time.getShortTimestamp(newRoster.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingRoster.getModifiedDate()),
			Time.getShortTimestamp(newRoster.getModifiedDate()));
		Assert.assertEquals(existingRoster.getClubId(), newRoster.getClubId());
		Assert.assertEquals(existingRoster.getName(), newRoster.getName());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid(StringPool.BLANK);

		_persistence.countByUuid(StringPool.NULL);

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByClubId() throws Exception {
		_persistence.countByClubId(RandomTestUtil.nextLong());

		_persistence.countByClubId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Roster newRoster = addRoster();

		Roster existingRoster = _persistence.findByPrimaryKey(newRoster.getPrimaryKey());

		Assert.assertEquals(existingRoster, newRoster);
	}

	@Test(expected = NoSuchRosterException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<Roster> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("ROSTER_Roster", "uuid",
			true, "rosterId", true, "createDate", true, "modifiedDate", true,
			"clubId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Roster newRoster = addRoster();

		Roster existingRoster = _persistence.fetchByPrimaryKey(newRoster.getPrimaryKey());

		Assert.assertEquals(existingRoster, newRoster);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Roster missingRoster = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRoster);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		Roster newRoster1 = addRoster();
		Roster newRoster2 = addRoster();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRoster1.getPrimaryKey());
		primaryKeys.add(newRoster2.getPrimaryKey());

		Map<Serializable, Roster> rosters = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, rosters.size());
		Assert.assertEquals(newRoster1, rosters.get(newRoster1.getPrimaryKey()));
		Assert.assertEquals(newRoster2, rosters.get(newRoster2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Roster> rosters = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rosters.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		Roster newRoster = addRoster();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRoster.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Roster> rosters = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rosters.size());
		Assert.assertEquals(newRoster, rosters.get(newRoster.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Roster> rosters = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rosters.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		Roster newRoster = addRoster();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRoster.getPrimaryKey());

		Map<Serializable, Roster> rosters = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rosters.size());
		Assert.assertEquals(newRoster, rosters.get(newRoster.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = RosterLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<Roster>() {
				@Override
				public void performAction(Roster roster) {
					Assert.assertNotNull(roster);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		Roster newRoster = addRoster();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Roster.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("rosterId",
				newRoster.getRosterId()));

		List<Roster> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Roster existingRoster = result.get(0);

		Assert.assertEquals(existingRoster, newRoster);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Roster.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("rosterId",
				RandomTestUtil.nextLong()));

		List<Roster> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		Roster newRoster = addRoster();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Roster.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("rosterId"));

		Object newRosterId = newRoster.getRosterId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("rosterId",
				new Object[] { newRosterId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingRosterId = result.get(0);

		Assert.assertEquals(existingRosterId, newRosterId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(Roster.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("rosterId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("rosterId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected Roster addRoster() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Roster roster = _persistence.create(pk);

		roster.setUuid(RandomTestUtil.randomString());

		roster.setCreateDate(RandomTestUtil.nextDate());

		roster.setModifiedDate(RandomTestUtil.nextDate());

		roster.setClubId(RandomTestUtil.nextLong());

		roster.setName(RandomTestUtil.randomString());

		_rosters.add(_persistence.update(roster));

		return roster;
	}

	private List<Roster> _rosters = new ArrayList<Roster>();
	private RosterPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}