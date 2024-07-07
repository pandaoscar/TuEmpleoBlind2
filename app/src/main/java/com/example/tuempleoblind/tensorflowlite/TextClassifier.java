package com.example.tuempleoblind.tensorflowlite;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class TextClassifier {
    private Interpreter tflite;
    private Map<Integer, String> categoryMapping;
    private Map<String, Integer> wordIndex;
    private int maxlen;
    private String name_category_mapping;
    private String name_word_index;
    private String name_maxlen;

    public TextClassifier(Context context, String model, String file, String s, String s1) throws Exception {
        this.name_category_mapping = s;
        this.name_word_index = s1;
        this.name_maxlen = file;
        tflite = new Interpreter(loadModelFile(context, model));
        categoryMapping = loadCategoryMapping(context);
        wordIndex = loadWordIndex(context);
        this.maxlen = loadMaxlen(context);
    }

    public void setName_category_mapping(String name_category_mapping) {
        this.name_category_mapping = name_category_mapping;
    }

    public void setName_word_index(String name_word_index) {
        this.name_word_index = name_word_index;
    }

    public void setName_maxlen(String name_maxlen) {
        this.name_maxlen = name_maxlen;
    }

    public String getName_category_mapping() {
        return name_category_mapping;
    }

    public String getName_word_index() {
        return name_word_index;
    }

    public String getName_maxlen() {
        return name_maxlen;
    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws Exception {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private Map<Integer, String> loadCategoryMapping(Context context) throws Exception {
        Map<Integer, String> mapping = new HashMap<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(getName_category_mapping());
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        String json = new String(buffer, "UTF-8");
        JSONObject jsonObject = new JSONObject(json);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            int categoryIndex = Integer.parseInt(key);
            String categoryName = jsonObject.getString(key);
            mapping.put(categoryIndex, categoryName);
        }
        return mapping;
    }

    private Map<String, Integer> loadWordIndex(Context context) throws Exception {
        Map<String, Integer> wordIndex = new HashMap<>();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(getName_word_index());
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        String json = new String(buffer, "UTF-8");
        JSONObject jsonObject = new JSONObject(json);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String word = keys.next();
            int index = jsonObject.getInt(word);
            wordIndex.put(word, index);
        }
        return wordIndex;
    }


    private int loadMaxlen(Context context) throws Exception {
        // Cargar maxlen desde un archivo en assets
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(getName_maxlen());
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        return Integer.parseInt(new String(buffer, "UTF-8").trim());
    }

    public String classifyText(String text) {
        // Preprocesar el texto
        text = text.toLowerCase();

        // Tokenizar y crear secuencia
        List<Integer> sequence = tokenize(text);

        // Padding
        int[] paddedSequence = pad(sequence, maxlen);

        // Preparar el input para el modelo
        float[][] input = new float[1][maxlen];
        for (int i = 0; i < maxlen; i++) {
            input[0][i] = paddedSequence[i];
        }

        // Preparar el output
        float[][] output = new float[1][categoryMapping.size()];

        // Ejecutar la inferencia
        tflite.run(input, output);

        // Obtener la categoría con mayor probabilidad
        int maxIndex = 0;
        float maxProb = output[0][0];
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                maxIndex = i;
            }
        }

        // Verificar el umbral de confianza
        if (maxProb < 0.91) {
            return "comando no reconocido";
        }

        // Devolver la categoría predicha
        return categoryMapping.get(maxIndex);
    }

    private List<Integer> tokenize(String text) {
        String[] words = text.split("\\s+");
        List<Integer> sequence = new ArrayList<>();
        for (String word : words) {
            sequence.add(wordIndex.getOrDefault(word, 0));
        }
        return sequence;
    }

    private int[] pad(List<Integer> sequence, int maxlen) {
        int[] paddedSequence = new int[maxlen];
        int seqLen = Math.min(sequence.size(), maxlen);
        for (int i = 0; i < seqLen; i++) {
            paddedSequence[maxlen - seqLen + i] = sequence.get(i);
        }
        return paddedSequence;
    }
}
