import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Path> cssFilePaths = Files
                .list(Path.of("./inputs"))
                .filter(f -> f.toString().endsWith(".css"))
                .toList();

        Map<String, Map<String, String>> json = new HashMap<>();
        for (Path cssPath : cssFilePaths) {
            String fileName = cssPath.getFileName().toString();
            Map<String, String> colors = new HashMap<>();
            json.put(fileName.substring(0, fileName.length()-4), colors);
            String text = Files.readString(cssPath);

            for (String property : getProperties(text)) {
                property = property.trim();
                if (!property.startsWith("--")) {
                    continue;
                }
                String[] keyValuePair = property.split(":");
                String key = keyValuePair[0].trim();
                String value = keyValuePair[1].trim();
                if (!key.contains("color")) {
                    continue;
                }
                String jsonColorKey = key.substring(2);
                if (value.startsWith("#")) {
                    colors.put(jsonColorKey, value);
                } else {
                    if (!value.startsWith("var(--")) {
                        System.err.println("Warning: " + property + " in " + fileName);
                        continue;
                    }
                    String varProperty = value.substring(6, value.length() - 1);
                    String mappedValue = colors.get(varProperty);
                    if (mappedValue == null) {
                        System.err.println("Warning, property " + varProperty + " is not found yet");
                        continue;
                    }
                    colors.put(jsonColorKey, mappedValue);
                }
            }
        }


        System.out.println(json);
    }

    public static String[] getProperties(String text) {
        int rootIndex = text.indexOf(":root");
        char[] textChars = text.toCharArray();
        int depth = 0;
        int start = -1;
        int end = -1;
        for (int i = rootIndex + ":root".length(), LUB = textChars.length; i < LUB; ++i) {
            char c = textChars[i];
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            }
            if (c == '}') {
                if (depth == 1) {
                    end = i;
                    break;
                }
                depth--;
            }
        }
        String rootStyles = text.substring(start + 1, end).trim();
        String[] properties = rootStyles.split(";");
        return properties;
    }

}