package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TutorialMdParser {

    public static TutorialData parse(String fileId) {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected();
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "tutorials/" + lang + "/" + fileId + ".md");
        String content;
        try {
            content = readResource(loc);
        } catch (Exception e) {
            loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "tutorials/en_us/" + fileId + ".md");
            try {
                content = readResource(loc);
            } catch (Exception ex) {
                return new TutorialData(fileId, fileId, "", new ArrayList<>());
            }
        }
        return parseContent(fileId, content);
    }

    private static String readResource(ResourceLocation loc) throws IOException {
        var optional = Minecraft.getInstance().getResourceManager().getResource(loc);
        if (optional.isEmpty()) throw new IOException("Resource not found: " + loc);
        var resource = optional.get();
        try (var reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private static TutorialData parseContent(String id, String content) {
        String[] lines = content.split("\n");
        String title = "";
        StringBuilder brief = new StringBuilder();
        List<TutorialLine> contentLines = new ArrayList<>();

        String section = "";
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.startsWith("![title]")) {
                section = "title";
                continue;
            } else if (line.startsWith("![brief]")) {
                section = "brief";
                continue;
            } else if (line.startsWith("![content]")) {
                section = "content";
                continue;
            }

            if (section.equals("title") && !line.isEmpty()) {
                title = line;
                section = "";
            } else if (section.equals("brief")) {
                if (line.isEmpty()) {
                    section = "";
                } else {
                    if (!brief.isEmpty()) brief.append(" ");
                    brief.append(line);
                }
            } else if (section.equals("content")) {
                contentLines.add(parseLine(line));
            }
        }

        return new TutorialData(id, title, brief.toString(), contentLines);
    }

    private static TutorialLine parseLine(String line) {
        if (line.isEmpty()) {
            return new TutorialLine(TutorialLine.Type.EMPTY, "", null, "");
        }

        if (line.startsWith("![")) {
            int closeBracket = line.indexOf("]");
            int openParen = line.indexOf("(", closeBracket);
            int closeParen = line.indexOf(")", openParen);
            if (closeBracket > 0 && openParen > closeBracket && closeParen > openParen) {
                String alt = line.substring(2, closeBracket);
                String path = line.substring(openParen + 1, closeParen);
                ResourceLocation loc = null;
                if (path.contains(":")) {
                    String[] parts = path.split(":", 2);
                    loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
                } else {
                    loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, path);
                }
                return new TutorialLine(TutorialLine.Type.IMAGE, alt, loc, "");
            }
        }

        if (line.startsWith("## ")) {
            return new TutorialLine(TutorialLine.Type.H2, "", null, processInline(line.substring(3).trim()));
        }

        if (line.startsWith("#")) {
            String text = line.substring(1).trim();
            return new TutorialLine(TutorialLine.Type.H1, "", null, processInline(text));
        }

        return new TutorialLine(TutorialLine.Type.TEXT, "", null, processInline(line));
    }

    private static String processInline(String text) {
        if (text == null || text.isEmpty()) return "";
        String result = text;

        result = result.replace("![misakaname]", "{@MISAKANAME@}");

        while (true) {
            int start = result.indexOf("__");
            if (start == -1) break;
            int end = result.indexOf("__", start + 2);
            if (end == -1) break;
            String bold = result.substring(start + 2, end);
            result = result.substring(0, start) + "§l" + bold + "§r" + result.substring(end + 2);
        }

        while (true) {
            int start = result.indexOf("![key");
            if (start == -1) break;
            int end = result.indexOf("]", start);
            if (end == -1) break;
            String keyContent = result.substring(start + 2, end);
            String keyName = "Key";
            int idStart = keyContent.indexOf("id=\"");
            if (idStart != -1) {
                int idEnd = keyContent.indexOf("\"", idStart + 4);
                if (idEnd != -1) {
                    keyName = keyContent.substring(idStart + 4, idEnd);
                }
            }
            result = result.substring(0, start) + "[§b" + keyName + "§r]" + result.substring(end + 1);
        }
        return result;
    }

    public record TutorialData(String id, String title, String brief, List<TutorialLine> contentLines) {}

    public record TutorialLine(Type type, String alt, ResourceLocation image, String text) {
        public enum Type {
            H1, H2, TEXT, IMAGE, EMPTY
        }
    }
}
