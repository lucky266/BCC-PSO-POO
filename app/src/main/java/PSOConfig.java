public class PSOConfig {
    // INPUT ZONE:
    public static final boolean debug = false,showGraph = true;
    public static int N = 2; 
    // public static double[] targetPOS = new double[N]; // Automatic Input (API)


    public static double[] targetPOS = {3.14/2,3.14/2}; // Manual Input (apagar depois em doido)
    public static double targetValue=4; // utilizado só se functionMode[2] = true // Input vem da API
    // INPUT
    public static boolean[] functionMode = {true,false,true};// 1: function mode ligado // 2: true-> max 2:false-> min 3: true: closest to target z with function
    public static String functionInput = "x_0/x_1"; // api tem q fornecer a string se functionMode[0] = true
    public static double fatorCognitivo = 5, fatorSocial = 0.0001,tolerance = 0;
    public static int maxParticles = 1000, refreshRate=10, maxIterations = 10000;
    public static double weight = 0.9;
    public static double[][] escopoDeBusca = new double[N][2]; // [Escopo N dim] [0 -> min e 1 -> max]    
    public static double maxVelocity = 1; // >0 -> sem limite
    public static boolean penalidade = true; // gera penalidade zerando velocidade de particulap caso elas sejam piores que pbest atual delas
    // DATA
    public static double[] globalBestPOS = new double[N];
}
