import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Surya De Datta
 */
public class Autocorrect
{
    private String[] words;
    private int threshold;

    private ArrayList<String>[] biGrams;
    private ArrayList<String>[] triGrams;
    private int[] lengths;
    private char[] firstChars;

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold)
    {
        this.words = words;
        this.threshold = threshold;
        biGrams = new ArrayList[words.length];
        triGrams = new ArrayList[words.length];
        lengths = new int[words.length];
        firstChars = new char[words.length];

        for(int i = 0; i < words.length; i++)
        {
            String str = words[i];
            lengths[i] = str.length();
            firstChars[i] = str.charAt(0);
            biGrams[i] = nGram(str, 2);
            triGrams[i] = nGram(str, 3);
        }

    }
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        String[] dict = loadDictionary("large");
        int threshold = 2;
        Autocorrect autocorrect = new Autocorrect(dict, threshold);
        while(true)
        {
            System.out.println("Write a word: ");
            String typed = scanner.nextLine();
            String[] results = autocorrect.runTest(typed);
            for(String word : results)
            {
                System.out.println(word);
            }
        }

    }
    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */

    public String[] runTest(String typed)
    {
        int gramLen = 2;
        if(typed.length() >= 9)
        {
            gramLen = 3;
        }

        ArrayList<String> typedGram = nGram(typed, gramLen);
        ArrayList<Pair> matches = new ArrayList<>();
        int overlap = 0;
        for(int i = 0; i < words.length; i++)
        {
            ArrayList<String> grams = new ArrayList<>();
            if(gramLen == 2)
            {
                grams = biGrams[i];
            }
            else
            {
                grams = triGrams[i];
            }
            if(Math.abs(lengths[i] - typed.length()) <= threshold)
            {
                for(String gram: grams)
                {
                    if(typedGram.contains(gram))
                    {
                        overlap++;
                    }
                }
                if(overlap >= 1 || typed.charAt(0) == firstChars[i])
                {
                    int distance = editDistance(words[i], typed);
                    if(distance <= threshold)
                    {
                        matches.add(new Pair(words[i], distance));
                    }
                }
            }
        }
        matches.sort(Comparator.comparingInt(Pair::getEditDistance).thenComparing(Pair::getWord));
        String[] result = new String[matches.size()];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = matches.get(i).getWord();
        }
        return result;
    }
    private ArrayList<String> nGram(String word, int nGram)
    {
        ArrayList<String> biGrams = new ArrayList<>();
        for(int i = 0; i < word.length() - (nGram - 1); i++)
        {
            biGrams.add(word.substring(i, i+nGram));
        }
        return biGrams;
    }
    private int editDistance(String a, String b)
    {
        int arr[][] = new int[a.length() + 1][b.length() + 1];
        for(int i = 0; i < arr[0].length; i++)
        {
            arr[0][i] = i;
        }
        for(int j = 0; j < arr.length ; j++)
        {
            arr[j][0] = j;
        }
        for(int i = 1; i < arr.length; i++)
        {
            for(int j = 1; j < arr[0].length; j++)
            {
                if(a.charAt(i - 1) == b.charAt(j - 1))
                {
                    arr[i][j] = arr[i-1][j-1];
                }
                else
                {
                   arr[i][j] = 1 + Math.min(arr[i-1][j-1], Math.min(arr[i][j-1], arr[i-1][j]));
                }
            }
        }
        return arr[a.length()][b.length()];
    }


    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}