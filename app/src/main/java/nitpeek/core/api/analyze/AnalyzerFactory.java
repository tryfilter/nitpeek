package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.Analyzer;

public interface AnalyzerFactory {

    Analyzer createAnalyzer();
}
