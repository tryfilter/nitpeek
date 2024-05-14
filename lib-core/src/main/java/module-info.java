module nitpeek.lib.core {
    requires org.slf4j;

    exports nitpeek.core.api.common;
    exports nitpeek.core.api.analyze;
    exports nitpeek.core.api.util;
    exports nitpeek.core.api.report;
    exports nitpeek.core.api.process;
    exports nitpeek.core.api.config;
    exports nitpeek.core.api.plugin;
    exports nitpeek.core.api.translate;
    exports nitpeek.core.impl.analyze.analyzer;
    exports nitpeek.core.impl.analyze.notify;
    exports nitpeek.core.impl.analyze.transformer;
    exports nitpeek.core.impl.analyze;
    exports nitpeek.core.impl.common;
    exports nitpeek.core.impl.process;
    exports nitpeek.core.impl.config;
    exports nitpeek.core.api.config.standard;
    exports nitpeek.core.api.config.standard.footnote;
    exports nitpeek.core.impl.report;
    exports nitpeek.core.impl.translate;

    // Compiler warns that it can't find the test.nitpeek.lib.core module, but this seems like a bogus warning.
    // Not only does the module exist, but its tests compile successfully, which they would not without the qualified exports.
    // Probably another quirk of Gradle's module path handling.
    // The warnings seem safe to ignore.
    exports nitpeek.core.internal to test.nitpeek.lib.core;
    exports nitpeek.core.impl.translate.helper to test.nitpeek.lib.core;
}