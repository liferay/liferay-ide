package com.liferay.knowledgebase.model.impl;

import com.liferay.knowledgebase.model.KBArticle;
import com.liferay.knowledgebase.model.KBArticleModel;

import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.model.impl.BaseModelImpl;
import java.util.Date;

@JSON(strict = true)
public class KBArticleModelImpl extends BaseModelImpl<KBArticle>
	implements KBArticleModel {

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_columnBitmask = -1L;

		_modifiedDate = modifiedDate;
	}

	@Override
	public int compareTo(KBArticle kbArticle) {
		int value = 0;

		value = DateUtil.compareTo(getModifiedDate(),
				kbArticle.getModifiedDate(),true);
		
		value = DateUtil.compareTo(getModifiedDate(),
				kbArticle.getModifiedDate());

		value = value * -1;

		if (value != 0) {
			return value;
		}

		return 0;
	}

	private Date _modifiedDate;
}