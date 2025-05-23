
import aed3.EntidadeArquivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Episodio implements EntidadeArquivo {

    public int id;
    public String nome;
    public int temporada;
    public LocalDate dataLancamento;
    public int duracao;
    public int idSerie;

    public Episodio() {
        this.id = -1;
        this.nome = "";
        this.temporada = 0;
        this.dataLancamento = LocalDate.now();
        this.duracao = 0;
        this.idSerie = 0;
    }

    public Episodio(String nome, int temporada, LocalDate dataLancamento, int duracao) {
        this.id = -1;
        this.nome = nome;
        this.temporada = temporada;
        this.dataLancamento = dataLancamento;
        this.duracao = duracao;
        this.idSerie = -1;

    }

    public Episodio(String nome, int temporada, LocalDate dataLancamento, int duracao, int idSerie) {
        this.id = -1;
        this.nome = nome;
        this.temporada = temporada;
        this.dataLancamento = dataLancamento;
        this.duracao = duracao;
        this.idSerie = idSerie;

    }

    public Episodio(int id, String nome, int temporada, LocalDate dataLancamento, int duracao, int idSerie) {
        this.id = id;
        this.nome = nome;
        this.temporada = temporada;
        this.dataLancamento = dataLancamento;
        this.duracao = duracao;
        this.idSerie = idSerie;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String toString() {
        return "\nID...................: " + this.id
                + "\nNome..............: " + this.nome
                + "\nTemporada.........: " + this.temporada
                + "\nData de lancamento: " + this.dataLancamento
                + "\nDuracao...........: " + this.duracao
                + "\nID Série..........: " + this.idSerie;
    }

    public static void mostraEpisodio(Episodio episodio) {
        if (episodio != null) {
            System.out.println("\nDetalhes do Episodio:");
            System.out.println("----------------------");

            System.out.print("\nNome..............: " + episodio.nome);
            System.out.print("\nTemporada.........: " + episodio.temporada);
            System.out.print("\nData de Lançamento: " + episodio.dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("\nDuração.........: " + episodio.duracao);
            System.out.println();
            System.out.println("----------------------");

        }
    }

    public void mostraEpisodio() {
        if (this != null) {
            System.out.println("\nDetalhes do this:");
            System.out.println("----------------------");

            System.out.print("\nNome..............: " + this.nome);
            System.out.print("\nTemporada.........: " + this.temporada);
            System.out.print("\nData de Lançamento: " + this.dataLancamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            System.out.println("\nDuração.........: " + this.duracao);
            System.out.println();
            System.out.println("----------------------");

        }
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeInt(this.temporada);
        dos.writeInt((int) this.dataLancamento.toEpochDay());
        dos.writeInt(this.duracao);
        dos.writeInt(this.idSerie);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.temporada = dis.readInt();
        this.dataLancamento = LocalDate.ofEpochDay(dis.readInt());
        this.duracao = dis.readInt();
        this.idSerie = dis.readInt();
    }
}
