
// MelodyComposer.java
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
        double durationSec=15.0; int bpm=120;
        double targetBeats=durationSec*bpm/60.0;
        FitnessEvaluator eval=new FitnessEvaluator(
                Set.of(C4,D4,E4,F4,G4,A4,B4), targetBeats
        );
        NotePool pool=new NotePool(
                new int[]{A2,C3,E3,A2}, new double[]{1,1,1,1},
                new int[]{A3,C4,E4,G4}, new double[]{0.5,0.25,1,0.75},
                new int[]{A4,C5,E5,G5,B5}, new double[]{0.25,0.5,0.75,1,1.5}
        );
        // Tres GAs independientes
        GeneticAlgorithm bajoGA=new GeneticAlgorithm(1000,100,0.1,0.8,
                pool::randomBajoNote, eval);
        GeneticAlgorithm pianoGA=new GeneticAlgorithm(1000,100,0.1,0.8,
                pool::randomPianoNote, eval);
        GeneticAlgorithm violinGA=new GeneticAlgorithm(1000,100,0.1,0.8,
                pool::randomViolinNote, eval);

        Phrase bestBajo=bajoGA.run();
        Phrase bestPiano=pianoGA.run();
        Phrase bestViolin=violinGA.run();

        Score score=new Score("GA Compuesta"); score.setTempo(bpm);
        Part pb=new Part("Bajo", ELECTRIC_PIANO,0); pb.add(bestBajo); score.add(pb);
        Part pp=new Part("Piano", PIANO,1); pp.add(bestPiano); score.add(pp);
        Part pv=new Part("Violin", FLUTE,2); pv.add(bestViolin); score.add(pv);

        Write.midi(score,"final.mid");
        playMidi("final.mid",bpm);
    }
    private static void playMidi(String file,int bpm)throws Exception{
        Synthesizer s=MidiSystem.getSynthesizer(); s.open();
        s.loadAllInstruments(MidiSystem.getSoundbank(new File("soundfonts/GeneralUser-GS.sf2")));
        Sequencer seq=MidiSystem.getSequencer(false); seq.open();
        seq.getTransmitter().setReceiver(s.getReceiver());
        Sequence seqn=MidiSystem.getSequence(new File(file));
        seq.setSequence(seqn); seq.setTempoInBPM(bpm); seq.start();
        while(seq.isRunning())Thread.sleep(100);
        seq.stop(); seq.close(); s.close();
    }
}
