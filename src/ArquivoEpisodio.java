import aed3.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Comparator;


public class ArquivoEpisodio extends Arquivo<Episodio> {

    ArquivoSerie arqSerie;
    HashExtensivel<ParNomeEpisodioID> indiceIndiretoNomeEpisodio;
    ArvoreBMais<ParIDSerieIDEpisodio> indiceIndiretoIDSerieIDEpisodio;
    private ListaInvertida indiceInversoEpisodio;

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
        indiceInversoEpisodio = new ListaInvertida(4,
               ".\\dados\\episodios\\indiceInversoEpisodioNome.d.db",
               ".\\dados\\episodios\\indiceInversoEpisodioNome.c.db");
    }

    @Override
    public int create(Episodio ep) throws Exception {
        int id = super.create(ep);
        String episodioNome = ep.getNome();
        ParNomeEpisodioID pneid = new ParNomeEpisodioID(episodioNome, id);
        indiceIndiretoNomeEpisodio.create(pneid);
        indiceIndiretoIDSerieIDEpisodio.create(new ParIDSerieIDEpisodio(ep.idSerie, ep.id));

        Map<String, Double> freqs = TextoUtils.termFrequencies(episodioNome);
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            ElementoLista p = new ElementoLista(id, (float)(double)entry.getValue());
            indiceInversoEpisodio.create(entry.getKey() ,p);
        }
        indiceInversoEpisodio.incrementaEntidades();
        return id;
    }

    public List<Episodio> readNome(String nomeQuery) throws Exception {
        List<Episodio> episodios = new ArrayList<>();
        List<ElementoLista> lista = this.searchByWords(nomeQuery);

        if(lista == null || lista.isEmpty()){
            Episodio epPorNomeExato = readByExactName(nomeQuery);
            if (epPorNomeExato != null) {
                episodios.add(epPorNomeExato);
            }
            return episodios.isEmpty() ? null : episodios;
        }

        for(ElementoLista item : lista){
            Episodio e = read(item.getId());
            if (e != null) {
                episodios.add(e);
            }
        }
        return episodios.isEmpty() ? null : episodios;
    }
    
    private Episodio readByExactName(String nome) throws Exception {
        ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    public ArrayList<Episodio> readSerie(String nomeSerie) throws Exception {
        if (nomeSerie == null) {
            return null;
        }
        List<Serie> seriesEncontradas = arqSerie.readNome(nomeSerie);
        Serie serie;
        if (seriesEncontradas == null || seriesEncontradas.isEmpty()) {
            return null; 
        }
        serie = seriesEncontradas.get(0);

        int serieID = serie.id;
        ParIDSerieIDEpisodio key = new ParIDSerieIDEpisodio(serieID, -1);
        ArrayList<ParIDSerieIDEpisodio> pse = indiceIndiretoIDSerieIDEpisodio.read(key);
        ArrayList<Episodio> episodios = new ArrayList<>();

        if (pse != null && !pse.isEmpty()) {
            for (ParIDSerieIDEpisodio par : pse) {
                if (par != null) {
                    Episodio ep = read(par.getIDEpisodio());
                    if (ep != null) episodios.add(ep);
                } else {
                    System.out.println("ArquivoEpisodio.readSerie: null ParIDSerieIDEpisodio in list");
                }
            }
            return episodios.isEmpty() ? null : episodios;
        } else {
            return null;
        }
    }
    
    public boolean delete(String nomeEpisodio, int IDSerie) throws Exception {
        Episodio epParaExcluir = null;
        List<Episodio> episodiosEncontrados = readNome(nomeEpisodio);

        if (episodiosEncontrados != null) {
            for (Episodio ep : episodiosEncontrados) {
                if (ep.idSerie == IDSerie) {
                    epParaExcluir = ep;
                    break;
                }
            }
        }
        
        if (epParaExcluir == null) {
            ParNomeEpisodioID pni = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nomeEpisodio));
             if (pni != null) {
                 Episodio tempEp = read(pni.getId());
                 if (tempEp != null && tempEp.idSerie == IDSerie) {
                     epParaExcluir = tempEp;
                 }
             }
        }

        if (epParaExcluir == null) {
            return false;
        }
        
        int episodioID = epParaExcluir.getID();

        if (super.delete(episodioID)) {
            indiceIndiretoNomeEpisodio.delete(ParNomeEpisodioID.hash(nomeEpisodio));
            indiceIndiretoIDSerieIDEpisodio.delete(new ParIDSerieIDEpisodio(IDSerie, episodioID));

            Map<String, Double> freqs = TextoUtils.termFrequencies(epParaExcluir.getNome());
            for (Map.Entry<String, Double> entry : freqs.entrySet()) {
                indiceInversoEpisodio.delete(entry.getKey(), episodioID);
            }
            indiceInversoEpisodio.decrementaEntidades();
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Episodio ep = super.read(id);
        if (ep == null) {
            return false;
        }
        return delete(ep.getNome(), ep.idSerie);
    }

    public boolean update(Episodio novoEpisodio, String nomeAntigo) throws Exception {
        Episodio episodioAntigo = null;
        List<Episodio> episodiosAntigosPossiveis = readNome(nomeAntigo); 
        
        if (episodiosAntigosPossiveis != null) {
            for (Episodio ep : episodiosAntigosPossiveis) {
                if (novoEpisodio.getID() != -1 && ep.getID() == novoEpisodio.getID()) {
                     episodioAntigo = ep;
                     break;
                } else if (novoEpisodio.getID() == -1 && ep.idSerie == novoEpisodio.idSerie) { 
                     episodioAntigo = ep;
                     novoEpisodio.setID(ep.getID()); 
                     break;
                }
            }
        }
        
        if (episodioAntigo == null) {
            ParNomeEpisodioID pni_antigo = indiceIndiretoNomeEpisodio.read(ParNomeEpisodioID.hash(nomeAntigo));
            if (pni_antigo != null) {
                Episodio tempEp = read(pni_antigo.getId());
                if (tempEp != null && (novoEpisodio.idSerie == -1 || tempEp.idSerie == novoEpisodio.idSerie)) {
                     episodioAntigo = tempEp;
                     if (novoEpisodio.getID() == -1) novoEpisodio.setID(episodioAntigo.getID());
                }
            }
        }

        if (episodioAntigo == null) {
            return false;
        }
        
        if (novoEpisodio.getID() == -1) novoEpisodio.setID(episodioAntigo.getID());
        else if (novoEpisodio.getID() != episodioAntigo.getID()) return false;


        if (super.update(novoEpisodio)) {
            if (!novoEpisodio.getNome().equals(episodioAntigo.getNome())) {
                indiceIndiretoNomeEpisodio.delete(ParNomeEpisodioID.hash(episodioAntigo.getNome()));
                indiceIndiretoNomeEpisodio.create(new ParNomeEpisodioID(novoEpisodio.getNome(), novoEpisodio.getID()));

                Map<String, Double> oldFreqs = TextoUtils.termFrequencies(episodioAntigo.getNome());
                for (Map.Entry<String, Double> e : oldFreqs.entrySet()) {
                    indiceInversoEpisodio.delete(e.getKey(), episodioAntigo.getID());
                }
                Map<String, Double> newFreqs = TextoUtils.termFrequencies(novoEpisodio.getNome());
                for (Map.Entry<String, Double> e : newFreqs.entrySet()) {
                    ElementoLista p = new ElementoLista(novoEpisodio.getID(), (float)(double)e.getValue());
                    indiceInversoEpisodio.create(e.getKey(), p);
                }
            }

            if (novoEpisodio.idSerie != episodioAntigo.idSerie) {
                indiceIndiretoIDSerieIDEpisodio.delete(new ParIDSerieIDEpisodio(episodioAntigo.idSerie, episodioAntigo.getID()));
                indiceIndiretoIDSerieIDEpisodio.create(new ParIDSerieIDEpisodio(novoEpisodio.idSerie, novoEpisodio.id));
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean update(Episodio novoEpisodio) throws Exception {
        Episodio episodioAntigo = super.read(novoEpisodio.getID());
        if (episodioAntigo == null) {
            return false;
        }
        return update(novoEpisodio, episodioAntigo.getNome());
    }

    private int ElementoListaListPos(List<ElementoLista> list, ElementoLista ele){
        int i=0;
        int res = -1;
        if (ele == null || list == null) return -1;
        while(i<list.size() && res==-1){
             if(list.get(i) != null && ele.getId() == list.get(i).getId()){
                res = i;
            }
            i++;
        }
        return res;
    }

    private List<ElementoLista> searchByWords(String query) throws Exception {
        List<ElementoLista> resultado = new ArrayList<>();
        ElementoLista[] aux;

        Map<String, Double> freqs = TextoUtils.termFrequencies(query);

        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            aux = indiceInversoEpisodio.read(entry.getKey());
            if(aux == null || aux.length == 0 || indiceInversoEpisodio.numeroEntidades() < 1){
                continue; 
            }

            double idf = Math.log((double)indiceInversoEpisodio.numeroEntidades() / aux.length) + 1;
            for(int i=0; i<aux.length; i++){
                 if (aux[i] != null) {
                    aux[i].setFrequencia((float)idf * aux[i].getFrequencia());
                 }
            }

            if(resultado.isEmpty()){
                for(ElementoLista item : aux){
                    if (item != null) resultado.add(item.clone());
                }
            } else {
                for(ElementoLista item : aux){
                    if (item == null) continue;
                    int termo_pos = ElementoListaListPos(resultado, item);

                    if(termo_pos > -1){
                        if (resultado.get(termo_pos) != null) {
                            resultado.get(termo_pos).setFrequencia(
                                resultado.get(termo_pos).getFrequencia() + item.getFrequencia()
                            );
                        }
                    } else {
                        resultado.add(item.clone());
                    }
                }
            }
        }
        if (resultado.isEmpty()) return null;
        resultado.sort(Comparator.comparing(ElementoLista::getFrequencia).reversed());
        return resultado;
    }
}