package org.example;

import jm.JMC;
import jm.music.data.Score;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Note;
import jm.util.Write;

import javax.sound.midi.Synthesizer;
import javax.sound.midi.Sequencer;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Sequence;
import javax.sound.midi.Transmitter;
import javax.sound.midi.Receiver;

import java.io.File;

public class Main implements JMC {
    public static void main(String[] args) {
        try {
            // 1) Crear la partitura con JMusic
            Score score = new Score("Música Triste con Base Pegajosa");

            // Bajo
            Part bajo = new Part("Bajo", ELECTRIC_PIANO, 0);
            Phrase melodyBajo = new Phrase();
            int[] notasBajo = {A2, C3, E3, A2};
            double[] duracionesBajo = {1.0, 1.0, 1.0, 1.0};
            for (int i = 0; i < 8; i++) {
                int n = notasBajo[i % notasBajo.length];
                melodyBajo.add(new Note(n, duracionesBajo[i % duracionesBajo.length]));
            }
            bajo.add(melodyBajo);
            score.add(bajo);

            // Piano
            Part piano = new Part("Piano", PIANO, 1);
            Phrase melodyPiano = new Phrase();
            int[] notasPiano = {A3, C4, E4, G4};
            double[] duracionesPiano = {0.5, 0.25, 1.0, 0.75};
            for (int i = 0; i < 16; i++) {
                int n = notasPiano[(int)(Math.random() * notasPiano.length)];
                melodyPiano.add(new Note(n, duracionesPiano[i % duracionesPiano.length]));
            }
            piano.add(melodyPiano);
            score.add(piano);

            // Violín
            Part violin = new Part("Violín", FLUTE, 2);
            Phrase melodyViolin = new Phrase();
            int[] notasViolin = {A4, C5, E5, G5, B5};
            double[] duracionesViolin = {0.25, 0.5, 0.75, 1.0, 1.5};
            for (int i = 0; i < 16; i++) {
                int n = notasViolin[(int)(Math.random() * notasViolin.length)];
                melodyViolin.add(new Note(n, duracionesViolin[i % duracionesViolin.length]));
            }
            violin.add(melodyViolin);
            score.add(violin);

            // 2) Exportar a MIDI
            String midiFile = "salida.mid";
            Write.midi(score, midiFile);
            System.out.println("MIDI creado: " + midiFile);

            // 3) Configurar sintetizador Gervill con SoundFont
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            File sf2 = new File("soundfonts/GeneralUser-GS.sf2");
            if (!sf2.exists()) {
                System.err.println("SoundFont no encontrado en: " + sf2.getPath());
                System.exit(1);
            }
            Soundbank sb = MidiSystem.getSoundbank(sf2);
            synth.loadAllInstruments(sb);

            // 4) Preparar secuenciador y conectar al sintetizador
            Sequencer seq = MidiSystem.getSequencer(false);
            seq.open();
            // Conectar secuenciador al sintetizador usando Transmitter/Receiver
            Transmitter transmitter = seq.getTransmitter();
            Receiver receiver = synth.getReceiver();
            transmitter.setReceiver(receiver);

            // 5) Cargar y reproducir la secuencia
            Sequence sequence = MidiSystem.getSequence(new File(midiFile));
            seq.setSequence(sequence);
            seq.start();
            System.out.println("Reproduciendo con SoundFont...");

            // Esperar finalización
            while (seq.isRunning()) {
                Thread.sleep(100);
            }
            seq.stop();
            seq.close();
            synth.close();
            System.out.println("Fin de reproducción.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



