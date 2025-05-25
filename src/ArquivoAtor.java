
import aed3.*;
import java.util.*;

public class ArquivoAtor extends Arquivo<Ator> {

    ArquivoSerie arqSerie;
    Arquivo<Ator> arqAtor;

    private HashExtensivel<ParPalavraAtorIDFreq> indiceInversoAtor;

    private HashExtensivel<ParAtorNomeID> indiceIndiretoNomeAtor;
    private ArvoreBMais<ParIDAtorIDSerie> indiceIndiretoAtorIDSerieID;
    private ArvoreBMais<ParIDSerieIDAtor> indiceIndiretoSerieIDAtorID;

    public ArquivoAtor() throws Exception {
        super("atores", Ator.class.getConstructor());
        arqSerie = new ArquivoSerie();
        indiceIndiretoNomeAtor = new HashExtensivel<>(
                ParAtorNomeID.class.getConstructor(), 4,
                ".\\dados\\atores\\indiceNomeAtor.d.db",
                ".\\dados\\atores\\indiceNomeAtor.c.db"
        );
        indiceIndiretoAtorIDSerieID = new ArvoreBMais<>(
                ParIDAtorIDSerie.class.getConstructor(), 5,
                ".\\dados\\atores\\indiceIndiretoAtorIDSerieID.db"
        );
        indiceIndiretoSerieIDAtorID = new ArvoreBMais<>(
                ParIDSerieIDAtor.class.getConstructor(), 5,
                ".\\dados\\atores\\indiceIndiretoSerieIDAtorID.db"
        );
        indiceInversoAtor = new HashExtensivel<>(
                ParPalavraAtorIDFreq.class.getConstructor(), 4,
                ".\\dados\\atores\\indiceInversoAtor.d.db",
                ".\\dados\\atores\\indiceInversoAtor.c.db"
        );
    }

    public List<Ator> searchTfIdf(String query) throws Exception {
        List<String> termos = TextoUtils.tokenize(query);
        int N = this.count();
        Map<Integer, Double> scores = new HashMap<>();

        for (String termo : termos) {
            int h = termo.hashCode();
            ParPalavraAtorIDFreq p = indiceInversoAtor.read(h);
            if (p == null) {
                continue;
            }
            double idf = Math.log(N / 1.0); 
            double tfidf = p.getFreq() * idf;
            scores.put(p.getAtorId(), tfidf);
        }

        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(scores.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Ator> resultado = new ArrayList<>();
        for (var e : entries) {
            resultado.add(this.read(e.getKey()));
        }
        return resultado;
    }

    @Override
    public int create(Ator at) throws Exception {

        int id = super.create(at);
        ParAtorNomeID pneid = new ParAtorNomeID(at.getNome(), id);
        indiceIndiretoNomeAtor.create(pneid);
        Map<String, Integer> freqs = TextoUtils.termFrequencies(at.getNome());
        for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
            ParPalavraAtorIDFreq p = new ParPalavraAtorIDFreq(entry.getKey(), id, entry.getValue());
            indiceInversoAtor.create(p);
        }
        return id;
    }

    @Override
    public boolean update(Ator novo) throws Exception {
        Ator antigo = super.read(novo.getID());
        if (antigo == null) {
            return false;
        }
        if (!antigo.getNome().equals(novo.getNome())) {
            indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(antigo.getNome()));
            indiceIndiretoNomeAtor.create(new ParAtorNomeID(novo.getNome(), novo.getID()));
        }
        Map<String, Integer> oldFreqs = TextoUtils.termFrequencies(antigo.getNome());
        for (Map.Entry<String, Integer> e : oldFreqs.entrySet()) {
            String palavra = e.getKey();
            int h = palavra.hashCode();
            indiceInversoAtor.delete(h);
        }
        boolean ok = super.update(novo);
        if (!ok) {
            return false;
        }
        Map<String, Integer> newFreqs = TextoUtils.termFrequencies(novo.getNome());
        for (Map.Entry<String, Integer> e : newFreqs.entrySet()) {
            ParPalavraAtorIDFreq p = new ParPalavraAtorIDFreq(e.getKey(), novo.getID(), e.getValue());
            indiceInversoAtor.create(p);
        }
        return true;
    }

    //   @Override
    public boolean delete(String nome) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nome));
        if (pni != null) {
            if (delete(pni.getId())) {
                return indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(nome));
            }
        }
        return false;

    }

    public boolean delete(int id) throws Exception {
        Ator at = super.read(id);
        if (at == null) {
            return false;
        }
        indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(at.getNome()));
        Map<String, Integer> freqs = TextoUtils.termFrequencies(at.getNome());
        for (Map.Entry<String, Integer> e : freqs.entrySet()) {
            String palavra = e.getKey();
            int h = palavra.hashCode();
            indiceInversoAtor.delete(h);
        }
        List<ParIDAtorIDSerie> links = indiceIndiretoAtorIDSerieID.read(new ParIDAtorIDSerie(id, -1));
        for (ParIDAtorIDSerie link : links) {
            indiceIndiretoAtorIDSerieID.delete(link);
            indiceIndiretoSerieIDAtorID.delete(new ParIDSerieIDAtor(link.getIDSerie(), id));
        }
        return super.delete(id);
    }

    public Ator readNome(String nome) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public List<Serie> readSeries(String nomeAtor) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nomeAtor));
        if (pni == null) {
            return null;
        }
        List<Serie> resultado = new ArrayList<>();
        List<ParIDAtorIDSerie> links = indiceIndiretoAtorIDSerieID.read(new ParIDAtorIDSerie(pni.getId(), -1));
        for (ParIDAtorIDSerie link : links) {
            resultado.add(arqSerie.read(link.getIDSerie()));
        }
        return resultado;
    }

  
    public boolean linkAtorSerie(int ator, int serie) throws Exception {
        boolean success = false;

        ParIDAtorIDSerie piais = new ParIDAtorIDSerie(ator, serie);
        ParIDSerieIDAtor pisia = new ParIDSerieIDAtor(serie, ator);

        if (indiceIndiretoAtorIDSerieID.create(piais) && indiceIndiretoSerieIDAtorID.create(pisia)) {
            success = true;
        }

        return success;
    }

    /*int ator e o id do ator, int serie e o id da serie
     */
    public boolean unlinkAtorSerie(int ator, int serie) throws Exception {
        boolean success = false;

        ParIDAtorIDSerie piais = new ParIDAtorIDSerie(ator, serie);
        ParIDSerieIDAtor pisia = new ParIDSerieIDAtor(serie, ator);

        if (indiceIndiretoAtorIDSerieID.delete(piais) && indiceIndiretoSerieIDAtorID.delete(pisia)) {
            success = true;
        }

        return success;
    }

    /*retorna atores vinculados a uma serie
     */
    public ArrayList<Ator> readAtores(String nomeSerie) throws Exception {
        if (nomeSerie == null) {
            return null;
        }

        ArrayList<Ator> lista_de_atores = new ArrayList<Ator>();
        Ator aux = null;

        //recupera id do nomeSerie
        Serie serie = arqSerie.read(nomeSerie);
        if (serie == null) {
            return null;
        }

        int serieId = serie.getID();

        ArrayList<ParIDSerieIDAtor> linkAtorSeries = indiceIndiretoSerieIDAtorID.read(new ParIDSerieIDAtor(serieId, -1));

        if (linkAtorSeries != null) {
            for (ParIDSerieIDAtor i : linkAtorSeries) {
                aux = this.read(i.getIDActor());
                lista_de_atores.add(aux);
            }

        }

        return lista_de_atores;
    }

    public HashExtensivel<ParPalavraAtorIDFreq> getIndiceInverso() {
        return indiceInversoAtor;
    }
}
