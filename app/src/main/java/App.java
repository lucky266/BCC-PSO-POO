import java.awt.BasicStroke;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import java.lang.Thread;

public class App {
    public class Config {
        // CONFIGURACAO

        // TODO: passar as config pra outro arquivo, fazer as coisa funcionar dando input JSON, retornar um resultado de verdade
        public static final int N = 3;
        public static final boolean debug = false, ShowGraph = false;
        public static final double EscopoDeBusca = 500; // INPUT
        public static final double[] TargetPOS = { 195, 103, 138 }; // para N = 2, INPUT
        public static final double FatorCognitivo = 2; // Coeficiente Cognitivo / INPUT
        public static final double FatorSocial = 0.8; // Coeficiente Social / INPUT
        public static final int maxParticles = 100; // INPUT
        public static final double maxDistance = 100; // TO DO
        public static final double Tolerance = 0.000001;
        public static double[] GlobalBestPOS = new double[N]; // trocar pelo indice objparticulas[i] q possui melhor coord?
        public static double weight = 0.1; // INPUT
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {

        // data pra chart (DataT pra marcar Target in chart)
        double[] XData = new double[Config.maxParticles];
        double[] YData = new double[Config.maxParticles];

        Particula[] objParticulas = new Particula[Config.maxParticles];

        // Particle Init
        for (int i = 0; i < Config.maxParticles; i++) {
            objParticulas[i] = new Particula();
            objParticulas[i].getRandomPosition();
            objParticulas[i].initVelocity();
            objParticulas[i].UpdatePersonalAndGlobal();
            // pos only (paia)
            // DEBUG IN CONSOLE
            if (Config.debug) {
                System.out.printf(" \n\n init-part [%d]: [%f,%f] V: [%f,%f] P:[%f,%f] G: [%f,%f] D: %f", i,
                        objParticulas[i].getpositionI(0), objParticulas[i].getpositionI(1),
                        objParticulas[i].getVelocityI(0), objParticulas[i].getVelocityI(1),
                        objParticulas[i].getpBestpositionI(0), objParticulas[i].getpBestpositionI(1),
                        Config.GlobalBestPOS[0], Config.GlobalBestPOS[1],
                        objParticulas[i].getTargetDistance(objParticulas[i].getPosition()));
            }

            // setar primeiro pbest e gbest

        }

        boolean targetFound = false;
        int iterations = 0;
        // XChart <=> N=2 -> plano
        if (Config.ShowGraph && Config.N>=2) {
            // position to chartdata
            for (int i = 0; i < Config.maxParticles; i++) {
                XData[i] = objParticulas[i].getpositionI(0);
                YData[i] = objParticulas[i].getpositionI(1);
            }
            // Target to XChart data type (vetor por algum motivo)
            double[] xDataT = { Config.TargetPOS[0] };
            double[] yDataT = { Config.TargetPOS[1] };

            XYChart chart = QuickChart.getChart("Particulas", "x", "y", "particulas", XData, YData);
            // XYChart chart = new
            // XYChartBuilder().width((int)Config.XMax).height((int)Config.YMax).title("particulas").xAxisTitle("X").yAxisTitle("Y").build();

            chart.getStyler().setSeriesLines(new BasicStroke[] {
                    new BasicStroke(0.0f)
            });

            chart.getStyler().setChartTitleVisible(false);
            // chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
            chart.getStyler().setMarkerSize(16);

            chart.addSeries("target", xDataT, yDataT);
            chart.addSeries("particula", XData, YData);
            final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
            sw.displayChart();
            Thread.sleep(3000);
            // Atualização Chart
            do {
                // Atualização XData e YData pra chart
                for (int i = 0; i < App.Config.maxParticles; i++) {
                    XData[i] = objParticulas[i].getpositionI(0);
                    YData[i] = objParticulas[i].getpositionI(1);
                }

                // Scan das particulas
                if (Config.debug) {
                    System.out.printf("\n\n\tCICLO!\n\n");
                }
                for (int i = 0; i < App.Config.maxParticles; i++) {

                    // Modificador de posição
                    objParticulas[i].calcularVelocity();
                    objParticulas[i].aplicarVelocity();

                    // DEBUG IN CONSOLE
                    if (Config.debug) {
                        System.out.printf("\n\n P-before update [%d]: [%f,%f] V: [%f,%f] G: [%f,%f] D: %f", i,
                                objParticulas[i].getpositionI(0), objParticulas[i].getpositionI(1),
                                objParticulas[i].getVelocityI(0), objParticulas[i].getVelocityI(1),
                                Config.GlobalBestPOS[0], Config.GlobalBestPOS[1],
                                objParticulas[i].getTargetDistance(objParticulas[i].getPosition()));
                    }
                    // Compare e Seta pbest e gbest
                    objParticulas[i].UpdatePersonalAndGlobal();

                    if (Config.debug) {
                        System.out.printf("\n\n P-after update [%d]: [%f,%f] V: [%f,%f] G: [%f,%f] D: %f", i,
                                objParticulas[i].getpositionI(0), objParticulas[i].getpositionI(1),
                                objParticulas[i].getVelocityI(0), objParticulas[i].getVelocityI(1),
                                Config.GlobalBestPOS[0], Config.GlobalBestPOS[1],
                                objParticulas[i].getTargetDistance(objParticulas[i].getPosition()));
                    }

                    // Condicao de parada
                    if (objParticulas[i].checkFound()) {
                        targetFound = true;
                    } else {
                        targetFound = false;
                    }
                }
                Thread.sleep(50); // taxa de atualizacao

                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        chart.updateXYSeries("particulas", XData, YData, null);
                        sw.repaintChart();
                    }
                });
                // Contador de iterações
                iterations++;
            } while (targetFound() == false);
            System.out.printf("Particulas chegaram ao ponto em: %d Iteracoes. GBest:", iterations);
            printOutCoords(Config.GlobalBestPOS);
        } else {
            Thread.sleep(3000);
            do {
                for (int i = 0; i < Config.maxParticles; i++) {

                    objParticulas[i].calcularVelocity();
                    
                    objParticulas[i].aplicarVelocity();

                    objParticulas[i].UpdatePersonalAndGlobal();
                }

                // Condicao de parada
                
                Thread.sleep(50); // Refresh rate
                iterations++;
            } while (targetFound() == false);
            
            System.out.printf("Particulas chegaram ao ponto em: %d Iteracoes. GBest:", iterations);
            printOutCoords(Config.GlobalBestPOS);
            
            
        }
    }
    public static boolean targetFound(){
        double distance = 0;
        for(int i=0;i<Config.N;i++){
            distance=Math.pow(Config.GlobalBestPOS[i]- Config.TargetPOS[i], 2);
        }
        distance=Math.sqrt(distance);
        if(distance<App.Config.Tolerance){
            return true;
        }else{
            return false;
        }
    }
    public static void printOutCoords(double[] pos){
        System.out.printf(" [");
        for(int i=0;i<Config.N;i++){
            if(Config.N-1==i){
                System.out.printf("%f",pos[i]);
            }else{
                System.out.printf("%f,",pos[i]);
            }
        }
        System.out.printf("] \n");
    }
}

