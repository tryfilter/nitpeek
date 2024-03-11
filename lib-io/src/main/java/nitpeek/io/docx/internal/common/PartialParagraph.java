package nitpeek.io.docx.internal.common;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.wml.P;

import java.util.List;

/**
 * This type represents an abstract counterpart to the {@link P} type. It represents a line in terms of
 * {@link TextCoordinate} coordinates and contains a subset of all runs of the corresponding
 * {@link P} paragraph.
 */
public final class PartialParagraph<C extends CompositeRun> implements DocxParagraph<C> {
    private final List<C> runs;
    private final Partitioner<DocxParagraph<C>, C> partitioner;

    public PartialParagraph(List<? extends C> runs) {
        this.runs = List.copyOf(runs);
        partitioner = new Partitioner<>(this.runs, PartialParagraph::new);
    }

    @Override
    public DocxParagraph<C> fullPartition() {
        return partitioner.fullPartition();
    }

    @Override
    public DocxParagraph<C> partitionBetween(int firstIndex, int lastIndex) {
        return partitioner.partitionBetween(firstIndex, lastIndex);
    }

    @Override
    public DocxParagraph<C> partitionFrom(int firstIndex) {
        return partitioner.partitionFrom(firstIndex);
    }

    @Override
    public DocxParagraph<C> partitionTo(int lastIndex) {
        return partitioner.partitionTo(lastIndex);
    }


    @Override
    public List<C> runs() {
        return runs;
    }
}
