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

package com.liferay.ide.upgrade.plan.ui.internal;

import com.liferay.ide.upgrade.plan.core.Pair;

import java.net.URL;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.osgi.framework.Bundle;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
public class BundleImageLabelProvider extends LabelProvider {

	public BundleImageLabelProvider(Function<Object, Pair<Bundle, String>> imagePathMapper) {
		_images = new HashMap<>();

		_imagePathMapper = imagePathMapper;
	}

	@Override
	public void dispose() {
		Collection<Image> values = _images.values();

		Stream<Image> stream = values.stream();

		stream.forEach(Image::dispose);

		super.dispose();
	}

	@Override
	public Image getImage(Object element) {
		if (element != null) {
			return Optional.ofNullable(
				element
			).map(
				_imagePathMapper
			).filter(
				Objects::nonNull
			).map(
				pair -> {
					Bundle bundle = pair.first();

					return bundle.getEntry(pair.second());
				}
			).filter(
				Objects::nonNull
			).map(
				this::_getImage
			).orElse(
				super.getImage(element)
			);
		}

		return super.getImage(element);
	}

	private Image _getImage(URL url) {
		return _images.computeIfAbsent(
			url,
			u -> {
				ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

				return imageDescriptor.createImage();
			});
	}

	private final Function<Object, Pair<Bundle, String>> _imagePathMapper;
	private final Map<URL, Image> _images;

}