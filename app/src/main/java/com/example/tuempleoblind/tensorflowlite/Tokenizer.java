package com.example.tuempleoblind.tensorflowlite;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private static final String TAG = "Tokenizer";
    private Map<String, Integer> wordIndex;

    public Tokenizer(Map<String, Integer> wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int[] tokenize(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        int[] tokens = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            tokens[i] = wordIndex.containsKey(words[i]) ? wordIndex.get(words[i]) : 0;
        }
        Log.d(TAG, "Tokenized sequence: " + Arrays.toString(tokens));
        return tokens;
    }
}