import java.awt.BasicStroke;
import java.lang.Thread;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String FASTIFY_URL = "http://localhost:3000/api/pso/iteracao";
    private static final String CONFIG_URL = "http://localhost:3000/api/pso/config";

    public static void main(String[] args) throws Exception {
        System.out.println("Aguardando comando de inicialização pelo React...");

        while (true) {
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONFIG_URL))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonResponse = response.body();

                
                if (jsonResponse != null && jsonResponse.contains("\"comando\":\"START\"")) {
                    System.out.println("\n========================================");
                    System.out.println("Comando START recebido! Configurando enxame...");

                    
                    PSOConfig config = new PSOConfig();

                    
                    atualizarConfigComJson(config, jsonResponse);

                    System.out.printf("Iniciando simulação com %d partículas em busca do alvo [%.2f, %.2f]\n",
                            config.getMaxParticles(), config.getTargetPOS()[0], config.getTargetPOS()[1]);

                    
                    IFuncaoObjetivo problema = new ProblemaAlvo(config.getTargetPOS());
                    int maxParticles = config.getMaxParticles();
                    Particula[] objParticulas = new Particula[maxParticles];

                    System.out.println("DEBUG: Inicializando partículas...");
                    for (int i = 0; i < maxParticles; i++) {
                        objParticulas[i] = new Particula(config, problema); 
                        objParticulas[i].UpdatePersonalAndGlobal();
                    }
                    System.out.println("DEBUG: Partículas inicializadas com sucesso!");

                    
                    int iterations = 0;
                    int maxIteracoes = 500;
                    do {
                        System.out.printf("DEBUG: Iniciando Iteração #%d\n", iterations);

                        
                        for (int i = 0; i < maxParticles; i++) {
                            
                            objParticulas[i].calcularVelocity();

                            
                            objParticulas[i].aplicarVelocity();

                            
                            objParticulas[i].UpdatePersonalAndGlobal();
                        }

                        System.out.println("DEBUG: Enviando dados para o Fastify...");
                        
                        enviarDadosParaFastify(iterations, objParticulas, config);

                        
                        Thread.sleep(config.getRefreshRate());

                        System.out.println("DEBUG: Checando condição de parada (targetFound)...");
                        iterations++;

                    } while (!targetFound(iterations, config) && iterations < maxIteracoes);
                    resetarComandoFastify();
                    System.out.println("Simulação finalizada com sucesso! Aguardando novos comandos...");
                    System.out.println("========================================\n");
                }
            } catch (Exception e) {
                System.err.println("Erro na comunicação com o backend: " + e.getMessage());
            }

            
            Thread.sleep(1000);
        }
    }

    /**
     * Faz o mapeamento manual do JSON vindo do Fastify diretamente para os métodos
     * setters do seu PSOConfig, evitando a necessidade de libs externas de parsing.
     */
    private static void atualizarConfigComJson(PSOConfig config, String json) {
        try {
            int numParticulas = Integer.parseInt(extrairValorJson(json, "numParticulas"));
            double targetX = Double.parseDouble(extrairValorJson(json, "targetX"));
            double targetY = Double.parseDouble(extrairValorJson(json, "targetY"));
            int refreshRate = Integer.parseInt(extrairValorJson(json, "refreshRate"));

            
            config.setMaxParticles(numParticulas);
            config.setTargetPOS(new double[] { targetX, targetY });
            config.setRefreshRate(refreshRate);

            
            
            config.setGlobalBestPOS(new double[] { 0.0, 0.0 });
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao parsear os campos de input do JSON. Usando padrões. " + e.getMessage());
        }
    }

    private static String extrairValorJson(String json, String chave) {
        String padraoChave = "\"" + chave + "\":";
        int indexChave = json.indexOf(padraoChave);
        if (indexChave == -1)
            return "0";

        int indexInicio = indexChave + padraoChave.length();
        int indexFim = json.indexOf(",", indexInicio);
        if (indexFim == -1) {
            indexFim = json.indexOf("}", indexInicio);
        }

        return json.substring(indexInicio, indexFim).trim().replace("\"", "");
    }

    /**
     * Avisa o Fastify para voltar o comando para STOP, evitando que a simulação
     * seja reiniciada infinitamente no mesmo clique.
     */
    private static void resetarComandoFastify() {
        try {
            
            String jsonReset = "{\"comando\":\"STOP\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONFIG_URL)) 
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonReset))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("🤖 Servidor Fastify resetado para STOP com sucesso.");
            } else {
                System.err.println("⚠️ O servidor respondeu com status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Falha ao resetar o comando no Fastify: " + e.getMessage());
        }
    }

    
    private static void enviarDadosParaFastify(int iteracao, Particula[] particulas, PSOConfig config) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"iteracao\":").append(iteracao).append(",");
            json.append("\"globalBest\":[").append(config.getGlobalBestPOS()[0]).append(",")
                    .append(config.getGlobalBestPOS()[1]).append("],");
            json.append("\"particulas\":[");

            for (int i = 0; i < particulas.length; i++) {
                json.append("[").append(particulas[i].getpositionI(0)).append(",").append(particulas[i].getpositionI(1))
                        .append("]");
                if (i < particulas.length - 1)
                    json.append(",");
            }
            json.append("]}");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FASTIFY_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            System.err.println("Erro ao enviar dados para o Fastify: " + e.getMessage());
        }
    }

    
    public static boolean targetFound(int iterations, PSOConfig config) {
        if (config.getMaxIterations() > 0 && iterations >= config.getMaxIterations()) {
            return true;
        }
        System.out.println("DEBUG: Iteração " + iterations + " | Distância: " + distance);
        double distance = 0;
        for (int i = 0; i < config.getN(); i++) {
            distance = distance + Math.pow(config.getGlobalBestPOS()[i] - config.getTargetPOS()[i], 2);
        }
        distance = Math.sqrt(distance);

        
        System.out.printf("DEBUG PARADA: Iteração #%d | Distância até o Alvo: %.4f | Tolerância: %.4f\n",
                iterations, distance, config.getTolerance());

        if (distance < 2.0) { 
            return true;
        }
        return false;
    }

    
    public static void printOutCoords(double[] pos, PSOConfig config) {
        System.out.printf(" [");
        for (int i = 0; i < config.getN(); i++) {
            if (config.getN() - 1 == i) {
                System.out.printf("%f", pos[i]);
            } else {
                System.out.printf("%f,", pos[i]);
            }
        }
        System.out.printf("] \n");
    }
}