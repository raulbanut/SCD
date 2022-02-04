import Main.FuzzyPVizualzer;
import View.MainView;
import core.FuzzyPetriLogic.Executor.AsyncronRunnableExecutor;
import core.FuzzyPetriLogic.FuzzyDriver;
import core.FuzzyPetriLogic.FuzzyToken;
import core.FuzzyPetriLogic.PetriNet.FuzzyPetriNet;
import core.FuzzyPetriLogic.PetriNet.Recorders.FullRecorder;
import core.FuzzyPetriLogic.Tables.OneXOneTable;
import core.FuzzyPetriLogic.Tables.OneXTwoTable;
import core.TableParser;

import java.util.HashMap;

public class FLETPN {

    static String doubleChannelAdder = ""
            + "{[<NL><NL><NL><NM><ZR>]"
            + " [<NL><NL><NM><ZR><PM>]"
            + " [<NL><NM><ZR><PM><PL>]"
            + " [<NM><ZR><PM><PL><PL>]"
            + " [<ZR><PM><PL><PL><PL>]}";

    static String doubleChannelDifferentiator2 = ""
            + "{[<ZR,ZR><nm,nm><nl,nl><nl,nl><nl,nl>]"
            + " [<pm,pm><ZR,ZR><nm,nm><nl,nl><nl,nl>]"
            + " [<pl,pl><pm,pm><ZR,ZR><nm,nm><nl,nl>]"
            + " [<pl,pl><pl,pl><pm,pm><ZR,ZR><nm,nm>]"
            + " [<pl,pl><pl,pl><pl,pl><pm,pm><ZR,ZR>]}";


    public static void main(String[] args) {
        TableParser parser = new TableParser();
        FuzzyPetriNet net = new FuzzyPetriNet();

        int p0 = net.addPlace();
        int p1 = net.addPlace();
        int p2 = net.addPlace();
        int p3 = net.addPlace();
        int p4 = net.addPlace();
        int p5 = net.addPlace();
        int p6 = net.addPlace();
        int p7 = net.addPlace();

        net.setInitialMarkingForPlace(p5, FuzzyToken.zeroToken());

        int t0 = net.addTransition(0, parser.parseTable(doubleChannelAdder));
        int t1 = net.addTransition(0, parser.parseTable(doubleChannelDifferentiator2));
        int t2 = net.addTransition(0, OneXTwoTable.defaultTable());
        int t3 = net.addTransition(0, OneXTwoTable.defaultTable());

        net.addArcFromPlaceToTransition(p0, t0, 1.0);
        net.addArcFromPlaceToTransition(p1, t0, 1.0);

        net.addArcFromTransitionToPlace(t0, p2);

        net.addArcFromPlaceToTransition(p2, t1, 1.0);
        net.addArcFromPlaceToTransition(p5, t1, 1.0);

        net.addArcFromTransitionToPlace(t1, p3);
        net.addArcFromTransitionToPlace(t1, p4);

        net.addArcFromPlaceToTransition(p3, t2, 1.0);

        net.addArcFromTransitionToPlace(t2, p5);
        net.addArcFromTransitionToPlace(t2, p6);

        net.addArcFromPlaceToTransition(p6, t3, 1.0);

        net.addArcFromTransitionToPlace(t3, p7);
        net.addArcFromTransitionToPlace(t3, p0);

        long period = 10;

        AsyncronRunnableExecutor executor = new AsyncronRunnableExecutor(net, period);
        FullRecorder recorder = new FullRecorder();
        executor.setRecorder(recorder);


        (new Thread(executor)).start();
        double command = 0.55;
        for (int i = 0; i < 200; i++) {
            if (i > 100) {
                command = 0.35;
            }
            HashMap<Integer, FuzzyToken> input = new HashMap<>();
            input.put(p1, FuzzyDriver.createDriverFromMinMax(-1.0, 1.0).fuzzifie(command));

            executor.putTokenInInputPlace(input);
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.stop();
        MainView mainView = FuzzyPVizualzer.visualize(net, recorder);
    }
}
