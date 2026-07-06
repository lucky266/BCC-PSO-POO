import java.util.Random;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Particula {

    private double[] position = new double[PSOConfig.N];
    private double[] pBestposition = new double[PSOConfig.N];
    private double[] velocity = new double[PSOConfig.N];
    
    public Particula() {
        Random random = new Random();
        double max,min;
        for(int i = 0;i < PSOConfig.N; i++){
            PSOConfig.escopoDeBusca[i][0]=-6.28; // seta min 4 all dimensions
            PSOConfig.escopoDeBusca[i][1]=6.28; // seta max 4 all dimensions
            min = PSOConfig.escopoDeBusca[i][0];
            max = PSOConfig.escopoDeBusca[i][1];

            pBestposition[i]=Math.abs(min)+Math.abs(max);
            position[i] = random.nextDouble() * (max - min) + min;
            velocity[i] = 0; //random.nextDouble() * (3 - (-3)) - 3;
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
        double c2 = PSOConfig.fatorSocial;
        double c1 = PSOConfig.fatorCognitivo;
        double[] vetor1 = new double[PSOConfig.N];
        double[] vetor2 = new double[PSOConfig.N];
        
            
        vetor1=subtracaoVetores(pBestposition, position);
        vetor2=subtracaoVetores(PSOConfig.globalBestPOS, position);
        // VETOR 1: FATOR PBEST VETOR 2: FATOR GBEST
        for(int i=0;i<PSOConfig.N;i++){
            // velocity = peso * velocity + coeficienteCognitivo(c1) * rand(0-1) * ( pBest - posicao ) + coeficienteSocial * rand(0-1) * ( gbest - posicao)
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            vetor1[i]=vetor1[i]*r1*c1; // FATOR BPEST c1 cognitivo
            vetor2[i]=vetor2[i]*r2*c2; // FATOOR GBEST c2 social         
        }
        double[] vetor3 = somaVetores(vetor1, vetor2); 
        for(int i=0;i<PSOConfig.N;i++){
            velocity[i]=peso*velocity[i]+vetor3[i];
        }
        if(PSOConfig.maxVelocity>=0){ // limitador de velocidade 
            for(int i=0;i<PSOConfig.N;i++){
                if(velocity[i]>PSOConfig.maxVelocity){
                    velocity[i]=PSOConfig.maxVelocity;
                }
            }
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
        if(PSOConfig.functionMode[0]){ // Function Mode ligado
            if(PSOConfig.functionMode[2]){ // closest matchs to input result
                if(Math.abs(calcularFitness(position)-PSOConfig.targetValue)<Math.abs(calcularFitness(pBestposition)-PSOConfig.targetValue)){
                    pBestposition=position.clone();
                }else{
                    if(PSOConfig.penalidade){
                            aplicarPenalidade();
                    }
                }
                if(Math.abs(calcularFitness(position)-PSOConfig.targetValue)<Math.abs(calcularFitness(PSOConfig.globalBestPOS)-PSOConfig.targetValue)){
                    PSOConfig.globalBestPOS=position.clone();
                }
            }else{              
                if(PSOConfig.functionMode[1]){ // Achar máximo
                    if(calcularFitness(position) > calcularFitness(pBestposition)){
                        pBestposition = position.clone();
                        
                    }else{
                        if(PSOConfig.penalidade){
                            aplicarPenalidade();
                        }
                    }
                    if(calcularFitness(position) > calcularFitness(PSOConfig.globalBestPOS)){
                        PSOConfig.globalBestPOS = position.clone();
                    }
                }else{ // Achar mínimo
                    if(calcularFitness(position) < calcularFitness(pBestposition)){
                        pBestposition = position.clone();
                    }else{
                        if(PSOConfig.penalidade){
                            aplicarPenalidade();
                        }
                    }
                    if(calcularFitness(position) < calcularFitness(PSOConfig.globalBestPOS)){
                            PSOConfig.globalBestPOS = pBestposition.clone();
                    }
                }
            }
        }else{

        }
    }
    public boolean checkFound(){
        if(getDistance(position, PSOConfig.targetPOS)<PSOConfig.tolerance){
            return true;
        }else{
            return false;
        }
    }

    // coisa nova ai: Calcular fitness baseado em expressao (new method)
    
    public double calcularFitness(double[] valor){
        // String para N var
        String[] variaveis = new String[PSOConfig.N];
        for(int i=0;i<PSOConfig.N;i++){
            variaveis[i]="x_"+i;
        }
        Expression expressao = new ExpressionBuilder(PSOConfig.functionInput)
        .variables(variaveis) // variaveis
        .build();

        for(int i=0;i<PSOConfig.N;i++){
            expressao.setVariable(variaveis[i],valor[i]);
        }
        double result = expressao.evaluate();
        return result;
    }
    
    // penalidade: zera velocidade se new pos for pior q pbest
    void aplicarPenalidade(){
        for(int i=0;i<PSOConfig.N;i++){
            velocity[i]=0;
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
