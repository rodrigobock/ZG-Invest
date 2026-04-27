package zg

import grails.converters.JSON
import java.util.stream.Collectors
import java.text.SimpleDateFormat

class UserTradeController {

    UserTradeService userTradeService

    def montaMapa(chave, valorReal, porcentagem) {
        Map<Object, Object> valores = new HashMap<>()
        valores.put("reais", valorReal)
        valores.put("porcentagem", porcentagem)

        Map<Object, Object> retorno = new HashMap<>()
        retorno.put(chave, valores)
        return retorno
    }

    def getSpecificValues() {
        if (params.startDate && params.endDate) {
            ArrayList<Object[]> transacoes = userTradeService.pegaItemDoBanco(params)
            if (!transacoes || transacoes.isEmpty()) {
                render([message: "Não há dados disponíveis."] as JSON)
                return
            }

            HashMap<String, ArrayList> teste = transacoes.stream().collect(Collectors.toMap(chave -> chave[1], valor -> valor, (chave, chaveDuplicada) -> chave))

            List<Map<Object, Object>> resultado = []
            teste.forEach { chave, valor ->
                {
                    def valorEmReais = userTradeService.calculaRetornoEmReais(chave, transacoes)
                    def porcentagem = userTradeService.calculaPorcentagemRendimento(chave, params)
                    resultado.add(montaMapa(chave, valorEmReais, porcentagem))
                }
            }
            render resultado as JSON
        } else {
            def error = [:]
            error.message = "Parâmetros startDate e endDate são obrigatórios."
            render error as JSON
        }
    }

    def daily() {
        if (params.startDate && params.endDate && params.simbol) {
            try {
                Date start = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
                Date end = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)
                def result = userTradeService.getDailyPortfolioPerformance(params.simbol, start, end)
                render result as JSON
            } catch (Exception e) {
                render([message: "Erro ao processar as datas."] as JSON)
            }
        } else {
            render([message: "Parâmetros startDate, endDate e simbol são obrigatórios."] as JSON)
        }
    }

    def cumulative() {
        if (params.startDate && params.endDate && params.simbol) {
            try {
                Date start = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
                Date end = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)
                def result = userTradeService.getCumulativePortfolioPerformance(params.simbol, start, end)
                if (result) {
                    render result as JSON
                } else {
                    render([message: "Não há dados no período informado."] as JSON)
                }
            } catch (Exception e) {
                render([message: "Erro ao processar as datas."] as JSON)
            }
        } else {
            render([message: "Parâmetros startDate, endDate e simbol são obrigatórios."] as JSON)
        }
    }
}
