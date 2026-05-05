import java.util.Random;

public class Particula {

    private double[] position = new double[App.Config.N];
    private double[] pBestposition = new double[App.Config.N];
    private double[] velocity = new double[App.Config.N];

    public void initpBestposition() {
        for (int i = 0; i < App.Config.N; i++) {
            pBestposition[i] = App.Config.maxDistance;
        }
    }

    public void initVelocity() {
        for (int i = 0; i < App.Config.N; i++) {
            velocity[i] = 0;
        }
    }

  
    
    public void aplicarVelocity() {
        for (int i = 0; i < App.Config.N; i++) {
            position[i] = position[i] + velocity[i];
        }
    }
    public void calcularVelocity(){
        Random random = new Random();
        double peso = App.Config.weight;
        double c1 = App.Config.FatorSocial;
        double c2 = App.Config.FatorCognitivo;
        double[] vetor1 = new double[App.Config.N];
        double[] vetor2 = new double[App.Config.N];
            
        vetor1=subtracaoVetores(pBestposition, position);
        vetor2=subtracaoVetores(App.Config.GlobalBestPOS, position);
        if(App.Config.debug){
            for(int i=0;i<App.Config.N;i++){
                System.out.printf("\n\nVETOR 1:[%d] %f %f",i,vetor1[i],vetor1[i]);
                System.out.printf("\n\nVETOR 2:[%d] %f %f",i,vetor2[i],vetor2[i]);
            }
        }

        for(int i=0;i<App.Config.N;i++){
            // velocity = peso * velocity + coeficienteCognitivo(c1) * rand(0-1) * ( pBest - posicao ) + coeficienteSocial * rand(0-1) * ( gbest - posicao)
            double r1 = random.nextDouble(1);
            double r2 = random.nextDouble(1);
            vetor1[i]=vetor1[i]*r1*c1;
            vetor2[i]=vetor2[i]*r2*c2;
            if(App.Config.debug){
                System.out.printf("\n\nVETOR 1 IN4 [%d] %f %f\tr1: %f c1: %f",i,vetor1[i],vetor1[i],r1,c1);
                System.out.printf("\n\nVETOR 2 IN4 [%d] %f %f\tr2: %f c2: %f",i,vetor2[i],vetor2[i],r2,c2); 
            }          
        }
        double[] vetor3 = somaVetores(vetor1, vetor2);
        for(int i=0;i<App.Config.N;i++){
            velocity[i]=peso*velocity[i]+vetor3[i];
        }
    }
    public void getRandomPosition() {
        Random random = new Random();
        double max = App.Config.EscopoDeBusca;
        double min = 0;
        for (int i = 0; i < App.Config.N; i++) {
            position[i] = random.nextDouble() * (max - min) + min;
        }
    }

    public double getDistance(double[] pos1, double[] pos2) { // Distancia de dois pontos
        double d = 0;
        for(int i=0;i<App.Config.N;i++){
            d=d+Math.pow(pos1[i] - pos2[i], 2);
        }
        return Math.sqrt(d);
    }

    public double[] somaVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[App.Config.N];
        for(int i=0;i<App.Config.N;i++){
            resultado[i]=pos1[i]+pos2[i];
        }
        return resultado;
    }
     public double[] subtracaoVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[App.Config.N];
        for(int i=0;i<App.Config.N;i++){
            resultado[i]=pos1[i]-pos2[i];
            if(App.Config.debug){
            System.out.printf("\nSUB: %f-%f=%f",pos1[i],pos2[i],resultado[i]);
            }
        }
        return resultado;
    }

    public double getTargetDistance(double[] pos) {
        double d = 0;
        for(int i=0;i<App.Config.N;i++){
        d = d+Math.pow(pos[i] - App.Config.TargetPOS[i], 2);
        }
        d=Math.sqrt(d);
        return d;
    }

    public void UpdatePersonalAndGlobal() {
        if (getTargetDistance(position) < getTargetDistance(pBestposition)) {
            pBestposition = position.clone(); // Pelo visto tem q clonar pq senao só aponta, obrigado chatgpt por explicar isso
        }
        if (getTargetDistance(position) < getTargetDistance(App.Config.GlobalBestPOS)) {
                App.Config.GlobalBestPOS = position.clone();
        }
    }
    public boolean checkFound(){
        if(getDistance(position, App.Config.TargetPOS)<App.Config.Tolerance){
            return true;
        }else{
            return false;
        }
    }

    public double[] getposition() {
        return position;
    }

    public double getpositionI(int i) {
        return position[i];
    }

    public void setposition(double[] position) {
        this.position = position;
    }

    public double[] getpBestposition() {
        return pBestposition;
    }
    
    public double getpBestpositionI(int i) {
        return pBestposition[i];
    }

    public void setpBestposition(double[] pBestposition) {
        this.pBestposition = pBestposition;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public double getVelocityI(int i) {
        return velocity[i];
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

}
