package ngram;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class  Ngram {
	
    public static List<String> readFile(File file) {
    	List<String> allSplittedWords = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().trim().replaceAll("\\p{Punct}", "").split("\\s+");
                for (String word : words) {
                    word = word.strip();
                    if (!word.equals("")) {
                    	allSplittedWords.add(word);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allSplittedWords;
    }
    public static List<String> groupWordsOfNGram(int n, List<String> allSplittedWords) {
    	List<String> ngrams = new ArrayList<String>();
    	 for (int i = 0; i < allSplittedWords.size() - n + 1; i++) 
    			 ngrams.add(concat(i, i+n,allSplittedWords)); 	
    	 return ngrams;
    }
    public static String concat(int start, int end, List<String> allSplittedWords) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + allSplittedWords.get(i));
        return sb.toString();
    }
    public static Map<String,Integer>  findFrequencyMap(List<String> words) {
    	Map<String,Integer> mp=new HashMap<>();
    	for (String word : words) { 
    	    mp.compute(word, (k, v) -> v == null ? 1 : v + 1); 
    	}
    	 return mp;
    }
    public static float calculateProbability(Map<String,Integer> mp,int n,List<String> allSplittedWords) {
    	//sorted map
    	 List<Map.Entry<String, Integer>> sortedEntries = mp.entrySet()
                 .stream()
                 .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                 .collect(Collectors.toList());
    	 
    	 Map<String,Integer> mpforunigram= findFrequencyMap(groupWordsOfNGram(1,allSplittedWords));
        int count = 0;
        float prob = 0;
       
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            if (count < 20) {
                System.out.println(count+1 + "- " + entry.getKey() + " --> " + entry.getValue());
                if(n == 2 || n == 3) {
                	 String[] splitWords = entry.getKey().split("\\s+");
                	 System.out.print("Count(" + entry.getKey()+ ") / " + "Count(" + splitWords[0]  + ") = Count(" + entry.getValue() + " / " +  mpforunigram.get(splitWords[0])+ ")");
                     prob = (float) entry.getValue() / Integer.valueOf(mpforunigram.get(splitWords[0]));
                     String formattedNumber = String.format("%.5f", prob);
                     System.out.println(" ----> Probability =" + formattedNumber);
                     System.out.println();
                }
                else if(n == 1) {
                	System.out.print("Count(" + entry.getKey()+ ") / " + "Count(TotalCountOfWords)" +  " = Count(" + entry.getValue() + " / " + allSplittedWords.size() + ")");
                	prob = (float) entry.getValue() / allSplittedWords.size();
                	String formattedNumber = String.format("%.5f", prob);
                    System.out.println(" ----> Probability =" + formattedNumber);
                    System.out.println();
                }
                count++;                
            } else {
                break;
            }
        }
        return prob;
    }
    public static void ngramCalculation(List<String> allSplittedWords) {
        System.out.println("Total count of words: " + allSplittedWords.size());
        System.out.println();

        printNgramResults("UNIGRAM", 1, allSplittedWords);
        printNgramResults("BIGRAM", 2, allSplittedWords);
        printNgramResults("TRIGRAM", 3, allSplittedWords);
    }
    private static void printNgramResults(String ngramType, int n, List<String> allSplittedWords) {
        System.out.println("----" + ngramType + "----");
        System.out.println();

        long startTime = System.nanoTime();
        calculateProbability(findFrequencyMap(groupWordsOfNGram(n, allSplittedWords)), n, allSplittedWords);
        long endTime = System.nanoTime();
        double seconds = (double) (endTime - startTime) / 1000000000;
        System.out.println("Total running time: " + seconds + " second.\n\n");
    }
    public static void main(String[] args) {
    	

    	List<String> fileNames = Arrays.asList("BOZKIRDA.txt", "DEĞİŞİM.txt", "DENEMELER.txt", "grimms-fairy-tales_P1.txt");

        for (String fileName : fileNames) {
            File file = new File(fileName);
            
            System.out.println("Corpus " + (fileNames.indexOf(fileName) + 1) + " - " + fileName);
            ngramCalculation(readFile(file));
        }
       
        
    }
}
