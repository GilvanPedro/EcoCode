package backend_ecocode.Util;

import Estrutura.Grafo;
import backend_ecocode.Domain.Bairro;

// distâncias em km, de carro no Google Maps ("Bairro, Goiânia", primeira rota)
public class GrafoBairros {

    public static Grafo<Bairro> criar() {
        Grafo<Bairro> grafo = new Grafo<>();

        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_OESTE,               3.6);
        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_LESTE_UNIVERSITARIO, 2.0);
        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_CAMPINAS,            5.8);
        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_BUENO,               5.3);
        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_MARISTA,             4.7);
        grafo.adicionarAresta(Bairro.SETOR_CENTRAL,             Bairro.SETOR_JAO,                 5.6);

        grafo.adicionarAresta(Bairro.SETOR_OESTE,               Bairro.SETOR_CAMPINAS,            4.7);
        grafo.adicionarAresta(Bairro.SETOR_OESTE,               Bairro.SETOR_BUENO,               2.5);
        grafo.adicionarAresta(Bairro.SETOR_OESTE,               Bairro.SETOR_MARISTA,             3.4);

        grafo.adicionarAresta(Bairro.SETOR_LESTE_UNIVERSITARIO, Bairro.JARDIM_NOVO_MUNDO,         4.4);
        grafo.adicionarAresta(Bairro.SETOR_LESTE_UNIVERSITARIO, Bairro.JARDIM_GOIAS,              2.7);
        grafo.adicionarAresta(Bairro.SETOR_LESTE_UNIVERSITARIO, Bairro.SETOR_JAO,                 6.0);

        grafo.adicionarAresta(Bairro.SETOR_MARISTA,             Bairro.SETOR_BUENO,               2.4);
        grafo.adicionarAresta(Bairro.SETOR_MARISTA,             Bairro.JARDIM_GOIAS,              4.6);
        grafo.adicionarAresta(Bairro.SETOR_MARISTA,             Bairro.SETOR_PEDRO_LUDOVICO,      2.4);

        grafo.adicionarAresta(Bairro.SETOR_BUENO,               Bairro.JARDIM_AMERICA,            2.4);
        grafo.adicionarAresta(Bairro.SETOR_BUENO,               Bairro.SETOR_PEDRO_LUDOVICO,      3.1);
        grafo.adicionarAresta(Bairro.SETOR_BUENO,               Bairro.PARQUE_AMAZONIA,           5.5);

        grafo.adicionarAresta(Bairro.SETOR_PEDRO_LUDOVICO,      Bairro.PARQUE_AMAZONIA,           4.8);

        grafo.adicionarAresta(Bairro.JARDIM_GOIAS,              Bairro.SETOR_PEDRO_LUDOVICO,      4.6);
        grafo.adicionarAresta(Bairro.JARDIM_GOIAS,              Bairro.JARDIM_NOVO_MUNDO,         4.6);

        grafo.adicionarAresta(Bairro.JARDIM_AMERICA,            Bairro.PARQUE_AMAZONIA,           4.5);
        grafo.adicionarAresta(Bairro.JARDIM_AMERICA,            Bairro.CIDADE_JARDIM,             5.0);

        grafo.adicionarAresta(Bairro.SETOR_CAMPINAS,            Bairro.CIDADE_JARDIM,             4.7);
        grafo.adicionarAresta(Bairro.SETOR_CAMPINAS,            Bairro.JARDIM_CURITIBA,           12.1);

        grafo.adicionarAresta(Bairro.CIDADE_JARDIM,             Bairro.JARDIM_CURITIBA,           14.2);

        grafo.adicionarAresta(Bairro.SETOR_JAO,                 Bairro.JARDIM_GUANABARA,          5.1);
        grafo.adicionarAresta(Bairro.SETOR_JAO,                 Bairro.JARDIM_NOVO_MUNDO,         7.4);

        grafo.adicionarAresta(Bairro.JARDIM_GUANABARA,          Bairro.SETOR_CAMPINAS,            12.2);
        grafo.adicionarAresta(Bairro.JARDIM_GUANABARA,          Bairro.JARDIM_CURITIBA,           16.8);

        // bairros dos ecopontos (ADR-072)
        grafo.adicionarAresta(Bairro.SETOR_FAICALVILLE,           Bairro.PARQUE_AMAZONIA,             5.6);
        grafo.adicionarAresta(Bairro.SETOR_FAICALVILLE,           Bairro.JARDIM_AMERICA,              7.0);
        grafo.adicionarAresta(Bairro.SETOR_FAICALVILLE,           Bairro.RESIDENCIAL_CAMPOS_DOURADOS, 9.4);

        grafo.adicionarAresta(Bairro.RESIDENCIAL_CAMPOS_DOURADOS, Bairro.PARQUE_AMAZONIA,             15.3);
        grafo.adicionarAresta(Bairro.RESIDENCIAL_CAMPOS_DOURADOS, Bairro.CIDADE_JARDIM,               17.4);

        grafo.adicionarAresta(Bairro.JARDIM_SAO_JOSE,             Bairro.PARQUE_ELDORADO_OESTE,       2.6);
        grafo.adicionarAresta(Bairro.JARDIM_SAO_JOSE,             Bairro.CIDADE_JARDIM,               13.9);
        grafo.adicionarAresta(Bairro.JARDIM_SAO_JOSE,             Bairro.JARDIM_CURITIBA,             21.2);

        grafo.adicionarAresta(Bairro.PARQUE_ELDORADO_OESTE,       Bairro.CIDADE_JARDIM,               12.3);
        grafo.adicionarAresta(Bairro.PARQUE_ELDORADO_OESTE,       Bairro.RESIDENCIAL_CAMPOS_DOURADOS, 19.8);
        return grafo;
    }
}