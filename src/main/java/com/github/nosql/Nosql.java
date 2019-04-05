package com.github.nosql;

import com.github.underscore.lodash.Json;
import com.github.underscore.lodash.U;
import com.github.underscore.lodash.Xml;
import org.apache.commons.cli.Options;

public class Nosql {
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        Options options = new Options();
        options.addOption("s", "src", true, "The source file/directory which should be converted from xml/json.");
        options.addOption("d", "dest", true, "The destanation file/directory for converted file(s).");
        options.addOption("mo", "mode", true, "The processing file mode (xmltojson|jsontoxml).");
    }
}

