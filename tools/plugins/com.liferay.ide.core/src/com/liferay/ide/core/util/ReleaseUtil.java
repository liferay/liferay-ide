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

package com.liferay.ide.core.util;

import com.liferay.release.util.ReleaseEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Drew Brokke
 */
public class ReleaseUtil extends com.liferay.release.util.ReleaseUtil {

	public static String[] getProductGroupVersions() {
		if (_productGroupVersions == null) {
			Stream<ReleaseEntry> releaseEntryStream = getReleaseEntryStream();

			_productGroupVersions = releaseEntryStream.map(
				ReleaseEntry::getProductGroupVersion
			).distinct(
			).toArray(
				String[]::new
			);
		}

		return _productGroupVersions;
	}

	public static String[] getProductVersions(boolean showAll) {
		Stream<ReleaseEntry> releaseEntryStream = getReleaseEntryStream();

		return releaseEntryStream.filter(
			releaseEntry -> {
				if (showAll) {
					return true;
				}

				return releaseEntry.isPromoted();
			}
		).map(
			ReleaseEntry::getReleaseKey
		).toArray(
			String[]::new
		);
	}

	public static ReleaseEntry getReleaseEntry(String version) {
		return getReleaseEntry(null, version);
	}

	public static ReleaseEntry getReleaseEntry(String product, String version) {
		Predicate<ReleaseEntry> productGroupVersionPredicate = releaseEntry -> Objects.equals(
			releaseEntry.getProductGroupVersion(), version);
		Predicate<ReleaseEntry> productPredicate = releaseEntry -> Objects.equals(releaseEntry.getProduct(), product);
		Predicate<ReleaseEntry> targetPlatformVersionPredicate = releaseEntry -> Objects.equals(
			releaseEntry.getTargetPlatformVersion(), version);

		List<Predicate<ReleaseEntry>> predicates = new ArrayList<>();

		predicates.add(releaseEntry -> Objects.equals(releaseEntry.getReleaseKey(), version));

		predicates.add(productPredicate.and(targetPlatformVersionPredicate));

		predicates.add(productPredicate.and(productGroupVersionPredicate));

		predicates.add(targetPlatformVersionPredicate);

		predicates.add(productGroupVersionPredicate);

		for (Predicate<ReleaseEntry> predicate : predicates) {
			Stream<ReleaseEntry> releaseEntryStream = getReleaseEntryStream();

			Optional<ReleaseEntry> first = releaseEntryStream.filter(
				predicate
			).findFirst();

			if (first.isPresent()) {
				return first.get();
			}
		}

		return null;
	}

	private static String[] _productGroupVersions;

}