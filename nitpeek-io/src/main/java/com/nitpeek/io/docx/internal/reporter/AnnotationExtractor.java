package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.io.docx.types.CompositeRun;

import java.util.List;

interface AnnotationExtractor<C extends CompositeRun> {

    /**
     * Extract from an underlying DOCX document annotations that correspond to the provided features
     *
     * @param features a list of features produced by an {@code Analyzer}/{@code Rule}
     * @param i18n     a translation object that facilitates internationalizing the messages that characterize the annotations
     * @return a list of abstract annotations corresponding to the representation of the provided {@code features} in some
     * specific context of a DOCX document
     */
    List<DocxAnnotation<C>> extractAnnotations(List<Feature> features, Translation i18n);
}
