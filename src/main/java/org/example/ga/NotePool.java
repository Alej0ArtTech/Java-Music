// NotePool.java
package org.example.ga;

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

    public NoteData randomBajoNote() {
        int pitch = bajoPitches[rand.nextInt(bajoPitches.length)];
        double dur = bajoDur[rand.nextInt(bajoDur.length)];
        return new NoteData(pitch, dur);
    }
    public NoteData randomPianoNote() {
        int pitch = pianoPitches[rand.nextInt(pianoPitches.length)];
        double dur = pianoDur[rand.nextInt(pianoDur.length)];
        return new NoteData(pitch, dur);
    }
    public NoteData randomViolinNote() {
        int pitch = violinPitches[rand.nextInt(violinPitches.length)];
        double dur = violinDur[rand.nextInt(violinDur.length)];
        return new NoteData(pitch, dur);
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