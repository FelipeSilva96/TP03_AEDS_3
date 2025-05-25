
import aed3.*;
import java.util.*;

public class ArquivoSerie extends Arquivo<Serie> {

    private HashExtensivel<ParPalavraSerieIDFreq> indiceInversoSerie;
    private HashExtensivel<ParNomeSerieID> indiceIndiretoNomeSerie;
    Arquivo<Serie> arqSeries;

    public ArquivoSerie() throws Exception {
        super("series", Serie.class.getConstructor());
        this.indiceIndiretoNomeSerie = new HashExtensivel<>(
                ParNomeSerieID.class.getConstructor(), 4,
                ".\\dados\\series\\indiceNomeSerie.d.db",
                ".\\dados\\series\\indiceNomeSerie.c.db"
        );
        this.indiceInversoSerie = new HashExtensivel<>(
                ParPalavraSerieIDFreq.class.getConstructor(), 4,
                ".\\dados\\series\\indiceInversoSerie.d.db",
                ".\\dados\\series\\indiceInversoSerie.c.db"
        );
    }

    public List<Serie> searchTfIdf(String query) throws Exception {
        List<String> termos = TextoUtils.tokenize(query);
        int N = this.count();
        Map<Integer, Double> scores = new HashMap<>();

        for (String termo : termos) {
            int h = termo.hashCode();
            ParPalavraSerieIDFreq p = indiceInversoSerie.read(h);
            if (p == null) {
                continue;
            }
            double idf = Math.log(N / 1.0);
            double tfidf = p.getFreq() * idf;
            scores.put(p.getSerieId(), tfidf);
        }

        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(scores.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Serie> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : entries) {
            resultado.add(this.read(e.getKey()));
        }
        return resultado;
    }

    @Override
    public int create(Serie se) throws Exception {
        int id = super.create(se);
        Map<String, Integer> freqs = TextoUtils.termFrequencies(se.getNome());
        for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
            ParPalavraSerieIDFreq p = new ParPalavraSerieIDFreq(entry.getKey(), id, entry.getValue());
            indiceInversoSerie.create(p);
        }
        return id;
    }

    @Override
    public boolean update(Serie novo) throws Exception {
        Serie antigo = super.read(novo.getID());
        if (antigo == null) {
            return false;
        }
        Map<String, Integer> oldFreqs = TextoUtils.termFrequencies(antigo.getNome());
        for (Map.Entry<String, Integer> e : oldFreqs.entrySet()) {
            ParPalavraSerieIDFreq p = new ParPalavraSerieIDFreq(e.getKey(), antigo.getID(), e.getValue());
            indiceInversoSerie.delete(p.hashCode());
        }
        boolean ok = super.update(novo);
        if (!ok) {
            return false;
        }
        if (!antigo.getNome().equals(novo.getNome())) {
            indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(antigo.getNome()));
            indiceIndiretoNomeSerie.create(new ParNomeSerieID(novo.getNome(), novo.getID()));
        }
        Map<String, Integer> newFreqs = TextoUtils.termFrequencies(novo.getNome());
        for (Map.Entry<String, Integer> e : newFreqs.entrySet()) {
            ParPalavraSerieIDFreq p = new ParPalavraSerieIDFreq(e.getKey(), novo.getID(), e.getValue());
            indiceInversoSerie.create(p);
        }
        return true;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Serie se = super.read(id);
        if (se == null) {
            return false;
        }
        Map<String, Integer> freqs = TextoUtils.termFrequencies(se.getNome());
        for (Map.Entry<String, Integer> e : freqs.entrySet()) {
            ParPalavraSerieIDFreq p = new ParPalavraSerieIDFreq(e.getKey(), id, e.getValue());
            indiceInversoSerie.delete(p.hashCode());
        }
        indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(se.getNome()));
        return super.delete(id);
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

    public Serie read(String nome) throws Exception {
        ParNomeSerieID pni = indiceIndiretoNomeSerie.read(ParNomeSerieID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public List<Serie> searchByTerm(String termo) throws Exception {
        List<Serie> resultado = new ArrayList<>();
        ParPalavraSerieIDFreq exemplo = new ParPalavraSerieIDFreq(termo, -1, -1);
        ParPalavraSerieIDFreq p = indiceInversoSerie.read(exemplo.hashCode());
        if (p != null) {
            resultado.add(read(p.getSerieId()));
        }
        return resultado;
    }

    public HashExtensivel<ParPalavraSerieIDFreq> getIndiceInverso() {
        return indiceInversoSerie;
    }
}
