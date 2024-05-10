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
            if (fileName.equals("dark_note.css")) {
                continue;
            }
            Map<String, String> colors = new HashMap<>();
            json.put(fileName.substring(0, fileName.length()-4), colors);

            List<String> lines = Files.readAllLines(cssPath);
            for (String line : lines) {
                String trimmedLine = line.trim();
                if (!trimmedLine.startsWith("--")) {
                    continue;
                }
                String[] parts = trimmedLine.split(":");
                String lhs = parts[0].trim();
                String rhs = parts[1].trim();
                if (!lhs.contains("color")) {
                    continue;
                }

                int semiIndex = rhs.indexOf(';');
                String color = rhs.trim().substring(0, semiIndex);
                String jsonColorKey = lhs.substring(2).replace('-', '_');
                if (color.startsWith("#")) {
                    colors.put(jsonColorKey, color);
                } else {
                    System.out.println("Warning: " + trimmedLine + " in " + fileName);
                }
            }
        }


        System.out.println(json);
    }

}