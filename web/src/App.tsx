import {
  ResponsiveContainer,
  ScatterChart,
  Scatter,
  XAxis,
  YAxis,
  ZAxis,
  CartesianGrid,
  Tooltip,
  Cell,
} from "recharts";
import "./App.css";
import { useState, useEffect } from "react";
import axios from "axios";

interface IteracaoPSO {
  iteracao: number;
  globalBest: [number, number];
  particulas: [number, number][];
}

function App() {
  // 1. Iniciamos os dados zerados/limpos para aguardar o Java
  const [dados, setDados] = useState<IteracaoPSO>({
    iteracao: 0,
    globalBest: [0, 0],
    particulas: [],
  });

  // 2. Estado para o Alvo (Target) começar onde o formulário padrão indica
  const [target, setTarget] = useState([{ x: 400, y: 400 }]);

  // 3. POLLING: Fica buscando o progresso do Java no Fastify a cada 100ms automaticamente
  useEffect(() => {
    const buscarProgresso = async () => {
      try {
        // Rota onde o Java está dando POST nas partículas a cada iteração
        const response = await axios.get<IteracaoPSO>("http://localhost:3000/api/pso/iteracao");
        if (response.data) {
          setDados(response.data);
        }
      } catch (error) {
        // Silencia enquanto o Java não envia nada
      }
    };

    const intervalo = setInterval(buscarProgresso, 100);
    return () => clearInterval(intervalo);
  }, []);

  // 4. FUNÇÃO QUE ENVIA A CONFIGURAÇÃO COMPLETA DO USUÁRIO
  async function handleIniciarSimulacao(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    
    const tX = Number(formData.get("targetX"));
    const tY = Number(formData.get("targetY"));

    // Atualiza a posição da cruz vermelha no Recharts instantaneamente
    setTarget([{ x: tX, y: tY }]);

    // Monta o payload exatamente como o backend Fastify e o Java esperam
    const payload = {
      numParticulas: Number(formData.get("numParticulas")),
      targetX: tX,
      targetY: tY,
      refreshRate: Number(formData.get("refreshRate")),
      comando: "START", // Aciona o gatilho do while(true) no Java
    };

    try {
      // Envia os inputs do usuário para a rota de configuração do Fastify
      await axios.post("http://localhost:3000/api/pso/config", payload);
      console.log("Configurações enviadas! Simulador Java iniciado.");
    } catch (error) {
      console.error("Erro ao enviar configurações para o Fastify:", error);
    }
  }

  const dadosGrafico = dados.particulas.map((p) => ({ x: p[0], y: p[1] }));

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans p-6 flex flex-col gap-6">
      {/* HEADER TECH */}
      <header className="border-b border-slate-800 pb-4 flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-black tracking-tight bg-gradient-to-r from-cyan-400 to-blue-500 bg-clip-text text-transparent">
            Particle Swarm Optimization
          </h1>
          <p className="text-sm text-slate-400 mt-1">
            Otimização Matemática via Enxame de Partículas
          </p>
        </div>
        <div className="flex items-center gap-3 bg-slate-900 border border-slate-800 px-4 py-2 rounded-lg">
          <span className="relative flex h-3 w-3">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-cyan-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-3 w-3 bg-cyan-500"></span>
          </span>
          <span className="text-xs font-mono tracking-wider text-slate-300">
            ITERAÇÃO: #{dados.iteracao}
          </span>
        </div>
      </header>

      {/* DASHBOARD GRID */}
      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 flex-1">
        {/* COLUNA ESQUERDA: PAINEL DE CONTROLE E MÉTRICAS */}
        <div className="flex flex-col gap-6 lg:col-span-1">
          {/* CARD GBEST */}
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-xl">
            <h2 className="text-xs font-bold uppercase tracking-widest text-slate-400 mb-3">
              Global Best (GBest)
            </h2>
            <div className="font-mono text-xl text-emerald-400 font-semibold bg-slate-950 border border-slate-800/60 p-3 rounded-lg text-center">
              X: {dados.globalBest[0].toFixed(2)} <br /> 
              Y: {dados.globalBest[1].toFixed(2)}
            </div>
          </div>

          {/* CARD NOVO: FORMULÁRIO DE INPUTS DO USUÁRIO */}
          <form 
            onSubmit={handleIniciarSimulacao} 
            className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-xl flex-1 flex flex-col justify-between"
          >
            <div className="space-y-4">
              <h2 className="text-xs font-bold uppercase tracking-widest text-slate-400">
                Configurações do Enxame
              </h2>
              
              <div>
                <label className="block text-[11px] font-mono text-slate-400 mb-1">Nº DE PARTÍCULAS</label>
                <input 
                  type="number" 
                  name="numParticulas" 
                  defaultValue="35" 
                  min="5" 
                  max="200"
                  className="w-full p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono text-cyan-400 focus:outline-none focus:border-cyan-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-[11px] font-mono text-slate-400 mb-1">ALVO X</label>
                  <input 
                    type="number" 
                    name="targetX" 
                    defaultValue="400" 
                    min="-1000" 
                    max="1000"
                    className="w-full p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono text-slate-200 focus:outline-none focus:border-cyan-500"
                  />
                </div>
                <div>
                  <label className="block text-[11px] font-mono text-slate-400 mb-1">ALVO Y</label>
                  <input 
                    type="number" 
                    name="targetY" 
                    defaultValue="400" 
                    min="-1000" 
                    max="1000"
                    className="w-full p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono text-slate-200 focus:outline-none focus:border-cyan-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-mono text-slate-400 mb-1">DELAY DA ATUALIZAÇÃO (ms)</label>
                <input 
                  type="number" 
                  name="refreshRate" 
                  defaultValue="100" 
                  min="20"
                  className="w-full p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono text-slate-400 focus:outline-none focus:border-cyan-500"
                />
              </div>
            </div>

            {/* BOTÃO E LEGENDA VISUAL */}
            <div className="mt-6 space-y-4">
              <button 
                type="submit"
                className="w-full py-2.5 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 font-bold rounded-lg text-sm tracking-wide transition-all shadow-lg active:scale-[0.98]"
              >
                Disparar Enxame 🚀
              </button>

              <div className="border-t border-slate-800/80 pt-3 space-y-1.5">
                <h3 className="text-[10px] font-bold uppercase tracking-wider text-slate-500">
                  Legenda do Mapa
                </h3>
                <div className="flex items-center gap-2 text-xs">
                  <div className="w-2.5 h-2.5 rounded-full bg-cyan-400"></div>
                  <span className="text-slate-400">Partícula ativa</span>
                </div>
                <div className="flex items-center gap-2 text-xs">
                  <div className="w-2.5 h-2.5 rounded-full bg-emerald-400"></div>
                  <span className="text-slate-400">Melhor posição global (GBest)</span>
                </div>
                <div className="flex items-center gap-2 text-xs">
                  <div className="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse"></div>
                  <span className="text-slate-400">Alvo Selecionado (Target)</span>
                </div>
              </div>
            </div>
          </form>
        </div>

        {/* COLUNA DIREITA: O MAPA GRÁFICO DO ESPAÇO DE BUSCA */}
       {/* COLUNA DIREITA: O MAPA GRÁFICO DO ESPAÇO DE BUSCA */}
<div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-xl lg:col-span-3 flex flex-col h-full">
  <div className="flex justify-between items-center mb-4">
    <div>
      <h2 className="text-sm font-bold uppercase tracking-wider text-slate-300">
        Espaço de Busca Bidimensional
      </h2>
      <p className="text-[11px] text-slate-500 font-mono mt-0.5">
        Limites do Universo: [-1000, 1000]
      </p>
    </div>
    <span className="text-xs font-mono text-cyan-500 bg-cyan-950/40 border border-cyan-800/30 px-2 py-0.5 rounded">
      LIVE_STREAM
    </span>
  </div>

  {/* CONTÊINER AJUSTADO: Agora ele força o Recharts a ocupar todo o espaço restante */}
  <div className="flex-1 h-[650px] lg:h-[calc(100vh-200px)] min-h-[500px] bg-slate-950 rounded-lg p-2 border border-slate-800/50 relative">
    <ResponsiveContainer width="100%" height="100%">
      <ScatterChart margin={{ top: 20, right: 30, bottom: 20, left: 20 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" opacity={0.6} />

        <XAxis
          type="number"
          dataKey="x"
          name="X"
          domain={[-1000, 1000]}
          stroke="#475569"
          fontSize={11}
          fontFamily="monospace"
        />
        <YAxis
          type="number"
          dataKey="y"
          name="Y"
          domain={[-1000, 1000]}
          stroke="#475569"
          fontSize={11}
          fontFamily="monospace"
        />
        <ZAxis type="number" range={[100, 100]} /> {/* Aumentado levemente o tamanho do ponto de 64 para 100 */}

        <Tooltip cursor={{ strokeDasharray: "3 3", stroke: "#334155" }} />

        {/* Camada 1: Alvo */}
        <Scatter name="Target" data={target} fill="#f43f5e" shape="cross" />

        {/* Camada 2: Partículas */}
        <Scatter name="Partículas" data={dadosGrafico}>
          {dadosGrafico.map((entry, index) => {
            const isBest =
              entry.x === dados.globalBest[0] &&
              entry.y === dados.globalBest[1];
            return (
              <Cell
                key={`cell-${index}`}
                fill={isBest ? "#10b981" : "#22d3ee"}
                className={isBest ? "stroke-emerald-300 stroke-2" : "stroke-cyan-300/40 stroke-1"}
              />
            );
          })}
        </Scatter>
      </ScatterChart>
    </ResponsiveContainer>
  </div>
</div>

  );
}
export default App;