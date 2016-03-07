package org.gooru.nuclues.search.indexers.app.utils;

import flexjson.transformer.AbstractTransformer;

/**
 * @author Renuka
 * 
 */
public class VoidTransformer extends AbstractTransformer {

	@Override
	public Boolean isInline() {
		return true;
	}

	@Override
	public void transform(Object object) {
	}

}
