import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import aed3.ElementoLista;

public class MenuAtor {

    String actorName;
    ArquivoSerie arqSerie;
    ArquivoAtor arqAtor;
    private static Scanner scan = new Scanner(System.in);

    public MenuAtor() throws Exception {
        arqSerie = new ArquivoSerie();
        arqAtor = new ArquivoAtor();
    }

    public void menu() throws Exception {

        int opcao;
        do {

            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Atores");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("5 - Ver series vinculadas a um ator");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(scan.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarAtor();
                    break;
                case 2:
                    incluirAtor();
                    break;
                case 3:
                    alterarAtor();
                    break;
                case 4:
                    excluirAtor();
                    break;
                case 5:
                    vinculoAtor();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);
    }

    public void buscarAtor() {
        System.out.print("\nnome do Ator: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário
        // Limpar o buffer após o nextInt()

        if (nome != null) {
            try {
                List<Ator> atores = arqAtor.readNome(nome);
                if (atores != null && atores.size()!=0) {
                    System.out.println("Digite o numero do ator que deseja: ");
                    int i=1;
                    for(Ator ator : atores){
                        System.out.print("\n"+i+ " - "+ator.nome);
                        i++;  // Exibe os detalhes do ator encontrado
                    }
                    System.out.println("");
                    int ator_id = scan.nextInt();
                    scan.nextLine();
                    mostrarAtor(arqAtor.read(atores.get(ator_id-1).getID()));

                } else {
                    System.out.println("Ator não encontrado.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar ator!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome não encontrado.");
        }
    }

    public void incluirAtor() {

        String nome = "";

        System.out.println("\nInclusão do Ator");

        do {
            System.out.print("\nNome (min. de 2 letras ou vazio para cancelar): ");
            nome = scan.nextLine();
            if (nome.length() == 0) {
                return;
            }
            if (nome.length() < 2) {
                System.err.println("O nome da ator deve ter no mínimo 2 caracteres.");
            }
        } while (nome.length() < 2);

        System.out.print("\nConfirma a inclusão da ator? (S/N) ");

        char resp = scan.nextLine().charAt(0);
        if (resp == 'S' || resp == 's') {
            try {
                Ator s = new Ator(nome);
                arqAtor.create(s);

                System.out.println("Ator incluído com sucesso.");

            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o ator!");
            }
        }
    }

    public void alterarAtor() throws Exception {

        System.out.print("\nDigite o Nome do ator a ser alterado: ");
        String nome = scan.nextLine();
        Ator ator = null;;

        if (nome != null) {
            try {
                // Tenta ler o serie com o ID fornecido
                List<Ator> atores = arqAtor.readNome(nome);
                
                if (atores != null && atores.size()!=0) {
                    System.out.println("Digite o numero do ator que deseja: ");
                    int i=1;
                    for(Ator ator_b : atores){
                        System.out.print("\n"+i+ " - "+ator_b.nome);
                        i++;
                    }
                    System.out.println("");
                    int ator_id = scan.nextInt();
                    scan.nextLine();
                    ator = arqAtor.read(atores.get(ator_id-1).getID());

                    // Alteração de Nome
                    System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                    String novoNome = scan.nextLine();
                    if (!novoNome.isEmpty()) {
                        ator.nome = novoNome;  // Atualiza o nome se fornecido
                    }

                    // Confirmação da alteração
                    System.out.print("\nConfirma as alterações? (S/N) ");
                    char resp = scan.next().charAt(0);
                    if (resp == 'S' || resp == 's') {
                        // Salva as alterações no arquivo
                        boolean alterado = arqAtor.update(ator);

                        if (alterado) {

                            System.out.println("Ator alterado com sucesso.");

                        } else {

                            System.out.println("Erro ao alterar o ator.");
                        }
                    } else {
                        System.out.println("Alterações canceladas.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível alterar o ator!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome não encontrado.");
        }
    }

    public void excluirAtor() {
        System.out.print("\nDigite o nome do ator a ser excluído: ");
        String nome = scan.nextLine();
        Ator ator;
        if (nome != null) {
            try {
                List<Ator> atores = arqAtor.readNome(nome);
                
                if (atores != null && atores.size()!=0) {
                    System.out.println("Digite o numero do ator que deseja: ");
                    int i=1;
                    for(Ator ator_b : atores){
                        System.out.print("\n"+i+ " - "+ator_b.nome);
                        i++;
                    }
                    System.out.println("");
                    int ator_id = scan.nextInt();
                    scan.nextLine();
                    ator = arqAtor.read(atores.get(ator_id-1).getID());

                    if (hasSeries(ator.nome)) {
                        System.out.print("\nAtor possui series associadas.\nPara excluir ator, exclua todos os vinculos dela a series");
                    } else {
                        System.out.print("\nConfirma a exclusão do ator? (S/N) ");
                        char resp = scan.next().charAt(0);  // Lê a resposta do usuário

                        if (resp == 'S' || resp == 's') {
                            boolean excluido = arqAtor.delete(ator.id);  // Chama o método de exclusão no arquivo
                            if (excluido) {
                                System.out.println("Ator excluído com sucesso.");
                            } else {
                                System.out.println("Erro ao excluir o ator.");
                            }
                        } else {
                            System.out.println("Exclusão cancelada.");
                        }
                    }

                } else {
                    System.out.println("Ator não encontrada.");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível excluir o ator!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome não encontrado.");
        }
    }

    public boolean hasSeries(String nome) throws Exception {
        boolean res = true;

        List<Serie> lista = arqAtor.readSeries(nome);

        if (lista == null || lista.isEmpty()) {
            res = false;
        }

        return res;
    }

    public void vinculoAtor() {
        System.out.print("\nNome do Ator: ");
        String nome = scan.nextLine();  // Lê o ID digitado pelo usuário
        // Limpar o buffer após o nextInt()

        if (nome != null) {
            try {

                List<Serie> lista_de_series = arqAtor.readSeries(nome);
                if (lista_de_series != null && !lista_de_series.isEmpty()) {
                    for (Serie i : lista_de_series) {
                        MenuSeries.mostraSerie(i);
                    }
                } else {
                    System.out.println("Nenhuma serie encontrada");
                }
            } catch (Exception e) {
                System.out.println("Erro do sistema. Não foi possível buscar ator!");
                e.printStackTrace();
            }
        } else {
            System.out.println("Nome null.");
        }
    }

    public static void mostrarAtor(Ator ator) {
        if (ator != null) {
            System.out.println("\nDetalhes do Ator:");
            System.out.println("----------------------");

            System.out.print("\nNome..............: " + ator.nome);
            System.out.println();
            System.out.println("----------------------");

        }
    }

}
