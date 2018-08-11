
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ParseCompressed {
    public static void main(String[] args) throws Exception {
        System.out.println("BEGIN");

        String encoding = "UTF-8";
        String line = null;
        double amountScanned = 0;
        long lines = 0;
        boolean firstTime = true;
        String currentTitle = null;
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test_hashmap_compressed.xml"), encoding));
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("entire_wikipedia_compressed.xml"), encoding))) {
            //This pass will extract the titles and links from each page.
            while ((line = reader.readLine()) != null) {

                if (firstTime) {
                    firstTime = false;
                    continue;
                }
                amountScanned += (line.getBytes(StandardCharsets.UTF_8).length) / 1000000.0;

                if (lines++ % 1000000 == 0) {
                    System.out.println("Processed " + Double.toString(amountScanned) + " megabytes");
                }

                if (line.contains("<title>")) {
                    currentTitle = line.substring(line.indexOf(">") + 1);
                    if (stringBuilder.toString().contains(" ")) writer.newLine();
                    if (stringBuilder.toString().contains(" ") && stringBuilder.toString().length() >= 2) writer.write(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(currentTitle.hashCode() & 0x7FFFFFFF);
                } else if (line.contains("<link>")) {
                    if (currentTitle == null) continue;
                    String linkLine = line.substring(line.indexOf(">") + 1);
                    stringBuilder.append(" ");
                    stringBuilder.append(linkLine.hashCode() & 0x7FFFFFFF);
                }
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("\n" + line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Processed " + Double.toString(amountScanned) + " megabytes");
        }

        System.out.println("DONE");

    }

}
