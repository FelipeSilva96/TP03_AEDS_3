
import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {

        Scanner scan;

        try {
            scan = new Scanner(System.in);
            int opcao;
            do {

                System.out.println("\n\nAEDsIII");
                System.out.println("-------");
                System.out.println("> Início");
                System.out.println("\n1) Séries");
                System.out.println("2) Episódios");
                System.out.println("3) Atores"); // ignorar por enquanto
                System.out.println("4) Sair");

                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.valueOf(scan.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        new MenuSeries().menu();
                        break;
                    case 2:
                        new MenuEpisodios().menu();
                        break;
                    case 3:
                        new MenuAtor().menu(); // Ignorar por enquanto
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Opção inválida!");
                        break;
                }

            } while (opcao != 4);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
