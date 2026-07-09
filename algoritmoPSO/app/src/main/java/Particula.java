import java.util.Random;

public class Particula {

    private double[] position;
    private double[] pBestposition;
    private double[] velocity;
    private IFuncaoObjetivo funcaoObjetivo; 
    
    private PSOConfig config;

    
    public Particula(PSOConfig config) {
        this.config = config;
        this.funcaoObjetivo = funcaoObjetivo;
        Random random = new Random();
        
        
        this.position = new double[config.getN()];
        this.pBestposition = new double[config.getN()];
        this.velocity = new double[config.getN()];
        
        double max, min;
        for(int i = 0; i < config.getN(); i++){
            
            config.getEscopoDeBusca()[i][0] = -1000; 
            config.getEscopoDeBusca()[i][1] = 1000;  
            
            min = config.getEscopoDeBusca()[i][0];
            max = config.getEscopoDeBusca()[i][1];

            pBestposition[i] = Math.abs(min) + Math.abs(max);
            position[i] = random.nextDouble() * (max - min) + min;
            velocity[i] = 0;
        }
    }
    public void UpdatePersonalAndGlobal() {
        if (funcaoObjetivo.avaliar(position) < funcaoObjetivo.avaliar(pBestposition)) {
            pBestposition = position.clone();
        }
        if (funcaoObjetivo.avaliar(position) < funcaoObjetivo.avaliar(config.getGlobalBestPOS())) {
            config.setGlobalBestPOS(position.clone());
        }
    }
    
    public void aplicarVelocity() {
        for (int i = 0; i < config.getN(); i++) {
            position[i] = position[i] + velocity[i];
            
            
            
            if(position[i] < config.getEscopoDeBusca()[i][0]){ 
                position[i] = config.getEscopoDeBusca()[i][0];
            }
            if(position[i] > config.getEscopoDeBusca()[i][1]){ 
                position[i] = config.getEscopoDeBusca()[i][1];
            }     
        }
    }

    public void calcularVelocity(){
        Random random = new Random();
        double peso = config.getWeight();
        double c1 = config.getFatorSocial();
        double c2 = config.getFatorCognitivo();
            
        double[] vetor1 = subtracaoVetores(pBestposition, position);
        double[] vetor2 = subtracaoVetores(config.getGlobalBestPOS(), position);

        for(int i = 0; i < config.getN(); i++){
            double r1 = random.nextDouble();
            double r2 = random.nextDouble();
            vetor1[i] = vetor1[i] * r1 * c1;
            vetor2[i] = vetor2[i] * r2 * c2;
        }
        
        double[] vetor3 = somaVetores(vetor1, vetor2);
        for(int i = 0; i < config.getN(); i++){
            velocity[i] = peso * velocity[i] + vetor3[i];
        }
    }

    public double getDistance(double[] pos1, double[] pos2) { 
        double d = 0;
        for(int i = 0; i < config.getN(); i++){
            d = d + Math.pow(pos1[i] - pos2[i], 2);
        }
        return Math.sqrt(d);
    }

    public double getTargetDistance(double[] pos) {
        double d = 0;
        for(int i = 0; i < config.getN(); i++){
            d = d + Math.pow(pos[i] - config.getTargetPOS()[i], 2);
        }
        return Math.sqrt(d);
    }

    public double[] somaVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[config.getN()];
        for(int i = 0; i < config.getN(); i++){
            resultado[i] = pos1[i] + pos2[i];
        }
        return resultado;
    }

    public double[] subtracaoVetores(double[] pos1, double[] pos2) {
        double[] resultado = new double[config.getN()];
        for(int i = 0; i < config.getN(); i++){
            resultado[i] = pos1[i] - pos2[i];
        }
        return resultado;
    }

    public void UpdatePersonalAndGlobal() {
        if (getTargetDistance(position) < getTargetDistance(pBestposition)) {
            pBestposition = position.clone(); 
        }
        if (getTargetDistance(position) < getTargetDistance(config.getGlobalBestPOS())) {
            config.setGlobalBestPOS(position.clone());
        }
    }

    public boolean checkFound(){
        if(getDistance(position, config.getTargetPOS()) < config.getTolerance()){
            return true;
        } else {
            return false;
        }
    }

    
    public double[] getposition() { return position; }
    public double getpositionI(int i) { return position[i]; }
    public void setposition(double[] position) { this.position = position; }

    public double[] getpBestposition() { return pBestposition; }
    public double getpBestpositionI(int i) { return pBestposition[i]; }
    public void setpBestposition(double[] pBestposition) { this.pBestposition = pBestposition; }

    public double[] getVelocity() { return velocity; }
    public double getVelocityI(int i) { return velocity[i]; }
    public void setVelocity(double[] velocity) { this.velocity = velocity; }

    public double[] getPosition() { return position; }
    public void setPosition(double[] position) { this.position = position; }
}