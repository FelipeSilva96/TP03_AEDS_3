# TP03_AEDS_3

Repositório para trabalhos da disciplina Algoritmos e Estruturas de Dados 3

O que o trabalho de vocês faz?

Nessa parte do trabalho, foi incrementado à Pucflix, a funcionalidade de atores. A implementação de relacionamento N:N entre atores e séries de modo seja possível fazer o CRUD completo de atores, além de um relacionamwento entre séries e atores implementado usando arvores B+. 

Participantes:

Felipe Pereira da Silva - Rikerson Antonio Freitas Silva - Maria Eduarda Pinto Martins - Kauan Gabriel Silva Pereira

Descrição das Classes e Principais Métodos:

Serie.java

Representa a entidade Série.

Métodos: toByteArray(), fromByteArray(), getID(), setID().

Episodio.java

Representa a entidade Episódio.

Métodos: toByteArray(), fromByteArray(), getID(), setID(), getSerieID(), setSerieID().

Ator.java

Representa a entidade Ator.

Métodos: toByteArray(), fromByteArray(), getID(), setID(), getSerieID().

CRUD.java

Classe genérica para operações CRUD sobre arquivos.

Métodos principais: create(), read(), update(), delete().

HashExtensivel.java

Implementa a Tabela Hash Extensível usada para indexação direta (ID de séries e episódios).

Métodos principais: create(), read(), update(), delete().

ArvoreBMais.java

Implementa a Árvore B+ usada para indexação indireta (séries → episódios). (Também há arvore B+ para implementação do relacionamento Séries - Atores e Atores - Séries).

Métodos principais: create(), read(), delete(), listarPorChave().

Principal.java

Interface principal para interação com o sistema.

Contém o menu e chamadas para as funcionalidades CRUD das entidades.

Relato da Experiência:

O desenvolvimento deste TP foi desafiador, porém recompensador. Implementamos todos os requisitos especificados, o que exigiu estudo aprofundado sobre estruturas de dados como Hash Extensível e Árvores B+. Uma das partes mais complexas foi a integração da árvore B+ com a entidade Episódio, garantindo que a navegação por episódios de uma série fosse eficiente e correta.

Também foi desafiador manter a integridade entre os dados — como impedir que uma série com episódios fosse removida, ou que episódios fossem adicionados sem uma série correspondente. Esses aspectos exigiram validações específicas durante as operações CRUD.

Conseguimos alcançar todos os objetivos propostos, e o trabalho está funcionando corretamente. A implementação foi feita com foco na clareza e eficiência, e nos esforçamos para manter o código organizado e bem documentado.

As operações de inclusão, busca, alteração e exclusão de atores estão implementadas e funcionando corretamente? Sim

O relacionamento entre séries e atores foi implementado com árvores B+ e funciona corretamente, assegurando a consistência entre as duas entidades? Sim

É possível consultar quais são os atores de uma série? Sim

É posssível consultar quais são as séries de um ator? Sim

A remoção de séries remove os seus vínculos de atores? Sim

A inclusão de um ator em uma série em um episódio se limita aos atores existentes? Sim

A remoção de um ator checa se há alguma série vinculado a ele? Sim

O trabalho está funcionando corretamente? Sim

O trabalho está completo? Sim

O trabalho é original e não a cópia de um trabalho de outro grupo? Sim

Como rodar:

Clicar duas vezes em build.bat

ou

no terminal

\TP03_AEDS3>
build.bat

