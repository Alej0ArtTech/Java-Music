package org.example.ga;

import java.util.*;

/**
 * Generador de notas basado en un modelo de Markov entrenado con frases melódicas.
 */
public class MarkovMelodyGenerator implements GeneticAlgorithm.NoteGenerator {

    private final Map<Integer, List<Integer>> markovChain = new HashMap<>();
    private final List<Integer> startingNotes = new ArrayList<>();
    private final Random rand = new Random();

    private int currentNote = -1; // Comienza sin nota previa

    public MarkovMelodyGenerator(List<List<Integer>> trainingMelodies) {
        train(trainingMelodies);
    }

    private void train(List<List<Integer>> melodies) {
        for (List<Integer> phrase : melodies) {
            if (phrase.isEmpty()) continue;

            startingNotes.add(phrase.get(0));

            for (int i = 0; i < phrase.size() - 1; i++) {
                int from = phrase.get(i);
                int to = phrase.get(i + 1);
                markovChain.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            }
        }
    }

    @Override
    public NotePool.NoteData next() {
        // Si no hay nota previa o no hay transición conocida, comienza de nuevo
        if (currentNote == -1 || !markovChain.containsKey(currentNote)) {
            currentNote = startingNotes.get(rand.nextInt(startingNotes.size()));
        } else {
            List<Integer> options = markovChain.get(currentNote);
            currentNote = options.get(rand.nextInt(options.size()));
        }

        // Duración aleatoria sencilla (puedes personalizar)
        double[] durations = {0.25, 0.5, 1.0};
        double dur = durations[rand.nextInt(durations.length)];

        return new NotePool.NoteData(currentNote, dur);
    }
}