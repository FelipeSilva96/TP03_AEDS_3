package indices;

import aed3.RegistroHashExtensivel;
import java.io.*;

public class ParPalavraEpisodioIDFreq implements RegistroHashExtensivel {
    private static final int MAX_WORD = 40;
    private String palavra;
    private int episodioId;
    private int freq;

    public ParPalavraEpisodioIDFreq() {
        this.palavra = "";
        this.episodioId = -1;
        this.freq = 0;
    }

    public ParPalavraEpisodioIDFreq(String palavra, int episodioId, int freq) {
        this.palavra = palavra;
        this.episodioId = episodioId;
        this.freq = freq;
    }

    @Override
    public int hashCode() {
        return Math.abs(palavra.hashCode());
    }

    @Override
    public short size() {
        return (short)(2 * MAX_WORD + Integer.BYTES * 2);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size());
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(palavra);
        dos.writeInt(episodioId);
        dos.writeInt(freq);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ba));
        this.palavra = dis.readUTF();
        this.episodioId = dis.readInt();
        this.freq = dis.readInt();
    }

    public String getPalavra() { return palavra; }
    public int getEpisodioId() { return episodioId; }
    public int getFreq() { return freq; }
}
