
import aed3.*;

public class ArquivoSerie extends Arquivo<Serie> {

    Arquivo<Serie> arqSeries;
    HashExtensivel<ParNomeSerieID> indiceIndiretoNomeSerie;

    public ArquivoSerie() throws Exception {
        super("series", Serie.class.getConstructor());
        indiceIndiretoNomeSerie = new HashExtensivel<>(
                ParNomeSerieID.class.getConstructor(),
                4,
                ".\\dados\\series\\indiceNomeSerie.d.db", // diretório
                ".\\dados\\series\\indiceNomeSerie.c.db" // cestos 
        );
    }

    @Override
    public int create(Serie se) throws Exception {
        int id = super.create(se);
        indiceIndiretoNomeSerie.create(new ParNomeSerieID(se.getNome(), id));
        return id;
    }

    public Serie read(String nome) throws Exception {
        ParNomeSerieID pni = indiceIndiretoNomeSerie.read(ParNomeSerieID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public boolean delete(String nome) throws Exception {
        ParNomeSerieID pni = indiceIndiretoNomeSerie.read(ParNomeSerieID.hash(nome));
        if (pni != null) {
            if (delete(pni.getId())) {
                return indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(nome));
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Serie se = super.read(id);
        if (se != null) {
            if (super.delete(id)) {
                return indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(se.getNome()));
            }
        }
        return false;
    }

    @Override
    public boolean update(Serie novaSerie) throws Exception {
        Serie serieAntiga = read(novaSerie.getNome());
        if (super.update(novaSerie)) {
            if (novaSerie.getNome().compareTo(serieAntiga.getNome()) != 0) {
                indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(serieAntiga.getNome()));
                indiceIndiretoNomeSerie.create(new ParNomeSerieID(novaSerie.getNome(), novaSerie.getID()));
            }
            return true;
        }
        return false;
    }

    public boolean update(Serie novaSerie, String nome) throws Exception {
        Serie serieAntiga = read(nome);
        if (super.update(novaSerie)) {
            if (novaSerie.getNome().compareTo(serieAntiga.getNome()) != 0) {
                indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(serieAntiga.getNome()));
                indiceIndiretoNomeSerie.create(new ParNomeSerieID(novaSerie.getNome(), novaSerie.getID()));
            }
            return true;
        }
        return false;
    }
}
