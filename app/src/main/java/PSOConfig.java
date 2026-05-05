import java.util.Scanner;
public class PSOConfig {

    // CONFIGURACAO MANUAL // TODO: fazer o user dar input do quer quer aqui (com
    // swing se deus quiser)
    public static final boolean debug = false;
    public static int N = 2; // dimensoes
    public static double[][] EscopoDeBusca = new double[N][2]; // [Escopo N dim] [0 -> min e 1 -> max]
    public static double[] TargetPOS = new double[N]; // para N = 2, INPUT
    public static double FatorCognitivo = 2; // Coeficiente Cognitivo / INPUT
    public static double FatorSocial = 0.8; // Coeficiente Social / INPUT
    public static int maxParticles = 100; // INPUT
    public static double maxDistance = 0; // TO DO
    public static double Tolerance = 1;
    public static double[] GlobalBestPOS = new double[N];
    public static double weight = 0.1; // INPUT

    private static void initNDimension(){
        Scanner read = new Scanner(System.in);
        int temp=0;
        System.out.printf("\nSet N Dimensions: (N>0)");
        temp = read.nextInt();
        if(temp > 0){
            N=temp;
            System.out.printf("\nN Set to %d\n",N);
        }else{
            System.out.printf("\nN > 0\n");
            N=0;
        }
        read.close();
    }
    public static void setPSOConfig(){
        System.out.printf("\nPSOConfig\n");
        while(N<1){
            initNDimension();
        }
        System.out.printf("\n Escolha o valor que deseja modificar\n[1] Escopo: ");
        double[] temp = new double[N];
        for(int i=0;i<N;i++){
            temp[i] = EscopoDeBusca[i][0];
        }
        printOutCoords(temp);
         for(int i=0;i<N;i++){
            temp[i] = EscopoDeBusca[i][1];
        }
        printOutCoords(temp);
        
    
        System.out.printf("\n[2] Target POS: ");
        printOutCoords(TargetPOS);

        System.out.printf("\n[3] Coeficiente Cognitivo: %f\n[4] Coeficiente Social: %f\n[5] Maximo de Particulas: %f\n[6] Tolerancia: %f\n[7] Peso (inercia): %f",
            FatorCognitivo,FatorSocial,maxParticles,Tolerance,weight
        );
        Scanner read = new Scanner(System.in);
        int opcao = read.nextInt();

        switch(opcao){
            case 1: {
                setarEscopo();
            }
            case 2: {
                setarTarget();
            }
            case 3: {
                System.out.println("Insira o valor para Coeficiente Cognitivo:");
                FatorCognitivo = read.nextDouble();
                break;
            }
            case 4: {
                System.out.println("Insira o valor para Coeficiente Social:");
                FatorSocial = read.nextDouble();
                break;
            }
            case 5: {
                System.out.println("Insira o valor para Maximo de Particulas:");
                maxParticles = read.nextInt();
                break;
            }
            case 6: {
                System.out.println("Insira o valor para Tolerancia:");
                Tolerance = read.nextDouble();
                break;
            }
            case 7: {
                System.out.println("Insira o valor para Peso:");
                weight = read.nextDouble();
                break;
            }
            default: {
                System.out.println("INVALIDO");
            }
        }
        read.close();
    }
    private static void setarEscopo(){
        Scanner read = new Scanner(System.in);
        System.out.printf("Insira o valor minimo da area de busca, com %d dimensoes\n[",N);
        for(int i=0;i<N;i++){
            EscopoDeBusca[i][0] = read.nextDouble();
            if(i+1!=N){
            System.out.printf(",");
            }
        }
        System.out.printf("]\nInsira o valor maximo da area de busca, com %d dimensoes\n[",N);
         for(int i=0;i<N;i++){
            EscopoDeBusca[i][1] = read.nextDouble();
            if(i+1!=N){
            System.out.printf(",");
            }
        }
        read.close();
    }
    private static void setarTarget(){
        Scanner read = new Scanner(System.in);
        System.out.printf("Insira o valor do alvo, com %d dimensoes\n[",N);
        for(int i=0;i<N;i++){
            TargetPOS[i] = read.nextDouble();
            if(i+1!=N){
            System.out.printf(",");
            }
        }
        System.out.printf("]");
        read.close();
    }

    private static void printOutCoords(double[] pos){
        System.out.printf(" [");
        for(int i=0;i<N;i++){
            if(N-1==i){
                System.out.printf("%f",pos[i]);
            }else{
                System.out.printf("%f,",pos[i]);
            }
        }
        System.out.printf("] ");
    }
}
