public class PSOConfig {
    // INPUT ZONE (Esses valores virão do site agora)
    private int N = 2;
    private double[] targetPOS = {130, 140}; 
    private double fatorCognitivo = 2;
    private double fatorSocial = 0.8;
    private double tolerance = 0;
    private int maxParticles = 100;
    private int refreshRate = 100;
    private int maxIterations = -1;
    private double weight = 0.1;
    private double[][] escopoDeBusca = new double[N][2];    
    
    // DATA (Gerado durante a execução do algoritmo)
    private double[] globalBestPOS = new double[N];

    // --- GETTERS E SETTERS ---
    // O Javalin/Fastify precisam disso para conseguir ler e escrever os dados no formato JSON
    
    public int getN() { return N; }
    public void setN(int n) { 
        this.N = n; 
        this.globalBestPOS = new double[n]; // Ajusta o tamanho do melhor global
    }

    public double[] getTargetPOS() { return targetPOS; }
    public void setTargetPOS(double[] targetPOS) { this.targetPOS = targetPOS; }

    public double getFatorCognitivo() { return fatorCognitivo; }
    public void setFatorCognitivo(double fatorCognitivo) { this.fatorCognitivo = fatorCognitivo; }

    public double getFatorSocial() { return fatorSocial; }
    public void setFatorSocial(double fatorSocial) { this.fatorSocial = fatorSocial; }

    public double getTolerance() { return tolerance; }
    public void setTolerance(double tolerance) { this.tolerance = tolerance; }

    public int getMaxParticles() { return maxParticles; }
    public void setMaxParticles(int maxParticles) { this.maxParticles = maxParticles; }

    public int getRefreshRate() { return refreshRate; }
    public void setRefreshRate(int refreshRate) { this.refreshRate = refreshRate; }

    public int getMaxIterations() { return maxIterations; }
    public void setMaxIterations(int maxIterations) { this.maxIterations = maxIterations; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double[][] getEscopoDeBusca() { return escopoDeBusca; }
    public void setEscopoDeBusca(double[][] escopoDeBusca) { this.escopoDeBusca = escopoDeBusca; }

    public double[] getGlobalBestPOS() { return globalBestPOS; }
    public void setGlobalBestPOS(double[] globalBestPOS) { this.globalBestPOS = globalBestPOS; }
}