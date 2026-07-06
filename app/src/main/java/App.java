import java.awt.BasicStroke;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import java.lang.Thread;

public class App {
    public static void main(String[] args) throws Exception {

        // Particle Init
        int iterations = 0;
        Particula[] objParticulas = new Particula[PSOConfig.maxParticles];
        for (int i = 0; i < PSOConfig.maxParticles; i++) {
            objParticulas[i] = new Particula();
            objParticulas[i].UpdatePersonalAndGlobal();
        }

        
        // XChart <=> N=2 -> plano
        if (PSOConfig.showGraph && PSOConfig.N>=2) {
            // data pra chart (DataT pra marcar Target in chart)
            double[] XData = new double[PSOConfig.maxParticles];
            double[] YData = new double[PSOConfig.maxParticles];
            
            // position to chartdata
            for (int i = 0; i < PSOConfig.maxParticles; i++) {
                XData[i] = objParticulas[i].getpositionI(0);
                YData[i] = objParticulas[i].getpositionI(1);
            }
            // Target to XChart data type (vetor por algum motivo)
            double[] xDataT = { PSOConfig.targetPOS[0] };
            double[] yDataT = { PSOConfig.targetPOS[1] };

            XYChart chart = QuickChart.getChart("Particulas", "x", "y", "particulas", XData, YData);
           
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
                for (int i = 0; i < PSOConfig.maxParticles; i++) {
                    XData[i] = objParticulas[i].getpositionI(0);
                    YData[i] = objParticulas[i].getpositionI(1);
                }

                // Scan das particulas

                for (int i = 0; i < PSOConfig.maxParticles; i++) {

                    // Modificador de posição
                    objParticulas[i].calcularVelocity();
                    objParticulas[i].aplicarVelocity();
                    // Update do pBest e gBest
                    objParticulas[i].UpdatePersonalAndGlobal();
                }
                
                Thread.sleep(PSOConfig.refreshRate); // taxa de atualizacao

                javax.swing.SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        chart.updateXYSeries("particulas", XData, YData, null);
                        sw.repaintChart();
                    }
                });
                
                iterations++;
            } while (targetFound(iterations) == false);
        } else {
            Thread.sleep(3000);
            do {
                for (int i = 0; i < PSOConfig.maxParticles; i++) {

                    objParticulas[i].calcularVelocity();
                    
                    objParticulas[i].aplicarVelocity();

                    objParticulas[i].UpdatePersonalAndGlobal();
                }

                // Condicao de parada
                
                Thread.sleep(PSOConfig.refreshRate); // Refresh rate
                iterations++;
            } while (targetFound(iterations) == false);
        }
    }
    public static boolean targetFound(int iterations){
        if(PSOConfig.maxIterations > 0 && iterations >= PSOConfig.maxIterations){
            System.out.printf("Limite de Iteracoes atingidas [%d]\nMelhor Particula:",iterations);
            printOutCoords(PSOConfig.globalBestPOS);
            return true;
        }

        double distance = 0;
        for(int i=0;i<PSOConfig.N;i++){
            distance=distance+Math.pow(PSOConfig.globalBestPOS[i] - PSOConfig.targetPOS[i], 2);
        }
        
        distance=Math.sqrt(distance);
        if(distance<PSOConfig.tolerance){
            System.out.printf("Particulas chegaram ao ponto em: %d Iteracoes. GBest:", iterations);
            printOutCoords(PSOConfig.globalBestPOS);
            return true;
        }else{
            return false;
        }
    }
    public static void printOutCoords(double[] pos){
        System.out.printf(" [");
        for(int i=0;i<PSOConfig.N;i++){
            if(PSOConfig.N-1==i){
                System.out.printf("%f",pos[i]);
            }else{
                System.out.printf("%f,",pos[i]);
            }
        }
        System.out.printf("] \n");
    }
}

