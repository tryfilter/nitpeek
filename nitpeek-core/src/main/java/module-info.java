module com.nitpeek.lib.core {

    exports com.nitpeek.core.api.common;
    exports com.nitpeek.core.api.analyze;
    exports com.nitpeek.core.api.util;
    exports com.nitpeek.core.api.report;
    exports com.nitpeek.core.api.process;
    exports com.nitpeek.core.api.config;
    exports com.nitpeek.core.api.plugin;
    exports com.nitpeek.core.api.translate;
    exports com.nitpeek.core.impl.analyze.analyzer;
    exports com.nitpeek.core.impl.analyze.notify;
    exports com.nitpeek.core.impl.analyze.transformer;
    exports com.nitpeek.core.impl.analyze;
    exports com.nitpeek.core.impl.common;
    exports com.nitpeek.core.impl.process;
    exports com.nitpeek.core.impl.config;
    exports com.nitpeek.core.api.config.standard;
    exports com.nitpeek.core.api.config.standard.footnote;
    exports com.nitpeek.core.impl.report;
    exports com.nitpeek.core.impl.translate;

    // Compiler warns that it can't find the com.nitpeek.test.lib.core module, but this seems like a bogus warning.
    // Not only does the module exist, but its tests compile successfully, which they would not without the qualified exports.
    // Probably another quirk of Gradle's module path handling.
    // The warnings seem safe to ignore.
    exports com.nitpeek.core.internal to com.nitpeek.test.lib.core;
    exports com.nitpeek.core.impl.translate.helper to com.nitpeek.test.lib.core;
}