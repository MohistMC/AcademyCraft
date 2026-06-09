package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * GitHub 风格 Markdown 解析器。
 * 支持：标题 h1-h4、粗体、斜体、删除线、行内代码、代码块、无序列表、有序列表、
 * 引用、水平线、链接、图片、表格。
 */
public class TutorialMdParser {

    // 匹配带 alt 的图片/资源引用: ![alt](path)
    private static final Pattern IMAGE_PAT = Pattern.compile("!\\[([^]]*)]\\(([^)]+)\\)");
    // 粗体: **text** 或 __text__
    private static final Pattern BOLD_PAT = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern BOLD2_PAT = Pattern.compile("__(.+?)__");
    // 斜体: *text* 或 _text_ (但不匹配 ** 和 __)
    private static final Pattern ITALIC_PAT = Pattern.compile("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)");
    private static final Pattern ITALIC2_PAT = Pattern.compile("(?<!_)_(?!_)(.+?)(?<!_)_(?!_)");
    // 删除线: ~~text~~
    private static final Pattern STRIKE_PAT = Pattern.compile("~~(.+?)~~");
    // 行内代码: `code`
    private static final Pattern CODE_PAT = Pattern.compile("`([^`]+)`");
    // 链接: [text](url)
    private static final Pattern LINK_PAT = Pattern.compile("\\[([^]]+)]\\(([^)]+)\\)");

    public static TutorialData parse(String fileId) {
        String lang = Minecraft.getInstance().getLanguageManager().getSelected();
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID,
                "tutorials/" + lang + "/" + fileId + ".md");
        String content;
        try {
            content = readResource(loc);
        } catch (Exception e) {
            loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID,
                    "tutorials/en_us/" + fileId + ".md");
            try {
                content = readResource(loc);
            } catch (Exception ex) {
                return new TutorialData(fileId, fileId, "", new ArrayList<>());
            }
        }
        return parseContent(fileId, content);
    }

    private static String readResource(ResourceLocation loc) throws IOException {
        var res = Minecraft.getInstance().getResourceManager().getResource(loc)
                .orElseThrow(() -> new IOException("Not found: " + loc));
        try (var r = new BufferedReader(new InputStreamReader(res.open(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        }
    }

    private static TutorialData parseContent(String id, String content) {
        String[] rawLines = content.split("\n");
        String title = "";
        StringBuilder brief = new StringBuilder();
        List<TutorialLine> result = new ArrayList<>();

        Section section = Section.NONE;
        List<String> codeBuffer = new ArrayList<>();
        List<List<String>> tableBuffer = new ArrayList<>();

        for (String raw : rawLines) {
            String line = raw.stripTrailing();

            // 代码块切换
            if (line.trim().startsWith("```")) {
                if (!codeBuffer.isEmpty()) {
                    result.add(new TutorialLine(Type.CODE_BLOCK, "", null,
                            String.join("\n", codeBuffer)));
                    codeBuffer.clear();
                }
                section = section == Section.CODE ? Section.NONE : Section.CODE;
                continue;
            }
            if (section == Section.CODE) {
                codeBuffer.add(line);
                continue;
            }

            // Section markers
            if (line.trim().startsWith("![title]")) { section = Section.TITLE; continue; }
            if (line.trim().startsWith("![brief]")) { section = Section.BRIEF; continue; }
            if (line.trim().startsWith("![content]")) { section = Section.CONTENT; continue; }

            switch (section) {
                case TITLE -> {
                    if (!line.trim().isEmpty()) { title = line.trim(); section = Section.NONE; }
                }
                case BRIEF -> {
                    if (line.trim().isEmpty()) section = Section.NONE;
                    else { if (!brief.isEmpty()) brief.append(" "); brief.append(line.trim()); }
                }
                case CONTENT -> {
                    TutorialLine parsed = parseLine(line.trim());
                    if (parsed != null) result.add(parsed);
                }
            }
        }
        // Flush remaining code block
        if (!codeBuffer.isEmpty()) {
            result.add(new TutorialLine(Type.CODE_BLOCK, "", null,
                    String.join("\n", codeBuffer)));
        }
        return new TutorialData(id, title, brief.toString(), result);
    }

    private static TutorialLine parseLine(String line) {
        if (line.isEmpty()) return new TutorialLine(Type.EMPTY, "", null, "");

        // Horizontal rule
        if (line.matches("^-{3,}$") || line.matches("^\\*{3,}$"))
            return new TutorialLine(Type.HR, "", null, "");

        // Table row
        if (line.startsWith("|") && line.endsWith("|")) {
            String[] cells = line.substring(1, line.length() - 1).split("\\|");
            List<String> row = new ArrayList<>();
            for (String c : cells) row.add(c.trim());
            if (line.contains("---")) return new TutorialLine(Type.TABLE_SEP, "", null, "");
            return new TutorialLine(Type.TABLE_ROW, "", null, "", row);
        }

        // Image: ![alt](path)
        Matcher imgM = IMAGE_PAT.matcher(line);
        if (imgM.matches()) {
            String alt = imgM.group(1);
            String path = imgM.group(2);
            ResourceLocation loc;
            if (path.contains(":")) {
                String[] parts = path.split(":", 2);
                loc = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
            } else {
                loc = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, path);
            }
            return new TutorialLine(Type.IMAGE, alt, loc, "");
        }

        // Heading
        if (line.startsWith("#### ")) return line(Type.H4, line.substring(5).trim());
        if (line.startsWith("### "))  return line(Type.H3, line.substring(4).trim());
        if (line.startsWith("## "))   return line(Type.H2, line.substring(3).trim());
        if (line.startsWith("# "))    return line(Type.H1, line.substring(2).trim());

        // Blockquote
        if (line.startsWith("> "))    return line(Type.QUOTE, line.substring(2).trim());

        // Unordered list
        if (line.matches("^[-*]\\s.+")) return line(Type.LI, line.replaceFirst("^[-*]\\s", ""));

        // Ordered list
        if (line.matches("^\\d+\\.\\s.+")) return line(Type.OL, line.replaceFirst("^\\d+\\.\\s", ""));

        // Plain text
        return line(Type.TEXT, line);
    }

    /** 处理行内格式：粗体、斜体、删除线、行内代码、链接 */
    static String processInline(String text) {
        if (text == null || text.isEmpty()) return "";
        String s = text;
        s = s.replace("![misakaname]", "{@MISAKANAME@}");
        // Apply inline patterns (order matters — bold before italic)
        s = BOLD_PAT.matcher(s).replaceAll("§l$1§r");
        s = BOLD2_PAT.matcher(s).replaceAll("§l$1§r");
        s = ITALIC_PAT.matcher(s).replaceAll("§o$1§r");
        s = ITALIC2_PAT.matcher(s).replaceAll("§o$1§r");
        s = STRIKE_PAT.matcher(s).replaceAll("§m$1§r");
        s = CODE_PAT.matcher(s).replaceAll("§7$1§r");
        s = processKeyTags(s);
        return s;
    }

    private static String processKeyTags(String text) {
        String result = text;
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
                if (idEnd != -1) keyName = keyContent.substring(idStart + 4, idEnd);
            }
            result = result.substring(0, start) + "[§b" + keyName + "§r]" + result.substring(end + 1);
        }
        return result;
    }

    private enum Section { NONE, TITLE, BRIEF, CONTENT, CODE }

    // ==================== Types ====================

    public enum Type {
        H1, H2, H3, H4, TEXT, IMAGE, EMPTY,
        CODE_BLOCK, LI, OL, QUOTE, HR, TABLE_ROW, TABLE_SEP
    }

    public record TutorialData(String id, String title, String brief, List<TutorialLine> contentLines) {}

    public record TutorialLine(Type type, String alt, ResourceLocation image, String text) {
        public TutorialLine(Type type, String alt, ResourceLocation image, String text) {
            this.type = type;
            this.alt = alt;
            this.image = image;
            this.text = text;
        }

        // 为 TABLE_ROW 使用（cells 存在 text 中）
        public List<String> cells() {
            return text.isEmpty() ? List.of() : List.of(text.split("\t"));
        }

        // Table row constructor
        public TutorialLine(Type type, String alt, ResourceLocation image, String text, List<String> cells) {
            this(type, alt, image, String.join("\t", cells));
        }
    }

    private static TutorialLine line(Type type, String text) {
        return new TutorialLine(type, "", null, text);
    }
}
