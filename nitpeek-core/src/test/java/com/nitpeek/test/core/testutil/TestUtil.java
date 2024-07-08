package com.nitpeek.test.core.testutil;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.impl.common.SimpleFeature;
import com.nitpeek.core.impl.common.SimpleFeatureType;

import java.util.Arrays;
import java.util.List;

public final class TestUtil {
    private TestUtil() {
    }

    public static TextPage emptyPage(int pageNumber) {
        return new SimpleTextPage(List.of(), pageNumber);
    }

    public static Feature featureFromComponents(SimpleFeatureType type, FeatureComponent... components) {
        return new SimpleFeature(type, Arrays.asList(components), 0);
    }
}
