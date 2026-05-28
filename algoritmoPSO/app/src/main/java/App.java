import java.awt.BasicStroke;
import java.lang.Thread;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class App {
    // Cliente HTTP nativo do Java para conversar com o Fastify
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String FASTIFY_URL = "http://localhost:3000/api/pso/iteracao";
    private static final String CONFIG_URL = "http://localhost:3000/api/pso/config";

    public static void main(String[] args) throws Exception {
        System.out.println("Aguardando comando de inicialização pelo React...");

        while (true) {
            // 1. Pergunta para o Fastify se o usuário enviou a configuração
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONFIG_URL))
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String jsonResponse = response.body();

                // Checa se o comando recebido do front-end é START
                if (jsonResponse != null && jsonResponse.contains("\"comando\":\"START\"")) {
                    System.out.println("\n========================================");
                    System.out.println("Comando START recebido! Configurando enxame...");

                    // Instancia um novo config para esta simulação específica
                    PSOConfig config = new PSOConfig();

                    // 2. Extrai e injeta os valores vindos da interface web no objeto config
                    atualizarConfigComJson(config, jsonResponse);

                    System.out.printf("Iniciando simulação com %d partículas em busca do alvo [%.2f, %.2f]\n",
                            config.getMaxParticles(), config.getTargetPOS()[0], config.getTargetPOS()[1]);

                    // 3. Inicializa o enxame de acordo com os inputs da interface
                    // 3. Inicializa o enxame de acordo com os inputs da interface
                    int maxParticles = config.getMaxParticles();
                    Particula[] objParticulas = new Particula[maxParticles];

                    System.out.println("DEBUG: Inicializando partículas...");
                    for (int i = 0; i < maxParticles; i++) {
                        objParticulas[i] = new Particula(config);
                        objParticulas[i].UpdatePersonalAndGlobal();
                    }
                    System.out.println("DEBUG: Partículas inicializadas com sucesso!");

                    // 4. Executa o loop matemático do PSO até convergir ou atingir o limite
                    int iterations = 0;
                    int maxIteracoes = 500;
                    do {
                        System.out.printf("DEBUG: Iniciando Iteração #%d\n", iterations);

                        // Calcula a movimentação de cada partícula
                        for (int i = 0; i < maxParticles; i++) {
                            // System.out.println("DEBUG: Calculando velocidade da partícula " + i);
                            objParticulas[i].calcularVelocity();

                            // System.out.println("DEBUG: Aplicando velocidade na partícula " + i);
                            objParticulas[i].aplicarVelocity();

                            // System.out.println("DEBUG: Atualizando PBest e GBest da partícula " + i);
                            objParticulas[i].UpdatePersonalAndGlobal();
                        }

                        System.out.println("DEBUG: Enviando dados para o Fastify...");
                        // Transmite a iteração atual para o Fastify repassar ao gráfico
                        enviarDadosParaFastify(iterations, objParticulas, config);

                        // Usa o tempo de atualização customizado pelo usuário no front-end
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

            // Aguarda 1 segundo antes de checar se o botão foi clicado novamente
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

            // Aplica as propriedades enviadas via input
            config.setMaxParticles(numParticulas);
            config.setTargetPOS(new double[] { targetX, targetY });
            config.setRefreshRate(refreshRate);

            // Opcional: Se quiser resetar o GBest ao iniciar uma nova busca com alvo
            // diferente
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
            // Envia um JSON dizendo ao Fastify para voltar o comando para "STOP" ou "IDLE"
            String jsonReset = "{\"comando\":\"STOP\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONFIG_URL)) // Bate na mesma rota de configuração
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

    // Função que monta o JSON e envia para o backend em Fastify
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

    // Atualizado para receber o "config" e com os parênteses () nos métodos
    public static boolean targetFound(int iterations, PSOConfig config) {
        if (config.getMaxIterations() > 0 && iterations >= config.getMaxIterations()) {
            return true;
        }

        double distance = 0;
        for (int i = 0; i < config.getN(); i++) {
            distance = distance + Math.pow(config.getGlobalBestPOS()[i] - config.getTargetPOS()[i], 2);
        }
        distance = Math.sqrt(distance);

        // Esse print vai te mostrar a distância real diminuindo (ou não) no terminal
        System.out.printf("DEBUG PARADA: Iteração #%d | Distância até o Alvo: %.4f | Tolerância: %.4f\n",
                iterations, distance, config.getTolerance());

        if (distance < 2.0) { // Teste com margem de 2 unidades
            return true;
        }
        return false;
    }

    // Atualizado para receber o "config" e com os parênteses () nos métodos
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