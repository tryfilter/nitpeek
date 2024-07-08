package com.nitpeek.core.api.process;

import com.nitpeek.core.api.common.Feature;

import java.util.List;

public interface FeatureProducingProcessor extends ProcessorWithResult<List<Feature>> {

    default List<Feature> getFeatures() {
        return getProcessingResult();
    }
}