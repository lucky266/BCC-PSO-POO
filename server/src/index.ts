import Fastify from "fastify";
import cors from "@fastify/cors";

const app = Fastify({
  logger: true,
});

app.register(cors, {
  origin: "*",
});

let ultimaIteracao = {
  iteracao: 0,
  globalBest: [0, 0],
  particulas: [0, 0]
}
let simulacaoConfig = {
  numParticulas: 35,
  targetX: 400,
  targetY: 400,
  refreshRate: 100,
  comando: "STOP" // Começa parado esperando o clique do React
};
// Ajustado para receber os parâmetros padrão do Fastify
app.post("/api/pso/iteracao", async (request, reply) => {
  const body = request.body as typeof ultimaIteracao;

  ultimaIteracao = body;

  
  return {status: "recebido"};
});

app.get("/api/pso/iteracao", async(request, reply) => {
    return ultimaIteracao
})

app.get("/api/pso/dados-recebidos", async (request, reply) => {
    if(!ultimaIteracao){
        return reply.status(404).send({erro: "Nenhuma iteracao rodando ou recebida."})
    }

    return reply.send({ultimaIteracao})
});

app.post("/api/pso/config", async (request, reply) => {
    const body = request.body as Partial<typeof simulacaoConfig>

    simulacaoConfig = {...simulacaoConfig,  ...body}
    console.log(simulacaoConfig)
    console.log("🔄 Configuração atualizada no Fastify:", simulacaoConfig);
    return {status: "sucesso", dados: simulacaoConfig}


})

app.get("/api/pso/config", async () => {
    return simulacaoConfig
})

const start = async () => {
  try {
    await app.listen({ port: 3000, host: "0.0.0.0" });
    console.log("🚀 Servidor online na porta 3000!");
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};

start();
