import java.io.*;

public class ParseWikipedia {
    public static void main(String[] args) throws Exception {
        System.out.println("BEGIN");

        String encoding = "UTF-8";
        String line = null;
        double amountScanned = 0;
        long lines = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("enwiki-20180720-pages-articles1.xml-p10p30302"), encoding));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test_compressed.xml"), encoding))) {
            while ((line = reader.readLine()) != null) {
                if (line.contains("<title>")) {
                    line = line.replace("<title>", "<t>");
                    line = line.replace("</title>", "</t>");
                    //while (line.charAt(0) == ' ') line = line.replaceFirst(" ", "");
                    writer.write(line);
                    writer.newLine();
                } if (line.lastIndexOf("]") > line.indexOf("[") && line.contains("[") && line.contains("]")) {
                    writer.write(line.substring(line.indexOf("["),line.indexOf("]")));
                    writer.newLine();
                }

                amountScanned += (line.getBytes("UTF-8").length) / 1000000.0;

                if (lines++ % 1000000 == 0) {
                    System.out.println("Processed " + Double.toString(amountScanned) + " megabytes");
                }
            }
        }
        catch (StringIndexOutOfBoundsException ex) {
            System.out.println(line);
        } finally {
            System.out.println("Processed " + Double.toString(amountScanned) + " megabytes");
        }

        System.out.println("DONE");
    }
}