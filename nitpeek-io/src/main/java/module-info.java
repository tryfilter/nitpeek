module com.nitpeek.lib.io {
    requires com.nitpeek.lib.core;
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.slf4j;

    requires jdk.unsupported; // more complex workarounds may be required in the future. See app/build.gradle.kts prior to this commit.
    requires org.docx4j.core;
    requires org.docx4j.JAXB_ReferenceImpl;
    requires jakarta.xml.bind;
    requires org.checkerframework.checker.qual;

    exports com.nitpeek.io.pdf;
    exports com.nitpeek.io.pdf.util;
    exports com.nitpeek.io.docx;
    exports com.nitpeek.io.docx.util;
    exports com.nitpeek.io.docx.render;
}