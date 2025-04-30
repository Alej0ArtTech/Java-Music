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
                new int[]{A2, C3, E3, G2},               // Graves
                new double[]{1, 2, 3},

                new int[]{D3, F3, A3, B3},               // Medios
                new double[]{0.25, 0.5, 1, 1.5},

                new int[]{C5, E5, G5, D6},               // Agudos (AQUÍ estamos en las notas más agudas)
                new double[]{0.25, 0.5, 0.75, 1}
        );

        // Generamos los compases (puedes crear varios de diferentes instrumentos)
        Phrase compasBajo = generarCompases(16, pool); // Generamos un compás de 4/4 para Bajo
        Phrase compasPiano = generarCompases(16, pool); // Generamos un compás de 4/4 para Piano
        Phrase compasViolin = generarCompasesViolin(16, pool); // Generamos un compás de 4/4 para Violín (agudo)

        // Creamos la partitura
        Score score = new Score("Melodía con Compases");

        // Creamos las partes para cada instrumento (Bajo, Piano y Violín)
        Part bajoPart = new Part("Bajo", ELECTRIC_PIANO, 0);
        bajoPart.add(compasBajo);
        score.add(bajoPart);

        Part pianoPart = new Part("Piano", PIANO, 1);
        pianoPart.add(compasPiano);
        score.add(pianoPart);

        Part violinPart = new Part("Violín", VIOLIN, 2);  // Añadimos la parte para el violín
        violinPart.add(compasViolin);
        score.add(violinPart);

        // Escribimos el archivo MIDI
        Write.midi(score, "melodia_con_compases_y_violin_agudo_solo2.mid");

        // Reproducir el archivo MIDI
        playMidi("melodia_con_compases_y_violin_agudo_solo2.mid", 124);
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
                } else if (rand.nextDouble() < 0.5) {  // Asignamos algunas notas para el piano
                    noteData = pool.randomPianoNote();
                } else {  // Para el violín, forzamos el rango agudo
                    noteData = pool.randomViolinNote();  // Función para generar notas agudas para el violín
                }

                if (duracionTotal + noteData.duration <= duracionCompas) {
                    if (noteData.pitch == Note.REST) {
                        compas.add(new Note(Note.REST, noteData.duration));  // Usamos Note.REST directamente
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

    // Método especial para generar notas del violín en un rango agudo
    public static Phrase generarCompasesViolin(int numeroDeCompases, NotePool pool) {
        Phrase fraseFinal = new Phrase();
        Random rand = new Random();

        for (int i = 0; i < numeroDeCompases; i++) {
            Phrase compas = new Phrase();
            double duracionTotal = 0.0;
            double duracionCompas = 4.0;

            while (duracionTotal < duracionCompas) {
                NotePool.NoteData noteData = pool.randomViolinNote();  // Solo generamos notas agudas para el violín

                if (duracionTotal + noteData.duration <= duracionCompas) {
                    if (noteData.pitch == Note.REST) {
                        compas.add(new Note(Note.REST, noteData.duration));
                    } else {
                        compas.add(new Note(noteData.pitch, noteData.duration));
                    }
                    duracionTotal += noteData.duration;
                }
            }

            // Añadimos las notas al compás final
            for (Note n : compas.getNoteArray()) {
                fraseFinal.addNote(n);
            }
        }

        return fraseFinal;
    }
}
