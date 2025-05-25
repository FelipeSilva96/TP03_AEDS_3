package indices;

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
        return Math.abs(palavra.hashCode());
    }

    @Override
    public short size() {
        return (short)(2 * MAX_WORD_LENGTH + Integer.BYTES * 2);
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size());
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(palavra);
        dos.writeInt(atorId);
        dos.writeInt(freq);
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

    // getters
    public String getPalavra() { return palavra; }
    public int getAtorId() { return atorId; }
    public int getFreq() { return freq; }
}