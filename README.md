# nitpeek
Detects features in text based on applying custom rules.
The core library comes with a selection of powerful predefined analyzers that may be used as building blocks for more specific needs.

Uses a pluggable architecture with custom plugins detected using ServiceLoader.

The modular implementation allows for varied frontends. The current default UI is a minimal console application that executes all rule-sets exported by all available plugins.


# Getting started
To install the console application:  
`gradlew :app:installDist`

To build the example plugin:  
`gradlew :demo-plugin:assemble`

- Place the resulting jar (demo-plugin/build/libs/demo-plugin.jar) inside the plugins folder of the app module (app/build/install/plugins).  
- Place a PDF file to be analyzed in the inputPDF folder (app/build/install/inputPDF), for instance lib-io/src/test/resources/nitpeek/io/pdf/TestFile.pdf.  
Alternatively (or in addition), place a DOCX file to be analyzed in the inputDOCX folder (app/build/install/inputDOCX), for instance lib-io/src/test/resources/nitpeek/io/docx/TestFile.docx.  
- To run the application, execute the start script for your platform from app/build/install/bin/.  
E.g. on Windows:  
`cd .\app\build\install\app\bin`  
`.\app.bat`  
- This should result in a new PDF file under app/build/install/outputPDF and/or a new DOCX file under app/build/install/outputDOCX.  
Given the demo-plugin and TestFile.pdf described above, this file should be called nitpicked_TestFile.pdf and should contain a single comment on the first page suggesting the word 'test' be deleted.


# API considerations
The API as a whole is not yet in a stable state. Major breaking changes may still be made.  
Whenever a collection is returned by the API, it is to be assumed unmodifiable, unless otherwise specified.
