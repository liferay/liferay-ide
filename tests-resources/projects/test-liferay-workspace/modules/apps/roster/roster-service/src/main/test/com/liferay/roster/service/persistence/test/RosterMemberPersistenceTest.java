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

import com.liferay.roster.exception.NoSuchRosterMemberException;
import com.liferay.roster.model.RosterMember;
import com.liferay.roster.service.RosterMemberLocalServiceUtil;
import com.liferay.roster.service.persistence.RosterMemberPersistence;
import com.liferay.roster.service.persistence.RosterMemberUtil;

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
public class RosterMemberPersistenceTest {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RosterMemberUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RosterMember> iterator = _rosterMembers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RosterMember rosterMember = _persistence.create(pk);

		Assert.assertNotNull(rosterMember);

		Assert.assertEquals(rosterMember.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RosterMember newRosterMember = addRosterMember();

		_persistence.remove(newRosterMember);

		RosterMember existingRosterMember = _persistence.fetchByPrimaryKey(newRosterMember.getPrimaryKey());

		Assert.assertNull(existingRosterMember);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRosterMember();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RosterMember newRosterMember = _persistence.create(pk);

		newRosterMember.setUuid(RandomTestUtil.randomString());

		newRosterMember.setCreateDate(RandomTestUtil.nextDate());

		newRosterMember.setModifiedDate(RandomTestUtil.nextDate());

		newRosterMember.setRosterId(RandomTestUtil.nextLong());

		newRosterMember.setContactId(RandomTestUtil.nextLong());

		_rosterMembers.add(_persistence.update(newRosterMember));

		RosterMember existingRosterMember = _persistence.findByPrimaryKey(newRosterMember.getPrimaryKey());

		Assert.assertEquals(existingRosterMember.getUuid(),
			newRosterMember.getUuid());
		Assert.assertEquals(existingRosterMember.getRosterMemberId(),
			newRosterMember.getRosterMemberId());
		Assert.assertEquals(Time.getShortTimestamp(
				existingRosterMember.getCreateDate()),
			Time.getShortTimestamp(newRosterMember.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingRosterMember.getModifiedDate()),
			Time.getShortTimestamp(newRosterMember.getModifiedDate()));
		Assert.assertEquals(existingRosterMember.getRosterId(),
			newRosterMember.getRosterId());
		Assert.assertEquals(existingRosterMember.getContactId(),
			newRosterMember.getContactId());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid(StringPool.BLANK);

		_persistence.countByUuid(StringPool.NULL);

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByRosterId() throws Exception {
		_persistence.countByRosterId(RandomTestUtil.nextLong());

		_persistence.countByRosterId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RosterMember newRosterMember = addRosterMember();

		RosterMember existingRosterMember = _persistence.findByPrimaryKey(newRosterMember.getPrimaryKey());

		Assert.assertEquals(existingRosterMember, newRosterMember);
	}

	@Test(expected = NoSuchRosterMemberException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			getOrderByComparator());
	}

	protected OrderByComparator<RosterMember> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("ROSTER_RosterMember",
			"uuid", true, "rosterMemberId", true, "createDate", true,
			"modifiedDate", true, "rosterId", true, "contactId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RosterMember newRosterMember = addRosterMember();

		RosterMember existingRosterMember = _persistence.fetchByPrimaryKey(newRosterMember.getPrimaryKey());

		Assert.assertEquals(existingRosterMember, newRosterMember);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RosterMember missingRosterMember = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRosterMember);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		RosterMember newRosterMember1 = addRosterMember();
		RosterMember newRosterMember2 = addRosterMember();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRosterMember1.getPrimaryKey());
		primaryKeys.add(newRosterMember2.getPrimaryKey());

		Map<Serializable, RosterMember> rosterMembers = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, rosterMembers.size());
		Assert.assertEquals(newRosterMember1,
			rosterMembers.get(newRosterMember1.getPrimaryKey()));
		Assert.assertEquals(newRosterMember2,
			rosterMembers.get(newRosterMember2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RosterMember> rosterMembers = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rosterMembers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		RosterMember newRosterMember = addRosterMember();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRosterMember.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RosterMember> rosterMembers = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rosterMembers.size());
		Assert.assertEquals(newRosterMember,
			rosterMembers.get(newRosterMember.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RosterMember> rosterMembers = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rosterMembers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		RosterMember newRosterMember = addRosterMember();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRosterMember.getPrimaryKey());

		Map<Serializable, RosterMember> rosterMembers = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rosterMembers.size());
		Assert.assertEquals(newRosterMember,
			rosterMembers.get(newRosterMember.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = RosterMemberLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod<RosterMember>() {
				@Override
				public void performAction(RosterMember rosterMember) {
					Assert.assertNotNull(rosterMember);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		RosterMember newRosterMember = addRosterMember();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(RosterMember.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("rosterMemberId",
				newRosterMember.getRosterMemberId()));

		List<RosterMember> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		RosterMember existingRosterMember = result.get(0);

		Assert.assertEquals(existingRosterMember, newRosterMember);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(RosterMember.class,
				_dynamicQueryClassLoader);

		dynamicQuery.add(RestrictionsFactoryUtil.eq("rosterMemberId",
				RandomTestUtil.nextLong()));

		List<RosterMember> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		RosterMember newRosterMember = addRosterMember();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(RosterMember.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"rosterMemberId"));

		Object newRosterMemberId = newRosterMember.getRosterMemberId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("rosterMemberId",
				new Object[] { newRosterMemberId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingRosterMemberId = result.get(0);

		Assert.assertEquals(existingRosterMemberId, newRosterMemberId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(RosterMember.class,
				_dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property(
				"rosterMemberId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("rosterMemberId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected RosterMember addRosterMember() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RosterMember rosterMember = _persistence.create(pk);

		rosterMember.setUuid(RandomTestUtil.randomString());

		rosterMember.setCreateDate(RandomTestUtil.nextDate());

		rosterMember.setModifiedDate(RandomTestUtil.nextDate());

		rosterMember.setRosterId(RandomTestUtil.nextLong());

		rosterMember.setContactId(RandomTestUtil.nextLong());

		_rosterMembers.add(_persistence.update(rosterMember));

		return rosterMember;
	}

	private List<RosterMember> _rosterMembers = new ArrayList<RosterMember>();
	private RosterMemberPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;
}