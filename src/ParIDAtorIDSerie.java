
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.*;

public class ParIDAtorIDSerie implements RegistroArvoreBMais<ParIDAtorIDSerie> {

    private int IDActor;
    private int IDSerie;
    private short TAMANHO = 8;

    public int getIDSerie() {
        return this.IDSerie;
    }

    public int getIDActor() {
        return this.IDActor;
    }

    public ParIDAtorIDSerie() {
        IDSerie = -1;
        IDActor = -1;
    }

    public ParIDAtorIDSerie(int idAtor, int idSerie) throws Exception {
        this.IDActor = idAtor;
        this.IDSerie = idSerie;

    }

    @Override
    public ParIDAtorIDSerie clone() {
        try {
            return new ParIDAtorIDSerie(this.IDActor, this.IDSerie);
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

    public int compareTo(ParIDAtorIDSerie a) {
        int id1 = this.IDActor;
        int id2 = a.IDActor;

        if (id1 == id2) {
            if (this.IDSerie == -1) {
                return 0;
            } else {
                return this.IDSerie - a.IDSerie;
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
