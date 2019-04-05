package com.github.nosql;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.underscore.lodash.Json;
import com.github.underscore.lodash.U;
import com.github.underscore.lodash.Xml;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class Nosql {
    private static final String HEADER = "Nosql translation tool";
    private static final String MESSAGE = "For docs, license, tests, and downloads, see: https://github.com/php4dev/nosql-translations";

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        Options options = new Options();
        options.addOption("s", "src", true, "The source file/directory which should be converted from xml/json.");
        options.addOption("d", "dest", true, "The destanation file/directory for converted file(s).");
        options.addOption("mo", "mode", true, "The processing file mode (xmltojson|jsontoxml).");
        options.addOption("?", "help", false, "This help text.");
        if (args.length > 0) {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);
            } catch (ParseException e) {
                help(options);
                return;
            }

            if (cmd.hasOption("?") || (!cmd.hasOption("s") && !cmd.hasOption("c"))) {
                help(options);
                return;
            }

            processCommandLine(cmd);
            return;
        }
        help(options);
    }

    private static void help(Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp(100, "java -jar nosql.jar", HEADER, options, MESSAGE);
    }

    private static void processCommandLine(CommandLine cmd) throws Exception {
        String sourceFileOrPath = ".";
        String targetFileOrPath = ".";
        String mode = "xmltojson";
        if (cmd.hasOption("s")) {
            sourceFileOrPath = cmd.getOptionValue("s");
        }
        if (cmd.hasOption("d")) {
            targetFileOrPath = cmd.getOptionValue("d");
        }
        if (cmd.hasOption("mo")) {
            mode = cmd.getOptionValue("mo");
        }
        if ("xmltojson".equals(mode)) {
            if (!new File(sourceFileOrPath).isFile()) {
                System.out.println("File not found: " + sourceFileOrPath);
            }
            File file = new File(sourceFileOrPath);
            try {
                final byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath(), new String[0]));
                String text = new String(removeBom(bytes), StandardCharsets.UTF_8);
                Map<String, Object> result = (Map<String, Object>) Xml.fromXml(text);
                List<Map<String, Object>> texts = (List<Map<String, Object>>) U.get(result, "document.texts.text");
                System.out.println("Found texts: " + texts.size());
                List<Map<String, Object>> textsEn = new ArrayList<>();
                List<Map<String, Object>> textsDe = new ArrayList<>();
                List<Map<String, Object>> textsRu = new ArrayList<>();
                for (Map<String, Object> textItem : texts) {
                    Map<String, Object> textItemEn = new LinkedHashMap<>();
                    textItemEn.put("-id", textItem.get("-id"));
                    textItemEn.put("en", textItem.get("en"));
                    textsEn.add(textItemEn);
                    Map<String, Object> textItemDe = new LinkedHashMap<>();
                    textItemDe.put("-id", textItem.get("-id"));
                    textItemDe.put("de", textItem.get("de"));
                    textsDe.add(textItemDe);
                    Map<String, Object> textItemRu = new LinkedHashMap<>();
                    textItemRu.put("-id", textItem.get("-id"));
                    textItemRu.put("ru", textItem.get("ru"));
                    textsRu.add(textItemRu);
                }
                final Path pathEn = Paths.get("", new String[] { file.getPath().replaceFirst("\\.xml$", "-en.json") });
                final Path pathDe = Paths.get("", new String[] { file.getPath().replaceFirst("\\.xml$", "-de.json") });
                final Path pathRu = Paths.get("", new String[] { file.getPath().replaceFirst("\\.xml$", "-ru.json") });
                Files.write(pathEn, Json.toJson(textsEn).getBytes(StandardCharsets.UTF_8));
                Files.write(pathDe, Json.toJson(textsDe).getBytes(StandardCharsets.UTF_8));
                Files.write(pathRu, Json.toJson(textsRu).getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                System.out.println(file.getPath() + " - " + ex.getMessage());
            }
        } else if ("jsontoxml".equals(mode)) {
            File file = new File(sourceFileOrPath);
            File fileEn = new File(sourceFileOrPath.replaceFirst("\\.xml$", "-en.json"));
            File fileDe = new File(sourceFileOrPath.replaceFirst("\\.xml$", "-de.json"));
            File fileRu = new File(sourceFileOrPath.replaceFirst("\\.xml$", "-ru.json"));
            File fileUa = new File(sourceFileOrPath.replaceFirst("\\.xml$", "-ua.json"));
            try {
                final byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath(), new String[0]));
                String text = new String(removeBom(bytes), StandardCharsets.UTF_8);
                Map<String, Object> result = (Map<String, Object>) Xml.fromXml(text);
                List<Map<String, Object>> texts = (List<Map<String, Object>>) U.get(result, "document.texts.text");

                final byte[] bytesEn = Files.readAllBytes(Paths.get(fileEn.getAbsolutePath(), new String[0]));
                String textEn = new String(bytesEn, StandardCharsets.UTF_8);
                final byte[] bytesDe = Files.readAllBytes(Paths.get(fileDe.getAbsolutePath(), new String[0]));
                String textDe = new String(bytesDe, StandardCharsets.UTF_8);
                final byte[] bytesRu = Files.readAllBytes(Paths.get(fileRu.getAbsolutePath(), new String[0]));
                String textRu = new String(bytesRu, StandardCharsets.UTF_8);
                final byte[] bytesUa = Files.readAllBytes(Paths.get(fileUa.getAbsolutePath(), new String[0]));
                String textUa = new String(bytesUa, StandardCharsets.UTF_8);
                List<Map<String, Object>> resultEn = (List<Map<String, Object>>) Json.fromJson(textEn);
                List<Map<String, Object>> resultDe = (List<Map<String, Object>>) Json.fromJson(textDe);
                List<Map<String, Object>> resultRu = (List<Map<String, Object>>) Json.fromJson(textRu);
                List<Map<String, Object>> resultUa = (List<Map<String, Object>>) Json.fromJson(textUa);
                int index = 0;
                for (Map<String, Object> textItem : texts) {
                    textItem.put("-id", resultEn.get(index).get("-id"));
                    textItem.put("en", resultEn.get(index).get("en"));
                    textItem.put("de", resultDe.get(index).get("de"));
                    textItem.put("ru", resultRu.get(index).get("ru"));
                    textItem.put("ua", resultUa.get(index).get("ua"));
                    index += 1;
                }
                final Path pathTarget = Paths.get("", new String[] { targetFileOrPath });
                Files.write(pathTarget, Xml.toXml(result).getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                System.out.println(file.getPath() + " - " + ex.getMessage());
            }
         }
    }

    private static byte[] removeBom(byte[] bytes) {
        if ((bytes.length >= 3) && (bytes[0] == -17) && (bytes[1] == -69) && (bytes[2] == -65)) {
            return Arrays.copyOfRange(bytes, 3, bytes.length);
        }
        if ((bytes.length >= 2) && (bytes[0] == -1) && (bytes[1] == -2)) {
            return Arrays.copyOfRange(bytes, 2, bytes.length);
        }
        if ((bytes.length >= 2) && (bytes[0] == -2) && (bytes[1] == -1)) {
            return Arrays.copyOfRange(bytes, 2, bytes.length);
        }
        return bytes;
    }
}


