package nitpeek.core.api.report;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.translate.Translation;

import java.util.List;

public interface Reporter {

    /**
     * @throws ReportingException when the implementation was unable to report all features
     */
    void reportFeatures(List<Feature> features, Translation i18n) throws ReportingException;
}
