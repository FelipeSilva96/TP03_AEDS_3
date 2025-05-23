
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.*;

public class ParNomeSerieID implements RegistroHashExtensivel {

    private String nome;
    private int id;
    private final short nomeTamanho = 30;
    private final short tamanho = 34;

    public ParNomeSerieID() {
        nome = "";
        id = -1;
    }

    public ParNomeSerieID(String nome, int id) throws Exception {
        this.nome = nome;
        this.id = id;
    }

    public String getNome() {
        return nome;

    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.nome.hashCode());
    }

    public short size() {
        return this.tamanho;
    }

    public String toString() {
        return "(" + this.nome + ";" + this.id + ")";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] vb = new byte[nomeTamanho];
        copyStringToByteArray(vb, nome); //copia a string para o array de bytes e adiciona espacos vazios no final se necessario
        dos.write(vb);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        byte[] vb = new byte[nomeTamanho];
        dis.read(vb);
        this.nome = new String(vb).trim();
        this.id = dis.readInt();
    }

    public static int hash(String nome) {
        return Math.abs(nome.hashCode());
    }

    /* 
    public static int hash(String nome) throws IllegalArgumentException {

        long nomeLong = Long.parseLong(nome);

        int hashValue = (int) (nomeLong % (int) (1e9 + 7));

        return Math.abs(hashValue);
    }
     */
    public void copyStringToByteArray(byte[] targetArray, String value) {
        byte[] stringBytes = value.getBytes(); // 
        int copyLength = Math.min(stringBytes.length, this.nomeTamanho);

        System.arraycopy(stringBytes, 0, targetArray, 0, copyLength);

        for (int i = copyLength; i < this.nomeTamanho; i++) {
            targetArray[i] = (byte) ' ';
        }
    }

}
