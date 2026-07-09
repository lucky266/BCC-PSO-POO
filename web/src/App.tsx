import {
  ResponsiveContainer,
  ScatterChart,
  Scatter,
  XAxis,
  YAxis,
  ZAxis,
  CartesianGrid,
  Tooltip,
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
  const [dados, setDados] = useState<IteracaoPSO>({
    iteracao: 0,
    globalBest: [0, 0],
    particulas: [],
  });

  const [target, setTarget] = useState([{ x: 400, y: 400 }]);
  const [isSimulando, setIsSimulando] = useState(false);

  // Cálculo simples da distância euclidiana para exibir no painel de métricas
  const fitness = Math.sqrt(
    Math.pow(dados.globalBest[0] - target[0].x, 2) +
      Math.pow(dados.globalBest[1] - target[0].y, 2),
  ).toFixed(2);

  useEffect(() => {
    const buscarDados = async () => {
      try {
        const resProgresso = await axios.get(
          "http://localhost:3000/api/pso/iteracao",
        );
        setDados(resProgresso.data);

        const resStatus = await axios.get(
          "http://localhost:3000/api/pso/status",
        );
        setIsSimulando(resStatus.data.comando === "START");
      } catch (error) {
        console.error("Erro ao sincronizar com servidor");
      }
    };

    const intervalo = setInterval(buscarDados, 200);
    return () => clearInterval(intervalo);
  }, []);

  async function handleIniciarSimulacao(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setIsSimulando(true);
    const formData = new FormData(e.currentTarget);
    const tX = Number(formData.get("targetX"));
    const tY = Number(formData.get("targetY"));

    setTarget([{ x: tX, y: tY }]);
    const payload = {
      numParticulas: Number(formData.get("numParticulas")),
      targetX: tX,
      targetY: tY,
      refreshRate: Number(formData.get("refreshRate")),
      comando: "START",
    };

    try {
      await axios.post("http://localhost:3000/api/pso/config", payload);
    } catch (error) {
      alert(
        "Erro ao iniciar simulação. Verifique se o backend Java está rodando.",
      );
      setIsSimulando(false);
    }
  }

  const dadosGrafico = dados.particulas.map((p) => ({ x: p[0], y: p[1] }));

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans p-6 flex flex-col gap-6">
      <header className="border-b border-slate-800 pb-4 flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-black tracking-tight bg-gradient-to-r from-cyan-400 to-blue-500 bg-clip-text text-transparent">
            Particle Swarm Optimization
          </h1>
        </div>
        <div className="flex items-center gap-4">
          <div className="bg-slate-900 border border-slate-800 px-4 py-2 rounded-lg text-xs font-mono">
            STATUS:{" "}
            <span
              className={isSimulando ? "text-emerald-400" : "text-amber-400"}
            >
              {isSimulando ? "EXECUTANDO" : "AGUARDANDO"}
            </span>
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        <div className="flex flex-col gap-6 lg:col-span-1">
          {/* Métricas */}
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-xl space-y-4">
            <div>
              <h2 className="text-[10px] font-bold text-slate-500 uppercase">
                Fitness (Distância ao Alvo)
              </h2>
              <p className="text-2xl font-mono text-emerald-400">{fitness}</p>
            </div>
            {/* CARD GBEST */}
            <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-xl">
              <h2 className="text-xs font-bold uppercase tracking-widest text-slate-400 mb-3">
                Global Best (GBest)
              </h2>
              <div className="font-mono text-xl text-emerald-400 font-semibold bg-slate-950 border border-slate-800/60 p-3 rounded-lg text-center">
                X: {dados.globalBest ? dados.globalBest[0].toFixed(2) : "0.00"}{" "}
                <br />
                Y: {dados.globalBest ? dados.globalBest[1].toFixed(2) : "0.00"}
              </div>
            </div>
            <div>
              <h2 className="text-[10px] font-bold text-slate-500 uppercase">
                Iteração Atual
              </h2>
              <p className="text-xl font-mono text-cyan-400">
                #{dados.iteracao}
              </p>
            </div>
          </div>

          <form
            onSubmit={handleIniciarSimulacao}
            className="bg-slate-900 border border-slate-800 rounded-xl p-5 flex flex-col gap-4"
          >
            <h2 className="text-xs font-bold uppercase text-slate-400">
              Configurações
            </h2>
            <input
              type="number"
              name="numParticulas"
              defaultValue="35"
              className="w-full p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono text-cyan-400"
            />
            <div className="grid grid-cols-2 gap-2">
              <input
                type="number"
                name="targetX"
                defaultValue="400"
                className="p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono"
              />
              <input
                type="number"
                name="targetY"
                defaultValue="400"
                className="p-2 bg-slate-950 border border-slate-800 rounded text-sm font-mono"
              />
            </div>
            <button
              disabled={isSimulando}
              type="submit"
              className="w-full py-2.5 bg-cyan-600 hover:bg-cyan-500 disabled:bg-slate-700 font-bold rounded-lg text-sm transition-all"
            >
              {isSimulando ? "Rodando..." : "Disparar Enxame 🚀"}
            </button>
          </form>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-5 lg:col-span-3 h-[600px]">
          <ResponsiveContainer width="100%" height="100%">
            <ScatterChart margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" />
              <XAxis
                type="number"
                dataKey="x"
                domain={[-1000, 1000]}
                stroke="#475569"
              />
              <YAxis
                type="number"
                dataKey="y"
                domain={[-1000, 1000]}
                stroke="#475569"
              />
              <ZAxis type="number" range={[100, 100]} />
              <Tooltip cursor={{ strokeDasharray: "3 3" }} />
              <Scatter
                name="Target"
                data={target}
                fill="#f43f5e"
                shape="cross"
              />
              <Scatter
                name="Partículas"
                data={dadosGrafico}
                shape={(props: any) => {
                  const { cx, cy, payload } = props;
                  const isBest =
                    payload.x === dados.globalBest[0] &&
                    payload.y === dados.globalBest[1];
                  return (
                    <circle
                      cx={cx}
                      cy={cy}
                      r={isBest ? 8 : 4}
                      fill={isBest ? "#10b981" : "#22d3ee"}
                    />
                  );
                }}
              />
            </ScatterChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}

export default App;
