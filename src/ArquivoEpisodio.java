
import aed3.*;
import java.util.*;

public class ArquivoEpisodio extends Arquivo<Episodio> {

    private HashExtensivel<ParPalavraEpisodioIDFreq> indiceInversoEpisodio;
    private HashExtensivel<ParNomeEpisodioID> indiceIndiretoNomeEpisodio;
    private ArvoreBMais<ParIDSerieIDEpisodio> indiceIndiretoIDSerieIDEpisodio;
    private ArquivoSerie arqSerie;
    private Arquivo<Episodio> arqEpisodios;

    public ArquivoEpisodio() throws Exception {
        super("episodios", Episodio.class.getConstructor());
        arqSerie = new ArquivoSerie();
        indiceIndiretoNomeEpisodio = new HashExtensivel<>(
                ParNomeEpisodioID.class.getConstructor(), 4,
                ".\\dados\\episodios\\indiceNomeEpisodio.d.db",
                ".\\dados\\episodios\\indiceNomeEpisodio.c.db"
        );
        indiceIndiretoIDSerieIDEpisodio = new ArvoreBMais<>(
                ParIDSerieIDEpisodio.class.getConstructor(), 5,
                ".\\dados\\episodios\\indiceIndiretoIDSerieIDEpisodio.db"
        );
        indiceInversoEpisodio = new HashExtensivel<>(
                ParPalavraEpisodioIDFreq.class.getConstructor(), 4,
                ".\\dados\\episodios\\indiceInversoEpisodio.d.db",
                ".\\dados\\episodios\\indiceInversoEpisodio.c.db"
        );
    }

    public List<Episodio> searchTfIdf(String query) throws Exception {
        List<String> termos = TextoUtils.tokenize(query);
        int N = this.count();
        Map<Integer, Double> scores = new HashMap<>();

        for (String termo : termos) {
            int h = termo.hashCode();
            ParPalavraEpisodioIDFreq p = indiceInversoEpisodio.read(h);
            if (p == null) {
                continue;
            }
            double idf = Math.log(N / 1.0);
            double tfidf = p.getFreq() * idf;
            scores.put(p.getEpisodioId(), tfidf);
        }

        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(scores.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Episodio> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : entries) {
            resultado.add(this.read(e.getKey()));
        }
        return resultado;
    }

    @Override
    public int create(Episodio ep) throws Exception {
        int id = super.create(ep);
        Map<String, Integer> freqs = TextoUtils.termFrequencies(ep.getNome());
        for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
            ParPalavraEpisodioIDFreq p = new ParPalavraEpisodioIDFreq(entry.getKey(), id, entry.getValue());
            indiceInversoEpisodio.create(p);
        }
        return id;
    }

    @Override
    public boolean update(Episodio novo) throws Exception {
        Episodio antigo = super.read(novo.getID());
        if (antigo == null) {
            return false;
        }
        Map<String, Integer> oldFreqs = TextoUtils.termFrequencies(antigo.getNome());
        for (Map.Entry<String, Integer> e : oldFreqs.entrySet()) {
            ParPalavraEpisodioIDFreq p = new ParPalavraEpisodioIDFreq(e.getKey(), antigo.getID(), e.getValue());
            indiceInversoEpisodio.delete(p.hashCode());
        }
        boolean ok = super.update(novo);
        if (!ok) {
            return false;
        }
        Map<String, Integer> newFreqs = TextoUtils.termFrequencies(novo.getNome());
        for (Map.Entry<String, Integer> e : newFreqs.entrySet()) {
            ParPalavraEpisodioIDFreq p = new ParPalavraEpisodioIDFreq(e.getKey(), novo.getID(), e.getValue());
            indiceInversoEpisodio.create(p);
        }
        return true;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Episodio ep = super.read(id);
        if (ep == null) {
            return false;
        }

        indiceIndiretoNomeEpisodio.delete(ParNomeEpisodioID.hash(ep.getNome()));
        indiceIndiretoIDSerieIDEpisodio.delete(new ParIDSerieIDEpisodio(ep.getIdSerie(), id));
        return super.delete(id);
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

    public Episodio read(String nome) throws Exception {
        ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public Episodio readNome(String nome) throws Exception {
        ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public List<Episodio> readSerie(String nome) throws Exception {
        int serieID = arqSerie.read(nome).getID();
        List<ParIDSerieIDEpisodio> links = indiceIndiretoIDSerieIDEpisodio.read(new ParIDSerieIDEpisodio(serieID, -1));
        List<Episodio> resultado = new ArrayList<>();
        for (ParIDSerieIDEpisodio link : links) {
            resultado.add(read(link.getIDEpisodio()));
        }
        return resultado;
    }

    public List<Episodio> searchByTerm(String termo) throws Exception {
        List<Episodio> resultado = new ArrayList<>();
        ParPalavraEpisodioIDFreq exemplo = new ParPalavraEpisodioIDFreq(termo, -1, -1);
        ParPalavraEpisodioIDFreq p = indiceInversoEpisodio.read(exemplo.hashCode());
        if (p != null) {
            resultado.add(read(p.getEpisodioId()));
        }
        return resultado;
    }


    /*
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
     */
    public HashExtensivel<ParPalavraEpisodioIDFreq> getIndiceInverso() {
        return indiceInversoEpisodio;
    }
}
