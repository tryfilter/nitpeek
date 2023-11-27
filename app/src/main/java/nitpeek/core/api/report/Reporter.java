package nitpeek.core.api.report;

import nitpeek.core.api.common.Feature;

import java.util.List;

public interface Reporter {

    void reportFeatures(List<Feature> features);
}
