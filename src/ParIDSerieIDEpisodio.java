
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.*;

public class ParIDSerieIDEpisodio implements RegistroArvoreBMais<ParIDSerieIDEpisodio> {

    private int IDSerie;
    private int IDEpisodio;
    private short TAMANHO = 8;

    public int getIDSerie() {
        return this.IDSerie;
    }

    public int getIDEpisodio() {
        return this.IDEpisodio;
    }

    public ParIDSerieIDEpisodio() {
        IDSerie = -1;
        IDEpisodio = -1;
    }

    public ParIDSerieIDEpisodio(int idSerie, int idEpisodio) throws Exception {
        this.IDSerie = idSerie;
        this.IDEpisodio = idEpisodio;
    }

    @Override
    public ParIDSerieIDEpisodio clone() {
        try {
            return new ParIDSerieIDEpisodio(this.IDSerie, this.IDEpisodio);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "(" + this.IDSerie + ";" + this.IDEpisodio + ")";
    }

    public int compareTo(ParIDSerieIDEpisodio a) {
        int id1 = this.IDSerie;
        int id2 = a.IDSerie;

        if (id1 == id2) {
            if (this.IDEpisodio == -1) {
                return 0;
            } else {
                return this.IDEpisodio - a.IDEpisodio;
            }
        } else {
            return id1 - id2;
        }
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.IDSerie);
        dos.writeInt(this.IDEpisodio);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.IDSerie = dis.readInt();
        this.IDEpisodio = dis.readInt();
    }

}
