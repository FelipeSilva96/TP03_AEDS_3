import aed3.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class ArquivoSerie extends Arquivo<Serie> {

    HashExtensivel<ParNomeSerieID> indiceIndiretoNomeSerie;
    private ListaInvertida indiceInversoSerie;

    public ArquivoSerie() throws Exception {
        super("series", Serie.class.getConstructor());
        indiceIndiretoNomeSerie = new HashExtensivel<>(
                ParNomeSerieID.class.getConstructor(),
                4,
                ".\\dados\\series\\indiceNomeSerie.d.db",
                ".\\dados\\series\\indiceNomeSerie.c.db"
        );
        indiceInversoSerie = new ListaInvertida(4,
               ".\\dados\\series\\indiceInversoSerieNome.d.db",
               ".\\dados\\series\\indiceInversoSerieNome.c.db");
    }

    @Override
    public int create(Serie se) throws Exception {
        int id = super.create(se);
        indiceIndiretoNomeSerie.create(new ParNomeSerieID(se.getNome(), id));

        String serieNome = se.getNome();
        Map<String, Double> freqs = TextoUtils.termFrequencies(serieNome);
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            ElementoLista p = new ElementoLista(id, (float)(double)entry.getValue());
            indiceInversoSerie.create(entry.getKey() ,p);
        }
        indiceInversoSerie.incrementaEntidades();
        return id;
    }

    public List<Serie> readNome(String nomeQuery) throws Exception {
        List<Serie> series = new ArrayList<>();
        List<ElementoLista> lista = this.searchByWords(nomeQuery);
        if(lista == null || lista.isEmpty()){
            Serie seriePorNomeExato = readByExactName(nomeQuery);
            if (seriePorNomeExato != null) {
                series.add(seriePorNomeExato);
            }
            return series.isEmpty() ? null : series;
        }

        for(ElementoLista item : lista){
            Serie s = read(item.getId());
            if (s != null) {
                series.add(s);
            }
        }
        return series.isEmpty() ? null : series;
    }

    public Serie readByExactName(String nome) throws Exception {
        ParNomeSerieID pni = indiceIndiretoNomeSerie.read(ParNomeSerieID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }


    @Override
    public boolean delete(int id) throws Exception {
        Serie se = super.read(id);
        if (se != null) {
            if (super.delete(id)) {
                indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(se.getNome()));

                Map<String, Double> freqs = TextoUtils.termFrequencies(se.getNome());
                for (Map.Entry<String, Double> entry : freqs.entrySet()) {
                    indiceInversoSerie.delete(entry.getKey(), se.getID());
                }
                indiceInversoSerie.decrementaEntidades();
                return true;
            }
        }
        return false;
    }

    public boolean delete(String nome) throws Exception {
        List<Serie> seriesParaExcluir = readNome(nome);
        if (seriesParaExcluir == null || seriesParaExcluir.isEmpty()) {
             Serie serieExata = readByExactName(nome);
             if (serieExata != null) {
                 return delete(serieExata.getID());
             }
            return false;
        }

        if (!seriesParaExcluir.isEmpty()) {
           
            return delete(seriesParaExcluir.get(0).getID());
        }
        return false;
    }


    @Override
    public boolean update(Serie novaSerie) throws Exception {
        Serie serieAntiga = super.read(novaSerie.getID());
        if (serieAntiga == null) {
            return false;
        }

        boolean ok = super.update(novaSerie);
        if (!ok) {
            return false;
        }

        String nomeAntigo = serieAntiga.getNome();
        String nomeNovo = novaSerie.getNome();

        if (!nomeAntigo.equals(nomeNovo)) {
            Map<String, Double> oldFreqs = TextoUtils.termFrequencies(nomeAntigo);
            for (Map.Entry<String, Double> e : oldFreqs.entrySet()) {
                indiceInversoSerie.delete(e.getKey(), serieAntiga.getID());
            }

            Map<String, Double> newFreqs = TextoUtils.termFrequencies(nomeNovo);
            for (Map.Entry<String, Double> e : newFreqs.entrySet()) {
                ElementoLista p = new ElementoLista(novaSerie.getID(), (float)(double)e.getValue());
                indiceInversoSerie.create(e.getKey(), p);
            }
            
            indiceIndiretoNomeSerie.delete(ParNomeSerieID.hash(serieAntiga.getNome()));
            indiceIndiretoNomeSerie.create(new ParNomeSerieID(novaSerie.getNome(), novaSerie.getID()));
        }
        return true;
    }

    public boolean update(Serie novaSerie, String nomeAntigoExato) throws Exception {
        Serie serieAntiga = readByExactName(nomeAntigoExato);
        if (serieAntiga == null) {
            return false;
        }
        novaSerie.setID(serieAntiga.getID());
        return update(novaSerie);
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
            aux = indiceInversoSerie.read(entry.getKey());
            if(aux == null || aux.length == 0 || indiceInversoSerie.numeroEntidades() < 1){
                continue;
            }

            double idf = Math.log((double)indiceInversoSerie.numeroEntidades() / aux.length) + 1;
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