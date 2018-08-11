import java.io.*;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;

public class ParseWikipedia {
    public static void main(String[] args) {
        System.out.println("BEGIN");

        String encoding = "UTF-8";
        String line = null;
        String links[];
        double amountScanned = 0;
        long lines = 0;
        StringBuilder stringBuilder = new StringBuilder();
        boolean isAGeographyPage = false;


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("enwiki-20180720-pages-articles.xml"), encoding));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("entire_wikipedia_compressed.xml"), encoding))) {
            //This pass will extract the titles and links from each page.
            while ((line = reader.readLine()) != null) {

                amountScanned += (line.getBytes(StandardCharsets.UTF_8).length) / 1000000.0;

                if (lines++ % 1000000 == 0) {
                    System.out.println("Processed " + Double.toString(amountScanned) + " megabytes");
                }

                if (line.contains("<page>")) {
                    isAGeographyPage = false;
                    stringBuilder = new StringBuilder();
                } else if (line.contains("http://") || line.contains("https://") || line.contains("www.") ||
                        line.contains("Www.")) {
                    continue;
                } else if (line.contains("<title>")) {
                    if (line.contains("Category:")) isAGeographyPage = true;
                    else if (line.contains("File:")) isAGeographyPage = true;
                    line = line.trim();
                    line = line.replace("</title>", "");
                    //System.out.println(title);
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                } else if (line.contains("infobox") || line.contains("Infobox")){
                    if (line.contains("U.S. state")) isAGeographyPage = true;
                    else if (line.contains("country")) isAGeographyPage = true;
                    else if (line.contains("province")) isAGeographyPage = true;
                    else if (line.contains("settlement")) isAGeographyPage = true;
                    //stringBuilder.append("<infobox> ");
                    //stringBuilder.append(line);
                    //stringBuilder.append('\n');
                } else if (line.contains("</page>")) {
                    if (isAGeographyPage) {
                        continue;
                    }
                    writer.write(stringBuilder.toString());
                    writer.newLine();
                }

                if ((links = StringUtils.substringsBetween(line, "[", "]")) != null) {
                    for (String s : links) {
                        //System.out.println(s);
                        if (s.length() <= 2) continue;
                        if (!s.contains("[")) continue;
                        if (s.indexOf("[") == s.length() - 1) continue;
                        if (s.charAt(0) == '[') s = s.substring(1);
                        if (s.contains("|")) s = s.substring(0, s.indexOf("|"));
                        if (s.indexOf(":") < 13 && s.contains(":")) break;
                        if (s.indexOf("#") < 13 && s.contains("#")) break;
                        stringBuilder.append("<link>");
                        stringBuilder.append(s);
                        stringBuilder.append('\n');
                    }
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