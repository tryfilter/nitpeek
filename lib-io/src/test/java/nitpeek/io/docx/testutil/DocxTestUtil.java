package nitpeek.io.docx.testutil;

import org.docx4j.wml.*;

public final class DocxTestUtil {

    private static final ObjectFactory objectFactory = new ObjectFactory();

    private DocxTestUtil() {
    }

    public static R createSampleRun(String text) {
        R run = objectFactory.createR();
        addText(run, text);
        return run;
    }

    private static void addText(R run, String text) {
        var textElement = objectFactory.createText();
        textElement.setValue(text);
        run.getContent().add(objectFactory.createRT(textElement));
    }

    public static void applyStyle(R run) {
        RPr runProperties = new RPr();
        runProperties.setI(new BooleanDefaultTrue());
        runProperties.setB(new BooleanDefaultTrue());
        run.setRPr(runProperties);
    }
}
