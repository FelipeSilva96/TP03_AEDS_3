
/* ArquivoAtor.java */
import aed3.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class ArquivoAtor extends Arquivo<Ator> {

    ArquivoSerie arqSerie;
    Arquivo<Ator> arqAtor;

    private ListaInvertida indiceInversoAtor;

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
        indiceInversoAtor = new ListaInvertida(4, 
               ".\\dados\\atores\\indiceIndiretoIDFreq.d.db", 
               ".\\dados\\atores\\indiceIndiretoIDFreq.c.db");
    }

    @Override
    public int create(Ator at) throws Exception {
        String atorNome = at.getNome();
        int id = super.create(at);

        ParAtorNomeID pneid = new ParAtorNomeID(atorNome, id);
        indiceIndiretoNomeAtor.create(pneid);

        Map<String, Double> freqs = TextoUtils.termFrequencies(atorNome);
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            ElementoLista p = new ElementoLista(id, (float)(double)entry.getValue());
            indiceInversoAtor.create(entry.getKey() ,p);
        }

        indiceInversoAtor.incrementaEntidades();

        return id;
    }

    /*
    public boolean update(Ator novoAtor, String antiga) throws Exception {
        Ator AtorAntigo = readNome(antiga);
        if (super.update(novoAtor)) {
            if (novoAtor.getNome().compareTo(AtorAntigo.getNome()) != 0) {
                indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(AtorAntigo.getNome()));
                indiceIndiretoNomeAtor.create(new ParAtorNomeID(novoAtor.getNome(), novoAtor.getID()));
            }
            return true;
        }
        return false;
    }
     */
    @Override
    public boolean update(Ator novo) throws Exception {
        Ator antigo = super.read(novo.getID());
        if (antigo == null) {
            return false;
        }
        if (!antigo.getNome().equals(novo.getNome())) {
            indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(antigo.getNome()));
            indiceIndiretoNomeAtor.create(new ParAtorNomeID(novo.getNome(), novo.getID()));

            Map<String, Double> oldFreqs = TextoUtils.termFrequencies(antigo.getNome());
            for (Map.Entry<String, Double> e : oldFreqs.entrySet()) {
                indiceInversoAtor.delete(e.getKey(), antigo.id);
            }
        }
        
        boolean ok = super.update(novo);
        if (!ok) {
            return false;
        }

        Map<String, Double> newFreqs = TextoUtils.termFrequencies(novo.getNome());
        for (Map.Entry<String, Double> e : newFreqs.entrySet()) {
            ElementoLista p = new ElementoLista(novo.getID(), (float)(double)e.getValue());
            indiceInversoAtor.create(e.getKey(), p);
        }
        return true;
    }

    public boolean delete(String nome) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nome));
        if (nome != null) {
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

        Map<String, Double> freqs = TextoUtils.termFrequencies(at.nome);
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            indiceInversoAtor.delete(entry.getKey(), at.getID());
        }
        indiceInversoAtor.decrementaEntidades();

        List<ParIDAtorIDSerie> links = indiceIndiretoAtorIDSerieID.read(new ParIDAtorIDSerie(id, -1));
        for (ParIDAtorIDSerie link : links) {
            indiceIndiretoAtorIDSerieID.delete(link);
            indiceIndiretoSerieIDAtorID.delete(new ParIDSerieIDAtor(link.getIDSerie(), id));
        }

        return super.delete(id);
    }

    public List<Ator> readNome(String nome) throws Exception {
        List<Ator> atores = new ArrayList<>();

        List<ElementoLista> lista = this.searchByWords(nome);
        if(lista == null || lista.size()==0){
            return null;
        }

        for(ElementoLista item : lista){
            atores.add(read(item.getId()));
        }

        return atores;
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

    public int elementoListaPosID(ElementoLista[] lista, int id){
        int i =0;

        while (i<lista.length){

            if(lista[i].getId()==id){
                return i;
            }
            i++;
        }

        return -1;
    }

    public List<ElementoLista> searchByWords(String string) throws Exception {
        List<ElementoLista> resultado = new ArrayList<>();
        ElementoLista[] aux = new ElementoLista[50];

        //recupera string transformada em termos
        Map<String, Double> freqs = TextoUtils.termFrequencies(string);

        //itera por todos os termos
        for (Map.Entry<String, Double> entry : freqs.entrySet()) {
            //recupera os id;freq do termo
            aux = indiceInversoAtor.read(entry.getKey());
            //calcular idf
            if(aux.length<1 || indiceInversoAtor.numeroEntidades()<1){
                return null;
            }

            double idf = Math.log((indiceInversoAtor.numeroEntidades()/aux.length))+1;
            //multiplica freq por idf
            for(int i=0; i<aux.length; i++){
                aux[i].setFrequencia((float)idf*aux[i].getFrequencia());
            }

            //adiciona aux ao resultado
            //se resultado estiver vazio, copiar elementos, se nao, testar se ja existem para somar freqs
            if(resultado.size()==0){
                for(int i=0; i<aux.length; i++){
                    resultado.add(aux[i]);
                }
                
            }else{
                for(int i=0; i<aux.length; i++){
                    //indexOf() retorna posicao do id em resultado, se nao houver, retorna -1
                    int termo_pos = ElementoListaListPos(resultado, aux[i]);

                    //se id nao estiver na lista, adiciona, se estiver, soma as freqs
                    if(termo_pos>-1){
                        resultado.get(termo_pos).setFrequencia(
                        resultado.get(termo_pos).getFrequencia() + aux[i].getFrequencia()
                        );
                    }else{
                        resultado.add(aux[i]);
                    }

                }
                
            }

        }

        resultado.sort(Comparator.comparing(ElementoLista::getFrequencia).reversed());

        return resultado;
    }

    public int ElementoListaListPos(List<ElementoLista> list, ElementoLista ele){
        int i=0;
        int res = -1;
        while(i<list.size() && res==-1){
            if(ele.getId() == list.get(i).getId()){
                res = i;
            }
            i++;
        }

        return res;
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
        ParNomeSerieID pni = arqSerie.indiceIndiretoNomeSerie.read(ParAtorNomeID.hash(nomeSerie));

        if (pni != null) {
            ArrayList<ParIDSerieIDAtor> linkAtorSeries = indiceIndiretoSerieIDAtorID.read(new ParIDSerieIDAtor(pni.getId(), -1));

            if (linkAtorSeries != null) {
                for (ParIDSerieIDAtor i : linkAtorSeries) {
                    System.out.println("\ninserido na lista\n");
                    aux = new Ator(read(i.getIDActor()));
                    lista_de_atores.add(aux);
                }
            }
        }

        return lista_de_atores;
    }

    public void printMap(Map<String, Double> map){
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            System.out.println(key + " ; " + value);
        }
    }

    /*
    public HashExtensivel<ParPalavraAtorIDFreq> getIndiceInverso() {
        return indiceInversoAtor;
    }
    */
}
