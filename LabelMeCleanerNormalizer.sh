#!/bin/sh

#indicate here your path to LabelMeworkbench
cd /home/nicolas/apps/LabelMeWorkbench/LabelMeWorkbench-0.45-2010.07.08-SNAPSHOT

libraries="./lib/commons-codec-1.3.jar:\
./lib/commons-httpclient-3.1.jar:\
./lib/commons-io-1.4.jar:\
./lib/commons-lang-2.4.jar:\
./lib/commons-logging-1.1.1.jar:\
./lib/conja-1.03-SNAPSHOT.jar:\
./lib/db.jar:\
./lib/dbxml.jar:\
./lib/FSExplorer-0.19-2010.06.29.jar:\
./lib/gpcj.jar:\
./lib/jiu.jar:\
./lib/log4j-1.2.15.jar:\
./lib/jai/jai_codec.jar:\
./lib/jai/jai_core.jar:\
./lib/jai/mlibwrapper_jai.jar:\
./lib/org-netbeans-swing-outline.jar:\
./lib/FSExplorer-0.19-2010.06.29.jar:\
./lib/Tinker-0.8-2010.07.09.jar:\
./lib/cbiaproto3-0.4-2010.06.30.jar:\
./lib/lucene-core-3.0.2.jar:\
./lib/batik-1.8pre/batik-all.jar:\
./lib/batik-1.8pre/batik-anim.jar:\
./lib/batik-1.8pre/batik-awt-util.jar:\
./lib/batik-1.8pre/batik-bridge.jar:\
./lib/batik-1.8pre/batik-codec.jar:\
./lib/batik-1.8pre/batik-css.jar:\
./lib/batik-1.8pre/batik-dom.jar:\
./lib/batik-1.8pre/batik-extension.jar:\
./lib/batik-1.8pre/batik-ext.jar:\
./lib/batik-1.8pre/batik-gui-util.jar:\
./lib/batik-1.8pre/batik-gvt.jar:\
./lib/batik-1.8pre/batik-parser.jar:\
./lib/batik-1.8pre/batik-script.jar:\
./lib/batik-1.8pre/batik-svg-dom.jar:\
./lib/batik-1.8pre/batik-svggen.jar:\
./lib/batik-1.8pre/batik-swing.jar:\
./lib/batik-1.8pre/batik-transcoder.jar:\
./lib/batik-1.8pre/batik-util.jar:\
./lib/batik-1.8pre/batik-xml.jar:\
./lib/batik-1.8pre/js.jar:\
./lib/batik-1.8pre/pdf-transcoder.jar:\
./lib/batik-1.8pre/xalan-2.6.0.jar:\
./lib/batik-1.8pre/xerces_2_5_0.jar:\
./lib/batik-1.8pre/xml-apis-ext.jar:\
./lib/batik-1.8pre/xml-apis.jar:\
./lib/jmagine-0.34-2010.07.01.jar:\
./lib/Galatee-1.44-2010.07.09.jar:\
./LabelMeWorkbench-0.46-2010.07.28.jar"

#for i586
#export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:./lib/jai/jai_i586/
#java -cp ${libraries} net.trevize.labelme.browser.LabelMeBrowser

#for amd64
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:./lib/jai/jai_amd64/
java -cp ${libraries} net.trevize.labelme.LabelMeCleanerNormalizer

#pure java mode
#java -cp ${libraries} net.trevize.labelme.browser.LabelMeBrowser
