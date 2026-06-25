import java.util.ArrayList;
import java.util.Scanner;

/**
 * Camada de Interface / Visão.
 * O papel desta classe é puramente interagir com o usuário (I/O - Entrada e Saída).
 * Ela não faz nenhum cálculo de grafos; apenas recolhe os dados do teclado,
 * envia para o Gerenciador (Camada de Negócio) e exibe os resultados na tela.
 */
public class Main {

    public static void main(String[] args) {
        // Inicializa o leitor de teclado
        Scanner scanner = new Scanner(System.in);

        // Instancia a Camada de Negócio, que por sua vez vai instanciar a Camada de Estrutura (Grafos)
        GerenciadorVoos gerenciador = new GerenciadorVoos();

        System.out.println("==================================================");
        System.out.println("      SISTEMA DE PLANEAMENTO DE VOOS (GRAFOS)     ");
        System.out.println("==================================================");

        // 1. Fase de Inicialização: Lê o banco de dados antes de exibir o menu
        gerenciador.carregarVoosDoArquivo("voos.txt");

        int opcao = -1;

        // 2. Loop principal: O programa continua rodando até o usuário digitar 0
        while (opcao != 0) {
            System.out.println("\n--------------------------------------------------");
            System.out.println(" MENU PRINCIPAL");
            System.out.println("--------------------------------------------------");
            System.out.println("1 - Buscar a viagem MAIS BARATA (Algoritmo de Dijkstra)");
            System.out.println("2 - Buscar a viagem com MENOS ESCALAS (Busca em Largura - BFS)");
            System.out.println("3 - Visualizar toda a Malha Aerea (Imprimir Grafo)");
            System.out.println("0 - Encerrar sistema");
            System.out.print("Escolha uma opcao: ");

            // Tratamento de erros:
            // Evita que o programa feche abruptamente se o usuário digitar uma letra em vez de número
            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[!] Opcao invalida. Por favor, digite um numero.");
                continue; // Pula para a próxima iteração do while, mostrando o menu novamente
            }

            // Lógica para as opções de busca de rotas (1 e 2)
            if (opcao == 1 || opcao == 2) {
                System.out.print("\nDigite a sigla do aeroporto de ORIGEM: ");
                // .trim() remove espaços acidentais e .toUpperCase() padroniza a busca
                String origem = scanner.nextLine().trim().toUpperCase();

                System.out.print("Digite a sigla do aeroporto de DESTINO: ");
                String destino = scanner.nextLine().trim().toUpperCase();

                ArrayList<String> rota = null;

                // Delega o processamento para o Gerenciador de acordo com a escolha
                if (opcao == 1) {
                    System.out.println("\nProcessando a rota de menor custo financeiro...");
                    rota = gerenciador.obterRotaMaisBarata(origem, destino);
                } else {
                    System.out.println("\nProcessando a rota mais direta (menos saltos)...");
                    rota = gerenciador.obterRotaMenosEscalas(origem, destino);
                }

                // 3. Fase de Exibição dos Resultados
                if (rota != null && !rota.isEmpty()) {
                    System.out.println("\n>>> SUCESSO! ROTA ENCONTRADA <<<");


                    System.out.println("Trajeto: " + String.join(" -> ", rota));

                    // Calcula a quantidade de voos e o preço total, independentemente do algoritmo usado
                    // Quantidade de voos é sempre o número de cidades na rota menos 1
                    int quantidadeVoos = rota.size() - 1;
                    Double custo = gerenciador.calcularCustoTotal(rota);

                    System.out.println("Quantidade total de voos: " + quantidadeVoos);


                    System.out.printf("Custo Total da Passagem: R$ %.2f\n", custo);

                } else {

                    System.out.println("\n[!] Nao foi possivel encontrar uma rota disponivel entre " + origem + " e " + destino + ".");
                }

            }
            // Lógica para visualizar a estrutura do grafo (Opção 3)
            else if (opcao == 3) {
                gerenciador.visualizarMalha();
            }
            // Trata números que não estão no menu (ex: digitou 9)
            else if (opcao != 0) {
                System.out.println("[!] Opcao nao reconhecida. Tente novamente.");
            }
        }

        // Encerramento limpo do sistema
        System.out.println("\nEncerrando o sistema. Obrigado por utilizar!");
        scanner.close(); // Libera o recurso de leitura de teclado da memória
    }
}