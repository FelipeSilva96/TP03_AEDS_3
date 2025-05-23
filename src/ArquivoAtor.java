import java.util.ArrayList;

import aed3.*;

public class ArquivoAtor extends Arquivo<Ator>{
    ArquivoSerie arqSerie;
    Arquivo<Ator> arqAtor;
    HashExtensivel<ParAtorNomeID> indiceIndiretoNomeAtor;
    ArvoreBMais<ParIDAtorIDSerie> indiceIndiretoAtorIDSerieID;
    ArvoreBMais<ParIDSerieIDAtor> indiceIndiretoSerieIDAtorID;

    public ArquivoAtor() throws Exception {
        super("atores", Ator.class.getConstructor());
        arqSerie = new ArquivoSerie();

        indiceIndiretoNomeAtor = new HashExtensivel<>(
                ParAtorNomeID.class.getConstructor(),
                4,
                ".\\dados\\atores\\indiceNomeAtor.d.db",
                ".\\dados\\atores\\indiceNomeAtor.c.db"
        );

        indiceIndiretoAtorIDSerieID = new ArvoreBMais<>(
            ParIDAtorIDSerie.class.getConstructor(),
            5,
            ".\\dados\\atores\\indiceIndiretoAtorIDSerieID.db"
        );

        indiceIndiretoSerieIDAtorID = new ArvoreBMais<>(
            ParIDSerieIDAtor.class.getConstructor(),
            5,
            ".\\dados\\atores\\indiceIndiretoSerieIDAtorID.db"
        );

    }
    
    @Override
    public int create(Ator ep) throws Exception {
        int id = super.create(ep);
        String atorNome = ep.getNome();
        ParAtorNomeID pneid = new ParAtorNomeID(atorNome, id);
        indiceIndiretoNomeAtor.create(pneid);
        return id;
    }

    //int ator e o id do ator, int serie e o id da serie
    public boolean linkAtorSerie(int ator, int serie) throws Exception{
        boolean success=false;

        ParIDAtorIDSerie piais = new ParIDAtorIDSerie(ator, serie);
        ParIDSerieIDAtor pisia = new ParIDSerieIDAtor(serie, ator);

        if(indiceIndiretoAtorIDSerieID.create(piais) && indiceIndiretoSerieIDAtorID.create(pisia)){
            success = true;
        }

        return success;
    }
    
    /*int ator e o id do ator, int serie e o id da serie
    */
    public boolean unlinkAtorSerie(int ator, int serie) throws Exception{
        boolean success=false;

        ParIDAtorIDSerie piais = new ParIDAtorIDSerie(ator, serie);
        ParIDSerieIDAtor pisia = new ParIDSerieIDAtor(serie, ator);

        if(indiceIndiretoAtorIDSerieID.delete(piais) && indiceIndiretoSerieIDAtorID.delete(pisia)){
            success = true;
        }

        return success;
    }

    public Ator readNome(String nome) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nome));
        if (pni == null) {
            return null;
        }
        return read(pni.getId());
    }

    /*retorna series vinculadas a um ator
    */
    public ArrayList<Serie> readSeries(String nomeAtor) throws Exception {
        if (nomeAtor == null) {
            return null;
        }

        ArrayList<Serie> lista_de_series = new ArrayList<Serie>();
        Serie aux=null;

        //recupera id ligado ao nome do ator
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nomeAtor));

        //recupera ids das series ligados ao id do ator
        ArrayList<ParIDAtorIDSerie> linkAtorSeries = indiceIndiretoAtorIDSerieID.read(new ParIDAtorIDSerie(pni.getId(), -1));

        //monta lista com as series vinculadas ao ator
        for (ParIDAtorIDSerie i: linkAtorSeries) {

            //recupera objeto serie usando id da serie
            aux = new Serie(arqSerie.read(i.getIDSerie()));
            //insere na lista
            lista_de_series.add(aux);
        }
        
        return lista_de_series;

    }

    /*retorna atores vinculados a uma serie
    */
    public ArrayList<Ator> readAtores(String nomeSerie) throws Exception {
        if (nomeSerie == null) {
            return null;
        }

        ArrayList<Ator> lista_de_atores = new ArrayList<Ator>();
        Ator aux=null;

        //recupera id do nomeSerie
        ParNomeSerieID pni = arqSerie.indiceIndiretoNomeSerie.read(ParAtorNomeID.hash(nomeSerie));

        if(pni!=null){
            //recupera id dos atores vinculados ao id da serie
            ArrayList<ParIDSerieIDAtor> linkAtorSeries = indiceIndiretoSerieIDAtorID.read(new ParIDSerieIDAtor(pni.getId(), -1));
            //System.out.println("\nDEBUG: retornados: "+linkAtorSeries.size()+"\n");

            if(linkAtorSeries!=null)
            {
                //monta lista com os atores vinculados a serie
                for (ParIDSerieIDAtor i: linkAtorSeries) {
                    System.out.println("\ninserido na lista\n");
                    //recupera objeto Ator usando id do ator
                    aux = new Ator(read(i.getIDActor()));
                    //insere na lista
                    lista_de_atores.add(aux);
                }
            }
        }
        
        return lista_de_atores;
    }

    public boolean delete(String nome) throws Exception {
        ParAtorNomeID pni = indiceIndiretoNomeAtor.read(ParAtorNomeID.hash(nome));
        if(nome!=null){
            if(delete(pni.getId())){
                return indiceIndiretoNomeAtor.delete(ParAtorNomeID.hash(nome));
            }
        }
        
        return false;
    }

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
    
}
