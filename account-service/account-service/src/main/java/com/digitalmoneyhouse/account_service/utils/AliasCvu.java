package com.digitalmoneyhouse.account_service.utils;

import jakarta.ws.rs.InternalServerErrorException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class AliasCvu {

    private final List<String> words = new ArrayList<>();
    private final Random random = new Random();

    public AliasCvu() {
        try {
            this.words.addAll(loadWords("alias.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar las palabras para generar el alias", e);
        }
    }

    private List<String> loadWords(String filename) throws IOException {
        List<String> wordList = new ArrayList<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("Archivo no encontrado: " + filename);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    wordList.add(trimmed);
                }
            }
        }
        return wordList;
    }

    public String generateAlias() {
        if (words.size() < 3) {
            throw new InternalServerErrorException("No hay suficientes palabras para generar el alias");
        }

        String word1 = words.get(random.nextInt(words.size()));
        String word2 = words.get(random.nextInt(words.size()));
        String word3 = words.get(random.nextInt(words.size()));

        return word1 + "." + word2 + "." + word3;
    }

    public String generateCVU() {
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }
}
