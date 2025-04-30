package org.example.ga;

import java.util.*;

public class TrainingData {
    public static List<List<Integer>> getMelodicPhrases() {
        List<List<Integer>> data = new ArrayList<>();

        // Fragmentos melódicos reales o similares a estilos reconocibles (C mayor)
        data.add(Arrays.asList(60, 62, 64, 65, 67));         // C-D-E-F-G
        data.add(Arrays.asList(67, 65, 64, 62, 60));         // G-F-E-D-C
        data.add(Arrays.asList(60, 60, 67, 67, 69, 69, 67)); // Twinkle start
        data.add(Arrays.asList(64, 64, 62, 62, 60));         // Respuesta
        data.add(Arrays.asList(60, 62, 60, 62, 64));         // patrón saltado
        data.add(Arrays.asList(65, 67, 69, 67, 65));         // subida y bajada

        // Agrega más si lo deseas (saca ideas de canciones simples, osts, etc.)
        return data;
    }
}