public class PSOConfig {
    // INPUT ZONE:
    public static int N; // INPUT via API
    public static double[] targetPOS = new double[N]; // INPUT via API
    public static double targetValue; // utilizado só se functionMode[2] = true // INPUT via API
    public static boolean[] functionMode;// 1: function mode ligado // 2: true-> max 2:false-> min 3: true: closest to target z with function // INPUT via API
    public static String functionInput; // api tem q fornecer a string se functionMode[0] = true // INPUT via API
    public static double fatorCognitivo, fatorSocial, tolerance; // INPUT via API
    public static int maxParticles, refreshRate, maxIterations; // INPUT via API
    public static double weight; // INPUT via API
    public static double[][] escopoDeBusca = new double[N][2]; // [Escopo N dim] [0 -> min e 1 -> max] // INPUT via API   
    public static double maxVelocity; // x<0 -> sem limite // INPUT via API
    public static boolean penalidade; // gera penalidade zerando velocidade de particulap caso elas sejam piores que pbest atual delas // INPUT via API
    // DATA
    public static double[] globalBestPOS = new double[N];
}
