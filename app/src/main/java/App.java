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

    public static boolean targetFound(int iterations) {
        if (PSOConfig.maxIterations > 0 && iterations >= PSOConfig.maxIterations) {
            System.out.printf("Limite de Iteracoes atingidas [%d]\nMelhor Particula:", iterations);
            printOutCoords(PSOConfig.globalBestPOS);
            return true;
        }

        double distance = 0;
        for (int i = 0; i < PSOConfig.N; i++) {
            distance = distance + Math.pow(PSOConfig.globalBestPOS[i] - PSOConfig.targetPOS[i], 2);
        }

        distance = Math.sqrt(distance);
        if (distance < PSOConfig.tolerance) {
            System.out.printf("Particulas chegaram ao ponto em: %d Iteracoes. GBest:", iterations);
            printOutCoords(PSOConfig.globalBestPOS);
            return true;
        } else {
            return false;
        }
    }

    public static void printOutCoords(double[] pos) {
        System.out.printf(" [");
        for (int i = 0; i < PSOConfig.N; i++) {
            if (PSOConfig.N - 1 == i) {
                System.out.printf("%f", pos[i]);
            } else {
                System.out.printf("%f,", pos[i]);
            }
        }
        System.out.printf("] \n");
    }
}
