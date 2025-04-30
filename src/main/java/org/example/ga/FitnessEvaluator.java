
// FitnessEvaluator.java
package org.example.ga;

import jm.music.data.Phrase;
import jm.music.data.Note;
import java.util.*;

/**
 * Calcula el "fitness" de una melodía según reglas musicales.
 */
public class FitnessEvaluator {
    private final Set<Integer> scale;
    private final double targetBeats;

    public FitnessEvaluator(Set<Integer> scale, double targetBeats) {
        this.scale = scale;
        this.targetBeats = targetBeats;
    }

    public double getTargetBeats() {
        return targetBeats;
    }

    /**
     * Puntaje total: combinación de cinco sub-fitness y penalización de duración.
     */
    public double fitness(Phrase p) {
        double score =  2.0 * fScale(p)
                + 1.5 * fStep(p)
                + 1.0 * fRhythm(p)
                + 1.2 * fMotif(p)
                + 1.0 * fContour(p)
                - 2.0 * fDurationPenalty(p);
        return score;
    }

    // 1) Pertenencia a la escala
    private double fScale(Phrase p) {
        Note[] notes = p.getNoteArray();
        int inScale = 0;
        for (Note n : notes) {
            if (scale.contains(n.getPitch())) inScale++;
        }
        return (double) inScale / notes.length;
    }

    // 2) Movimiento suave (saltos <= tercera mayor)
    private double fStep(Phrase p) {
        Note[] notes = p.getNoteArray();
        if (notes.length < 2) return 0;
        int good = 0;
        for (int i = 0; i < notes.length - 1; i++) {
            int interval = Math.abs(notes[i+1].getPitch() - notes[i].getPitch());
            if (interval <= 4) good++;
        }
        return (double) good / (notes.length - 1);
    }

    // 3) Variedad rítmica
    private double fRhythm(Phrase p) {
        Map<Double,Integer> count = new HashMap<>();
        for (Note n : p.getNoteArray()) {
            count.merge(n.getDuration(), 1, Integer::sum);
        }
        int maxFreq = Collections.max(count.values());
        return 1.0 - (double) maxFreq / p.size();
    }

    // 4) Repetición con variación (motivo)
    private double fMotif(Phrase p) {
        Note[] notes = p.getNoteArray();
        int motifLen = Math.min(4, notes.length/2);
        List<Note> motif = Arrays.asList(Arrays.copyOfRange(notes, 0, motifLen));
        int count = 0;
        for (int i = 1; i + motifLen <= notes.length; i++) {
            boolean match = true;
            for (int j = 0; j < motifLen; j++) {
                Note a = motif.get(j), b = notes[i+j];
                if (Math.abs(a.getPitch() - b.getPitch()) > 1
                        || Math.abs(a.getDuration() - b.getDuration()) / a.getDuration() > 0.2) {
                    match = false;
                    break;
                }
            }
            if (match) count++;
        }
        int maxPossible = (notes.length / motifLen) - 1;
        return maxPossible > 0 ? (double) count / maxPossible : 0;
    }

    // 5) Contorno melódico (cambios de dirección)
    private double fContour(Phrase p) {
        Note[] notes = p.getNoteArray();
        if (notes.length < 2) return 0;
        int changes = 0;
        int lastDir = Integer.signum(notes[1].getPitch() - notes[0].getPitch());
        for (int i = 1; i < notes.length - 1; i++) {
            int dir = Integer.signum(notes[i+1].getPitch() - notes[i].getPitch());
            if (dir != 0 && dir != lastDir) {
                changes++;
                lastDir = dir;
            }
        }
        return (double) changes / (notes.length - 1);
    }

    // Penaliza desviación de duración objetivo
    private double fDurationPenalty(Phrase p) {
        double sum = 0;
        for (Note n : p.getNoteArray()) sum += n.getDuration();
        return Math.abs(sum - targetBeats) / targetBeats;
    }
}
