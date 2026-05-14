import java.util.Random;

public class Particula {

    private double[] position = new double[PSOConfig.N];
    private double[] pBestposition = new double[PSOConfig.N];
    private double[] velocity = new double[PSOConfig.N];
    
    public Particula() {
        Random random = new Random();
        double max,min;
        for(int i = 0;i < PSOConfig.N; i++){
            // TODO: MANUAL INPUT (RESOLVER DEPOIS)
            PSOConfig.escopoDeBusca[i][0]=-1000; // seta min 4 all dimensions
            PSOConfig.escopoDeBusca[i][1]=0; // seta max 4 all dimensions
            // 
            min = PSOConfig.escopoDeBusca[i][0];
            max = PSOConfig.escopoDeBusca[i][1];

            pBestposition[i]=Math.abs(min)+Math.abs(max);
            position[i] = random.nextDouble() * (max - min) + min;
            velocity[i] = 0;
        }
    }
    
    public void aplicarVelocity() {
        for (int i = 0; i < PSOConfig.N; i++) {
            position[i] = position[i] + velocity[i];
            if(position[i]<PSOConfig.escopoDeBusca[i][0]){ // new pos < min
                position[i]=PSOConfig.escopoDeBusca[i][0];
            }
            if(position[i]>PSOConfig.escopoDeBusca[i][1]){ // new pos > max 
                position[i]=PSOConfig.escopoDeBusca[i][1];
            }     
        }
    }
    public void calcularVelocity(){
        Random random = new Random();
        double peso = PSOConfig.weight;
        double c1 = PSOConfig.fatorSocial;
        double c2 = PSOConfig.fatorCognitivo;
        double[] vetor1 = new double[PSOConfig.N];
        double[] vetor2 = new double[PSOConfig.N];
            
        vetor1=subtracaoVetores(pBestposition, position);
        vetor2=subtracaoVetores(PSOConfig.globalBestPOS, position);
        if(PSOConfig.debug){
            for(int i=0;i<PSOConfig.N;i++){
                System.out.printf("\n\nVETOR 1:[%d] %f %f",i,vetor1[i],vetor1[i]);
                System.out.printf("\n\nVETOR 2:[%d] %f %f",i,vetor2[i],vetor2[i]);
            }
        }

        for(int i=0;i<PSOConfig.N;i++){
            // velocity = peso * velocity + coeficienteCognitivo(c1) * rand(0-1) * ( pBest - posicao ) + coeficienteSocial * rand(0-1) * ( gbest - posicao)
            double r1 = random.nextDouble(1);
            double r2 = random.nextDouble(1);
            vetor1[i]=vetor1[i]*r1*c1;
            vetor2[i]=vetor2[i]*r2*c2;
            if(PSOConfig.debug){
                System.out.printf("\n\nVETOR 1 IN4 [%d] %f %f\tr1: %f c1: %f",i,vetor1[i],vetor1[i],r1,c1);
                System.out.printf("\n\nVETOR 2 IN4 [%d] %f %f\tr2: %f c2: %f",i,vetor2[i],vetor2[i],r2,c2); 
            }          
        }
        double[] vetor3 = somaVetores(vetor1, vetor2);
        for(int i=0;i<PSOConfig.N;i++){
            velocity[i]=peso*velocity[i]+vetor3[i];
        }
    }

    public double getDistance(double[] pos1, double[] pos2) { // Distancia de dois pontos
        double d = 0;
        for(int i=0;i<PSOConfig.N;i++){
            d=d+Math.pow(pos1[i] - pos2[i], 2);
        }
        return Math.sqrt(d);
    }

    public double getTargetDistance(double[] pos) {
        double d = 0;
        for(int i=0;i<PSOConfig.N;i++){
        d = d+Math.pow(pos[i] - PSOConfig.targetPOS[i], 2);
        }
        d=Math.sqrt(d);
        return d;
    }

    public double[] somaVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[PSOConfig.N];
        for(int i=0;i<PSOConfig.N;i++){
            resultado[i]=pos1[i]+pos2[i];
        }
        return resultado;
    }

    public double[] subtracaoVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[PSOConfig.N];
        for(int i=0;i<PSOConfig.N;i++){
            resultado[i]=pos1[i]-pos2[i];
            if(PSOConfig.debug){
            System.out.printf("\nSUB: %f-%f=%f",pos1[i],pos2[i],resultado[i]);
            }
        }
        return resultado;
    }


    public void UpdatePersonalAndGlobal() {
        if (getTargetDistance(position) < getTargetDistance(pBestposition)) {
            pBestposition = position.clone(); // Pelo visto tem q clonar pq senao só aponta, obrigado chatgpt por explicar isso
        }
        if (getTargetDistance(position) < getTargetDistance(PSOConfig.globalBestPOS)) {
                PSOConfig.globalBestPOS = position.clone();
        }
    }
    public boolean checkFound(){
        if(getDistance(position, PSOConfig.targetPOS)<PSOConfig.tolerance){
            return true;
        }else{
            return false;
        }
    }

    public double[] getposition () {
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
