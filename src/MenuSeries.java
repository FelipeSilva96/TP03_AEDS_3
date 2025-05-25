
import java.util.ArrayList;
import java.util.Scanner;

public class MenuSeries {

    ArquivoSerie arqSeries;
    ArquivoEpisodio arqEpisodios;
    ArquivoAtor arqAtor;

    private static Scanner scan = new Scanner(System.in);

    public MenuSeries() throws Exception {
        arqSeries = new ArquivoSerie();
        arqEpisodios = new ArquivoEpisodio();
        arqAtor = new ArquivoAtor();
    }

    public void menu() throws Exception {

        int opcao;
        do {

            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Series");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Temporada");
            System.out.println("6 - Todos os Episodios");
            System.out.println("7 - Vincular Atores");
            System.out.println("8 - desvincular Atores");
            System.out.println("9 - Mostrar atores de uma serie");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarSerie();
                    break;
                case 2:
                    incluirSerie();
                    break;
                case 3:
                    alterarSerie();
                    break;
                case 4:
                    excluirSerie();
                    break;
                case 5:
                    buscarTemporada();
                    break;
                case 6:
                    buscarEpisodios();
                    break;
                case 7:
                    vincularAtores();
                    break;
                case 8:
                    desvincularAtores();
                    break;
                case 9:
                    vinculoSerie();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

    public void buscarTemporada() throws Exception {
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine();
        System.out.println("\nTemporada: ");
        int temporada = scan.nextInt();
        scan.nextLine();
        Episodio ep;

        if (nome != null) {
            try {
                ArrayList<Episodio> series = arqEpisodios.readSerie(nome);  // Chama o método de leitura da classe Arquivo
                if (series != null) {
                    for (int i = 0; i < series.size(); i++) {
                        ep = series.get(i);
                        if (ep != null && ep.temporada == temporada) {
                            ep.mostraEpisodio();
                        }

                    }
                } else {
                    System.out.println("Nenhum episodio encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void buscarEpisodios() throws Exception {
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário
        Episodio ep;

        if (nome != null) {
            try {
                ArrayList<Episodio> series = arqEpisodios.readSerie(nome);  // Chama o método de leitura da classe Arquivo
                if (series != null) {
                    for (int i = 0; i < series.size(); i++) {
                        ep = series.get(i);
                        if (ep != null) {
                            ep.mostraEpisodio();
                        }
                    }
                } else {
                    System.out.println("Nenhum episodio encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar VELHO a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    /*  // ANTIGO
    public void buscarSerie() {
        System.out.print("\nnome da Serie: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário
        // Limpar o buffer após o nextInt()

        if (nome != null) {
            try {
                Serie serie = arqSeries.read(nome);  // Chama o método de leitura da classe Arquivo
                if (serie != null) {
                    mostraSerie(serie);  // Exibe os detalhes do serie encontrado
                } else {
                    System.out.println("Serie não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar VELHO a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }
     */
    public void buscarSerie() {
        System.out.print("\nnome da Serie: ");
        String nome = scan.nextLine();
        try {
            List<Serie> resultados = arqSeries.searchTfIdf(nome);
            if (resultados.isEmpty()) {
                System.out.println("Serie não encontrada.");
            } else {
                for (Serie s : resultados) {
                    mostraSerie(s);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar séries!");
            e.printStackTrace();
        }
    }

    public void incluirSerie() {

        String nome = "";
        short anoLancamento = 0;
        String sinopse = "";
        String streaming = "";

        boolean dadosCorretos = false;

        System.out.println("\nInclusão de Serie");

        do {
            System.out.print("\nNome (min. de 2 letras ou vazio para cancelar): ");
            nome = scan.nextLine();
            if (nome.length() == 0) {
                return;
            }
            if (nome.length() < 2) {
                System.err.println("O nome da serie deve ter no mínimo 2 caracteres.");
            }
        } while (nome.length() < 2);

        do {

            dadosCorretos = false;
            System.out.print("Ano de lançamento: ");
            if (scan.hasNextInt()) {
                anoLancamento = scan.nextShort();
                dadosCorretos = true;
            } else {
                System.err.println("Ano de Lançamento inválido! Por favor, insira um número válido.");
            }

            scan.nextLine(); // Limpar o buffer 
        } while (!dadosCorretos);

        do {
            System.out.print("\nSinopse (min. de 10 letras ou vazio para cancelar): ");
            sinopse = scan.nextLine();
            if (sinopse.length() == 0) {
                return;
            }
            if (sinopse.length() < 10) {
                System.err.println("A sinopse da serie deve ter no mínimo 10 caracteres.");
            }
        } while (sinopse.length() < 10);

        do {
            System.out.print("\nNome do streaming (min. de 4 letras ou vazio para cancelar): ");
            streaming = scan.nextLine();
            if (streaming.length() == 0) {
                return;
            }
            if (streaming.length() < 4) {
                System.err.println("O nome do streaming deve ter no mínimo 4 caracteres.");
            }
        } while (streaming.length() < 4);

        System.out.print("\nConfirma a inclusão da serie? (S/N) ");

        char resp = scan.nextLine().charAt(0);
        if (resp == 'S' || resp == 's') {
            try {
                Serie s = new Serie(nome, anoLancamento, sinopse, streaming);
                arqSeries.create(s);

                System.out.println("Serie incluída com sucesso.");

            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o serie!");
            }
        }
    }

    public void alterarSerie() throws Exception {

        System.out.print("\nDigite o Nome do serie a ser alterada: ");
        String nome = scan.nextLine();
        Serie serie;

        if (nome != null) {
            try {
                // Tenta ler o serie com o ID fornecido
                serie = arqSeries.read(nome);
                if (serie != null) {
                    System.out.println("Serie encontrada:");
                    mostraSerie(serie);  // Exibe os dados do serie para confirmação

                    // Alteração de Nome
                    System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                    String novoNome = scan.nextLine();
                    if (!novoNome.isEmpty()) {
                        serie.nome = novoNome;  // Atualiza o nome se fornecido
                    }

                    // Alteração de ano de Lançamento
                    System.out.print("Novo Ano de Lançamento: ");

                    if (scan.hasNextInt()) {
                        short novoAno = scan.nextShort();
                        serie.anoLancamento = novoAno;
                    } else {
                        System.out.println("Insira um Ano de Lançamento válido");
                    }

                    // Alteração de sinopse
                    System.out.print("\nNova sinopse (deixe um espaco em branco para manter a anterior): ");
                    if (scan.hasNext()) {
                        String novaSinopse = scan.nextLine();
                        novaSinopse = scan.nextLine();
                        if (!novaSinopse.equals("") || !novaSinopse.equals(" ")) {
                            serie.sinopse = novaSinopse;
                        } else {
                            System.out.print("\nSinopse nao pode ser espaco em branco");
                        }
                    }

                    System.out.print("\nNome do streaming (min. de 4 letras ou vazio para cancelar): ");
                    String novoStreaming = scan.nextLine();
                    if (!novoStreaming.isEmpty()) {
                        serie.streaming = novoStreaming;
                    }

                    // Confirmação da alteração
                    System.out.print("\nConfirma as alterações? (S/N) ");
                    char resp = scan.next().charAt(0);
                    if (resp == 'S' || resp == 's') {
                        // Salva as alterações no arquivo
                        boolean alterado = arqSeries.update(serie, nome);

                        if (alterado) {

                            System.out.println("Serie alterada com sucesso.");

                        } else {

                            System.out.println("Erro ao alterar a Serie.");
                        }
                    } else {
                        System.out.println("Alterações canceladas.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível alterar a Serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void excluirSerie() {
        System.out.print("\nDigite o nome da serie a ser excluída: ");
        String nome = scan.nextLine();
        //  scan.nextInt();
        if (nome != null) {
            try {
                // Tenta ler o serie com o ID fornecido
                Serie serie = arqSeries.read(nome);
                if (serie != null) {
                    System.out.println("Serie encontrada:");
                    mostraSerie(serie);  // Exibe os dados do serie para confirmação

                    if (hasEpisodios(serie.nome)) {
                        System.out.print("\nSerie possui episodios associados.\nPara excluir serie, exclua todos os episodios associados a ela");
                    } else {
                        System.out.print("\nConfirma a exclusão do serie? (S/N) ");
                        char resp = scan.next().charAt(0);  // Lê a resposta do usuário

                        if (resp == 'S' || resp == 's') {
                            ArrayList<Ator> list_ator = arqAtor.readAtores(serie.nome);

                            //excluir todos os vinculos da serie
                            for (Ator i : list_ator) {
                                arqAtor.unlinkAtorSerie(i.getID(), serie.getID());
                            }

                            boolean excluido = arqSeries.delete(nome);  // Chama o método de exclusão no arquivo
                            if (excluido) {
                                System.out.println("Serie excluída com sucesso.");
                            } else {
                                System.out.println("Erro ao excluir a serie.");
                            }
                        } else {
                            System.out.println("Exclusão cancelada.");
                        }
                    }

                } else {
                    System.out.println("Serie não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível excluir a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void vinculoSerie() {
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário
        // Limpar o buffer após o nextInt()

        if (nome != null) {
            try {

                ArrayList<Ator> lista_de_atores = arqAtor.readAtores(nome);
                if (lista_de_atores == null) {
                    System.out.println("Serie não encontrada.");
                } else if (lista_de_atores.isEmpty()) {
                    System.out.println("Nenhum ator encontrado.");
                } else {
                    for (Ator i : lista_de_atores) {
                        MenuAtor.mostrarAtor(i);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome null.");
        }
    }

    public void vincularAtores() {
        System.out.print("\nNome da Serie: ");
        String nome_ator = "";
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário

        if (nome != null) {
            try {

                Serie serie = arqSeries.read(nome);

                if (serie != null && serie.getID() > -1) {
                    while (!nome_ator.equals("0")) {
                        System.out.print("\nPara terminar as insercoes, digite 0 e pressione enter\n ");

                        System.out.print("\nNome do ator: ");
                        nome_ator = scan.nextLine().trim();
                        if (!nome_ator.equals("0")) {
                            Ator ator = arqAtor.readNome(nome_ator);
                            if (ator != null && ator.getID() > -1) {
                                if (arqAtor.linkAtorSerie(ator.getID(), serie.getID())) {
                                    System.out.println("Vinculo criado com sucesso");
                                } else {
                                    System.out.println("Criacao de vinculo falhou");
                                }
                            } else {
                                System.out.println("Ator nao encontrado.");
                            }
                        } else {
                            System.out.println("insercao encerrada");
                        }
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da serie null.");
        }
    }

    public void desvincularAtores() {
        System.out.print("\nNome da Serie: ");
        String nome_ator = "";
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário

        if (nome != null) {
            try {

                Serie serie = arqSeries.read(nome);

                if (serie != null && serie.getID() > -1) {
                    while (!nome_ator.equals("0")) {
                        System.out.print("\nPara terminar, digite 0 e pressione enter\n ");

                        System.out.print("\nNome do ator: ");
                        nome_ator = scan.nextLine().trim();
                        if (!nome_ator.equals("0")) {
                            Ator ator = arqAtor.readNome(nome_ator);
                            if (ator != null && ator.getID() > -1) {
                                if (arqAtor.unlinkAtorSerie(ator.getID(), serie.getID())) {
                                    System.out.println("Vinculo excluido com sucesso");
                                } else {
                                    System.out.println("Exclusao de vinculo falhou");
                                }
                            } else {
                                System.out.println("Ator nao encontrado.");
                            }
                        } else {
                            System.out.println("Exclusao de vinculos encerrada");
                        }
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da serie null.");
        }
    }

    public boolean hasEpisodios(String nome) throws Exception {
        boolean res = true;

        ArrayList<Episodio> lista = arqEpisodios.readSerie(nome);

        if (lista == null) {
            res = false;
        }

        return res;
    }

    public static void mostraSerie(Serie serie) {

        if (serie != null) {
            System.out.println("\nDetalhes do serie:");
            System.out.println("----------------------");
            System.out.print("\nNome.............: " + serie.nome);
            System.out.print("\nAno de lançamento: " + serie.anoLancamento);
            System.out.print("\nSinopse..........: " + serie.sinopse);
            System.out.print("\nStreaming........: " + serie.streaming);
            System.out.println();

            System.out.println("----------------------");
        }
    }
}
