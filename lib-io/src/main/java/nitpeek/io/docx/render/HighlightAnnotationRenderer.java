package nitpeek.io.docx.render;

import jakarta.xml.bind.JAXBElement;
import nitpeek.io.docx.internal.pagesource.DocxSegment;
import org.docx4j.XmlUtils;
import org.docx4j.wml.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public final class HighlightAnnotationRenderer implements AnnotationRenderer {

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final HighlightColor highlightColor;

    public HighlightAnnotationRenderer(HighlightColor highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void renderAnnotation(RenderableAnnotation annotation) {
        for (var segment : annotation.segments()) {
            highlightSegment(segment, annotation.message().author() + ": " + annotation.message().messageText());
        }
    }

    private void highlightSegment(DocxSegment segment, String authorString) {
        var runsToHighlight = segment.getRuns();
        for (var run : runsToHighlight) {
            highlightRun(run, authorString);
        }
    }

    private void highlightRun(R run, String authorString) {
        var runProperties = getOrCreateRunProperties(run);
        trackChangeWithAuthor(runProperties, authorString);
        var highlight = new Highlight();
        highlight.setVal(highlightColor.getName());
        runProperties.setHighlight(highlight);
    }

    private RPr getOrCreateRunProperties(R run) {
        var existingProperties = run.getRPr();
        if (existingProperties != null) return existingProperties;

        var newProperties = objectFactory.createRPr();
        run.setRPr(newProperties);

        return newProperties;
    }

    private void trackChangeWithAuthor(RPr runProperties, String authorString) {
        var change = objectFactory.createCTRPrChange();
        change.setAuthor(authorString);
        change.setRPr(copyOriginalProperties(runProperties));
        runProperties.setRPrChange(change);
    }

    private XMLGregorianCalendar getCurrentDateTimeUtc() {
        var nowUtc = Instant.now().atZone(ZoneOffset.UTC);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(nowUtc.toString());
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

    private CTRPrChange.RPr copyOriginalProperties(RPr runProperties) {
        var properties = objectFactory.createCTRPrChangeRPr();

        var propertyList = properties.getEGRPrBase();
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrB(runProperties.getB()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrBCs(runProperties.getBCs()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrI(runProperties.getI()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrICs(runProperties.getICs()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrCaps(runProperties.getCaps()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSmallCaps(runProperties.getSmallCaps()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrStrike(runProperties.getStrike()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrDstrike(runProperties.getDstrike()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrOutline(runProperties.getOutline()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrShadow(runProperties.getShadow()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrEmboss(runProperties.getEmboss()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrImprint(runProperties.getImprint()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrNoProof(runProperties.getNoProof()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSnapToGrid(runProperties.getSnapToGrid()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrVanish(runProperties.getVanish()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrWebHidden(runProperties.getWebHidden()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSpacing(runProperties.getSpacing()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrW(runProperties.getW()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrKern(runProperties.getKern()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrPosition(runProperties.getPosition()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSz(runProperties.getSz()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSzCs(runProperties.getSzCs()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrEffect(runProperties.getEffect()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrBdr(runProperties.getBdr()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrShd(runProperties.getShd()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrFitText(runProperties.getFitText()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrVertAlign(runProperties.getVertAlign()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrRtl(runProperties.getRtl()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrCs(runProperties.getCs()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrEm(runProperties.getEm()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrLang(runProperties.getLang()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrEastAsianLayout(runProperties.getEastAsianLayout()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrSpecVanish(runProperties.getSpecVanish()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrOMath(runProperties.getOMath()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrLigatures(runProperties.getLigatures()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrNumForm(runProperties.getNumForm()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrNumSpacing(runProperties.getNumSpacing()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrStylisticSets(runProperties.getStylisticSets()));
        addIfNotNull(propertyList, objectFactory.createCTRPrChangeRPrCntxtAlts(runProperties.getCntxtAlts()));

        return properties;
    }

    private void addIfNotNull(List<Object> existingProperties, JAXBElement<?> element) {
        if (element.getValue() == null) return;
        existingProperties.add(XmlUtils.deepCopy(element));
    }

    public enum HighlightColor {
        BLACK("black"),
        BLUE("blue"),
        CYAN("cyan"),
        GREEN("green"),
        MAGENTA("magenta"),
        RED("red"),
        YELLOW("yellow"),
        WHITE("white"),
        DARK_BLUE("darkBlue"),
        DARK_CYAN("darkCyan"),
        DARK_GREEN("darkGreen"),
        DARK_MAGENTA("darkMagenta"),
        DARK_RED("darkRed"),
        DARK_YELLOW("darkYellow"),
        DARK_GRAY("darkGray"),
        LIGHT_GRAY("lightGray"),
        NONE("none");
        private final String name;

        public String getName() {
            return name;
        }

        HighlightColor(String name) {
            this.name = name;
        }
    }
}
