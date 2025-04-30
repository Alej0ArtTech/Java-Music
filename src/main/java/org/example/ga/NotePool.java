package org.example.ga;

import jm.music.data.Note;
import java.util.*;

/**
 * Contiene los conjuntos de notas y duraciones por voz.
 */public class NotePool {
    private final int[] bajoPitches;
    private final double[] bajoDur;
    private final int[] pianoPitches;
    private final double[] pianoDur;
    private final int[] violinPitches;
    private final double[] violinDur;
    private final Random rand = new Random();

    public NotePool(int[] bajoPitches, double[] bajoDur,
                    int[] pianoPitches, double[] pianoDur,
                    int[] violinPitches, double[] violinDur) {
        this.bajoPitches = bajoPitches;
        this.bajoDur = bajoDur;
        this.pianoPitches = pianoPitches;
        this.pianoDur = pianoDur;
        this.violinPitches = violinPitches;
        this.violinDur = violinDur;
    }

    public NoteData randomBajoNote() {
        return randomNoteFrom(bajoPitches, bajoDur, 0.1); // 10% silencio
    }

    public NoteData randomPianoNote() {
        return randomNoteFrom(pianoPitches, pianoDur, 0.5); // 50% silencio
    }

    public NoteData randomViolinNote() {
        return randomNoteFrom(violinPitches, violinDur, 0.2); // 20% silencio
    }

    private NoteData randomNoteFrom(int[] pitches, double[] durations, double silenceProbability) {
        boolean insertRest = rand.nextDouble() < silenceProbability;
        double dur = durations[rand.nextInt(durations.length)];
        int pitch = insertRest ? Note.REST : pitches[rand.nextInt(pitches.length)];
        return new NoteData(pitch, dur);
    }

    // Ahora incluye violín también y selecciona instrumento de forma aleatoria
    public List<NoteData> generarCompas4_4() {
        List<NoteData> compas = new ArrayList<>();
        double duracionTotal = 0.0;
        double duracionCompas = 4.0;

        while (duracionTotal < duracionCompas) {
            NoteData noteData;

            // Elige instrumento aleatoriamente
            int selector = rand.nextInt(3);
            switch (selector) {
                case 0 -> noteData = randomBajoNote();
                case 1 -> noteData = randomPianoNote();
                default -> noteData = randomViolinNote();
            }

            // Agrega solo si cabe dentro del compás
            if (duracionTotal + noteData.duration <= duracionCompas) {
                compas.add(noteData);
                duracionTotal += noteData.duration;
            }
        }

        return compas;
    }

    public static class NoteData {
        public final int pitch;
        public final double duration;

        public NoteData(int pitch, double duration) {
            this.pitch = pitch;
            this.duration = duration;
        }
    }
}
