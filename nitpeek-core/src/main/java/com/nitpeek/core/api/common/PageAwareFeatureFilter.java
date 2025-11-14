package com.nitpeek.core.api.common;

import com.nitpeek.core.api.process.PageConsumer;

public interface PageAwareFeatureFilter extends FeatureFilter, PageConsumer<Void> {

}
