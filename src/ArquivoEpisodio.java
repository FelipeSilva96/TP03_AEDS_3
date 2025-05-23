
import aed3.*;
import java.util.ArrayList;

public class ArquivoEpisodio extends Arquivo<Episodio> {

    Arquivo<Episodio> arqEpisodios;
    ArquivoSerie arqSerie;
    HashExtensivel<ParNomeEpisodioID> indiceIndiretoNomeEpisodio;
    ArvoreBMais<ParIDSerieIDEpisodio> indiceIndiretoIDSerieIDEpisodio;

    public ArquivoEpisodio() throws Exception {
        super("episodios", Episodio.class.getConstructor());

        arqSerie = new ArquivoSerie();

        indiceIndiretoNomeEpisodio = new HashExtensivel<>(
                ParNomeEpisodioID.class.getConstructor(),
                4,
                ".\\dados\\episodios\\indiceNomeEpisodio.d.db",
                ".\\dados\\episodios\\indiceNomeEpisodio.c.db"
        );

        indiceIndiretoIDSerieIDEpisodio = new ArvoreBMais<>(
                ParIDSerieIDEpisodio.class.getConstructor(),
                5,
                ".\\dados\\episodios\\indiceIndiretoIDSerieIDEpisodio.db"
        );
    }

    @Override
    public int create(Episodio ep) throws Exception {
        int id = super.create(ep);
        String serieNome = ep.getNome();
        ParNomeEpisodioID pneid = new ParNomeEpisodioID(serieNome, id);
        indiceIndiretoNomeEpisodio.create(pneid);
        indiceIndiretoIDSerieIDEpisodio.create(new ParIDSerieIDEpisodio(ep.idSerie, ep.id));
        return id;
    }

    public Episodio readNome(String nome) throws Exception {
        ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public ArrayList<Episodio> readSerie(String nome) throws Exception {
        if (nome == null) {
            return null;
        }
        int serieID = arqSerie.read(nome).id;
        ParIDSerieIDEpisodio key = new ParIDSerieIDEpisodio(serieID, -1);
        ArrayList<ParIDSerieIDEpisodio> pse = indiceIndiretoIDSerieIDEpisodio.read(key);
        ArrayList<Episodio> episodios = new ArrayList<>();
        ParIDSerieIDEpisodio pseaux = null;

        if (pse.size() > 0) {
            for (int i = 0; i < pse.size(); i++) {
                pseaux = pse.get(i);

                if (pseaux != null) {
                    pseaux = pse.get(i);
                    episodios.add(read(pseaux.getIDEpisodio()));
                } else {
                    System.out.println("ArquivoEpisodio.readSerie: erro na recuperacao de episodio");
                }
            }
            return episodios;
        } else {
            return null;
        }

    }

    public boolean delete(String nome, int IDSerie) throws Exception {
        ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nome));
        ParIDSerieIDEpisodio pse = new ParIDSerieIDEpisodio(IDSerie, pni.getId());
        if (pni != null) {
            if (delete(pni.getId())) {
                return indiceIndiretoNomeEpisodio.delete(ParNomeEpisodioID.hash(nome))
                        && indiceIndiretoIDSerieIDEpisodio.delete(pse);
            }
        }
        return false;
    }

    public boolean update(Episodio novoEpisodio, String antiga) throws Exception {
        Episodio episodioAntigo = readNome(antiga);
        if (super.update(novoEpisodio)) {
            if (novoEpisodio.getNome().compareTo(episodioAntigo.getNome()) != 0) {
                indiceIndiretoNomeEpisodio.delete(ParNomeEpisodioID.hash(episodioAntigo.getNome()));
                indiceIndiretoNomeEpisodio.create(new ParNomeEpisodioID(novoEpisodio.getNome(), novoEpisodio.getID()));
            }
            if (novoEpisodio.getID() != episodioAntigo.getID()) {
                indiceIndiretoIDSerieIDEpisodio.delete(new ParIDSerieIDEpisodio(episodioAntigo.idSerie, episodioAntigo.getID()));
                indiceIndiretoIDSerieIDEpisodio.create(new ParIDSerieIDEpisodio(novoEpisodio.idSerie, novoEpisodio.id));
            }
            return true;
        }
        return false;
    }
}
