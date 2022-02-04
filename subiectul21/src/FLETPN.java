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
        int eM = net.addInputPlace();
        int st_1 = net.addPlace();
        int p4 = net.addPlace();
        int dtc_1 = net.addPlace();
        int p6 = net.addPlace();
        int ez = net.addInputPlace();
        int p8 = net.addPlace();
        int alt_1 = net.addPlace();
        int p10 = net.addPlace();

        net.setInitialMarkingForPlace(p0, FuzzyToken.zeroToken());

        int t0 = net.addTransition(0, OneXOneTable.defaultTable());
        int t1 = net.addTransition(0, parser.parseTable(doubleChannelDifferentiator2));
        int t2 = net.addTransition(0, OneXTwoTable.defaultTable());
        int t3 = net.addTransition(0, parser.parseTable(doubleChannelAdder));
        int t5 = net.addTransition(0, OneXTwoTable.defaultTable());
        int t6 = net.addTransition(0, OneXOneTable.defaultTable());
        int delta1 = net.addTransition(0, OneXOneTable.defaultTable());

        net.addArcFromPlaceToTransition(p0, t0, 1.0);

        net.addArcFromTransitionToPlace(t0, p1);

        net.addArcFromPlaceToTransition(p1, t1, 1.0);
        net.addArcFromPlaceToTransition(eM, t1, 1.0);

        net.addArcFromTransitionToPlace(t1, st_1);
        net.addArcFromTransitionToPlace(t1, p4);

        net.addArcFromPlaceToTransition(p4, t2, 1.0);

        net.addArcFromTransitionToPlace(t2, dtc_1);
        net.addArcFromTransitionToPlace(t2, p6);

        net.addArcFromPlaceToTransition(p6, delta1, 1.0);
        net.addArcFromPlaceToTransition(p6, t3, 1.0);

        net.addArcFromTransitionToPlace(delta1, p4);

        net.addArcFromPlaceToTransition(ez, t3, 1.0);

        net.addArcFromTransitionToPlace(t3, p8);

        net.addArcFromPlaceToTransition(p8, t5, 1.0);
        net.addArcFromTransitionToPlace(t5, p10);
        net.addArcFromTransitionToPlace(t5, alt_1);

        net.addArcFromPlaceToTransition(p10, t6, 1);

        net.addArcFromTransitionToPlace(t6, p1);
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
            input.put(eM, FuzzyDriver.createDriverFromMinMax(-1.0, 1.0).fuzzifie(command));

            input.put(ez, FuzzyDriver.createDriverFromMinMax(-1.0, 1.0).fuzzifie(command));
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
