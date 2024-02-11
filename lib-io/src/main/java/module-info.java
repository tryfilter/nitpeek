open module nitpeek.lib.io {
    requires nitpeek.lib.core;
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.slf4j;

    requires jdk.unsupported; // more complex workarounds may be required in the future. See app/build.gradle.kts prior to this commit.
    requires org.docx4j.core;
    requires org.docx4j.JAXB_ReferenceImpl;
    requires jakarta.xml.bind;

    exports nitpeek.io.pdf;
    exports nitpeek.io.pdf.convenience;
    exports nitpeek.io.docx;
}