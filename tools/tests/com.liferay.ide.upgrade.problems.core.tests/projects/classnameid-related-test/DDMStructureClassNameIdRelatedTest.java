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

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLinkException;
import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.dynamic.data.mapping.service.DDMStructureLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLinkUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructurePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTemplateLinkPersistence;
import com.liferay.dynamic.data.mapping.service.persistenceDDMTemplateLinkUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class DDMStructureClassNameIdRelatedTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMStructureLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStructureLink> iterator = _ddmStructureLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink ddmStructureLink = _persistence.create(pk);

		Assert.assertNotNull(ddmStructureLink);

		Assert.assertEquals(ddmStructureLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		_persistence.remove(newDDMStructureLink);

		DDMStructureLink existingDDMStructureLink =
			_persistence.fetchByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertNull(existingDDMStructureLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStructureLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink newDDMStructureLink = _persistence.create(pk);

		newDDMStructureLink.setMvccVersion(RandomTestUtil.nextLong());

		newDDMStructureLink.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStructureLink.setCompanyId(RandomTestUtil.nextLong());

		newDDMStructureLink.setClassNameId(RandomTestUtil.nextLong());

		newDDMStructureLink.setClassPK(RandomTestUtil.nextLong());

		newDDMStructureLink.setStructureId(RandomTestUtil.nextLong());

		_ddmStructureLinks.add(_persistence.update(newDDMStructureLink));

		DDMStructureLink existingDDMStructureLink =
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureLink.getMvccVersion(),
			newDDMStructureLink.getMvccVersion());
		Assert.assertEquals(
			existingDDMStructureLink.getCtCollectionId(),
			newDDMStructureLink.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStructureLink.getStructureLinkId(),
			newDDMStructureLink.getStructureLinkId());
		Assert.assertEquals(
			existingDDMStructureLink.getCompanyId(),
			newDDMStructureLink.getCompanyId());
		Assert.assertEquals(
			existingDDMStructureLink.getClassNameId(),
			newDDMStructureLink.getClassNameId());
		Assert.assertEquals(
			existingDDMStructureLink.getClassPK(),
			newDDMStructureLink.getClassPK());
		Assert.assertEquals(
			existingDDMStructureLink.getStructureId(),
			newDDMStructureLink.getStructureId());
	}

	@Test
	public void testCountByClassNameId() throws Exception {
		_persistence.countByClassNameId(RandomTestUtil.nextLong());

		_persistence.countByClassNameId(0L);
	}

	@Test
	public void testCountByStructureId() throws Exception {
		_persistence.countByStructureId(RandomTestUtil.nextLong());

		_persistence.countByStructureId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_S(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DDMStructureLink existingDDMStructureLink =
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLink, newDDMStructureLink);
	}

	@Test(expected = NoSuchStructureLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStructureLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStructureLink", "mvccVersion", true, "ctCollectionId", true,
			"structureLinkId", true, "companyId", true, "classNameId", true,
			"classPK", true, "structureId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DDMStructureLink existingDDMStructureLink =
			_persistence.fetchByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLink, newDDMStructureLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink missingDDMStructureLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMStructureLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStructureLink newDDMStructureLink1 = addDDMStructureLink();
		DDMStructureLink newDDMStructureLink2 = addDDMStructureLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink1.getPrimaryKey());
		primaryKeys.add(newDDMStructureLink2.getPrimaryKey());

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink1,
			ddmStructureLinks.get(newDDMStructureLink1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStructureLink2,
			ddmStructureLinks.get(newDDMStructureLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink,
			ddmStructureLinks.get(newDDMStructureLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLink.getPrimaryKey());

		Map<Serializable, DDMStructureLink> ddmStructureLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLinks.size());
		Assert.assertEquals(
			newDDMStructureLink,
			ddmStructureLinks.get(newDDMStructureLink.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DDMStructureLinkLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DDMStructureLink>() {

				@Override
				public void performAction(DDMStructureLink ddmStructureLink) {
					Assert.assertNotNull(ddmStructureLink);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructureLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"structureLinkId", newDDMStructureLink.getStructureLinkId()));

		List<DDMStructureLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DDMStructureLink existingDDMStructureLink = result.get(0);

		Assert.assertEquals(existingDDMStructureLink, newDDMStructureLink);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructureLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"structureLinkId", RandomTestUtil.nextLong()));

		List<DDMStructureLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructureLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("structureLinkId"));

		Object newStructureLinkId = newDDMStructureLink.getStructureLinkId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"structureLinkId", new Object[] {newStructureLinkId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStructureLinkId = result.get(0);

		Assert.assertEquals(existingStructureLinkId, newStructureLinkId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructureLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("structureLinkId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"structureLinkId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStructureLink newDDMStructureLink = addDDMStructureLink();

		_persistence.clearCache();

		DDMStructureLink existingDDMStructureLink =
			_persistence.findByPrimaryKey(newDDMStructureLink.getPrimaryKey());

		Assert.assertEquals(
			Long.valueOf(existingDDMStructureLink.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				existingDDMStructureLink, "getOriginalClassNameId",
				new Class<?>[0]));
		Assert.assertEquals(
			Long.valueOf(existingDDMStructureLink.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				existingDDMStructureLink, "getOriginalClassPK",
				new Class<?>[0]));
		Assert.assertEquals(
			Long.valueOf(existingDDMStructureLink.getStructureId()),
			ReflectionTestUtil.<Long>invoke(
				existingDDMStructureLink, "getOriginalStructureId",
				new Class<?>[0]));
	}

	@Test
	public void testFindByClassNameId() {
		long a = 343453;
		int b = 4;
		int c = 7;
		long d = 80987;

		_persistence.findByClassNameId(a);

		_persistence.findByClassNameId(a, b, c);

		_persistence.findByClassNameId(a, b, c, getOrderByComparator());

		_persistence.findByClassNameId(a, b, c, getOrderByComparator(), true);

		_persistence.findByClassNameId_First(a, getOrderByComparator());

		_persistence.fetchByClassNameId_First(a, getOrderByComparator());

		_persistence.findByClassNameId_Last(a, getOrderByComparator());

		_persistence.fetchByClassNameId_Last(a, getOrderByComparator());

		_persistence.findByClassNameId_PrevAndNext(a, d, getOrderByComparator());

		_persistence.removeByClassNameId(a);

		_persistence.countByClassNameId(a);

		DDMStructureLinkUtil.findByClassNameId (a);

		DDMStructureLinkUtil.findByClassNameId (a, b, c);

		DDMStructureLinkUtil.findByClassNameId (a, b, c, getOrderByComparator());

		DDMStructureLinkUtil.findByClassNameId (a, b, c, getOrderByComparator(), d);

		DDMStructureLinkUtil.findByClassNameId_First (a, getOrderByComparator());

		DDMStructureLinkUtil.fetchByClassNameId_First (a, getOrderByComparator());

		DDMStructureLinkUtil.findByClassNameId_Last (a, getOrderByComparator());

		DDMStructureLinkUtil.fetchByClassNameId_Last (a, getOrderByComparator());

		DDMStructureLinkUtil.findByClassNameId_PrevAndNext (a, b, getOrderByComparator());

		DDMStructureLinkUtil.removeByClassNameId (a);

		DDMStructureLinkUtil.countByClassNameId (a);

		_persistence.findByClassNameId(a);

		_persistence2.findByClassNameId(a, b, c);

		_persistence2.findByClassNameId(a, b, c, getOrderByComparator());

		_persistence2.findByClassNameId(a, b, c, getOrderByComparator(), true);

		_persistence2.findByClassNameId_First(a, getOrderByComparator());

		_persistence2.fetchByClassNameId_First(a, getOrderByComparator());

		_persistence2.findByClassNameId_Last(a, getOrderByComparator());

		_persistence2.fetchByClassNameId_Last(a, getOrderByComparator());

		_persistence2.findByClassNameId_PrevAndNext(a, d, getOrderByComparator());

		_persistence2.removeByClassNameId(a);

		_persistence2.countByClassNameId(a);

		DDMStructureUtil.findByClassNameId (a);

		DDMStructureUtil.findByClassNameId (a, b, c);

		DDMStructureUtil.findByClassNameId (a, b, c, getOrderByComparator());

		DDMStructureUtil.findByClassNameId (a, b, c, getOrderByComparator(), d);

		DDMStructureUtil.findByClassNameId_First (a, getOrderByComparator());

		DDMStructureUtil.fetchByClassNameId_First (a, getOrderByComparator());

		DDMStructureUtil.findByClassNameId_Last (a, getOrderByComparator());

		DDMStructureUtil.fetchByClassNameId_Last (a, getOrderByComparator());

		DDMStructureUtil.findByClassNameId_PrevAndNext (a, b, getOrderByComparator());

		DDMStructureUtil.removeByClassNameId (a);

		DDMStructureUtil.countByClassNameId (a);

		_persistence3.findByClassNameId(a);

		_persistence3.findByClassNameId(a, b, c);

		_persistence3.findByClassNameId(a, b, c, getOrderByComparator());

		_persistence3.findByClassNameId(a, b, c, getOrderByComparator(), true);

		_persistence3.findByClassNameId_First(a, getOrderByComparator());

		_persistence3.fetchByClassNameId_First(a, getOrderByComparator());

		_persistence3.findByClassNameId_Last(a, getOrderByComparator());

		_persistence3.fetchByClassNameId_Last(a, getOrderByComparator());

		_persistence3.findByClassNameId_PrevAndNext(a, d, getOrderByComparator());

		_persistence3.removeByClassNameId(a);

		_persistence3.countByClassNameId(a);

		DDMTemplateLinkUtil.findByClassNameId (a);

		DDMTemplateLinkUtil.findByClassNameId (a, b, c);

		DDMTemplateLinkUtil.findByClassNameId (a, b, c, getOrderByComparator());

		DDMTemplateLinkUtil.findByClassNameId (a, b, c, getOrderByComparator(), d);

		DDMTemplateLinkUtil.findByClassNameId_First (a, getOrderByComparator());

		DDMTemplateLinkUtil.fetchByClassNameId_First (a, getOrderByComparator());

		DDMTemplateLinkUtil.findByClassNameId_Last (a, getOrderByComparator());

		DDMTemplateLinkUtil.fetchByClassNameId_Last (a, getOrderByComparator());

		DDMTemplateLinkUtil.findByClassNameId_PrevAndNext (a, b, getOrderByComparator());

		DDMTemplateLinkUtil.removeByClassNameId (a);

		DDMTemplateLinkUtil.countByClassNameId (a);
	}

	protected DDMStructureLink addDDMStructureLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLink ddmStructureLink = _persistence.create(pk);

		ddmStructureLink.setMvccVersion(RandomTestUtil.nextLong());

		ddmStructureLink.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStructureLink.setCompanyId(RandomTestUtil.nextLong());

		ddmStructureLink.setClassNameId(RandomTestUtil.nextLong());

		ddmStructureLink.setClassPK(RandomTestUtil.nextLong());

		ddmStructureLink.setStructureId(RandomTestUtil.nextLong());

		_ddmStructureLinks.add(_persistence.update(ddmStructureLink));

		return ddmStructureLink;
	}

	private List<DDMStructureLink> _ddmStructureLinks =
		new ArrayList<DDMStructureLink>();
	private DDMStructureLinkPersistence _persistence;
	private DDMStructurePersistence _persistence2;
	private DDMTemplateLinkPersistence _persistence3;
	private ClassLoader _dynamicQueryClassLoader;

}