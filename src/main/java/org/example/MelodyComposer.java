package org.example;

import jm.JMC;
import jm.music.data.*;
import jm.util.Write;
import org.example.ga.*;

import javax.sound.midi.*;
import java.io.File;
import java.util.*;

public class MelodyComposer implements JMC {

    public static void main(String[] args) throws Exception {
        // Creamos el pool de notas


        NotePool pool = new NotePool(
                new int[]{A2, C3, E3, A2}, new double[]{2, 2, 2, 2},
                new int[]{A3, C4, E4, G4}, new double[]{0.5, 0.25, 1, 0.75},
                new int[]{A4, C5, E5, G5, B5}, new double[]{0.25, 0.5, 0.75, 1, 1.5}
        );

        // Generamos los compases (puedes crear varios de diferentes instrumentos)
        Phrase compasBajo = generarCompases(16,pool); // Generamos un compás de 4/4 para Bajo
        Phrase compasPiano = generarCompases(16, pool); // Generamos un compás de 4/4 para Piano

        // Creamos la partitura
        Score score = new Score("Melodía con Compases");

        // Creamos las partes para cada instrumento (Bajo y Piano)
        Part bajoPart = new Part("Bajo", ELECTRIC_PIANO, 0);
        bajoPart.add(compasBajo);
        score.add(bajoPart);

        Part pianoPart = new Part("Piano", PIANO, 1);
        pianoPart.add(compasPiano);
        score.add(pianoPart);

        // Escribimos el archivo MIDI
        Write.midi(score, "melodia_con_compases.mid");

        // Reproducir el archivo MIDI
        playMidi("melodia_con_compases.mid", 124);
    }

    private static void playMidi(String file, int bpm) throws Exception {
        Synthesizer s = MidiSystem.getSynthesizer();
        s.open();
        s.loadAllInstruments(MidiSystem.getSoundbank(new File("soundfonts/GeneralUser-GS.sf2")));
        Sequencer seq = MidiSystem.getSequencer(false);
        seq.open();
        seq.getTransmitter().setReceiver(s.getReceiver());
        Sequence seqn = MidiSystem.getSequence(new File(file));
        seq.setSequence(seqn);
        seq.setTempoInBPM(bpm);
        seq.start();
        while (seq.isRunning()) Thread.sleep(100);
        seq.stop();
        seq.close();
        s.close();
    }

    // Método que genera un compás de 4/4
    public static Phrase generarCompases(int numeroDeCompases, NotePool pool) {
        Phrase fraseFinal = new Phrase();
        Random rand = new Random();

        for (int i = 0; i < numeroDeCompases; i++) {
            Phrase compas = new Phrase();
            double duracionTotal = 0.0;
            double duracionCompas = 4.0;

            while (duracionTotal < duracionCompas) {
                NotePool.NoteData noteData;
                if (rand.nextDouble() < 0.1) {
                    noteData = pool.randomBajoNote();
                } else {
                    noteData = pool.randomPianoNote();
                }

                if (duracionTotal + noteData.duration <= duracionCompas) {
                    if (noteData.pitch == -1) {
                        compas.add(new Note(Note.REST, noteData.duration));
                    } else {
                        compas.add(new Note(noteData.pitch, noteData.duration));
                    }
                    duracionTotal += noteData.duration;
                }
            }

            // 💡 Aquí añadimos nota por nota del compás a la frase final
            for (Note n : compas.getNoteArray()) {
                fraseFinal.addNote(n);
            }
        }

        return fraseFinal;
    }

}