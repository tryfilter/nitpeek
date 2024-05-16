package nitpeek.io.docx.render;

import jakarta.xml.bind.JAXBElement;
import nitpeek.io.docx.types.CompositeRun;
import org.docx4j.XmlUtils;
import org.docx4j.wml.*;

import java.util.List;
import java.util.function.Function;

public final class HighlightAnnotationRenderer implements AnnotationRenderer {

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final HighlightColor highlightColor;

    public HighlightAnnotationRenderer(HighlightColor highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void renderAnnotation(RenderableAnnotation annotation) {
        highlightRuns(annotation.runs(), annotation.message().author() + ": " + annotation.message().messageText());
    }

    private void highlightRuns(List<? extends CompositeRun> runs, String authorString) {
        for (var run : runs) {
            highlightCompositeRun(run, authorString);
        }
    }

    private void highlightCompositeRun(CompositeRun run, String authorString) {
        for (R r : run.componentRuns()) {
            highlightRun(r, authorString);
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
    
    private CTRPrChange.RPr copyOriginalProperties(RPr runProperties) {
        var properties = objectFactory.createCTRPrChangeRPr();

        var propertyList = properties.getEGRPrBase();
        copyFont(propertyList, runProperties);
        copyColor(propertyList, runProperties);
        copyStyle(propertyList, runProperties);
        copyHighlight(propertyList, runProperties);
        copyUnderline(propertyList, runProperties);

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

    private void copyFont(List<Object> targetProperties, RPr sourceRunProperties) {
        copyDeepIfNotNull(targetProperties, sourceRunProperties, RPrAbstract::getRFonts);
    }

    private void copyColor(List<Object> targetProperties, RPr sourceRunProperties) {
        copyDeepIfNotNull(targetProperties, sourceRunProperties, RPrAbstract::getColor);
    }

    private void copyStyle(List<Object> targetProperties, RPr sourceRunProperties) {
        copyDeepIfNotNull(targetProperties, sourceRunProperties, RPrAbstract::getRStyle);
    }

    private void copyHighlight(List<Object> targetProperties, RPr sourceRunProperties) {
        copyDeepIfNotNull(targetProperties, sourceRunProperties, RPrAbstract::getHighlight);
    }

    private void copyUnderline(List<Object> targetProperties, RPr sourceRunProperties) {
        copyDeepIfNotNull(targetProperties, sourceRunProperties, RPrAbstract::getU);
    }

    private void copyDeepIfNotNull(List<Object> targetProperties, RPr sourceRunProperties, Function<RPr, ?> propertyExtractor) {
        var value = propertyExtractor.apply(sourceRunProperties);
        if (value != null) targetProperties.add(XmlUtils.deepCopy(value));
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
