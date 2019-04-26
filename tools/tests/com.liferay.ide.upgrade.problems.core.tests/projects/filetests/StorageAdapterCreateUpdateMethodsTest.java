package blade.migrate.liferay70;

import java.util.Map;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntryMetadata;
import com.liferay.portlet.dynamicdatamapping.StorageException;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Field;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.dynamicdatamapping.storage.StorageEngineUtil;

public class StorageAdapterCreateUpdateMethodsTest {

	protected void updateFieldValues(long storageId,
			Map<String, String> fieldValues) throws Exception {

		Fields fields = new Fields();

		for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
			Field field = new Field(storageId, entry.getKey(), entry.getValue());

			fields.put(field);
		}

		ServiceContext serviceContext = new ServiceContext();

		StorageEngineUtil.update(storageId, fields, true, serviceContext);
	}

	protected void updateFieldValues(long storageId,
			Map<String, String> fieldValues) throws Exception {

		Fields fields = new Fields();

		for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
			Field field = new Field(storageId, entry.getKey(), entry.getValue());

			fields.put(field);
		}

		ServiceContext serviceContext = new ServiceContext();

		StorageEngineUtil.update(storageId, fields, serviceContext);
	}

	protected void updateFileEntryMetadata(
			long companyId, DDMStructure ddmStructure, long fileEntryTypeId,
			long fileEntryId, long fileVersionId, Fields fields,
			ServiceContext serviceContext)
		throws StorageException, SystemException {

		DLFileEntryMetadata fileEntryMetadata =
			dlFileEntryMetadataPersistence.fetchByD_F(
				ddmStructure.getStructureId(), fileVersionId);

		if (fileEntryMetadata != null) {
			StorageEngineUtil.update(
				fileEntryMetadata.getDDMStorageId(), fields, true,
				serviceContext);
		}
		else {

			// File entry metadata

			long fileEntryMetadataId = counterLocalService.increment();

			fileEntryMetadata = dlFileEntryMetadataPersistence.create(
				fileEntryMetadataId);

			long ddmStorageId = StorageEngineUtil.create(
				companyId, ddmStructure.getStructureId(), fields,
				serviceContext);

			fileEntryMetadata.setDDMStorageId(ddmStorageId);

			fileEntryMetadata.setDDMStructureId(ddmStructure.getStructureId());
			fileEntryMetadata.setFileEntryTypeId(fileEntryTypeId);
			fileEntryMetadata.setFileEntryId(fileEntryId);
			fileEntryMetadata.setFileVersionId(fileVersionId);

			dlFileEntryMetadataPersistence.update(fileEntryMetadata);

			// Dynamic data mapping structure link

			long classNameId = PortalUtil.getClassNameId(
				DLFileEntryMetadata.class);

			ddmStructureLinkLocalService.addStructureLink(
				classNameId, fileEntryMetadata.getFileEntryMetadataId(),
				ddmStructure.getStructureId(), serviceContext);
		}
	}

}
