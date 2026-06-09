import java.io.*;
import java.util.Scanner;

class NoCreche {
    String nome;
    NoCreche prox;

    NoCreche(String nome) {
        this.nome = nome;
    }
}

class ListaCreches {
    private NoCreche inicio;
    private int tamanho = 0;

    public void adicionar(String nome) {
        if (buscar(nome) != -1) return;

        NoCreche novo = new NoCreche(nome);

        if (inicio == null) {
            inicio = novo;
        } else {
            NoCreche atual = inicio;
            while (atual.prox != null) atual = atual.prox;
            atual.prox = novo;
        }
        tamanho++;
    }

    public int buscar(String nome) {
        NoCreche atual = inicio;
        int indice = 0;

        while (atual != null) {
            if (atual.nome.equals(nome)) return indice;
            atual = atual.prox;
            indice++;
        }
        return -1;
    }

    public String getNome(int indice) {
        NoCreche atual = inicio;
        int i = 0;

        while (atual != null) {
            if (i == indice) return atual.nome;
            atual = atual.prox;
            i++;
        }
        return null;
    }

    public int tamanho() {
        return tamanho;
    }
}

class Grafo {
    private static final int MAX = 30;

    private int[][] adj = new int[MAX][MAX];
    private double[][] dist = new double[MAX][MAX];

    private ListaCreches creches = new ListaCreches();

    public void adicionarConexao(String origem, String destino, double distancia) {
        creches.adicionar(origem);
        creches.adicionar(destino);

        int i = creches.buscar(origem);
        int j = creches.buscar(destino);

        adj[i][j] = 1;
        adj[j][i] = 1;

        dist[i][j] = distancia;
        dist[j][i] = distancia;
    }

    public void lerArquivo(String caminho) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(caminho));
        String linha;

        while ((linha = br.readLine()) != null) {
            String[] partes = linha.split(";");

            adicionarConexao(
                    partes[0],
                    partes[1],
                    Double.parseDouble(partes[2].replace(',', '.'))
            );
        }
        br.close();
    }

    public int quantidadeCreches() {
        return creches.tamanho();
    }

    public String nomeCreche(int indice) {
        return creches.getNome(indice);
    }

    public void listarCreches() {
        System.out.println("\n=== CRECHES ===");
        for (int i = 0; i < creches.tamanho(); i++) {
            System.out.println((i + 1) + " - " + creches.getNome(i));
        }
        System.out.println("0 - Cancelar");
    }

    public boolean existeConexao(String origem, String destino) {
        int i = creches.buscar(origem);
        int j = creches.buscar(destino);
        return i != -1 && j != -1 && adj[i][j] == 1;
    }

    public void numeroConexoes() {
        int n = creches.tamanho();

        for (int i = 0; i < n; i++) {
            int cont = 0;
            for (int j = 0; j < n; j++) {
                if (adj[i][j] == 1) cont++;
            }
            System.out.println(creches.getNome(i) + ": " + cont + " conexões");
        }
    }

    public void listarConexoesOrdenadas(String creche) {
        int i = creches.buscar(creche);

        if (i == -1) return;

        int n = creches.tamanho();
        String[] nomes = new String[n];
        double[] dists = new double[n];
        int qtd = 0;

        for (int j = 0; j < n; j++) {
            if (adj[i][j] == 1) {
                nomes[qtd] = creches.getNome(j);
                dists[qtd] = dist[i][j];
                qtd++;
            }
        }

        for (int a = 0; a < qtd - 1; a++) {
            for (int b = 0; b < qtd - a - 1; b++) {
                if (dists[b] > dists[b + 1]) {
                    double td = dists[b];
                    dists[b] = dists[b + 1];
                    dists[b + 1] = td;

                    String tn = nomes[b];
                    nomes[b] = nomes[b + 1];
                    nomes[b + 1] = tn;
                }
            }
        }

        System.out.println("\nConexões de " + creche + ":");
        for (int k = 0; k < qtd; k++) {
            System.out.println(nomes[k] + " -> " + dists[k] + " km");
        }
    }

    public void distanciaEntre(String origem, String destino) {
        int i = creches.buscar(origem);
        int j = creches.buscar(destino);

        if (i == -1 || j == -1) {
            System.out.println("Creche não encontrada.");
            return;
        }

        if (adj[i][j] == 1)
            System.out.println("Distância: " + dist[i][j] + " km");
        else
            System.out.println("Não existe conexão direta.");
    }
}

public class Main {

    private static double lerDouble(String texto) {
        return Double.parseDouble(texto.replace(',', '.'));
    }

    private static int selecionarCreche(Scanner sc, Grafo grafo, String msg) {
        while (true) {
            System.out.println("\n" + msg);
            grafo.listarCreches();
            System.out.print("Escolha: ");

            try {
                int op = Integer.parseInt(sc.nextLine());

                if (op == 0) return -1;

                if (op >= 1 && op <= grafo.quantidadeCreches())
                    return op - 1;

            } catch (Exception ignored) {}

            System.out.println("Opção inválida.");
        }
    }

    public static void main(String[] args) {

        Grafo grafo = new Grafo();

        try {
            grafo.lerArquivo("grafo.txt");
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU ===");
            System.out.println("1 - Número de conexões");
            System.out.println("2 - Listar conexões");
            System.out.println("3 - Distância entre creches");
            System.out.println("4 - Adicionar conexão");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = Integer.parseInt(sc.nextLine());

            switch (opcao) {

                case 1:
                    grafo.numeroConexoes();
                    break;

                case 2: {
                    int idx = selecionarCreche(sc, grafo, "Selecione uma creche");
                    if (idx != -1)
                        grafo.listarConexoesOrdenadas(grafo.nomeCreche(idx));
                    break;
                }

                case 3: {
                    int o = selecionarCreche(sc, grafo, "Origem");
                    if (o == -1) break;

                    int d = selecionarCreche(sc, grafo, "Destino");
                    if (d == -1) break;

                    grafo.distanciaEntre(
                            grafo.nomeCreche(o),
                            grafo.nomeCreche(d)
                    );
                    break;
                }

                case 4: {
                    int o = selecionarCreche(sc, grafo, "Selecione a origem");
                    if (o == -1) break;

                    int d = selecionarCreche(sc, grafo, "Selecione o destino");
                    if (d == -1) break;

                    if (o == d) {
                        System.out.println("Não é permitido ligar uma creche a ela mesma.");
                        break;
                    }

                    String origem = grafo.nomeCreche(o);
                    String destino = grafo.nomeCreche(d);

                    if (grafo.existeConexao(origem, destino)) {
                        System.out.println("Essa conexão já existe.");
                        break;
                    }

                    System.out.print("Distância (km): ");
                    double km = lerDouble(sc.nextLine());

                    grafo.adicionarConexao(origem, destino, km);

                    System.out.println("Conexão adicionada com sucesso.");
                    break;
                }

                case 0:
                    System.out.println("Encerrando...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        sc.close();
    }
}
