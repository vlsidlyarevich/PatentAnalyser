package com.github.vlsidlyarevich.core;

import com.github.vlsidlyarevich.utils.Patterns;
import com.github.vlsidlyarevich.utils.Utils;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceSearcher {

    private HashSet<String> words;
    private HashSet<String> smallWordsSet;
    private HashSet<String> radicals;
    private Pattern wordPattern;
    private Pattern seqPatternWithoutSEQID;
    private Pattern seqPatternWithSEQID;
    private Pattern aminPattern;
    private Pattern nucPatternWithoutSEQID;
    private int counter;

    public SequenceSearcher(HashSet<String> words, HashSet<String> smallWords, HashSet<String> radicals) {
        this.words = words;
        smallWordsSet = smallWords;
        this.radicals = radicals;
        wordPattern = Pattern.compile(Patterns.WORD_PATTERN);
        seqPatternWithoutSEQID = Pattern.compile(Patterns.SEQ_PATTERN_WITHOUT_ID);
        seqPatternWithSEQID = Pattern.compile(Patterns.SEQ_PATTERN_WITH_ID);
        aminPattern = Pattern.compile(Patterns.SEQ_PATTERN_AMIN);
        nucPatternWithoutSEQID = Pattern.compile(Patterns.SEQ_PATTERN_NUC_WITHOUT_ID);
        counter = 0;
    }

    public String searchSeq(String patent) {

        StringBuilder result = new StringBuilder();
        String word;
        int position = 0;
        int curSeqNumber = 0;
        int seqNumber = 0;

        Matcher wordMatcher = wordPattern.matcher(patent);

        while (wordMatcher.find(position)) {
            word = wordMatcher.group();
            word = word.toLowerCase();

            Matcher seqMatcher = seqPatternWithoutSEQID.matcher(word);
            Matcher seqMatcherWithSEQID = seqPatternWithSEQID.matcher(word);
            Matcher aminSeqMatcher = aminPattern.matcher(word);
            Matcher nucSeqMatcher = nucPatternWithoutSEQID.matcher(word);


            if (seqMatcherWithSEQID.matches()) {
                if (!smallWordsSet.contains(this.deleteSeqIdPart(word).trim())) {
                    //System.out.println(++counter + ") " + word.toUpperCase());
                    result.insert(0, "Patent No:" + Utils.getPatentID(patent));
                    return result.toString();
                }
            }

            if (aminSeqMatcher.matches()) {
                //System.out.println(++counter + ") " + word.toUpperCase());
                result.insert(0, "Patent No:" + Utils.getPatentID(patent));
                return result.toString();
            }

            if (nucSeqMatcher.matches()) {
                //System.out.println(++counter + ") " + word.toUpperCase());
                result.insert(0, "Patent No:" + Utils.getPatentID(patent));
                return result.toString();
            }

            if (!this.words.contains(word) && seqMatcher.matches() && !compositeWord(word)) {
                //System.out.println(++counter + ") " + word.toUpperCase());
                result.insert(0, "Patent No:" + Utils.getPatentID(patent));
                return result.toString();
            }

            position = wordMatcher.end();
        }
        return null;
    }

    private String deleteSeqIdPart(String text) {

        int pos = text.indexOf("seq");
        if(pos>=2) return text.subSequence(0, pos-1).toString();
        return null;
    }

    private Boolean compositeWord(String word) {

        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < word.length(); i++) {
            if ('\\' != (word.charAt(i)) && '/' != (word.charAt(i))
                    && '(' != (word.charAt(i)) && ')' != (word.charAt(i)))
                temp.append(word.charAt(i));
            if (radicals.contains(temp.toString()) || smallWordsSet.contains(temp.toString())) return true;
            if (temp.length()>=3 && words.contains(temp.toString())) return true;
        }
        return false;
    }

    public String sequenceListCheck(String patent){

        Pattern pattern = Pattern.compile("\\s*(<sequence-list)");
        if(pattern.matcher(patent).find()){
            return "Patent No:" + Utils.getPatentID(patent);
        }
        return null;
    }



    @Override
    public String toString() {
        return "SequenceSearcher{" +
                "words=" + words +
                '}';
    }
}
