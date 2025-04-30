package org.example.ga;

import jm.music.data.Note;
import java.util.*;

/**
 * Contiene los conjuntos de notas y duraciones por voz.
 */
public class NotePool {
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

    // Método para generar una nota aleatoria para el Bajo
    public NoteData randomBajoNote() {
        boolean insertRest = rand.nextDouble() < 0.1; // 10% de probabilidad de silencio
        if (insertRest) {
            double dur = bajoDur[rand.nextInt(bajoDur.length)];
            return new NoteData(Note.REST, dur); // Usamos Note.REST para el silencio
        }
        int pitch = bajoPitches[rand.nextInt(bajoPitches.length)];
        double dur = bajoDur[rand.nextInt(bajoDur.length)];
        return new NoteData(pitch, dur);
    }

    // Método para generar una nota aleatoria para el Piano
    public NoteData randomPianoNote() {
        boolean insertRest = rand.nextDouble() < 0.5; // 50% de probabilidad de silencio
        if (insertRest) {
            double dur = pianoDur[rand.nextInt(pianoDur.length)];
            return new NoteData(Note.REST, dur); // Usamos Note.REST para el silencio
        }
        int pitch = pianoPitches[rand.nextInt(pianoPitches.length)];
        double dur = pianoDur[rand.nextInt(pianoDur.length)];
        return new NoteData(pitch, dur);
    }

    // Método para generar una nota aleatoria para el Violín
    public NoteData randomViolinNote() {
        boolean insertRest = rand.nextDouble() < 0.2; // 20% de probabilidad de silencio
        if (insertRest) {
            double dur = violinDur[rand.nextInt(violinDur.length)];
            return new NoteData(Note.REST, dur); // Usamos Note.REST para el silencio
        }
        int pitch = violinPitches[rand.nextInt(violinPitches.length)];
        double dur = violinDur[rand.nextInt(violinDur.length)];
        return new NoteData(pitch, dur);
    }

    // Método para generar un compás de 4/4 con notas aleatorias
    public List<NoteData> generarCompas4_4() {
        List<NoteData> compas = new ArrayList<>();
        double duracionTotal = 0.0;
        double duracionCompas = 4.0; // 4 beats en total para el compás 4/4

        // Generamos notas hasta que se llene el compás
        while (duracionTotal < duracionCompas) {
            NoteData noteData;
            // Cambiar el instrumento aleatoriamente o elegir uno por defecto
            if (rand.nextBoolean()) {
                noteData = randomBajoNote();  // Escoge bajo aleatorio
            } else {
                noteData = randomPianoNote();  // Escoge piano aleatorio
            }

            // Aseguramos que la duración total no supere 4 beats
            if (duracionTotal + noteData.duration <= duracionCompas) {
                compas.add(noteData);
                duracionTotal += noteData.duration;
            }
        }

        return compas;
    }

    // Clase interna que contiene las notas y duraciones
    public static class NoteData {
        public final int pitch;
        public final double duration;

        public NoteData(int pitch, double duration) {
            this.pitch = pitch;
            this.duration = duration;
        }
    }
}

