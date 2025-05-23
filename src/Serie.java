
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.EntidadeArquivo;

public class Serie implements EntidadeArquivo {

    public int id;
    public String nome;
    public short anoLancamento;
    public String sinopse;
    public String streaming;

    public Serie() {
        this.id = -1;
        this.nome = "";
        this.anoLancamento = 0;
        this.sinopse = "";
        this.streaming = "";

    }

    public Serie(Serie s) {
        this.id = s.id;
        this.nome = new String(s.nome);
        this.anoLancamento = s.anoLancamento;
        this.sinopse = new String(s.sinopse);
        this.streaming = new String(s.streaming);
    }

    public Serie(String nome, short anoLancamento, String sinopse, String streaming) {
        this.id = -1;
        this.nome = nome;
        this.anoLancamento = anoLancamento;
        this.sinopse = sinopse;
        this.streaming = streaming;
    }

    public Serie(int id, String nome, short anoLancamento, String sinopse, String streaming) {

        this.id = id;
        this.nome = nome;
        this.anoLancamento = anoLancamento;
        this.sinopse = sinopse;
        this.streaming = streaming;

    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String toString() {
        return "\nID..................: " + this.id
                + "\nNome.............: " + this.nome
                + "\nAno de lançamento: " + this.anoLancamento
                + "\nSinopse..........: " + this.sinopse
                + "\nStreaming........: " + this.streaming;
    }

    public byte[] toByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeShort(this.anoLancamento);
        dos.writeUTF(this.sinopse);
        dos.writeUTF(this.streaming);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.anoLancamento = dis.readShort();
        this.sinopse = dis.readUTF();
        this.streaming = dis.readUTF();

    }
}
