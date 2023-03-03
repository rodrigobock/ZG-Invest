package zg

import grails.converters.JSON
import java.util.stream.Collectors

class UserTradeController {

    UserTradeService userTradeService

    def montaMapa(chave, valorReal, porcentagem) {
        Map<Object, Object> valores = new HashMap<>();
        valores.put("reais", valorReal)
        valores.put("porcentagem", porcentagem)

        Map<Object, Object> retorno = new HashMap<>();
        retorno.put(chave, valores)
        return retorno;
    }

    def getSpecificValues() {

        if (params.startDate != "" && params.endDate != "") {
            ArrayList<Object[]> transacoes = userTradeService.pegaItemDoBanco(params)

            //percorre a lista e monta um mapa com a chave e os proprios resultados do banco (daquela chave)
            //mergeFunction - só escolhe qual chave deve ser retornada para montar o mapa
            HashMap<String, ArrayList> teste = transacoes.stream().collect(Collectors.toMap(chave -> chave[1], valor -> valor, (chave, chaveDuplicada) -> chave))

            List<Map<Object, Object>> resultado = []
            teste.forEach { chave, valor ->
                {
                    String valorEmReais = userTradeService.calculaRetornoEmReais(chave, transacoes);
                    String porcentagem = userTradeService.calculaPorcentagemRendimento(chave, params);
                    resultado.add(montaMapa(chave, valorEmReais, porcentagem))
                }
            }
            render resultado as JSON
        } else {
            def error = [:]
            error.message = "Não há dados disponíveis."
            render error as JSON
        }


    }

}
