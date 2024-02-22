package nitpeek.io.docx.internal.pagesource;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.openpackaging.parts.JaxbXmlPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.ArrayList;
import java.util.List;

// These helper methods feel a bit hacky, but they are quite convenient.
public final class DocxUtil {
    private DocxUtil() {
    }

    public static List<JAXBElement<?>> keepJaxbElements(List<Object> objects) {
        var result = new ArrayList<JAXBElement<?>>();
        for (var object : objects) {
            if (object instanceof JAXBElement<?> jaxb) result.add(jaxb);
        }
        return result;
    }

    public static <T> List<T> keepElementsOfType(List<?> elements, Class<T> clazz) {
        return elements.stream().filter(clazz::isInstance).map(clazz::cast).toList();
    }

    public static <T> T getRelatedObject(RelationshipsPart r, Class<T> clazz) {
        var relationships = r.getRelationships().getRelationship();
        var parts = filterToXmlParts(relationships.stream().map(r::getPart).toList());
        var relevantRelationships = keepElementsOfType(parts.stream().map(JaxbXmlPart::getJaxbElement).toList(), clazz);
        if (relevantRelationships.isEmpty()) return null;
        return relevantRelationships.getFirst();
    }

    private static List<JaxbXmlPart<?>> filterToXmlParts(List<Part> parts) {
        var result = new ArrayList<JaxbXmlPart<?>>();
        for (var part : parts) {
            if (part instanceof JaxbXmlPart<?> jaxb) result.add(jaxb);
        }
        return result;
    }

    public static List<R> getRuns(P paragraph) {
        return keepElementsOfType(paragraph.getContent(), R.class);
    }

    public static List<P> getAllParagraphs(List<Object> paragraphs) {
        return DocxUtil.keepElementsOfType(paragraphs, P.class);
    }

    public static List<P> getNonEmptyParagraphs(List<Object> paragraphs) {
        return getAllParagraphs(paragraphs).stream().filter(p -> !DocxUtil.isEmpty(p)).toList();
    }

    public static boolean isEmpty(P paragraph) {
        return DocxUtil.getRuns(paragraph).isEmpty();
    }

    public static int getIndexOfLastRun(List<P> paragraphs) {
        if (paragraphs.isEmpty()) return -1;
        var lastParagraph = paragraphs.getLast();
        return getIndexOfLastRun(lastParagraph);
    }

    public static int getIndexOfLastRun(P paragraph) {
        var runs = getRuns(paragraph);
        return runs.size() - 1;
    }
}
