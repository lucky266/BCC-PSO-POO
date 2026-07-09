public class ProblemaAlvo implements IFuncaoObjetivo {
    private double[] target;

    public ProblemaAlvo(double[] target) {
        this.target = target;
    }

    @Override
    public double avaliar(double[] posicao) {
        double d = 0;
        for (int i = 0; i < posicao.length; i++) {
            d += Math.pow(posicao[i] - target[i], 2);
        }
        return Math.sqrt(d);
    }
}