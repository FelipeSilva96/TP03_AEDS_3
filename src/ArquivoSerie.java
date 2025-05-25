
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
        int N = this.count();  // total de séries
        Map<Integer, Double> scores = new HashMap<>();

        for (String termo : termos) {
            // monta chave para ler no índice invertido
            ParPalavraSerieIDFreq exemplo = new ParPalavraSerieIDFreq(termo, -1, -1);
            List<ParPalavraSerieIDFreq> postings = indiceInversoSerie.read(exemplo);
            int DF = postings.size();
            if (DF == 0) {
                continue;
            }
            double idf = Math.log(N / (double) DF);

            for (ParPalavraSerieIDFreq p : postings) {
                double tfidf = p.getFreq() * idf;
                scores.merge(p.getSerieId(), tfidf, Double::sum);
            }
        }

        // ordena IDs por score e mapeia para objetos
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()))
                .map(e -> {
                    try {
                        return this.read(e.getKey());
                    } catch (Exception ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
            indiceInversoSerie.delete(p);
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

    @Override
    public boolean delete(int id) throws Exception {
        Serie se = super.read(id);
        if (se == null) {
            return false;
        }
        Map<String, Integer> freqs = TextoUtils.termFrequencies(se.getNome());
        for (Map.Entry<String, Integer> e : freqs.entrySet()) {
            ParPalavraSerieIDFreq p = new ParPalavraSerieIDFreq(e.getKey(), id, e.getValue());
            indiceInversoSerie.delete(p);
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
        List<ParPalavraSerieIDFreq> postings = indiceInversoSerie.read(exemplo);
        for (ParPalavraSerieIDFreq p : postings) {
            resultado.add(read(p.getSerieId()));
        }
        return resultado;
    }

    public HashExtensivel<ParPalavraSerieIDFreq> getIndiceInverso() {
        return indiceInversoSerie;
    }
}
