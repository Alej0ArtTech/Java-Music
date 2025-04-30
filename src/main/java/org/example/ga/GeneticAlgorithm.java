package org.example.ga;

import jm.music.data.Phrase;
import java.util.*;

/**
 * GA parametrizado para una voz (NoteGenerator).
 */
public class GeneticAlgorithm {
    public interface NoteGenerator { NotePool.NoteData next(); }

    private final int popSize;
    private final int generations;
    private final double mutationRate;
    private final double crossoverRate;
    private final NoteGenerator generator;
    private final FitnessEvaluator evaluator;
    private final Random rand = new Random();

    public GeneticAlgorithm(int popSize, int generations,
                            double mutationRate, double crossoverRate,
                            NoteGenerator generator,
                            FitnessEvaluator evaluator) {
        this.popSize = popSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.generator = generator;
        this.evaluator = evaluator;
    }

    public Phrase run() {
        List<Phrase> pop = initPopulation();
        for (int gen = 0; gen < generations; gen++) {
            pop.sort(Comparator.comparingDouble(evaluator::fitness).reversed());
            List<Phrase> next = new ArrayList<>();
            int elit = popSize/20;
            next.addAll(pop.subList(0, elit));
            while (next.size() < popSize) {
                Phrase p1 = select(pop), p2 = select(pop);
                Phrase child = rand.nextDouble()<crossoverRate ? crossover(p1,p2) : copyPhrase(p1);
                mutate(child);
                next.add(child);
            }
            pop = next;
        }
        return Collections.max(pop, Comparator.comparingDouble(evaluator::fitness));
    }

    private List<Phrase> initPopulation() {
        List<Phrase> pop = new ArrayList<>();
        for (int i=0; i<popSize; i++) pop.add(randomPhrase());
        return pop;
    }

    private Phrase randomPhrase() {
        Phrase p = new Phrase();
        double sum = 0, limit = evaluator.getTargetBeats();
        while (sum < limit) {
            NotePool.NoteData nd = generator.next();
            double d = nd.duration;
            if (sum + d > limit) d = limit - sum;
            p.add(new jm.music.data.Note(nd.pitch, d)); sum += d;
        }
        return p;
    }

    private Phrase select(List<Phrase> pop) {
        Phrase best=null;
        for (int i=0;i<3;i++){
            Phrase c=pop.get(rand.nextInt(popSize));
            if (best==null || evaluator.fitness(c)>evaluator.fitness(best)) best=c;
        }
        return best;
    }
    // One-point crossover: toma la primera parte de ‘a’ y el resto de ‘b’
    private Phrase crossover(Phrase a, Phrase b) {
        Phrase child = new Phrase();
        int cut = rand.nextInt(a.size());
        // copia hasta el punto de corte de ‘a’
        for (int i = 0; i < cut; i++) {
            child.add(a.getNote(i));
        }
        // copia desde el punto de corte de ‘b’
        for (int i = cut; i < b.size(); i++) {
            child.add(b.getNote(i));
        }
        return child;
    }

    // Mutación: con probabilidad mutationRate cambia pitch o duración
    private void mutate(Phrase p) {
        for (int i = 0; i < p.size(); i++) {
            if (rand.nextDouble() < mutationRate) {
                if (rand.nextBoolean()) {
                    // muta pitch
                    NotePool.NoteData nd = generator.next();
                    p.getNote(i).setPitch(nd.pitch);
                } else {
                    // muta duración
                    NotePool.NoteData nd = generator.next();
                    p.getNote(i).setDuration(nd.duration);
                }
            }
        }
    }

    // Copia profunda de una Phrase
    private Phrase copyPhrase(Phrase src) {
        Phrase copy = new Phrase();
        for (jm.music.data.Note n : src.getNoteArray()) {
            copy.add(new jm.music.data.Note(n.getPitch(), n.getDuration()));
        }
        return copy;
    }
}