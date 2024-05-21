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
        var existingHighlight = runProperties.getHighlight();
        var newHighlight = new Highlight();
        if (existingHighlight != null && highlightColor.getName().equals(existingHighlight.getVal())) {
            newHighlight.setVal(highlightColor.closestColor().getName());
        } else {
            newHighlight.setVal(highlightColor.getName());
        }

        runProperties.setHighlight(newHighlight);
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
        BLACK("black") {
            @Override
            public HighlightColor closestColor() {
                return DARK_GRAY;
            }
        },
        BLUE("blue") {
            @Override
            public HighlightColor closestColor() {
                return DARK_BLUE;
            }
        },
        CYAN("cyan") {
            @Override
            public HighlightColor closestColor() {
                return DARK_CYAN;
            }
        },
        GREEN("green") {
            @Override
            public HighlightColor closestColor() {
                return DARK_GREEN;
            }
        },
        MAGENTA("magenta") {
            @Override
            public HighlightColor closestColor() {
                return DARK_MAGENTA;
            }
        },
        RED("red") {
            @Override
            public HighlightColor closestColor() {
                return DARK_RED;
            }
        },
        YELLOW("yellow") {
            @Override
            public HighlightColor closestColor() {
                return DARK_YELLOW;
            }
        },
        WHITE("white") {
            @Override
            public HighlightColor closestColor() {
                return LIGHT_GRAY;
            }
        },
        DARK_BLUE("darkBlue") {
            @Override
            public HighlightColor closestColor() {
                return BLUE;
            }
        },
        DARK_CYAN("darkCyan") {
            @Override
            public HighlightColor closestColor() {
                return CYAN;
            }
        },
        DARK_GREEN("darkGreen") {
            @Override
            public HighlightColor closestColor() {
                return GREEN;
            }
        },
        DARK_MAGENTA("darkMagenta") {
            @Override
            public HighlightColor closestColor() {
                return MAGENTA;
            }
        },
        DARK_RED("darkRed") {
            @Override
            public HighlightColor closestColor() {
                return RED;
            }
        },
        DARK_YELLOW("darkYellow") {
            @Override
            public HighlightColor closestColor() {
                return YELLOW;
            }
        },
        DARK_GRAY("darkGray") {
            @Override
            public HighlightColor closestColor() {
                return LIGHT_GRAY;
            }
        },
        LIGHT_GRAY("lightGray") {
            @Override
            public HighlightColor closestColor() {
                return DARK_GRAY;
            }
        },
        NONE("none") {
            @Override
            public HighlightColor closestColor() {
                return NONE;
            }
        };
        private final String name;

        public String getName() {
            return name;
        }

        HighlightColor(String name) {
            this.name = name;
        }

        public abstract HighlightColor closestColor();
    }
}