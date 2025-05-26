# TP03_AEDS_3 - Pucflix: Gestão de Séries, Episódios e Atores com Busca por Palavras

### O que o trabalho de vocês faz?

Este trabalho consiste na evolução do sistema Pucflix, uma plataforma para gerenciamento de séries e episódios. Nesta terceira etapa, o foco foi aprimorar a busca por informações e expandir o gerenciamento de entidades, introduzindo a funcionalidade completa para "Atores".

### As principais implementações incluem:

CRUD Completo para Atores: Foi desenvolvido o cadastro completo (Criar, Ler, Atualizar e Excluir) para a entidade Ator, permitindo o gerenciamento de informações sobre os artistas.

Relacionamento N:N entre Atores e Séries: Implementou-se um relacionamento muitos-para-muitos entre Atores e Séries utilizando Árvores B+ para indexação. Isso permite associar múltiplos atores a uma série e, reciprocamente, listar todas as séries em que um ator participou.

Índices Invertidos para Busca por Palavras: Foram criados índices invertidos, utilizando a classe ListaInvertida, para os termos presentes nos títulos/nomes de Séries, Episódios e Atores. Esta funcionalidade permite que os usuários realizem buscas por palavras-chave, encontrando registros mesmo que não saibam o nome completo, com resultados ordenados por relevância (TF-IDF).

O objetivo é oferecer uma ferramenta mais robusta e flexível para o gerenciamento e consulta de dados no Pucflix, melhorando a experiência do usuário ao buscar informações.

### Participantes:

##### Felipe Pereira da Silva

##### Rikerson Antonio Freitas Silva

##### Maria Eduarda Pinto Martins

##### Kauan Gabriel Silva Pereira

### Descrição das Classes Criadas e Seus Métodos Principais:

A seguir, são detalhadas as classes desenvolvidas e/ou modificadas significativamente para este trabalho, agrupadas por funcionalidade.

### Classes de Entidade:
As classes de entidade representam os objetos de dados principais do sistema.

#### Serie.java

Descrição: Representa a entidade Série na Pucflix, contendo informações como título, ano de lançamento, sinopse e plataforma de streaming.

Atributos Principais: id (int), nome (String), anoLancamento (short), sinopse (String), streaming (String).

Métodos Principais:

toByteArray(): Serializa o objeto Serie para um array de bytes, permitindo seu armazenamento em arquivo.

fromByteArray(byte[] ba): Desserializa um array de bytes para um objeto Serie.

setID(int id): Define o identificador único da série.

getID(): Retorna o identificador único da série.

getNome(): Retorna o nome da série.

Construtores para diversas formas de inicialização do objeto.

#### Episodio.java

Descrição: Representa a entidade Episódio, que está sempre vinculada a uma Série. Contém informações como nome, temporada, data de lançamento e duração.

Atributos Principais: id (int), nome (String), temporada (int), dataLancamento (LocalDate), duracao (int), idSerie (int).

Métodos Principais:

toByteArray(): Serializa o objeto Episodio para um array de bytes.

fromByteArray(byte[] ba): Desserializa um array de bytes para um objeto Episodio.

setID(int id): Define o identificador único do episódio.

getID(): Retorna o identificador único do episódio.

getNome(): Retorna o nome do episódio.

mostraEpisodio(): Imprime os detalhes do episódio formatados no console.

#### Ator.java

Descrição: Representa a entidade Ator, contendo informações sobre os artistas que participam das séries.

Atributos Principais: id (int), nome (String).

Métodos Principais:

toByteArray(): Serializa o objeto Ator para um array de bytes.

fromByteArray(byte[] ba): Desserializa um array de bytes para um objeto Ator.

setID(int id): Define o identificador único do ator.

getID(): Retorna o identificador único do ator.

getNome(): Retorna o nome do ator.

Classes de ArquivoEntidade

Estas classes são responsáveis pela persistência dos dados em arquivo e pela criação e manutenção das estruturas de indexação.

#### ArquivoSerie.java 

Descrição: Especialização da classe Arquivo para gerenciar entidades do tipo Serie. Além do índice direto herdado, implementa um índice indireto por nome exato (indiceIndiretoNomeSerie usando HashExtensivel) e um índice invertido (indiceInversoSerie usando ListaInvertida) para busca por palavras no nome da série.

Métodos Principais (além dos herdados e adaptados):

create(Serie se): Cria a série e atualiza o índice direto, o índice de nome exato e o índice invertido com os termos do nome.

readNome(String nomeQuery): Busca séries. Prioriza a busca por palavras usando o índice invertido (com TF-IDF implícito na ordenação dos resultados pela ListaInvertida se esta suportar, ou pela lógica de searchByWords). Se não encontrar, tenta por nome exato. Retorna uma List<Serie>.

readByExactName(String nome): Busca uma série unicamente pelo nome exato.

delete(String nome) / delete(int id): Exclui a série e remove suas referências de todos os índices associados.

update(Serie novaSerie) / update(Serie novaSerie, String nomeAntigoExato): Atualiza os dados da série e todos os seus índices.

searchByWords(String query): Método privado auxiliar que utiliza a ListaInvertida para buscar IDs de séries baseados em termos da query, aplicando lógica de relevância (TF-IDF).

#### ArquivoEpisodio.java

Descrição: Especialização da classe Arquivo para gerenciar entidades do tipo Episodio. Utiliza um índice indireto por nome exato (indiceIndiretoNomeEpisodio usando HashExtensivel), uma Árvore B+ (indiceIndiretoIDSerieIDEpisodio) para relacionar IDs de séries com IDs de episódios, e um índice invertido (indiceInversoEpisodio usando ListaInvertida) para busca por palavras no nome do episódio.

Métodos Principais (além dos herdados e adaptados):

create(Episodio ep): Cria o episódio e atualiza todos os seus índices.

readNome(String nomeQuery): Busca episódios por palavras do nome. Retorna List<Episodio>.

readByExactName(String nome): Busca um episódio pelo nome exato.

readSerie(String nomeSerie): Retorna uma lista de todos os episódios pertencentes a uma série específica (identificada pelo nome).

delete(String nomeEpisodio, int IDSerie) / delete(int id): Exclui o episódio e suas referências nos índices.

update(Episodio novoEpisodio, String nomeAntigo) / update(Episodio novoEpisodio): Atualiza os dados do episódio e seus índices.

searchByWords(String query): Método privado auxiliar para busca por palavras na ListaInvertida.

#### ArquivoAtor.java 

Descrição: Especialização da classe Arquivo para gerenciar entidades do tipo Ator. Implementa um índice indireto por nome exato (indiceIndiretoNomeAtor usando HashExtensivel), duas Árvores B+ para o relacionamento N:N com Séries (indiceIndiretoAtorIDSerieID e indiceIndiretoSerieIDAtorID), e um índice invertido (indiceInversoAtor usando ListaInvertida) para busca por palavras no nome do ator.

Métodos Principais (além dos herdados e adaptados):

create(Ator at): Cria o ator e atualiza todos os seus índices.

readNome(String nome): Busca atores por palavras do nome. Retorna List<Ator>.

readSeries(String nomeAtor): Lista todas as séries em que um ator (identificado pelo nome) participou.

readAtores(String nomeSerie): Lista todos os atores que participaram de uma série (identificada pelo nome).

delete(String nome) / delete(int id): Exclui o ator, desfaz seus vínculos com séries e remove suas referências dos índices.

update(Ator novo): Atualiza os dados do ator e seus índices.

linkAtorSerie(int idAtor, int idSerie): Cria o vínculo entre um ator e uma série nas Árvores B+ correspondentes.

unlinkAtorSerie(int idAtor, int idSerie): Remove o vínculo entre um ator e uma série.

searchByWords(String query): Método privado auxiliar para busca por palavras na ListaInvertida, aplicando lógica de relevância (TF-IDF) para ordenação.

Estruturas de Dados Fundamentais (Pacote aed3)
Estas classes foram fornecidas como base ou implementadas para suportar as funcionalidades de indexação e armazenamento.


### Classes de Registros para Estruturas de Dados

Estas classes servem como invólucros para os dados que são armazenados nas estruturas de indexação (HashExtensivel, ArvoreBMais, ListaInvertida).

ParNomeSerieID.java: Armazena (hash(nomeSerie), idSerie) para o índice indireto de nome em ArquivoSerie.

ParNomeEpisodioID.java (src): Armazena (hash(nomeEpisodio), idEpisodio) para o índice indireto de nome em ArquivoEpisodio.

ParAtorNomeID.java (src): Armazena (hash(nomeAtor), idAtor) para o índice indireto de nome em ArquivoAtor.

ParIDSerieIDEpisodio.java (src): Armazena (IDSerie, IDEpisodio) para a Árvore B+ em ArquivoEpisodio.

ParIDAtorIDSerie.java (src): Armazena (IDAtor, IDSerie) para uma das Árvores B+ em ArquivoAtor.

ParIDSerieIDAtor.java (src): Armazena (IDSerie, IDAtor) para a outra Árvore B+ em ArquivoAtor.

ElementoLista.java (aed3): Armazena (idDocumento, frequenciaDoTermo) para a ListaInvertida.

### Classes de Interface com Usuário
Estas classes são responsáveis pela interação com o usuário via console.

#### Principal.java

Descrição: Ponto de entrada da aplicação. Apresenta o menu principal ao usuário, permitindo a navegação para os módulos de Séries, Episódios ou Atores.

Métodos Principais: main(String[] args).

#### MenuSeries.java

Descrição: Gerencia as interações do usuário relacionadas a Séries. Permite buscar, incluir, alterar, excluir séries, listar episódios por temporada ou todos os episódios de uma série, e gerenciar o vínculo de atores com séries.

Métodos Principais: menu(), buscarSerie(), incluirSerie(), alterarSerie(), excluirSerie(), buscarTemporada(), buscarEpisodios(), vincularAtores(), desvincularAtores(), vinculoSerie() (mostrar atores de uma série).

#### MenuEpisodios.java

Descrição: Gerencia as interações do usuário relacionadas a Episódios. Permite buscar, incluir, alterar e excluir episódios.

Métodos Principais: menu(), buscarEpisodio(), incluirEpisodio(), alterarEpisodio(), excluirEpisodio().

#### MenuAtor.java

Descrição: Gerencia as interações do usuário relacionadas a Atores. Permite buscar, incluir, alterar, excluir atores e visualizar as séries em que um ator participou.

Métodos Principais: menu(), buscarAtor(), incluirAtor(), alterarAtor(), excluirAtor(), vinculoAtor() (mostrar séries de um ator).

## Relato da Experiência 

<br> O desenvolvimento deste trabalho prático representou um desafio significativo, mas também uma grande oportunidade de aprendizado. A implementação de todas as funcionalidades propostas, especialmente a integração dos índices invertidos com as operações de CRUD e a gestão dos relacionamentos N:N, exigiu um planejamento cuidadoso e um entendimento profundo das estruturas de dados envolvidas.

Implementação dos Requisitos: Sim, acreditamos ter implementado todos os requisitos solicitados para esta etapa, incluindo o CRUD completo para atores, o relacionamento N:N entre atores e séries com Árvores B+, e a busca por palavras utilizando listas invertidas para todas as três entidades principais (Séries, Episódios e Atores).

Operações Mais Difíceis:
A sincronização e manutenção da consistência entre os múltiplos índices (direto no arquivo principal, indiretos por nome exato, árvores B+ para relacionamentos e as listas invertidas para termos) durante as operações de update e delete foram particularmente complexas. Por exemplo, ao alterar o nome de uma série, era preciso atualizar o registro no arquivo, o índice de nome exato e remover/adicionar todos os termos relevantes na lista invertida. A exclusão de uma série ou ator também exigia a remoção de todos os seus vínculos nas Árvores B+ e a limpeza de suas entradas em todos os índices.

Desafios na Implementação:
Um desafio considerável foi a implementação da busca por palavras com relevância (TF-IDF). A classe ListaInvertida armazena a frequência do termo (TF), e o cálculo do IDF (Inverse Document Frequency) e a combinação das pontuações para múltiplos termos de uma consulta exigiram uma lógica cuidadosa nos métodos searchByWords das classes ArquivoSerie, ArquivoEpisodio e ArquivoAtor. Garantir que os resultados fossem corretamente ordenados pela relevância combinada foi um processo iterativo de desenvolvimento e teste.
Outro ponto foi a gestão da interface com o usuário nos menus, especialmente quando uma busca por nome poderia retornar múltiplos resultados (devido à busca por palavras). Decidimos, em muitos casos, operar sobre o primeiro resultado mais relevante ou permitir que o usuário escolhesse, para simplificar a interação inicial, mas cientes de que interfaces mais ricas poderiam ser desenvolvidas.

Resultados Alcançados:
Apesar dos desafios, os resultados foram alcançados. O sistema agora permite uma gestão mais completa dos dados da Pucflix, e a funcionalidade de busca por palavras tornou a recuperação de informações muito mais flexível e poderosa. A experiência de aplicar conceitos teóricos de estruturas de dados avançadas em um projeto prático foi extremamente valiosa para solidificar o conhecimento. A depuração de estruturas complexas como Árvores B+ e Listas Invertidas em disco também proporcionou um aprendizado intenso sobre o funcionamento interno dessas estruturas.

### Checklist:
O índice invertido com os termos dos títulos das séries foi criado usando a classe ListaInvertida? Sim

O índice invertido com os termos dos títulos dos episódios foi criado usando a classe ListaInvertida? Sim

O índice invertido com os termos dos nomes dos atores foi criado usando a classe ListaInvertida? Sim

É possível buscar séries por palavras usando o índice invertido? Sim

É possível buscar episódios por palavras usando o índice invertido? Sim

É possível buscar atores por palavras usando o índice invertido? Sim

O trabalho está completo? Sim (Considerando os requisitos especificados para esta etapa do trabalho prático).

O trabalho é original e não a cópia de um trabalho de um colega? Sim

Como Rodar o Projeto
Pré-requisitos:

Java Development Kit (JDK) versão 8 ou superior instalado e configurado nas variáveis de ambiente do sistema.

### Passos para Execução:

Clone ou faça o download deste repositório.

Abra um terminal ou prompt de comando na pasta raiz do projeto (onde se encontra o arquivo build_run.bat).

Execute o script fornecido:

No Windows: build_run.bat

Este script se encarregará de limpar compilações anteriores, compilar todos os arquivos .java dos pacotes src e aed3, e então executar a classe Principal.

Após a execução, o menu interativo da Pucflix será apresentado no console. Siga as opções do menu para interagir com o sistema.

Os arquivos de dados (.db) serão criados e gerenciados dentro de uma pasta dados na raiz do projeto.