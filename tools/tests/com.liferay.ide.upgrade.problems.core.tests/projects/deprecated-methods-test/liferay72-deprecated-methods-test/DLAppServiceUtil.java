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

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * Provides the remote service utility for DLApp. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLAppServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppService
 * @generated
 */
public class DLAppServiceUtil {

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLAppServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a file entry and associated metadata. It is created based on a byte
	 * array.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param bytes the file's data (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String description,
				String changeLog, byte[] bytes,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFileEntry(
			repositoryId, folderId, sourceFileName, mimeType, title,
			description, changeLog, bytes, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link File} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param file the file's data (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String description,
				String changeLog, java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFileEntry(
			repositoryId, folderId, sourceFileName, mimeType, title,
			description, changeLog, file, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link InputStream} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param is the file's data (optionally <code>null</code>)
	 * @param size the file's size (optionally <code>0</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				long repositoryId, long folderId, String sourceFileName,
				String mimeType, String title, String description,
				String changeLog, java.io.InputStream is, long size,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFileEntry(
			repositoryId, folderId, sourceFileName, mimeType, title,
			description, changeLog, is, size, serviceContext);
	}

	/**
	 * Adds a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file shortcut's file entry
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			addFileShortcut(
				long repositoryId, long folderId, long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFileShortcut(
			repositoryId, folderId, toFileEntryId, serviceContext);
	}

	/**
	 * Adds a folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @param description the folder's description
	 * @param serviceContext the service context to be applied. In a Liferay
	 repository, it may include boolean mountPoint specifying whether
	 folder is a facade for mounting a third-party repository
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder addFolder(
			long repositoryId, long parentFolderId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addFolder(
			repositoryId, parentFolderId, name, description, serviceContext);
	}

	/**
	 * Adds a temporary file entry.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @param file the file's data (optionally <code>null</code>)
	 * @param mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				long groupId, long folderId, String folderName, String fileName,
				java.io.File file, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addTempFileEntry(
			groupId, folderId, folderName, fileName, file, mimeType);
	}

	/**
	 * Adds a temporary file entry. It is created based on the {@link
	 * InputStream} object.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @param inputStream the file's data
	 * @param mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				long groupId, long folderId, String folderName, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addTempFileEntry(
			groupId, folderId, folderName, fileName, inputStream, mimeType);
	}

	/**
	 * Cancels the check out of the file entry. If a user has not checked out
	 * the specified file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to cancel the
	 checkout
	 * @throws PortalException if a portal exception occurred
	 * @see #checkInFileEntry(long, boolean, String, ServiceContext)
	 * @see #checkOutFileEntry(long, ServiceContext)
	 */
	public static void cancelCheckOut(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().cancelCheckOut(fileEntryId);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 #checkInFileEntry(long, DLVersionNumberIncrease, String,
	 ServiceContext)}
	 */
	@Deprecated
	public static void checkInFileEntry(
			long fileEntryId, boolean majorVersion, String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().checkInFileEntry(
			fileEntryId, majorVersion, changeLog, serviceContext);
	}

	/**
	 * Checks in the file entry. If a user has not checked out the specified
	 * file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to check in
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param changeLog the file's version change log
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkOutFileEntry(long, ServiceContext)
	 */
	public static void checkInFileEntry(
			long fileEntryId,
			com.liferay.document.library.kernel.model.DLVersionNumberIncrease
				dlVersionNumberIncrease,
			String changeLog,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().checkInFileEntry(
			fileEntryId, dlVersionNumberIncrease, changeLog, serviceContext);
	}

	/**
	 * Checks in the file entry using the lock's UUID. If a user has not checked
	 * out the specified file entry, invoking this method will result in no
	 * changes. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to check in
	 * @param lockUuid the lock's UUID
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkOutFileEntry(long, String, long, ServiceContext)
	 */
	public static void checkInFileEntry(
			long fileEntryId, String lockUuid,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().checkInFileEntry(fileEntryId, lockUuid, serviceContext);
	}

	/**
	 * Check out a file entry.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the file entry to check out
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkInFileEntry(long, boolean, String, ServiceContext)
	 */
	public static void checkOutFileEntry(
			long fileEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().checkOutFileEntry(fileEntryId, serviceContext);
	}

	/**
	 * Checks out the file entry. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the file entry to check out
	 * @param owner the owner string for the checkout (optionally
	 <code>null</code>)
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkInFileEntry(long, String)
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			checkOutFileEntry(
				long fileEntryId, String owner, long expirationTime,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().checkOutFileEntry(
			fileEntryId, owner, expirationTime, serviceContext);
	}

	/**
	 * Performs a deep copy of the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param sourceFolderId the primary key of the folder to copy
	 * @param parentFolderId the primary key of the new folder's parent folder
	 * @param name the new folder's name
	 * @param description the new folder's description
	 * @param serviceContext the service context to be applied
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder copyFolder(
			long repositoryId, long sourceFolderId, long parentFolderId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().copyFolder(
			repositoryId, sourceFolderId, parentFolderId, name, description,
			serviceContext);
	}

	/**
	 * Deletes the file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFileEntry(fileEntryId);
	}

	/**
	 * Deletes the file entry with the title in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param title the file entry's title
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileEntryByTitle(
			long repositoryId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFileEntryByTitle(repositoryId, folderId, title);
	}

	/**
	 * Deletes the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFileShortcut(fileShortcutId);
	}

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileVersion(long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFileVersion(fileVersionId);
	}

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param version the version label of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileVersion(long fileEntryId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFileVersion(fileEntryId, version);
	}

	/**
	 * Deletes the folder with the primary key and all of its subfolders and
	 * file entries.
	 *
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFolder(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFolder(folderId);
	}

	/**
	 * Deletes the folder with the name in the parent folder and all of its
	 * subfolders and file entries.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFolder(
			long repositoryId, long parentFolderId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteFolder(repositoryId, parentFolderId, name);
	}

	/**
	 * Deletes the temporary file entry.
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry was
	 eventually to reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public static void deleteTempFileEntry(
			long groupId, long folderId, String folderName, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteTempFileEntry(
			groupId, folderId, folderName, fileName);
	}

	/**
	 * Returns all the file entries in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @return the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(repositoryId, folderId);
	}

	/**
	 * Returns a name-ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(repositoryId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the file entries (optionally
	 <code>null</code>)
	 * @return the range of file entries in the folder ordered by comparator
	 <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(
			repositoryId, folderId, start, end, obc);
	}

	/**
	 * Returns the file entries with the file entry type in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @return the file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, long fileEntryTypeId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(
			repositoryId, folderId, fileEntryTypeId);
	}

	/**
	 * Returns a name-ordered range of all the file entries with the file entry
	 * type in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, long fileEntryTypeId,
				int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(
			repositoryId, folderId, fileEntryTypeId, start, end);
	}

	/**
	 * Returns an ordered range of all the file entries with the file entry type
	 * in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the results by (optionally
	 <code>null</code>)
	 * @return the range of file entries with the file entry type in the folder
	 ordered by <code>null</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, long fileEntryTypeId,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(
			repositoryId, folderId, fileEntryTypeId, start, end, obc);
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, String[] mimeTypes)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(repositoryId, folderId, mimeTypes);
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry> getFileEntries(
				long repositoryId, long folderId, String[] mimeTypes, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.FileEntry> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntries(
			repositoryId, folderId, mimeTypes, start, end, obc);
	}

	/**
	 * Returns a range of all the file entries and shortcuts in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List<Object> getFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, start, end);
	}

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status);
	}

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @param mimeTypes allowed media types
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, mimeTypes);
	}

	/**
	 * Returns the number of file entries in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @return the number of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFileEntriesCount(long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesCount(repositoryId, folderId);
	}

	/**
	 * Returns the number of file entries with the file entry type in the
	 * folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @return the number of file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFileEntriesCount(
			long repositoryId, long folderId, long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesCount(
			repositoryId, folderId, fileEntryTypeId);
	}

	public static int getFileEntriesCount(
			long repositoryId, long folderId, String[] mimeTypes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntriesCount(
			repositoryId, folderId, mimeTypes);
	}

	/**
	 * Returns the file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @return the file entry with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntry(long fileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntry(fileEntryId);
	}

	/**
	 * Returns the file entry with the title in the folder.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param folderId the primary key of the file entry's folder
	 * @param title the file entry's title
	 * @return the file entry with the title in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntry(long groupId, long folderId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntry(groupId, folderId, title);
	}

	/**
	 * Returns the file entry with the UUID and group.
	 *
	 * @param uuid the file entry's UUID
	 * @param groupId the primary key of the file entry's group
	 * @return the file entry with the UUID and group
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @return the file shortcut with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			getFileShortcut(long fileShortcutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileShortcut(fileShortcutId);
	}

	/**
	 * Returns the file version with the primary key.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the file version with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileVersion
			getFileVersion(long fileVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFileVersion(fileVersionId);
	}

	/**
	 * Returns the folder with the primary key.
	 *
	 * @param folderId the primary key of the folder
	 * @return the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolder(folderId);
	}

	/**
	 * Returns the folder with the name in the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @return the folder with the name in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			long repositoryId, long parentFolderId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolder(repositoryId, parentFolderId, name);
	}

	/**
	 * Returns all immediate subfolders of the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(repositoryId, parentFolderId);
	}

	/**
	 * Returns all immediate subfolders of the parent folder, optionally
	 * including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId,
				boolean includeMountFolders)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, includeMountFolders);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder, optionally including mount folders for third-party
	 * repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId,
				boolean includeMountFolders, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, includeMountFolders, start, end);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId,
				boolean includeMountFolders, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, includeMountFolders, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId, int status,
				boolean includeMountFolders, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, status, includeMountFolders, start,
			end, obc);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getFolders(
				long repositoryId, long parentFolderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFolders(
			repositoryId, parentFolderId, start, end, obc);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders, file
	 * entries, and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders, file entries, and
	 file shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				long repositoryId, long folderId, int status,
				boolean includeMountFolders, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, includeMountFolders, start, end);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders, file entries,
	 * and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the results (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders, file entries, and file
	 shortcuts in the parent folder ordered by comparator
	 <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				long repositoryId, long folderId, int status,
				boolean includeMountFolders, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?> obc)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, includeMountFolders, start, end,
			obc);
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				long repositoryId, long folderId, int status,
				String[] mimeTypes, boolean includeMountFolders,
				boolean includeOwner, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<?> obc)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, mimeTypes, includeMountFolders,
			includeOwner, start, end, obc);
	}

	public static java.util.List<Object>
			getFoldersAndFileEntriesAndFileShortcuts(
				long repositoryId, long folderId, int status,
				String[] mimeTypes, boolean includeMountFolders, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<?> obc)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcuts(
			repositoryId, folderId, status, mimeTypes, includeMountFolders,
			start, end, obc);
	}

	/**
	 * Returns the number of immediate subfolders, file entries, and file
	 * shortcuts in the parent folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders, file entries, and file
	 shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, includeMountFolders);
	}

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, mimeTypes, includeMountFolders);
	}

	public static int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersAndFileEntriesAndFileShortcutsCount(
			repositoryId, folderId, status, mimeTypes, includeMountFolders,
			includeOwner);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFoldersCount(long repositoryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersCount(repositoryId, parentFolderId);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFoldersCount(
			long repositoryId, long parentFolderId, boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersCount(
			repositoryId, parentFolderId, includeMountFolders);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFoldersCount(
			long repositoryId, long parentFolderId, int status,
			boolean includeMountFolders)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersCount(
			repositoryId, parentFolderId, status, includeMountFolders);
	}

	/**
	 * Returns the number of immediate subfolders and file entries across the
	 * folders.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderIds the primary keys of folders from which to count
	 immediate subfolders and file entries
	 * @param status the workflow status
	 * @return the number of immediate subfolders and file entries across the
	 folders
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getFoldersFileEntriesCount(
			long repositoryId, java.util.List<Long> folderIds, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getFoldersFileEntriesCount(
			repositoryId, folderIds, status);
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the repository default parent folder that are stored within the Liferay
	 * repository. This method is primarily used to search for recently modified
	 * file entries. It can be limited to the file entries modified by a given
	 * user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					long groupId, long userId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntries(groupId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the file entries in the group that are
	 * stored within the Liferay repository. This method is primarily used to
	 * search for recently modified file entries. It can be limited to the file
	 * entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the file entries (optionally
	 <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					long groupId, long userId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntries(
			groupId, userId, start, end, obc);
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					long groupId, long userId, long rootFolderId, int start,
					int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntries(
			groupId, userId, rootFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the file entries (optionally
	 <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					long groupId, long userId, long rootFolderId, int start,
					int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntries(
			groupId, userId, rootFolderId, start, end, obc);
	}

	public static java.util.List
		<com.liferay.portal.kernel.repository.model.FileEntry>
				getGroupFileEntries(
					long groupId, long userId, long rootFolderId,
					String[] mimeTypes, int status, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.portal.kernel.repository.model.FileEntry>
							obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntries(
			groupId, userId, rootFolderId, mimeTypes, status, start, end, obc);
	}

	/**
	 * Returns the number of file entries in a group starting at the repository
	 * default parent folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getGroupFileEntriesCount(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntriesCount(groupId, userId);
	}

	/**
	 * Returns the number of file entries in a group starting at the root folder
	 * that are stored within the Liferay repository. This method is primarily
	 * used to search for recently modified file entries. It can be limited to
	 * the file entries modified by a given user.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntriesCount(
			groupId, userId, rootFolderId);
	}

	public static int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getGroupFileEntriesCount(
			groupId, userId, rootFolderId, mimeTypes, status);
	}

	/**
	 * Returns all immediate subfolders of the parent folder that are used for
	 * mounting third-party repositories. This method is only supported by the
	 * Liferay repository.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder that are used for
	 mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				long repositoryId, long parentFolderId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMountFolders(repositoryId, parentFolderId);
	}

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder that are used for mounting third-party repositories. This
	 * method is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder that are used for mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				long repositoryId, long parentFolderId, int start, int end)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMountFolders(
			repositoryId, parentFolderId, start, end);
	}

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder that are used for mounting third-party repositories. This method
	 * is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param obc the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder that are
	 used for mounting third-party repositories ordered by comparator
	 <code>obc</code>
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List
		<com.liferay.portal.kernel.repository.model.Folder> getMountFolders(
				long repositoryId, long parentFolderId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.kernel.repository.model.Folder> obc)
			throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMountFolders(
			repositoryId, parentFolderId, start, end, obc);
	}

	/**
	 * Returns the number of immediate subfolders of the parent folder that are
	 * used for mounting third-party repositories. This method is only supported
	 * by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @return the number of folders of the parent folder that are used for
	 mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	public static int getMountFoldersCount(
			long repositoryId, long parentFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMountFoldersCount(repositoryId, parentFolderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static void getSubfolderIds(
			long repositoryId, java.util.List<Long> folderIds, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().getSubfolderIds(repositoryId, folderIds, folderId);
	}

	/**
	 * Returns all the descendant folders of the folder with the primary key.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List<Long> getSubfolderIds(
			long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getSubfolderIds(repositoryId, folderId);
	}

	/**
	 * Returns descendant folders of the folder with the primary key, optionally
	 * limiting to one level deep.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param recurse whether to recurse through each subfolder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static java.util.List<Long> getSubfolderIds(
			long repositoryId, long folderId, boolean recurse)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getSubfolderIds(repositoryId, folderId, recurse);
	}

	/**
	 * Returns all the temporary file entry names.
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @return the temporary file entry names
	 * @throws PortalException if a portal exception occurred
	 * @see #addTempFileEntry(long, long, String, String, File, String)
	 * @see TempFileEntryUtil
	 */
	public static String[] getTempFileNames(
			long groupId, long folderId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getTempFileNames(groupId, folderId, folderName);
	}

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long repositoryId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().lockFolder(repositoryId, folderId);
	}

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param owner the owner string for the checkout (optionally
	 <code>null</code>)
	 * @param inheritable whether the lock must propagate to descendants
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.lock.Lock lockFolder(
			long repositoryId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().lockFolder(
			repositoryId, folderId, owner, inheritable, expirationTime);
	}

	/**
	 * Moves the file entry to the new folder.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param newFolderId the primary key of the new folder
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntry(
				long fileEntryId, long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().moveFileEntry(
			fileEntryId, newFolderId, serviceContext);
	}

	/**
	 * Moves the folder to the new parent folder with the primary key.
	 *
	 * @param folderId the primary key of the folder
	 * @param parentFolderId the primary key of the new parent folder
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder moveFolder(
			long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().moveFolder(
			folderId, parentFolderId, serviceContext);
	}

	/**
	 * Refreshes the lock for the file entry. This method is primarily used by
	 * WebDAV.
	 *
	 * @param lockUuid the lock's UUID
	 * @param companyId the primary key of the file entry's company
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.lock.Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().refreshFileEntryLock(
			lockUuid, companyId, expirationTime);
	}

	/**
	 * Refreshes the lock for the folder. This method is primarily used by
	 * WebDAV.
	 *
	 * @param lockUuid the lock's UUID
	 * @param companyId the primary key of the file entry's company
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.lock.Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().refreshFolderLock(
			lockUuid, companyId, expirationTime);
	}

	/**
	 * Reverts the file entry to a previous version. A new version will be
	 * created based on the previous version and metadata.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param version the version to revert back to
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public static void revertFileEntry(
			long fileEntryId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().revertFileEntry(fileEntryId, version, serviceContext);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long repositoryId, long creatorUserId, int status, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().search(
			repositoryId, creatorUserId, status, start, end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long repositoryId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().search(
			repositoryId, creatorUserId, folderId, mimeTypes, status, start,
			end);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long repositoryId,
			com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.search.SearchException {

		return getService().search(repositoryId, searchContext);
	}

	public static com.liferay.portal.kernel.search.Hits search(
			long repositoryId,
			com.liferay.portal.kernel.search.SearchContext searchContext,
			com.liferay.portal.kernel.search.Query query)
		throws com.liferay.portal.kernel.search.SearchException {

		return getService().search(repositoryId, searchContext, query);
	}

	/**
	 * Subscribe the user to changes in documents of the file entry type. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public static void subscribeFileEntryType(
			long groupId, long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().subscribeFileEntryType(groupId, fileEntryTypeId);
	}

	/**
	 * Subscribe the user to document changes in the folder. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void subscribeFolder(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().subscribeFolder(groupId, folderId);
	}

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unlockFolder(
			long repositoryId, long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unlockFolder(repositoryId, folderId, lockUuid);
	}

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @param name the folder's name
	 * @param lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unlockFolder(
			long repositoryId, long parentFolderId, String name,
			String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unlockFolder(repositoryId, parentFolderId, name, lockUuid);
	}

	/**
	 * Unsubscribe the user from changes in documents of the file entry type.
	 * This method is only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unsubscribeFileEntryType(
			long groupId, long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unsubscribeFileEntryType(groupId, fileEntryTypeId);
	}

	/**
	 * Unsubscribe the user from document changes in the folder. This method is
	 * only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unsubscribeFolder(long groupId, long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unsubscribeFolder(groupId, folderId);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 #updateFileEntry(long, String, String, String, String,
	 String, DLVersionNumberIncrease, byte[], ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				boolean majorVersion, byte[] bytes,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, majorVersion, bytes, serviceContext);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 #updateFileEntry(long, String, String, String, String,
	 String, DLVersionNumberIncrease, File, ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				boolean majorVersion, java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, majorVersion, file, serviceContext);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 #updateFileEntry(long, String, String, String, String,
	 String, DLVersionNumberIncrease, InputStream, long,
	 ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				boolean majorVersion, java.io.InputStream is, long size,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, majorVersion, is, size, serviceContext);
	}

	/**
	 * Updates a file entry and associated metadata based on a byte array
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param bytes the file's data (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				byte[] bytes,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, dlVersionNumberIncrease, bytes, serviceContext);
	}

	/**
	 * Updates a file entry and associated metadata based on a {@link File}
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param file the file's data (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, dlVersionNumberIncrease, file, serviceContext);
	}

	/**
	 * Updates a file entry and associated metadata based on an {@link
	 * InputStream} object. If the file data is <code>null</code>, then only the
	 * associated metadata (i.e., <code>title</code>, <code>description</code>,
	 * and parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param is the file's data (optionally <code>null</code>)
	 * @param size the file's size (optionally <code>0</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.InputStream is, long size,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntry(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, dlVersionNumberIncrease, is, size, serviceContext);
	}

	/**
	 * @deprecated As of Judson (7.1.x),  As of Judson (7.1.x), replaced by
	 {@link #updateFileEntryAndCheckIn(long, String, String,
	 String, String, String, DLVersionNumberIncrease, File,
	 ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntryAndCheckIn(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				boolean majorVersion, java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntryAndCheckIn(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, majorVersion, file, serviceContext);
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 #updateFileEntryAndCheckIn(long, String, String, String,
	 String, String, DLVersionNumberIncrease, InputStream, long,
	 ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntryAndCheckIn(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				boolean majorVersion, java.io.InputStream is, long size,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntryAndCheckIn(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, majorVersion, is, size, serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntryAndCheckIn(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntryAndCheckIn(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, dlVersionNumberIncrease, file, serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntryAndCheckIn(
				long fileEntryId, String sourceFileName, String mimeType,
				String title, String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.InputStream is, long size,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileEntryAndCheckIn(
			fileEntryId, sourceFileName, mimeType, title, description,
			changeLog, dlVersionNumberIncrease, is, size, serviceContext);
	}

	/**
	 * Updates a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file shortcut's file entry
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			updateFileShortcut(
				long fileShortcutId, long folderId, long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFileShortcut(
			fileShortcutId, folderId, toFileEntryId, serviceContext);
	}

	/**
	 * Updates the folder.
	 *
	 * @param folderId the primary key of the folder
	 * @param name the folder's new name
	 * @param description the folder's new description
	 * @param serviceContext the service context to be applied. In a Liferay
	 repository, it may include:  <ul> <li> defaultFileEntryTypeId -
	 the file entry type to default all Liferay file entries to </li>
	 <li> dlFileEntryTypesSearchContainerPrimaryKeys - a
	 comma-delimited list of file entry type primary keys allowed in
	 the given folder and all descendants </li> <li> restrictionType -
	 specifying restriction type of file entry types allowed </li>
	 <li> workflowDefinitionXYZ - the workflow definition name
	 specified per file entry type. The parameter name must be the
	 string <code>workflowDefinition</code> appended by the
	 <code>fileEntryTypeId</code> (optionally <code>0</code>).</li>
	 </ul>
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder
			updateFolder(
				long folderId, String name, String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateFolder(
			folderId, name, description, serviceContext);
	}

	/**
	 * Returns <code>true</code> if the file entry is checked out. This method
	 * is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key for the repository
	 * @param fileEntryId the primary key for the file entry
	 * @param lockUuid the lock's UUID
	 * @return <code>true</code> if the file entry is checked out;
	 <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public static boolean verifyFileEntryCheckOut(
			long repositoryId, long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().verifyFileEntryCheckOut(
			repositoryId, fileEntryId, lockUuid);
	}

	public static boolean verifyFileEntryLock(
			long repositoryId, long fileEntryId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().verifyFileEntryLock(
			repositoryId, fileEntryId, lockUuid);
	}

	/**
	 * Returns <code>true</code> if the inheritable lock exists. This method is
	 * primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key for the repository
	 * @param folderId the primary key for the folder
	 * @param lockUuid the lock's UUID
	 * @return <code>true</code> if the inheritable lock exists;
	 <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public static boolean verifyInheritableLock(
			long repositoryId, long folderId, String lockUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().verifyInheritableLock(
			repositoryId, folderId, lockUuid);
	}

	public static DLAppService getService() {
		if (_service == null) {
			_service = (DLAppService)PortalBeanLocatorUtil.locate(
				DLAppService.class.getName());
		}

		return _service;
	}

	private static DLAppService _service;

}