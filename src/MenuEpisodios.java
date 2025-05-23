
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuEpisodios {

    ArquivoEpisodio arqEpisodios;
    ArquivoSerie arqSerie;

    private static Scanner scan = new Scanner(System.in);

    public MenuEpisodios() throws Exception {
        arqEpisodios = new ArquivoEpisodio();
        arqSerie = new ArquivoSerie();
    }

    public void menu() throws Exception {

        int opcao;
        do {

            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Episodios");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarEpisodio();
                    break;
                case 2:
                    incluirEpisodio();
                    break;
                case 3:
                    alterarEpisodio();
                    break;
                case 4:
                    excluirEpisodio();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

    public void buscarEpisodio() {
        System.out.print("\nNome do episodio: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário

        if (nome != null) {
            try {
                Episodio episodio = arqEpisodios.readNome(nome);  // Chama o método de leitura da classe Arquivo
                if (episodio != null) {
                    mostraEpisodio(episodio);  // Exibe os detalhes do episodio encontrado
                } else {
                    System.out.println("Episodio não encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar o episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void incluirEpisodio() throws Exception {
        String nomeSerie;
        int idSerie;
        String nome = "";
        int temporada = 0;
        LocalDate dataLancamento = null;
        int duracao = 0;
        boolean dadosCorretos = false;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("\nInclusão de episodio");

        System.out.print("\nNome da serie (Id menor que 1 ou vazio para cancelar): ");
        nomeSerie = scan.nextLine();
        idSerie = arqSerie.read(nomeSerie).getID();

        if (nomeSerie == null) {
            System.out.println("\nnome vazio\n");
            return;
        }

        do {
            System.out.print("\nNome (min. de 2 letras ou vazio para cancelar): ");
            nome = scan.nextLine();
            if (nome.length() == 0) {
                return;
            }
            if (nome.length() < 2) {
                System.err.println("O nome do episodio deve ter no mínimo 2 caracteres.");
            }
        } while (nome.length() < 2);

        do {

            dadosCorretos = false;
            System.out.print("Temporada: ");
            if (scan.hasNextInt()) {
                temporada = scan.nextInt();
                dadosCorretos = true;
            } else {
                System.err.println("Temporada inválida! Por favor, insira um número válido.");
            }

            scan.nextLine(); // Limpar o buffer 
        } while (!dadosCorretos);

        do {
            System.out.print("Data de lançamento (DD/MM/AAAA): ");
            String dataStr = scan.nextLine();
            dadosCorretos = false;
            try {
                dataLancamento = LocalDate.parse(dataStr, format);
                dadosCorretos = true;
            } catch (Exception e) {
                System.err.println("Data inválida! Use o formato DD/MM/AAAA.");
            }
        } while (!dadosCorretos);

        do {
            dadosCorretos = false;
            System.out.print("Duração: ");

            if (scan.hasNextInt()) {
                duracao = scan.nextInt();
                scan.nextLine();
                if (duracao > 0) {
                    dadosCorretos = true;
                }
            } else {
                System.out.println("Duração invalida:");

            }

        } while (!dadosCorretos);

        System.out.print("\nConfirma a inclusão da episodio? (S/N) ");
        char resp = scan.next().charAt(0);
        if (resp == 'S' || resp == 's') {
            try {
                Episodio ep = new Episodio(nome, temporada, dataLancamento, duracao, idSerie);
                arqEpisodios.create(ep);

                System.out.println("episodio incluído com sucesso.");

            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o episodio!");
            }
        }
    }

    public void alterarEpisodio() {

        System.out.print("\nDigite o nome do episodio a ser alterado: ");
        String nome = scan.nextLine();

        if (nome != null) {
            try {
                // Tenta ler o episodio com o ID fornecido
                Episodio episodio = arqEpisodios.readNome(nome);
                if (episodio != null) {
                    System.out.println("episodio encontrado:");
                    mostraEpisodio(episodio);  // Exibe os dados do episodio para confirmação

                    // Alteração de Nome
                    System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                    String novoNome = scan.nextLine();
                    if (!novoNome.isEmpty()) {
                        episodio.nome = novoNome;  // Atualiza o nome se fornecido
                    }

                    // Alteração de Temporada
                    System.out.print("Nova temporada: ");

                    if (scan.hasNextInt()) {
                        int novaTemp = scan.nextInt();
                        episodio.temporada = novaTemp;
                    } else {
                        System.out.println("Insira uma temporada válida");
                    }
                    scan.nextLine(); // TENATNDO LIMPAR BUFFER
                    // Alteração de data de lançamento
                    System.out.print("Nova data de lançamento (DD/MM/AAAA) (deixe em branco para manter a anterior): ");
                    String novaDataStr = scan.nextLine();
                    if (!novaDataStr.isEmpty()) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            episodio.dataLancamento = LocalDate.parse(novaDataStr, formatter);  // Atualiza a data de lançamento se fornecida
                        } catch (Exception e) {
                            System.err.println("Data inválida. Valor mantido.");
                        }
                    }

                    // Alteração de Temporada
                    System.out.print("Nova duração: ");

                    if (scan.hasNextInt()) {
                        int novaDuracao = scan.nextInt();
                        episodio.duracao = novaDuracao;
                    } else {
                        System.out.println("Insira uma duração valida válida");
                    }
                    scan.nextLine();  // Limpa o buffer

                    // Confirmação da alteração
                    System.out.print("\nConfirma as alterações? (S/N) ");
                    char resp = scan.next().charAt(0);

                    if (resp == 'S' || resp == 's') {

                        // Salva as alterações no arquivo
                        boolean alterado = arqEpisodios.update(episodio, nome);

                        if (alterado) {

                            System.out.println("episodio alterado com sucesso.");

                        } else {

                            System.out.println("Erro ao alterar o episodio.");
                        }
                    } else {
                        System.out.println("Alterações canceladas.");
                    }
                } else {
                    System.out.println("episodio não encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível alterar o episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void excluirEpisodio() throws Exception {
        System.out.print("\nDigite o nome da serie do episodio a ser excluído: ");
        String serieNome = scan.nextLine();
        int serieID = arqSerie.read(serieNome).getID();
        System.out.print("\nDigite o nome do episodio a ser excluído: ");
        String episodioNome = scan.nextLine();  // Lê o ID digitado pelo usuário

        if (episodioNome != null) {
            try {
                // Tenta ler o episodio com o ID fornecido
                Episodio episodio = arqEpisodios.readNome(episodioNome);
                if (episodio != null) {
                    System.out.println("Episodio encontrado:");
                    mostraEpisodio(episodio);  // Exibe os dados do episodio para confirmação

                    System.out.print("\nConfirma a exclusão do episodio? (S/N) ");
                    char resp = scan.next().charAt(0);  // Lê a resposta do usuário

                    if (resp == 'S' || resp == 's') {
                        boolean excluido = arqEpisodios.delete(episodioNome, serieID);  // Chama o método de exclusão no arquivo
                        if (excluido) {
                            System.out.println("Episodio excluído com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir o episodio.");
                        }
                    } else {
                        System.out.println("Exclusão cancelado.");
                    }
                } else {
                    System.out.println("Episodio não encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível excluir o Episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("ID inválido.");
        }
    }

    public void mostraEpisodio(Episodio episodio) {
        if (episodio != null) {
            System.out.println("\nDetalhes do Episodio:");
            System.out.println("----------------------");

            System.out.print("\nNome..............: " + episodio.nome);
            System.out.print("\nTemporada.........: " + episodio.temporada);
            System.out.print("\nData de Lançamento: " + episodio.dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("\nDuração...........: " + episodio.duracao);
            System.out.println();
            System.out.println("----------------------");

        }
    }
}
