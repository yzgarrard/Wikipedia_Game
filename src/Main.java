import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        System.out.println("Getting ready...Will take a few minutes");

        String encoding = "UTF-8", line;
        int hits = 0, misses = 0, keyCount = 0;
        ConcurrentHashMap<Integer, Vector<Integer>> linkMap = new ConcurrentHashMap<>();
        HashMap<String, Integer> stringToInteger = new HashMap<>();
        BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
        String currentTitle = null, startPage = null, endPage = null;

        System.out.println("Creating mapping");
        //Reads data in to create mapping
        try (BufferedReader readMap = new BufferedReader(new InputStreamReader(new FileInputStream("mapping.xml"), encoding))) {
            line = readMap.readLine();//Clears first line, which is nothing
            while ((line = readMap.readLine()) != null) {
                try {
                    stringToInteger.put(line.substring(line.indexOf("<") + 1, line.indexOf(">")),Integer.parseInt(line.substring(line.indexOf(">") + 1)));
                } catch (Exception ex) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Creating map for the game");
        //Reads data in to create linkMap
        try (BufferedReader readTitlesAndLinks = new BufferedReader(new InputStreamReader(new FileInputStream("entire_wikipedia_compressed.xml"), encoding))) {
            while ((line = readTitlesAndLinks.readLine()) != null) {
                if (line.contains("<title>")) {
                    currentTitle = line.substring(line.indexOf(">") + 1);
                    linkMap.put(stringToInteger.get(currentTitle), new Vector<>());
                } else if (line.contains("<link>")) {
                    if (currentTitle == null) continue;
                    String linkLine = line.substring(line.indexOf(">") + 1);
                    linkMap.get(stringToInteger.get(currentTitle)).add(stringToInteger.get(linkLine));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Size of hashmap is: " + linkMap.size());

        System.out.println("Computing coupling...");

        Set<Integer> keys = linkMap.keySet();

        for (Integer key : keys) {
            Vector<Integer> links = linkMap.get(key);
            keyCount++;
            if (links != null) {
                for (Integer link : links) {
                    if (linkMap.containsKey(link)) hits++;
                    else misses++;
                }
            }
        }

        System.out.println("Hits: " + hits + "\nMisses: " + misses + "\n% hits: " + (double)hits / (double)(hits + misses));
        System.out.println("Keycout: " + keyCount);

        while (true) {
            System.out.print("Type in starting page: ");
            try {
                startPage = systemReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print("Type in ending page: ");
            try {
                endPage = systemReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (startPage != null && stringToInteger.get(startPage) == null) {
                System.out.println("Start page not found. Please try again");
                continue;
            } else if (endPage != null && stringToInteger.get(endPage) == null) {
                System.out.println("End page not found. Please try again");
                continue;
            }
            System.out.println("Start and end pages have been found.");
            break;
        }
    }
}
