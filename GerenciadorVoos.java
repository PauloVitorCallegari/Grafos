import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Camada de Negócio (Serviço).
 * Esta classe é responsável por aplicar a biblioteca genérica de Grafos ao contexto do problema (Voos).
 * Ela gerencia os dados, lê o arquivo de texto e orquestra as chamadas aos algoritmos.
 */
public class GerenciadorVoos {

    // Mantemos dois grafos em memória com a mesma topologia (mesmos nós e ligações),
    // mas com pesos diferentes para facilitar a execução dos dois algoritmos distintos.
    private Grafo<String> grafoFinanceiro; // Usado pelo Dijkstra (pesos = R$)
    private Grafo<String> grafoConexoes;   // Usado pela BFS (pesos = 1, contando apenas as "escalas")

    /**
     * Construtor: Inicializa os dois grafos vazios.
     */
    public GerenciadorVoos() {
        this.grafoFinanceiro = new Grafo<String>();
        this.grafoConexoes = new Grafo<String>();
    }

    /**
     * Lê o banco de dados (arquivo .txt) e constrói a malha aérea na memória.
     * * @param caminhoArquivo O nome do arquivo a ser lido (ex: "voos.txt")
     */
    public void carregarVoosDoArquivo(String caminhoArquivo) {
        try {
            // Prepara o leitor de arquivos do Java
            File arquivo = new File(caminhoArquivo);
            Scanner leitor = new Scanner(arquivo);

            // Lê o arquivo linha por linha até o fim
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine();

                // Ignora linhas vazias (para evitar erros de formatação no .txt)
                if (linha.trim().isEmpty()) continue;

                // Divide a linha em 3 partes usando o ponto e vírgula como separador
                String[] dados = linha.split(";");

                // Garante que a linha tem exatamente Origem, Destino e Preço
                if (dados.length == 3) {
                    // Limpa os espaços em branco e padroniza tudo para letras MAIÚSCULAS
                    String origem = dados[0].trim().toUpperCase();
                    String destino = dados[1].trim().toUpperCase();
                    Double preco = Double.parseDouble(dados[2].trim());

                    // ======================================================
                    // 1. POPULAR O GRAFO FINANCEIRO (Foco no valor em Reais)
                    // ======================================================
                    // Verifica se o aeroporto já existe; se não, cria o vértice
                    if (grafoFinanceiro.getVertice(origem) == null) grafoFinanceiro.adicionarVertice(origem);
                    if (grafoFinanceiro.getVertice(destino) == null) grafoFinanceiro.adicionarVertice(destino);
                    // Adiciona o voo passando o 'preco' real como peso da aresta
                    grafoFinanceiro.adicionarAresta(preco, origem, destino);

                    // ======================================================
                    // 2. POPULAR O GRAFO DE CONEXÕES (Foco na quantidade de voos)
                    // ======================================================
                    // Repete a checagem de vértices para o segundo grafo
                    if (grafoConexoes.getVertice(origem) == null) grafoConexoes.adicionarVertice(origem);
                    if (grafoConexoes.getVertice(destino) == null) grafoConexoes.adicionarVertice(destino);
                    // Adiciona o voo passando '1.0' como peso, pois aqui só importa o número de "saltos"
                    grafoConexoes.adicionarAresta(1.0, origem, destino);
                }
            }
            leitor.close();
            System.out.println("[OK] Malha aerea carregada com sucesso do arquivo: " + caminhoArquivo);

        } catch (FileNotFoundException e) {
            // Tratamento de erro
            System.out.println("[ERRO] Arquivo '" + caminhoArquivo + "' nao encontrado.");
        } catch (Exception e) {
            // Captura falhas de conversão (ex: se o preço no txt tiver letras no lugar de números)
            System.out.println("[ERRO] Falha ao ler os dados: " + e.getMessage());
        }
    }

    /**
     * Delega a busca da rota econômica para o Algoritmo de Dijkstra no grafo com valores financeiros.
     */
    public ArrayList<String> obterRotaMaisBarata(String origem, String destino) {
        // Padroniza as entradas para maiúsculas para evitar erros de digitação do usuário
        origem = origem.toUpperCase();
        destino = destino.toUpperCase();
        return grafoFinanceiro.dijkstra(origem, destino);
    }

    /**
     * Delega a busca da rota mais direta para a Busca em Largura (BFS) no grafo padronizado com pesos 1.
     */
    public ArrayList<String> obterRotaMenosEscalas(String origem, String destino) {
        origem = origem.toUpperCase();
        destino = destino.toUpperCase();
        return grafoConexoes.buscarMenosConexoes(origem, destino);
    }

    /**
     * Aciona o método de impressão do Grafo para visualizar a Lista de Adjacências no terminal.
     */
    public void visualizarMalha() {
        System.out.println("\n=== VISUALIZACAO DO GRAFO (MALHA AEREA) ===");
        // Imprime a estrutura do grafo financeiro, que contém as ligações e os preços reais
        grafoFinanceiro.imprimirGrafo();
        System.out.println("===========================================");
    }

    /**
     * Recebe uma rota calculada (lista de aeroportos) e pede ao Grafo para somar os valores dos voos.
     * Serve tanto para ver o preço da rota do Dijkstra quanto da rota da BFS.
     */
    public Double calcularCustoTotal(ArrayList<String> rota) {
        return grafoFinanceiro.calcularCustoRota(rota);
    }
}