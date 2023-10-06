package assignment;
import java.io.BufferedReader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * CS 314H Assignment 2 - Random Writing
 *
 * Your task is to implement this RandomWriter class
 */
public class RandomWriter implements TextProcessor {
    int level;
    String source_text;
    public static void main(String[] args){
        if(args.length!=4){
            System.err.println("there must be 4 command line arguments");
        }
        //checks if each argument is correct type , if it is then it assigns it to a variable
        if (args[0] instanceof String){
        }else{
            System.err.println("source text is not a String");
        }
        String source = args[0];
        if (args[1] instanceof String){
        }else{
            System.err.println("source text is not a String");
        }
        String result = args[1];
        try {
            Integer.parseInt(args[2]);
        }
        catch(NumberFormatException e){
            System.err.println("level is not an integer");
        }
        int level = Integer.parseInt(args[2]);
        try {
            Integer.parseInt(args[3]);
        }
        catch(NumberFormatException e){
            System.err.println("level is not an integer");
        }
        int length = Integer.parseInt(args[3]);
        //checks if the source text has actual text and is greater than k in length
        File inputText = new File(source);
        boolean exists = inputText.exists();
        if(exists==false){
            System.err.println("file does not exist");
        }
        if(inputText.length() == 0){
            System.err.println("source code must have some text.");
        }
        if (inputText.length()<level){
            System.err.println("source code must have more characters than k!");
        }
        if((level<0) || (length<0)){
            System.err.println("the level of analysis and length of output are negative");
        }
        TextProcessor reader = createProcessor(level);
        try {
            reader.readText(source);
        } catch (IOException e) {
            System.err.println("file can't be read");
        }
        try {
            reader.writeText(result, length);
        } catch (IOException e) {
            System.err.println("result file cannot be opened/read");
        }
    }

    public static TextProcessor createProcessor(int level) {
      return new RandomWriter(level);
    }

    //CHANGE BACK TO PRIVATE BEFORE SUBMITTING..
    private RandomWriter(int level) {
        this.level = level;
    }

    private String seedGenerator(String file){
        //generates random seed
        int random_Index = (int)(Math.random()*(file.length()-level));
        String seed = file.substring(random_Index, random_Index+level);
        /*seedGenerator Test
        System.out.println("seed: " + seed + "\nlength of seed: " + seed.length() + "\nexpected length of seed(level): "+ level);
        if(seed.length()!=level){
            System.out.println("the seed and expected seed length are not equal");
        }
        if(file.indexOf(seed)<0){
            System.out.println("cannot find the seed in source text");
        }*/
        return seed;
    }

    public String hasher(String file, String seed, String writing) {
        String outputText = writing;

        //finds all occurences of this random seed and adds the next character to a hashmap
        HashMap<Character, Integer> occurences = new HashMap<Character, Integer>();
        for (int last_index = 0; last_index < file.length(); last_index++) {
            int index_OfOccurence = file.indexOf(seed, last_index);
            if (index_OfOccurence >= 0) {
                //checks that seed is not the last substring in file text
                char new_Char = ' ';
                //if seed occurs at the very end of source text, I assume the next character is a space
                if(index_OfOccurence + seed.length()<file.length()){
                    new_Char = file.charAt(index_OfOccurence + seed.length());
                }
                if (occurences.containsKey(new_Char)) {
                    occurences.put(new_Char, occurences.get(new_Char) + 1);
                } else {
                    occurences.put(new_Char, 1);
                }
            } else {
                break;
            }
            last_index = index_OfOccurence;
        }
        //if no possible characters follow the seed, generate new seed and add to output text
        if (occurences.size() == 0) {
            String newSeed = seedGenerator(source_text);
            return newSeed;
        }
        //sum of each character occurence, so we know the range when selecting a character randomly
        int sum = 0;
        for (int value: occurences.values()) {
            sum += value;
        }
        //selects a random value starting at 1 (the very first occurence) to the total number of occurences
        int randomValue = (int) (Math.random()*sum);
        int indexRandom = 0;
        String following_letter = null;
        for (Map.Entry<Character, Integer> set : occurences.entrySet()) {
            indexRandom += set.getValue();
            if (indexRandom >= randomValue) {
                following_letter = Character.toString(set.getKey());
                break;
            }
        }
        //Testing for hasher function
        /*if(occurences.containsKey(following_letter)==false){
            System.out.println("the random letter following the seed does not exist in the hashmap");
        }else{
            System.out.println("the random letter chosen exists from the hashmap");
        }*/
        return following_letter;
    }
    public void readText(String inputFilename) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            content.append(currentLine);
            //treat each new line as a character
            content.append('\n');
        }
        //turns content StringBuilder into String
        String file = content.toString();
        /*Test for readText()
        System.out.println(file);*/
        this.source_text = file;
    }

    public void writeText(String outputFilename, int length) throws IOException {
        if(source_text==null){
            System.err.println("must read text before trying to write it.");
        }
        String inputFile = source_text;
        String seed = seedGenerator(source_text);
        String outputText = seed;
        //need to update the seed and outputText and iterate through hasher until output text is desired length
        while(outputText.length()<length){
            String letter = hasher(source_text, seed, outputText);
            outputText= outputText + letter;
            seed = outputText.substring(outputText.length()-level);
        }
        //creates new file with outputFilename name
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilename));
        bw.write(outputText);
        bw.close();
        /*Test for writeText()
        File tempFile = new File(outputFilename);
        boolean exists = tempFile.exists();
        if(exists==false){
            System.out.println("writeText() function did not create the new file properly");
        }
        if(tempFile==null){
            System.out.println("no text was written in the result file :(");
        }
        if(tempFile.length()!=length){
            System.out.println("output file is not the correctly expected length");
        } */
    }

}
