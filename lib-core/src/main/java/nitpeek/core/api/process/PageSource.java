package nitpeek.core.api.process;

public interface PageSource {
    <R> R dischargeTo(PageConsumer<R> consumer);
}