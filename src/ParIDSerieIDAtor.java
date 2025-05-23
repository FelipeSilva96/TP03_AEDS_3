import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.*;


public class ParIDSerieIDAtor implements RegistroArvoreBMais<ParIDSerieIDAtor> {
    private int IDActor;
    private int IDSerie;
    private short TAMANHO = 8;

    public int getIDSerie() {
        return this.IDSerie; 
    }

    public int getIDActor() {
        return this.IDActor;
    }

    public ParIDSerieIDAtor() {
        IDSerie = -1;
        IDActor = -1;
    }

    public ParIDSerieIDAtor(int idSerie, int idAtor) throws Exception {
        this.IDSerie = idSerie;
        this.IDActor = idAtor;
    }

    @Override
    public ParIDSerieIDAtor clone() {
        try {
            return new ParIDSerieIDAtor(this.IDSerie, this.IDActor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public short size() {
        return this.TAMANHO;
    }

    public String toString() {
        return "(" + this.IDSerie + ";" + this.IDActor + ")";
    }

    public int compareTo(ParIDSerieIDAtor a) {
        int id1 = this.IDSerie;
        int id2 = a.IDSerie;

        if (id1 == id2) {
            if (this.IDActor == -1) {
                return 0;
            } else {
                return this.IDActor - a.IDActor;
            }
        } else {
            return id1 - id2;
        }
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.IDSerie);
        dos.writeInt(this.IDActor);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.IDSerie = dis.readInt();
        this.IDActor = dis.readInt();
    }
    
}
