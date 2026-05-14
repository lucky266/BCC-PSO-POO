public class PSOConfig {
    // INPUT ZONE:
    public static final boolean debug = false, showGraph = true;
    public static int N = 2;
    // public static double[] targetPOS = new double[N]; // Automatic Input (API)
    public static double[] targetPOS = {130, 140}; // Manual Input (apagar depois em doido)
    public static double fatorCognitivo = 2, fatorSocial = 0.8,tolerance = 0;
    public static int maxParticles = 100, refreshRate=100, maxIterations = -1;
    public static double weight = 0.1;
    public static double[][] escopoDeBusca = new double[N][2]; // [Escopo N dim] [0 -> min e 1 -> max]    
    // DATA
    
    public static double[] globalBestPOS = new double[N];
}
