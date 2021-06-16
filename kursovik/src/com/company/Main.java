package com.company;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import org.apache.commons.collections15.Factory;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.PajekNetReader;

public class Main {
    private static Random rand = new Random();
    private static NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

    static {
        format.setMaximumFractionDigits(10);
        format.setMinimumFractionDigits(10);
    }

    private static Graph<Node, Object> loadGraph(String path) {
        Factory<Node> vertexFactory = Node::new;
        Factory<Object> edgeFactory = Object::new;
        Graph<Node, Object> graph = new SparseGraph<>();
        PajekNetReader<Graph<Node, Object>, Node, Object> pnr;
        try {
            pnr = new PajekNetReader<>(vertexFactory, edgeFactory);
            pnr.load(path, graph);
        } catch (IOException ignored) {
        }
        return graph;
    }

    private static void showCountInfectedNodes(Graph<Node, Object> graph){
        int col = 0;
        for (Node node : graph.getVertices()){
            if (node.isInfected()){
                col++;
            }
        }
        System.out.println(col);
    }

    public static void main(String[] args) {
        Graph<Node, Object> graph = loadGraph("C:\\Users\\miha7\\OneDrive\\Рабочий стол\\юдин\\kursovik\\src\\graphs\\YeastS.net");
//        Graph<Node, Object> graph = loadGraph("C:\Users\miha7\OneDrive\Рабочий стол\юдин\kursovik\src\graphs\\hep-th.net");
        System.out.println("Vertexes : " + graph.getVertexCount() + " Edges : " + graph.getEdgeCount());
        double[] exp1 = useVarLambda(graph, 0.1, 1, 0.1, 1100, 1000, 0.01);
        System.out.println("\nРезультат моделирования при 1% имунного населения");
        for (double v : exp1) {
            System.out.println(format.format(v));
        }
        showCountInfectedNodes(graph);
        double[] exp2 = useVarLambda(graph, 0.1, 1, 0.1, 1100, 1000, 0.1);
        System.out.println("\nРезультат моделирования при 10% имунного населения");
        for (double v : exp2) {
            System.out.println(format.format(v));
        }
        showCountInfectedNodes(graph);
        //double[] exp3 = useVarLambda(graph, 0.1, 1, 0.1, 1100, 1000, 0.3);
        //System.out.println("\nРезультат моделирования при 30% имунного населения");
        //for (double v : exp3) {
        //    System.out.println(format.format(v));
        //}
        //showCountInfectedNodes(graph);
    }

    /**
     * @param graph       - исследуемый граф
     * @param from_lambda - начальное значение интенсивности распространения
     * @param to_lambda   - конечное значение интенсивности распространения
     * @param step_lambda - шаг изменения значение интенсивности распространения
     * @param totalStep   - всего шагов моделирования
     * @param missedStep  - число шагов для установки стационарного процесса
     * @return массив со значениями плотности "инфицированных" вершин
     */
    private static double[] useVarLambda(Graph<Node, Object> graph, double from_lambda, double to_lambda, double step_lambda, int totalStep, int missedStep, double pop_immune) {
        double[] infVertDensity = new double[1 + (int) Math.ceil((to_lambda - from_lambda) / step_lambda)];
        int j = 0;
        for (double lambda = from_lambda; lambda <= to_lambda; lambda += step_lambda) {
            double[] d = modelingLambda(graph, totalStep, missedStep, lambda, pop_immune);
            double sum = 0;
            for (double v : d) {
                sum = sum + v;
            }
            infVertDensity[j] = sum / ((double) (totalStep - missedStep));
            j++;
        }
        return infVertDensity;
    }

    private static double[] modelingLambda(Graph<Node, Object> graph, int totalStep, int missedStep, double lambda, double pop_immune) {
        double[] mass = new double[totalStep - missedStep];
        Collection<Node> list = graph.getVertices();
        for (Node node : graph.getVertices()) {
            if (rand.nextDouble() < pop_immune) {
                node.setImmune();
            }
            if (rand.nextDouble() < 0.2) {
                node.setNew_infected(true);
            } else {
                node.setNew_infected(false);
            }
            node.fixNewState();
        }
        for (int i = 0; i < totalStep; i++) {
            for (Node node : list) {
                if (node.isInfected()) {
                    node.setNew_infected(false);
                    for (Node node_neighbor : graph.getNeighbors(node)) {
                        if (Math.random() < lambda) {
                            node_neighbor.setNew_infected(true);
                        }
                    }
                }
            }
            int inf = 0;
            int wel = 0;
            for (Node node : graph.getVertices()) {
                node.fixNewState();
                if (node.isInfected()) {
                    inf++;
                } else {
                    wel++;
                }
            }
            if (i >= missedStep) {
                mass[i - missedStep] = (inf) / (1.0 * (inf + wel));
            }
        }
        return mass;
    }
}