import java.util.Scanner;
import java.util.ArrayList;
import java.util.List; 

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

        if (nome != null && !nome.trim().isEmpty()) { 
            try {
                ArrayList<Episodio> episodios = arqEpisodios.readSerie(nome);
                if (episodios != null && !episodios.isEmpty()) {
                    boolean found = false;
                    for (Episodio ep : episodios) {
                        if (ep != null && ep.temporada == temporada) {
                            ep.mostraEpisodio();
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("Nenhum episódio encontrado para esta temporada.");
                    }
                } else {
                    System.out.println("Nenhum episodio encontrado para a série '" + nome + "'.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar a temporada!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }

    public void buscarEpisodios() throws Exception {
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine();

        if (nome != null && !nome.trim().isEmpty()) {
            try {
                ArrayList<Episodio> episodios = arqEpisodios.readSerie(nome);
                if (episodios != null && !episodios.isEmpty()) {
                    for (Episodio ep : episodios) {
                        if (ep != null) {
                            ep.mostraEpisodio();
                        }
                    }
                } else {
                    System.out.println("Nenhum episodio encontrado para a série '" + nome + "'.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar os episódios!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }

    public void buscarSerie() {
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine();

        if (nome != null && !nome.trim().isEmpty()) {
            try {
                List<Serie> series = arqSeries.readNome(nome); 
                if (series != null && !series.isEmpty()) {
                   
                    mostraSerie(series.get(0));
                    if (series.size() > 1) {
                        System.out.println("Nota: Foram encontradas múltiplas séries com termos semelhantes. Exibindo a primeira.");
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
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
            if (scan.hasNextShort()) { 
                anoLancamento = scan.nextShort();
                dadosCorretos = true;
            } else {
                System.err.println("Ano de Lançamento inválido! Por favor, insira um número válido.");
            }
            scan.nextLine(); 
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
                System.out.println("Erro do sistema. Não foi possível incluir a serie!");
                 e.printStackTrace();
            }
        }
    }

    public void alterarSerie() throws Exception {
        System.out.print("\nDigite o Nome da serie a ser alterada: ");
        String nome = scan.nextLine();
        Serie serieParaAlterar = null;

        if (nome != null && !nome.trim().isEmpty()) {
            List<Serie> seriesEncontradas = arqSeries.readNome(nome);
            if (seriesEncontradas != null && !seriesEncontradas.isEmpty()) {
                serieParaAlterar = seriesEncontradas.get(0); 
                 if (seriesEncontradas.size() > 1) {
                    System.out.println("Nota: Múltiplas séries encontradas. Alterando a primeira: " + serieParaAlterar.getNome());
                }
            } else {
                System.out.println("Serie não encontrada.");
                return;
            }

            try {
                System.out.println("Serie encontrada:");
                mostraSerie(serieParaAlterar);

                System.out.print("\nNovo nome (deixe em branco para manter o anterior '" + serieParaAlterar.nome + "'): ");
                String novoNome = scan.nextLine();
                if (!novoNome.trim().isEmpty()) { 
                    serieParaAlterar.nome = novoNome;
                }

                System.out.print("Novo Ano de Lançamento (atual: " + serieParaAlterar.anoLancamento + ", deixe em branco ou digite 0 para manter): ");
                String anoInput = scan.nextLine();
                if (!anoInput.trim().isEmpty()) {
                    try {
                        short novoAno = Short.parseShort(anoInput);
                        if (novoAno != 0) serieParaAlterar.anoLancamento = novoAno;
                    } catch (NumberFormatException e) {
                        System.out.println("Formato de ano inválido. Ano de lançamento não alterado.");
                    }
                }
                
                System.out.print("\nNova sinopse (deixe em branco para manter a anterior): ");
                String novaSinopse = scan.nextLine();
                if (!novaSinopse.trim().isEmpty()) {
                     serieParaAlterar.sinopse = novaSinopse;
                }

                System.out.print("\nNovo nome do streaming (atual: " + serieParaAlterar.streaming + ", deixe em branco para manter): ");
                String novoStreaming = scan.nextLine();
                if (!novoStreaming.trim().isEmpty()) {
                    serieParaAlterar.streaming = novoStreaming;
                }

                System.out.print("\nConfirma as alterações? (S/N) ");
                char resp = scan.nextLine().charAt(0); 
                if (resp == 'S' || resp == 's') {
                    
                    boolean alterado = arqSeries.update(serieParaAlterar); 

                    if (alterado) {
                        System.out.println("Serie alterada com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar a Serie.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível alterar a Serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }

    public void excluirSerie() {
        System.out.print("\nDigite o nome da serie a ser excluída: ");
        String nome = scan.nextLine();
        Serie serieParaExcluir = null;

        if (nome != null && !nome.trim().isEmpty()) {
             try {
                List<Serie> seriesEncontradas = arqSeries.readNome(nome);
                 if (seriesEncontradas != null && !seriesEncontradas.isEmpty()) {
                    serieParaExcluir = seriesEncontradas.get(0); 
                    if (seriesEncontradas.size() > 1) {
                         System.out.println("Nota: Múltiplas séries encontradas. Tentando excluir a primeira: " + serieParaExcluir.getNome());
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                    return;
                }

                System.out.println("Serie encontrada:");
                mostraSerie(serieParaExcluir);

                if (hasEpisodios(serieParaExcluir.nome)) { 
                    System.out.print("\nSerie possui episodios associados.\nPara excluir serie, exclua todos os episodios associados a ela.");
                } else {
                    System.out.print("\nConfirma a exclusão da serie? (S/N) ");
                    char resp = scan.nextLine().charAt(0); 

                    if (resp == 'S' || resp == 's') {
                        ArrayList<Ator> list_ator = arqAtor.readAtores(serieParaExcluir.nome); 

                        if (list_ator != null) { 
                            for(Ator i : list_ator){
                                if (i != null) arqAtor.unlinkAtorSerie(i.getID(), serieParaExcluir.getID());
                            }
                        }
                        boolean excluido = arqSeries.delete(serieParaExcluir.nome);
                        if (excluido) {
                            System.out.println("Serie excluída com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir a serie.");
                        }
                    } else {
                        System.out.println("Exclusão cancelada.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível excluir a serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }
     public void vinculoSerie(){
        System.out.print("\nNome da Serie: ");
        String nome = scan.nextLine(); 

        if (nome != null && !nome.trim().isEmpty()) {
            try {
             
                ArrayList<Ator> lista_de_atores = arqAtor.readAtores(nome);
                if(lista_de_atores == null || lista_de_atores.isEmpty()){ 
                    System.out.println("Nenhum ator encontrado para esta série ou série não encontrada.");
                } else {
                    for(Ator i:lista_de_atores){
                        MenuAtor.mostrarAtor(i);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar atores da serie!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }
    
    public void vincularAtores(){
        System.out.print("\nNome da Serie: ");
        String nome_serie_busca = scan.nextLine();
        Serie serie = null;

        if (nome_serie_busca != null && !nome_serie_busca.trim().isEmpty()) {
            try {
                List<Serie> seriesEncontradas = arqSeries.readNome(nome_serie_busca);
                if (seriesEncontradas != null && !seriesEncontradas.isEmpty()) {
                    serie = seriesEncontradas.get(0); // Select the first one
                     if (seriesEncontradas.size() > 1) {
                        System.out.println("Múltiplas séries encontradas. Vinculando atores à primeira: " + serie.getNome());
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                    return;
                }

                String nome_ator_busca;
                do {
                    System.out.print("\nNome do ator para vincular (ou '0' para terminar): ");
                    nome_ator_busca = scan.nextLine().trim();
                    if(!nome_ator_busca.equals("0")){
                        List<Ator> atores = arqAtor.readNome(nome_ator_busca); 
                
                        if (atores != null && !atores.isEmpty()) {
                            Ator atorParaVincular;
                            if (atores.size() == 1) {
                                atorParaVincular = atores.get(0);
                            } else {
                                System.out.println("Múltiplos atores encontrados. Digite o número do ator que deseja:");
                                for(int i=0; i < atores.size(); i++){
                                    System.out.println((i+1) + " - " + atores.get(i).getNome());
                                }
                                int escolha = Integer.parseInt(scan.nextLine()) -1;
                                if (escolha >= 0 && escolha < atores.size()) {
                                    atorParaVincular = atores.get(escolha);
                                } else {
                                    System.out.println("Seleção inválida.");
                                    continue;
                                }
                            }
                            
                            if(arqAtor.linkAtorSerie(atorParaVincular.getID(), serie.getID())){
                                System.out.println("Vínculo entre " + atorParaVincular.getNome() + " e " + serie.getNome() + " criado com sucesso.");
                            }else{
                                System.out.println("Criação de vínculo falhou (possivelmente já existe).");
                            }
                        } else {
                             System.out.println("Ator '" + nome_ator_busca + "' não encontrado.");
                        }
                    }
                } while (!nome_ator_busca.equals("0"));
                System.out.println("Vinculação de atores encerrada.");

            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível vincular atores!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }

    public void desvincularAtores(){
        System.out.print("\nNome da Serie: ");
        String nome_serie_busca = scan.nextLine();
        Serie serie = null;

        if (nome_serie_busca != null && !nome_serie_busca.trim().isEmpty()) {
             try {
                List<Serie> seriesEncontradas = arqSeries.readNome(nome_serie_busca);
                if (seriesEncontradas != null && !seriesEncontradas.isEmpty()) {
                    serie = seriesEncontradas.get(0);
                    if (seriesEncontradas.size() > 1) {
                        System.out.println("Múltiplas séries encontradas. Desvinculando atores da primeira: " + serie.getNome());
                    }
                } else {
                    System.out.println("Serie não encontrada.");
                    return;
                }
                
                String nome_ator_busca;
                do {
                    System.out.print("\nNome do ator para desvincular (ou '0' para terminar): ");
                    nome_ator_busca = scan.nextLine().trim();
                    if(!nome_ator_busca.equals("0")){
                        List<Ator> atores = arqAtor.readNome(nome_ator_busca);
                
                        if (atores != null && !atores.isEmpty()) {
                             Ator atorParaDesvincular;
                            if (atores.size() == 1) {
                                atorParaDesvincular = atores.get(0);
                            } else {
                                System.out.println("Múltiplos atores encontrados. Digite o número do ator que deseja:");
                                for(int i=0; i < atores.size(); i++){
                                    System.out.println((i+1) + " - " + atores.get(i).getNome());
                                }
                                int escolha = Integer.parseInt(scan.nextLine()) -1;
                                if (escolha >= 0 && escolha < atores.size()) {
                                    atorParaDesvincular = atores.get(escolha);
                                } else {
                                    System.out.println("Seleção inválida.");
                                    continue;
                                }
                            }

                            if(arqAtor.unlinkAtorSerie(atorParaDesvincular.getID(), serie.getID())){
                                System.out.println("Vínculo entre " + atorParaDesvincular.getNome() + " e " + serie.getNome() + " excluído com sucesso.");
                            }else{
                                System.out.println("Exclusão de vínculo falhou (possivelmente não existia).");
                            }
                        } else {
                            System.out.println("Ator '" + nome_ator_busca + "' não encontrado.");
                        }
                    }
                } while (!nome_ator_busca.equals("0"));
                 System.out.println("Desvinculação de atores encerrada.");

            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível desvincular atores!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome da série inválido.");
        }
    }


    public boolean hasEpisodios(String nomeSerie) throws Exception {
        ArrayList<Episodio> lista = arqEpisodios.readSerie(nomeSerie);
        return (lista != null && !lista.isEmpty()); 
    }

    public static void mostraSerie(Serie serie) {
        if (serie != null) {
            System.out.println("\nDetalhes da serie:");
            System.out.println("----------------------");
            System.out.println("ID...............: " + serie.id); 
            System.out.println("Nome.............: " + serie.nome);
            System.out.println("Ano de lançamento: " + serie.anoLancamento);
            System.out.println("Sinopse..........: " + serie.sinopse);
            System.out.println("Streaming........: " + serie.streaming);
            System.out.println("----------------------");
        }
    }
}