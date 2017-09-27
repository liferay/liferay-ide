/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package org.liferay.jukebox.portlet;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.trash.util.TrashUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.liferay.jukebox.AlbumNameException;
import org.liferay.jukebox.ArtistNameException;
import org.liferay.jukebox.DuplicatedSongException;
import org.liferay.jukebox.SongNameException;
import org.liferay.jukebox.model.Album;
import org.liferay.jukebox.model.Artist;
import org.liferay.jukebox.model.Song;
import org.liferay.jukebox.service.AlbumServiceUtil;
import org.liferay.jukebox.service.ArtistServiceUtil;
import org.liferay.jukebox.service.SongServiceUtil;

/**
 * @author Julio Camarero
 * @author Sergio González
 * @author Eudaldo Alonso
 */
public class JukeboxPortlet extends MVCPortlet {

	public void addAlbum(ActionRequest request, ActionResponse response)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(request);

		long artistId = ParamUtil.getLong(uploadPortletRequest, "artistId");
		String name = ParamUtil.getString(uploadPortletRequest, "name");
		int year = ParamUtil.getInteger(uploadPortletRequest, "year");

		InputStream inputStream = uploadPortletRequest.getFileAsStream("file");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Album.class.getName(), uploadPortletRequest);

		try {
			AlbumServiceUtil.addAlbum(
				artistId, name, year, inputStream, serviceContext);

			SessionMessages.add(request, "albumAdded");

			String redirect = ParamUtil.getString(
				uploadPortletRequest, "redirect");

			response.sendRedirect(redirect);
		}
		catch (Exception e) {
			SessionErrors.add(request, e.getClass().getName());

			if (e instanceof AlbumNameException ||
				e instanceof PrincipalException) {

				response.setRenderParameter(
					"jspPage", "/html/albums/edit_album.jsp");
			}
			else {
				response.setRenderParameter("jspPage", "/html/error.jsp");
			}
		}
	}

	public void addArtist(ActionRequest request, ActionResponse response)
			throws Exception {

			UploadPortletRequest uploadPortletRequest =
				PortalUtil.getUploadPortletRequest(request);

			String name = ParamUtil.getString(uploadPortletRequest, "name");

			String bio = ParamUtil.getString(uploadPortletRequest, "bio");

			InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Artist.class.getName(), uploadPortletRequest);

			try {
				ArtistServiceUtil.addArtist(
					name, bio, inputStream, serviceContext);

				SessionMessages.add(request, "artistAdded");

				String redirect = ParamUtil.getString(
					uploadPortletRequest, "redirect");

				response.sendRedirect(redirect); }
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				if (e instanceof ArtistNameException ||
					e instanceof PrincipalException) {

					response.setRenderParameter(
						"jspPage", "/html/artists/edit_artist.jsp");
				}
				else {
					response.setRenderParameter("jspPage", "/html/error.jsp");
				}
			}
		}

		public void deleteArtist(ActionRequest request, ActionResponse response)
			throws Exception {

			long artistId = ParamUtil.getLong(request, "artistId");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Artist.class.getName(), request);

			try {
				ArtistServiceUtil.deleteArtist(artistId, serviceContext);

				SessionMessages.add(request, "artistDeleted");

				sendRedirect(request, response);
			}
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				response.setRenderParameter("jspPage", "/html/error.jsp");
			}
		}

		public void updateArtist(ActionRequest request, ActionResponse response)
			throws Exception {

			UploadPortletRequest uploadPortletRequest =
				PortalUtil.getUploadPortletRequest(request);

			long artistId = ParamUtil.getLong(uploadPortletRequest, "artistId");
			String name = ParamUtil.getString(uploadPortletRequest, "name");
			String bio = ParamUtil.getString(uploadPortletRequest, "bio");

			InputStream inputStream = uploadPortletRequest.getFileAsStream(
				"file");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Artist.class.getName(), uploadPortletRequest);

			try {
				ArtistServiceUtil.updateArtist(
					artistId, name, bio, inputStream, serviceContext);

				SessionMessages.add(request, "artistUpdated");

				String redirect = ParamUtil.getString(
					uploadPortletRequest, "redirect");

				response.sendRedirect(redirect);
			}
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				if (e instanceof ArtistNameException ||
					e instanceof PrincipalException) {

					response.setRenderParameter(
						"jspPage", "/html/artists/edit_artist.jsp");
				}
				else {
					response.setRenderParameter("jspPage", "/html/error.jsp");
				}
			}
		}

	public void addSong(ActionRequest request, ActionResponse response)
			throws Exception {

			UploadPortletRequest uploadPortletRequest =
				PortalUtil.getUploadPortletRequest(request);

			long albumId = ParamUtil.getLong(uploadPortletRequest, "albumId");
			String name = ParamUtil.getString(uploadPortletRequest, "name");

			InputStream songInputStream = uploadPortletRequest.getFileAsStream(
				"songFile");
			String songFileName = uploadPortletRequest.getFileName("songFile");

			InputStream lyricsInputStream =
				uploadPortletRequest.getFileAsStream("lyricsFile");
			String lyricsFileName = uploadPortletRequest.getFileName(
				"lyricsFile");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Song.class.getName(), uploadPortletRequest);

			try {
				SongServiceUtil.addSong(
					albumId, name, songFileName, songInputStream,
					lyricsFileName, lyricsInputStream, serviceContext);

				SessionMessages.add(request, "songAdded");

				String redirect = ParamUtil.getString(
					uploadPortletRequest, "redirect");

				response.sendRedirect(redirect);
			}
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				if (e instanceof SongNameException ||
					e instanceof DuplicatedSongException ||
					e instanceof PrincipalException) {

					response.setRenderParameter(
						"jspPage", "/html/songs/edit_song.jsp");
				}
				else {
					response.setRenderParameter("jspPage", "/html/error.jsp");
				}
			}
		}

		public void deleteSong(ActionRequest request, ActionResponse response)
			throws Exception {

			long songId = ParamUtil.getLong(request, "songId");

			boolean moveToTrash = ParamUtil.getBoolean(request, "moveToTrash");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Song.class.getName(), request);

			try {
				if (moveToTrash) {
					Song song = SongServiceUtil.moveSongToTrash(songId);

					Map<String, String[]> data =
						new HashMap<String, String[]>();

					data.put(
						"deleteEntryClassName",
						new String[] {Song.class.getName()});
					data.put(
						"deleteEntryTitle",
						new String[] {
							TrashUtil.getOriginalTitle(song.getName())});
					data.put(
						"restoreEntryIds",
						new String[] {String.valueOf(songId)});

					SessionMessages.add(
						request,
						PortalUtil.getPortletId(request) +
							SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA,
						data);

					SessionMessages.add(
						request,
						PortalUtil.getPortletId(request) +
							SessionMessages.
								KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
				}
				else {
					SongServiceUtil.deleteSong(songId, serviceContext);

					SessionMessages.add(request, "songDeleted");
				}

				sendRedirect(request, response);
			}
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				response.setRenderParameter("jspPage", "/html/error.jsp");
			}
		}

		public void restoreSong(ActionRequest request, ActionResponse response)
			throws Exception {

			long[] restoreEntryIds = StringUtil.split(
				ParamUtil.getString(request, "restoreEntryIds"), 0L);

			for (long restoreEntryId : restoreEntryIds) {
				SongServiceUtil.restoreSongFromTrash(restoreEntryId);
			}
		}

		public void updateSong(ActionRequest request, ActionResponse response)
			throws Exception {

			UploadPortletRequest uploadPortletRequest =
				PortalUtil.getUploadPortletRequest(request);

			long albumId = ParamUtil.getLong(uploadPortletRequest, "albumId");
			long songId = ParamUtil.getLong(uploadPortletRequest, "songId");
			String name = ParamUtil.getString(uploadPortletRequest, "name");

			InputStream songInputStream = uploadPortletRequest.getFileAsStream(
				"songFile");
			String songFileName = uploadPortletRequest.getFileName("songFile");

			InputStream lyricsInputStream =
				uploadPortletRequest.getFileAsStream("lyricsFile");
			String lyricsFileName = uploadPortletRequest.getFileName(
				"lyricsFile");

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				Song.class.getName(), uploadPortletRequest);

			try {
				SongServiceUtil.updateSong(
					songId, albumId, name, songFileName, songInputStream,
					lyricsFileName, lyricsInputStream, serviceContext);

				SessionMessages.add(request, "songUpdated");

				String redirect = ParamUtil.getString(
					uploadPortletRequest, "redirect");

				response.sendRedirect(redirect);
			}
			catch (Exception e) {
				SessionErrors.add(request, e.getClass().getName());

				if (e instanceof SongNameException ||
					e instanceof PrincipalException) {

					response.setRenderParameter(
						"jspPage", "/html/songs/edit_song.jsp");
				}
				else {
					response.setRenderParameter("jspPage", "/html/error.jsp");
				}
			}
		}

	public void deleteAlbum(ActionRequest request, ActionResponse response)
		throws Exception {

		long albumId = ParamUtil.getLong(request, "albumId");

		boolean moveToTrash = ParamUtil.getBoolean(request, "moveToTrash");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Album.class.getName(), request);

		try {
			if (moveToTrash) {
				Album album = AlbumServiceUtil.moveAlbumToTrash(albumId);

				Map<String, String[]> data = new HashMap<String, String[]>();

				data.put(
					"deleteEntryClassName",
					new String[] {Album.class.getName()});
				data.put("deleteEntryTitle", new String[] {album.getName()});
				data.put(
					"restoreEntryIds", new String[] {String.valueOf(albumId)});

				SessionMessages.add(
					request,
					PortalUtil.getPortletId(request) +
						SessionMessages.KEY_SUFFIX_DELETE_SUCCESS_DATA, data);

				SessionMessages.add(
					request,
					PortalUtil.getPortletId(request) +
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);
			}
			else {
				AlbumServiceUtil.deleteAlbum(albumId, serviceContext);

				SessionMessages.add(request, "albumDeleted");
			}

			sendRedirect(request, response);
		}
		catch (Exception e) {
			SessionErrors.add(request, e.getClass().getName());

			response.setRenderParameter("jspPage", "/html/error.jsp");
		}
	}

	public void restoreAlbum(ActionRequest request, ActionResponse response)
		throws Exception {

		long[] restoreEntryIds = StringUtil.split(
			ParamUtil.getString(request, "restoreEntryIds"), 0L);

		for (long restoreEntryId : restoreEntryIds) {
			AlbumServiceUtil.restoreAlbumFromTrash(restoreEntryId);
		}
	}

	public void updateAlbum(ActionRequest request, ActionResponse response)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			PortalUtil.getUploadPortletRequest(request);

		long albumId = ParamUtil.getLong(uploadPortletRequest, "albumId");
		long artistId = ParamUtil.getLong(uploadPortletRequest, "artistId");
		String name = ParamUtil.getString(uploadPortletRequest, "name");
		int year = ParamUtil.getInteger(uploadPortletRequest, "year");

		InputStream inputStream = uploadPortletRequest.getFileAsStream("file");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Album.class.getName(), uploadPortletRequest);

		try {
			AlbumServiceUtil.updateAlbum(
				albumId, artistId, name, year, inputStream, serviceContext);

			SessionMessages.add(request, "albumUpdated");

			String redirect = ParamUtil.getString(
					uploadPortletRequest, "redirect");

				response.sendRedirect(redirect);
		}
		catch (Exception e) {
			SessionErrors.add(request, e.getClass().getName());

			if (e instanceof AlbumNameException ||
				e instanceof PrincipalException) {

				response.setRenderParameter(
					"jspPage", "/html/albums/edit_album.jsp");
			}
			else {
				response.setRenderParameter("jspPage", "/html/error.jsp");
			}
		}
	}

}