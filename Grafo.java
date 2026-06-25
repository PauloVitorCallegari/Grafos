import java.util.ArrayList;
import java.util.Collections;

/**
 * Classe principal do Grafo.
 * O <T> significa que ela é Genérica (pode guardar Strings, Inteiros, etc.).
 * No nosso caso, o T será substituído por String (nome das cidades).
 */
public class Grafo<T> {

    // ==========================================================
    // ESTRUTURAS BÁSICAS DO GRAFO
    // ==========================================================

    /**
     * Classe interna que representa um Nó (ex: Aeroporto) no grafo.
     */
    public static class Vertice<T> {
        private T dado; // O valor armazenado (ex: "Vitória")
        // Listas de adjacências: separamos quem chega e quem sai deste vértice
        private ArrayList<Aresta<T>> arestasEntrada;
        private ArrayList<Aresta<T>> arestasSaida;

        public Vertice(T valor) {
            this.dado = valor;
            this.arestasEntrada = new ArrayList<Aresta<T>>();
            this.arestasSaida = new ArrayList<Aresta<T>>();
        }

        public T getDado() { return dado; }
        public ArrayList<Aresta<T>> getArestasEntrada() { return arestasEntrada; }
        public ArrayList<Aresta<T>> getArestasSaida() { return arestasSaida; }

        public void adicionarArestaEntrada(Aresta<T> aresta) {
            this.arestasEntrada.add(aresta);
        }

        public void adicionarArestaSaida(Aresta<T> aresta) {
            this.arestasSaida.add(aresta);
        }
    }

    /**
     * Classe interna que representa uma Conexão (ex: Voo) entre dois vértices.
     */
    public static class Aresta<T> {
        private Double peso; // Custo do voo (ou 1.0 para contar escalas)
        private Vertice<T> inicio; // De onde sai
        private Vertice<T> fim;    // Para onde vai

        public Aresta(Double peso, Vertice<T> inicio, Vertice<T> fim) {
            this.peso = peso;
            this.inicio = inicio;
            this.fim = fim;
        }

        public Double getPeso() { return peso; }
        public Vertice<T> getInicio() { return inicio; }
        public Vertice<T> getFim() { return fim; }
    }

    // Listas globais que guardam todos os vértices e arestas do nosso grafo
    private ArrayList<Vertice<T>> vertices;
    private ArrayList<Aresta<T>> arestas;

    public Grafo() {
        this.vertices = new ArrayList<Vertice<T>>();
        this.arestas = new ArrayList<Aresta<T>>();
    }

    /**
     * Cria um novo nó e o adiciona à lista geral do grafo.
     */
    public void adicionarVertice(T dado) {
        Vertice<T> novoVertice = new Vertice<T>(dado);
        this.vertices.add(novoVertice);
    }

    /**
     * Cria uma ligação direcionada entre dois vértices existentes.
     */
    public void adicionarAresta(Double peso, T dadoInicio, T dadoFim) {
        Vertice<T> inicio = this.getVertice(dadoInicio);
        Vertice<T> fim = this.getVertice(dadoFim);

        Aresta<T> aresta = new Aresta<T>(peso, inicio, fim);

        // Como é um grafo direcionado (dígrafo), registramos a saída em um e a entrada no outro
        inicio.adicionarArestaSaida(aresta);
        fim.adicionarArestaEntrada(aresta);
        this.arestas.add(aresta);
    }

    /**
     * Busca um vértice na lista pelo seu valor (ex: procura o vértice "Miami").
     */
    public Vertice<T> getVertice(T dado) {
        Vertice<T> vertice = null;
        for (int i = 0; i < this.vertices.size(); i++) {
            if (this.vertices.get(i).getDado().equals(dado)) {
                vertice = this.vertices.get(i);
                break;
            }
        }
        return vertice;
    }

    /**
     * Algoritmo padrão de Busca em Largura (apenas para travessia e impressão).
     * Explora o grafo em "camadas" a partir do primeiro vértice.
     */
    public void buscaEmLargura() {
        ArrayList<Vertice<T>> marcados = new ArrayList<Vertice<T>>();
        ArrayList<Vertice<T>> fila = new ArrayList<Vertice<T>>();

        if (this.vertices.size() == 0) return;

        Vertice<T> atual = this.vertices.get(0);
        marcados.add(atual);
        System.out.println(atual.getDado());
        fila.add(atual);

        while (fila.size() > 0) {
            Vertice<T> visitado = fila.get(0); // Pega o primeiro da fila
            for (int i = 0; i < visitado.getArestasSaida().size(); i++) {
                Vertice<T> proximo = visitado.getArestasSaida().get(i).getFim();

                // Se o vizinho ainda não foi visitado, entra na fila
                if (!marcados.contains(proximo)) {
                    marcados.add(proximo);
                    System.out.println(proximo.getDado());
                    fila.add(proximo);
                }
            }
            fila.remove(0); // Remove o vértice atual pois já processamos seus vizinhos
        }
    }

    // ==========================================================
    // IMPRIMIR LISTA DE ADJACÊNCIAS
    // ==========================================================

    /**
     * Imprime a malha aérea no terminal provando a estrutura de Lista de Adjacências.
     */
    public void imprimirGrafo() {
        for (Vertice<T> v : this.vertices) {
            System.out.print("Aeroporto " + v.getDado() + " tem voos para: ");
            if (v.getArestasSaida().isEmpty()) {
                System.out.print("Nenhum destino direto.");
            } else {
                for (Aresta<T> a : v.getArestasSaida()) {
                    System.out.print("[" + a.getFim().getDado() + " (Custo: " + a.getPeso() + ")]  ");
                }
            }
            System.out.println();
        }
    }

    // ==========================================================
    // ALGORITMOS DO TRABALHO PRÁTICO (VOOS)
    // ==========================================================

    /**
     * Utiliza a lógica da BFS (Busca em Largura) para encontrar o caminho com menos saltos.
     * Como a BFS explora em círculos concêntricos, o primeiro caminho que chegar ao destino é garantidamente o mais curto em número de arestas.
     */
    public ArrayList<T> buscarMenosConexoes(T origem, T destino) {
        Vertice<T> vOrigem = getVertice(origem);
        Vertice<T> vDestino = getVertice(destino);

        if (vOrigem == null || vDestino == null) return null;

        ArrayList<Vertice<T>> fila = new ArrayList<>();
        ArrayList<Vertice<T>> visitados = new ArrayList<>();

        // Vetores paralelos usados para rastrear "quem descobriu quem", essencial para desenhar a rota final
        ArrayList<Vertice<T>> caminhoAtual = new ArrayList<>();
        ArrayList<Vertice<T>> caminhoAnterior = new ArrayList<>();

        fila.add(vOrigem);
        visitados.add(vOrigem);
        caminhoAtual.add(vOrigem);
        caminhoAnterior.add(null); // A origem não tem predecessor

        while (!fila.isEmpty()) {
            Vertice<T> visitado = fila.remove(0);

            // Se achou o destino, para a busca para economizar processamento
            if (visitado.getDado().equals(destino)) break;

            for (Aresta<T> aresta : visitado.getArestasSaida()) {
                Vertice<T> proximo = aresta.getFim();
                if (!visitados.contains(proximo)) {
                    visitados.add(proximo);
                    fila.add(proximo);

                    // Registra que chegamos no 'proximo' através do 'visitado'
                    caminhoAtual.add(proximo);
                    caminhoAnterior.add(visitado);
                }
            }
        }

        return reconstruirCaminho(caminhoAtual, caminhoAnterior, vDestino);
    }

    /**
     * Algoritmo de Dijkstra para encontrar o caminho de MENOR CUSTO financeiro.
     * Ignora a quantidade de escalas e foca apenas em minimizar a soma dos pesos das arestas.
     */
    public ArrayList<T> dijkstra(T origem, T destino) {
        Vertice<T> vOrigem = getVertice(origem);
        Vertice<T> vDestino = getVertice(destino);

        if (vOrigem == null || vDestino == null) return null;

        // Lista de vértices que ainda precisam ser avaliados
        ArrayList<Vertice<T>> naoVisitados = new ArrayList<>(this.vertices);

        // Controla o custo mínimo conhecido até cada vértice
        ArrayList<Double> distancias = new ArrayList<>();

        // Controla qual vértice ofereceu o melhor caminho até o momento (para desenhar a rota depois)
        ArrayList<Vertice<T>> predecessores = new ArrayList<>();

        // Passo 1: Inicialização (Distâncias = Infinito, Predecessores = nulo)
        for (int i = 0; i < vertices.size(); i++) {
            distancias.add(Double.MAX_VALUE);
            predecessores.add(null);
        }

        // A distância da origem para ela mesma é zero
        int indiceOrigem = vertices.indexOf(vOrigem);
        distancias.set(indiceOrigem, 0.0);

        // Passo 2: Relaxamento das Arestas
        while (!naoVisitados.isEmpty()) {
            Vertice<T> atual = null;
            Double menorDistancia = Double.MAX_VALUE;

            // Extrai o vértice não visitado com a menor distância acumulada
            for (Vertice<T> v : naoVisitados) {
                int indice = vertices.indexOf(v);
                if (distancias.get(indice) < menorDistancia) {
                    menorDistancia = distancias.get(indice);
                    atual = v;
                }
            }

            // Se não achou ninguém alcançável ou chegou no destino, encerra
            if (atual == null || atual.getDado().equals(destino)) {
                break;
            }

            naoVisitados.remove(atual);
            int indiceAtual = vertices.indexOf(atual);

            // Avalia os vizinhos do vértice atual
            for (Aresta<T> aresta : atual.getArestasSaida()) {
                Vertice<T> vizinho = aresta.getFim();

                if (naoVisitados.contains(vizinho)) {
                    int indiceVizinho = vertices.indexOf(vizinho);

                    // Calcula a distância se for por este novo caminho
                    Double novaDistancia = distancias.get(indiceAtual) + aresta.getPeso();

                    // Se o novo caminho for mais barato que o conhecido, atualiza (Relaxamento)
                    if (novaDistancia < distancias.get(indiceVizinho)) {
                        distancias.set(indiceVizinho, novaDistancia);
                        predecessores.set(indiceVizinho, atual);
                    }
                }
            }
        }

        return reconstruirCaminhoDijkstra(predecessores, vDestino);
    }

    // ==========================================================
    // MÉTODOS AUXILIARES
    // ==========================================================

    /**
     * Lê os vetores paralelos da BFS de trás para frente (do Destino até a Origem) para montar a lista final.
     */
    private ArrayList<T> reconstruirCaminho(ArrayList<Vertice<T>> atuais, ArrayList<Vertice<T>> anteriores, Vertice<T> destino) {
        ArrayList<T> rota = new ArrayList<>();
        Vertice<T> passo = destino;

        int index = atuais.indexOf(passo);
        if (index == -1) return null; // Destino inalcançável

        while (passo != null) {
            rota.add(passo.getDado());
            index = atuais.indexOf(passo);
            passo = anteriores.get(index);
        }
        Collections.reverse(rota); // Inverte para ficar Origem -> Destino
        return rota;
    }

    /**
     * Lê a lista de predecessores gerada pelo Dijkstra de trás para frente para montar a rota final.
     */
    private ArrayList<T> reconstruirCaminhoDijkstra(ArrayList<Vertice<T>> predecessores, Vertice<T> destino) {
        ArrayList<T> rota = new ArrayList<>();
        Vertice<T> passo = destino;

        int index = vertices.indexOf(passo);

        // Se o destino não tem predecessor e não é a origem, não há rota possível
        if (predecessores.get(index) == null && !passo.equals(vertices.get(0))) return null;

        while (passo != null) {
            rota.add(passo.getDado());
            index = vertices.indexOf(passo);
            passo = predecessores.get(index);
        }
        Collections.reverse(rota);
        return rota;
    }

    /**
     * Dada uma rota já calculada (ex: [Vitoria, Rio, SP]), percorre o grafo somando os preços dos voos.
     */
    public Double calcularCustoRota(ArrayList<T> rota) {
        if (rota == null || rota.size() < 2) return 0.0;
        Double custoTotal = 0.0;

        for (int i = 0; i < rota.size() - 1; i++) {
            Vertice<T> vAtual = getVertice(rota.get(i));
            T proximoDado = rota.get(i + 1);

            if (vAtual != null) {
                // Procura a aresta exata que liga o vértice atual ao próximo da rota
                for (Aresta<T> aresta : vAtual.getArestasSaida()) {
                    if (aresta.getFim().getDado().equals(proximoDado)) {
                        custoTotal += aresta.getPeso();
                        break;
                    }
                }
            }
        }
        return custoTotal;
    }
}