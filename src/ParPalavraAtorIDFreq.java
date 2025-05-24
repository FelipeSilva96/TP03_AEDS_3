
import aed3.RegistroHashExtensivel;
import java.io.*;

public class ParPalavraAtorIDFreq implements RegistroHashExtensivel {

    private static final int MAX_WORD_LENGTH = 50;
    private String palavra;
    private int atorId;
    private int freq;

    public ParPalavraAtorIDFreq() {
        this.palavra = "";
        this.atorId = -1;
        this.freq = 0;
    }

    public ParPalavraAtorIDFreq(String palavra, int atorId, int freq) {
        this.palavra = palavra;
        this.atorId = atorId;
        this.freq = freq;
    }

    @Override
    public int hashCode() {
        // hash apenas pela palavra, para agrupar postagens
        return Math.abs(palavra.hashCode());
    }

    @Override
    public short size() {
        // 2 bytes per char (UTF-8) aproximado + 4 bytes int + 4 bytes int
        return (short) (2 * MAX_WORD_LENGTH + Integer.BYTES * 2);
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size());
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(palavra);
        dos.writeInt(atorId);
        dos.writeInt(freq);
        // completar até size() se necessário
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.palavra = dis.readUTF();
        this.atorId = dis.readInt();
        this.freq = dis.readInt();
    }

    public String getPalavra() {
        return palavra;
    }

    public int getAtorId() {
        return atorId;
    }

    public int getFreq() {
        return freq;
    }
}
