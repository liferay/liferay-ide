
package com.liferay.ide.upgrade.problems.test

import com.liferay.asset.kernel.model.AssetEntries_AssetCategoriesTable;

public class AssetEntriesAssetCategoriesTableTest {
	long a = 123456;
	long b = 456789;
	long[] c = {123456, 456789};
	int d = 8;
	int e = 6;
	OrderByComparator<com.liferay.asset.kernel.model.AssetEntry> orderByComparator = new OrderByComparator<com.liferay.asset.kernel.model.AssetEntry>();
	com.liferay.asset.kernel.model.AssetCategory assetCategory = new com.liferay.asset.kernel.model.AssetCategory();
	List<com.liferay.asset.kernel.model.AssetCategory> assetCategoryList= new List<com.liferay.asset.kernel.model.AssetCategory>();

	public test () {
		AssetCategoryLocalServiceBaseImpl assetCategoryLocalServiceBaseImpl = new AssetCategoryLocalServiceBaseImpl();

		assetCategoryLocalServiceBaseImpl.addAssetEntryAssetCategory(a, b);

		assetCategoryLocalServiceBaseImpl.setAssetEntryFinder(new AssetEntryFinder());

		AssetCategoryServiceBaseImpl assetCategoryServiceBaseImpl = new AssetCategoryServiceBaseImpl();

		assetCategoryServiceBaseImpl.getAssetEntryLocalService();

		AssetEntryLocalServiceBaseImpl assetEntryLocalServiceBaseImpl = new AssetEntryLocalServiceBaseImpl();

		assetEntryLocalServiceBaseImpl.addAssetCategoryAssetEntry(a, new AssetEntry());

		assetEntryLocalServiceBaseImpl.setAssetCategoryAssetEntries(a, c);

		AssetCategoryPersistenceImpl assetCategoryPersistenceImpl = new AssetCategoryPersistenceImpl();

		assetCategoryPersistenceImpl.getAssetEntries(a, d, e);

		assetCategoryPersistenceImpl.getAssetEntries(a, d, e, orderByComparator);

		AssetEntryPersistenceImpl assetEntryPersistenceImpl = new AssetEntryPersistenceImpl();

		assetEntryPersistenceImpl.removeAssetCategory(a, assetCategory);

		assetEntryPersistenceImpl.setAssetCategories(a, assetCategoryList);

		AssetCategoryLocalService assetCategoryLocalService = new AssetCategoryLocalService();

		assetCategoryLocalService.getAssetEntryPrimaryKeys(a);

		AssetCategoryLocalServiceUtil.getAssetEntryAssetCategories(a);

		AssetCategoryLocalServiceWrapper assetCategoryLocalServiceWrapper = new AssetCategoryLocalServiceWrapper();

		assetCategoryLocalServiceWrapper.deleteAssetEntryAssetCategories(a, c);

		AssetEntryLocalService assetEntryLocalService = new AssetEntryLocalService();

		assetEntryLocalService.hasAssetCategoryAssetEntry(a);

		AssetEntryLocalServiceUtil.getAssetCategoryAssetEntriesCount(a);

		AssetEntryLocalServiceWrapper assetEntryLocalServiceWrapper = new AssetEntryLocalServiceWrapper();

		assetEntryLocalServiceWrapper.getAssetCategoryAssetEntries(a);

		AssetCategoryPersistence assetCategoryPersistence = new AssetCategoryPersistence();

		assetCategoryPersistence.addAssetEntry(a, b);

		AssetCategoryUtil.clearAssetEntries(a);

		AssetEntryPersistence assetEntryPersistence = new AssetEntryPersistence();

		assetEntryPersistence.containsAssetCategory(a, b);

		AssetEntryUtil.getAssetCategoryPrimaryKeys(a);
	}
}


