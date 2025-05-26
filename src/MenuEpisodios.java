import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.List; 
import java.util.ArrayList; 

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
        String nome = scan.nextLine();

        if (nome != null && !nome.trim().isEmpty()) {
            try {
                List<Episodio> episodios = arqEpisodios.readNome(nome); 
                if (episodios != null && !episodios.isEmpty()) {
                    mostraEpisodio(episodios.get(0));
                    if (episodios.size() > 1) {
                         System.out.println("Nota: Foram encontrados múltiplos episódios com termos semelhantes. Exibindo o primeiro.");
                    }
                } else {
                    System.out.println("Episodio não encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar o episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome do episódio inválido.");
        }
    }

    public void incluirEpisodio() throws Exception {
        String nomeSerieBusca;
        int idSerie = -1; 
        String nome = "";
        int temporada = 0;
        LocalDate dataLancamento = null;
        int duracao = 0;
        boolean dadosCorretos = false;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("\nInclusão de episodio");

        System.out.print("\nNome da serie a qual o episódio pertence (ou vazio para cancelar): ");
        nomeSerieBusca = scan.nextLine();
        if (nomeSerieBusca.trim().isEmpty()) {
            System.out.println("Inclusão de episódio cancelada.");
            return;
        }

        List<Serie> seriesEncontradas = arqSerie.readNome(nomeSerieBusca);
        if (seriesEncontradas == null || seriesEncontradas.isEmpty()) {
            System.out.println("Nenhuma série encontrada com o nome '" + nomeSerieBusca + "'. Não é possível adicionar episódio.");
            return;
        }
        
        Serie serieSelecionada = seriesEncontradas.get(0); 
        if (seriesEncontradas.size() > 1) {
            System.out.println("Múltiplas séries encontradas. Selecionando a primeira: " + serieSelecionada.getNome());
        }
        idSerie = serieSelecionada.getID();


        do {
            System.out.print("\nNome do episódio (min. de 2 letras ou vazio para cancelar): ");
            nome = scan.nextLine();
            if (nome.trim().isEmpty()) { 
                System.out.println("Inclusão de episódio cancelada.");
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
            scan.nextLine(); 
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
            System.out.print("Duração (em minutos): ");
            if (scan.hasNextInt()) {
                duracao = scan.nextInt();
                scan.nextLine(); 
                if (duracao > 0) {
                    dadosCorretos = true;
                } else {
                     System.err.println("Duração deve ser maior que zero.");
                }
            } else {
                System.err.println("Duração inválida! Por favor, insira um número válido.");
                scan.nextLine(); 
            }
        } while (!dadosCorretos);

        System.out.print("\nConfirma a inclusão do episodio? (S/N) ");
        char resp = scan.nextLine().charAt(0); 
        if (resp == 'S' || resp == 's') {
            try {
                Episodio ep = new Episodio(nome, temporada, dataLancamento, duracao, idSerie);
                arqEpisodios.create(ep);
                System.out.println("Episodio incluído com sucesso.");
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o episodio!");
                 e.printStackTrace();
            }
        } else {
             System.out.println("Inclusão cancelada.");
        }
    }

    public void alterarEpisodio() {
        System.out.print("\nDigite o nome do episodio a ser alterado: ");
        String nome = scan.nextLine();
        Episodio episodioParaAlterar = null;

        if (nome != null && !nome.trim().isEmpty()) {
            try {
                List<Episodio> episodiosEncontrados = arqEpisodios.readNome(nome);
                if (episodiosEncontrados != null && !episodiosEncontrados.isEmpty()) {
                    
                    episodioParaAlterar = episodiosEncontrados.get(0);
                     if (episodiosEncontrados.size() > 1) {
                        System.out.println("Nota: Múltiplos episódios encontrados com este nome. Alterando o primeiro encontrado.");
                    }
                } else {
                    System.out.println("Episodio não encontrado.");
                    return;
                }

                System.out.println("Episodio encontrado:");
                mostraEpisodio(episodioParaAlterar);

                System.out.print("\nNovo nome (deixe em branco para manter o anterior '" + episodioParaAlterar.nome + "'): ");
                String novoNome = scan.nextLine();
                if (!novoNome.trim().isEmpty()) {
                    episodioParaAlterar.nome = novoNome;
                }

                System.out.print("Nova temporada (atual: " + episodioParaAlterar.temporada + ", deixe em branco ou digite 0 para manter): ");
                String tempInput = scan.nextLine();
                if (!tempInput.trim().isEmpty()) {
                     try {
                        int novaTemp = Integer.parseInt(tempInput);
                        if (novaTemp != 0) episodioParaAlterar.temporada = novaTemp;
                    } catch (NumberFormatException e) {
                        System.out.println("Formato de temporada inválido. Temporada não alterada.");
                    }
                }

                System.out.print("Nova data de lançamento (DD/MM/AAAA) (atual: " + episodioParaAlterar.dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ", deixe em branco para manter): ");
                String novaDataStr = scan.nextLine();
                if (!novaDataStr.trim().isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        episodioParaAlterar.dataLancamento = LocalDate.parse(novaDataStr, formatter);
                    } catch (Exception e) {
                        System.err.println("Data inválida. Valor mantido.");
                    }
                }

                System.out.print("Nova duração (atual: " + episodioParaAlterar.duracao + ", deixe em branco ou digite 0 para manter): ");
                String duracaoInput = scan.nextLine();
                if (!duracaoInput.trim().isEmpty()) {
                    try {
                        int novaDuracao = Integer.parseInt(duracaoInput);
                        if (novaDuracao != 0) episodioParaAlterar.duracao = novaDuracao;
                    } catch (NumberFormatException e) {
                        System.out.println("Formato de duração inválido. Duração não alterada.");
                    }
                }

                System.out.print("\nConfirma as alterações? (S/N) ");
                char resp = scan.nextLine().charAt(0); 

                if (resp == 'S' || resp == 's') {
                    boolean alterado = arqEpisodios.update(episodioParaAlterar, nome); 
                    if (alterado) {
                        System.out.println("Episodio alterado com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o episodio.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível alterar o episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome do episódio inválido.");
        }
    }

    public void excluirEpisodio() throws Exception {
        System.out.print("\nDigite o nome da serie do episodio a ser excluído: ");
        String nomeSerieBusca = scan.nextLine();
        Serie serieDoEpisodio = null;
        int serieID = -1;

        if (nomeSerieBusca.trim().isEmpty()) {
            System.out.println("Nome da série não pode ser vazio.");
            return;
        }
        List<Serie> seriesEncontradas = arqSerie.readNome(nomeSerieBusca);
        if (seriesEncontradas == null || seriesEncontradas.isEmpty()) {
            System.out.println("Nenhuma série encontrada com o nome '" + nomeSerieBusca + "'.");
            return;
        }
        serieDoEpisodio = seriesEncontradas.get(0); 
        if (seriesEncontradas.size() > 1) {
             System.out.println("Múltiplas séries encontradas. Usando a primeira: " + serieDoEpisodio.getNome());
        }
        serieID = serieDoEpisodio.getID();


        System.out.print("\nDigite o nome do episodio a ser excluído: ");
        String episodioNome = scan.nextLine();
        Episodio episodioParaExcluir = null;

        if (episodioNome != null && !episodioNome.trim().isEmpty()) {
            List<Episodio> episodiosEncontrados = arqEpisodios.readNome(episodioNome);
            if (episodiosEncontrados != null) {
                for (Episodio ep : episodiosEncontrados) {
                    if (ep.idSerie == serieID) { 
                        episodioParaExcluir = ep;
                        break;
                    }
                }
            }

            if (episodioParaExcluir == null) {
                 System.out.println("Episodio '" + episodioNome + "' não encontrado na série '" + serieDoEpisodio.getNome() + "'.");
                 return;
            }
            
            try {
                System.out.println("Episodio encontrado:");
                mostraEpisodio(episodioParaExcluir);

                System.out.print("\nConfirma a exclusão do episodio? (S/N) ");
                char resp = scan.nextLine().charAt(0); 

                if (resp == 'S' || resp == 's') {
                    boolean excluido = arqEpisodios.delete(episodioParaExcluir.getNome(), serieID);
                    if (excluido) {
                        System.out.println("Episodio excluído com sucesso.");
                    } else {
                        System.out.println("Erro ao excluir o episodio.");
                    }
                } else {
                    System.out.println("Exclusão cancelada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível excluir o Episodio!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome do episódio inválido.");
        }
    }

    public void mostraEpisodio(Episodio episodio) {
        if (episodio != null) {
            System.out.println("\nDetalhes do Episodio:");
            System.out.println("----------------------");
            System.out.println("ID................: " + episodio.id); 
            System.out.println("Nome..............: " + episodio.nome);
            System.out.println("Temporada.........: " + episodio.temporada);
            System.out.println("Data de Lançamento: " + episodio.dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("Duração...........: " + episodio.duracao + " min"); 
            System.out.println("ID Série..........: " + episodio.idSerie); 
            System.out.println("----------------------");
        }
    }
}