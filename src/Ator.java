import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.EntidadeArquivo;

public class Ator implements EntidadeArquivo{
    int id;
    String nome;

    public Ator(){
        this.id=-1;
        this.nome="";
    }

    public Ator(int id, String nome){
        this.id=id;
        this.nome=new String(nome);
    }

    public Ator(Ator a){
        this.id=a.id;
        this.nome=new String(a.nome);
    }

    public Ator(String nome){
        this.id=-1;
        this.nome=new String(nome);
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public int getID() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String toString() {
        return "\nID...................: " + this.id
                + "\nNome..............: " + this.nome;
    }

    public static void mostraAtor(Ator ator) {
        if (ator != null) {
            System.out.println("\nDetalhes do Ator:");
            System.out.println("----------------------");

            System.out.print("\nNome..............: " + ator.nome);
            System.out.println();
            System.out.println("----------------------");

        }
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeUTF(this.nome);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
    }
    
    
}
