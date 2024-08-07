package com.nitpeek.core.api.common;

import java.util.List;

/**
 * Represents some point of interest in a text. This may be a problem found in the text, or a statistic about one of the pages, etc.
 */
public interface Feature {

    /**
     * @return a value between 0 and 1 inclusive representing how confident the program is that
     * this instance of Feature is not a false positive.
     */
    double getConfidence();

    FeatureType getType();

    /**
     * @return a list of individual issues that make up the problem; this may be a single-element list, but it should
     * generally not be empty, as there would be no reference to a location in the text
     */
    List<FeatureComponent> getComponents();
}
