package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.common.DocxUtil;
import org.docx4j.XmlUtils;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;

import java.util.Optional;

final class TextRunSplitter implements RunSplitter {
    private final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public SplitResult splitAfter(R runToSplit, int lastIndexToKeep) {
        String originalText = DocxUtil.getTextValue(runToSplit);
        if (originalText.length() <= 1 || lastIndexToKeep >= originalText.length())
            return new SplitResult(runToSplit, Optional.empty()); // nothing to split
        String firstRunText = originalText.substring(0, lastIndexToKeep + 1);
        String secondRunText = originalText.substring(lastIndexToKeep + 1);

        return new SplitResult(updateExisting(runToSplit, firstRunText), Optional.of(createNewFrom(runToSplit, secondRunText)));
    }

    private R createNewFrom(R run, String text) {
        R splitOff = clone(run);
        setTextContent(splitOff, text);
        return splitOff;
    }

    private R updateExisting(R run, String newText) {
        setTextContent(run, newText);
        return run;
    }

    private void setTextContent(R run, String text) {
        var textElement = getOrCreateTextElement(run);
        setTextSpacePreserving(textElement, text);
    }

    private Text getOrCreateTextElement(R run) {
        var existingTextElement = DocxUtil.getText(run).orElse(null);
        if (existingTextElement != null) return existingTextElement;

        var newTextElement = objectFactory.createText();
        run.getContent().add(objectFactory.createRT(newTextElement));
        return newTextElement;
    }

    private void setTextSpacePreserving(Text text, String value) {
        if (text == null) return;

        text.setValue(value);
        text.setSpace("preserve");
    }

    private R clone(R run) {
        R clone = objectFactory.createR();
        var runProperties = run.getRPr();
        if (runProperties == null) return clone;
        clone.setRPr(cloneRunProperties(runProperties));
        return clone;
    }

    private RPr cloneRunProperties(RPr properties) {
        return XmlUtils.deepCopy(properties);
    }
}