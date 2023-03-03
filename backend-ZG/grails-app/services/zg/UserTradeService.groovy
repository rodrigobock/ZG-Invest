package zg

import grails.gorm.transactions.Transactional
import grails.util.Holders

import java.text.SimpleDateFormat;

@Transactional
class UserTradeService {

    def pegaItemDoBanco(def params) {

        def simbol = params.simbol
        def startDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
        def endDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)

        final session = Holders.grailsApplication.mainContext.getBean('sessionFactory').currentSession

        String query = """
            select 
            
               tipo_operacao, 
               instrument,
               valor_total
            
            from user_trade ut 
            
            join instrument_quote iq on iq.simbol = ut.instrument
    
            where
                ("data" between :startDate and :endDate)
                and ("date" between :startDate and :endDate)
                and "date" = "data"
        """

        if (simbol) {
            query += """ and iq.simbol = :simbol"""
        }

        def results = session.createSQLQuery(query)
                .setParameter('startDate', startDate)
                .setParameter('endDate', endDate)
        if (simbol) {
            results.setParameter('simbol', simbol)
        }

        def retorno = results.list()

        return retorno
    }

    def calculaPorcentagemRendimento(acao, def params) {

        def resultadoDosCalculos = [:]

        // Obtém o símbolo da ação dos parâmetros
        def simbol = params.simbol

        // Cria uma cópia dos parâmetros e atualiza o símbolo para o símbolo da ação passada como parâmetro
        def param = params.clone()
        param.simbol = acao

        // Obtém o valor de compra da ação no período
        def valorCompra = pegaPrimeiroTrade(param)
        // Obtém o valor de venda da ação no final do período
        def valorFinalPeriodo = pegaTradeUltimaData(param)

        if (valorCompra != null && valorFinalPeriodo != null) {
            def resultadoCalculo = ((new BigDecimal(valorFinalPeriodo) / new BigDecimal(valorCompra)) - 1) * 100
            def resultadoArredondado = resultadoCalculo.round(2)
            resultadoDosCalculos.put(acao, resultadoArredondado)
        } else {
            resultadoDosCalculos.put(acao, "Não foram encontrados registros de compra de ações!")
        }

        return simbol ? resultadoDosCalculos[simbol] : resultadoDosCalculos.get(acao)
    }

    def calculaRetornoEmReais(acao, transacoes) {
        def acoes = [:]
        transacoes.each { t ->
            if (t[1].equals(acao)) {
                def movimentacao = t[0]
                def valorTotal = t[2]

                // Adiciona ou subtrai o valor total da transação do dicionário de ações, dependendo da movimentação
                def value = acoes.getOrDefault(acao, 0.0) + (movimentacao == "C" ? valorTotal : -Math.abs(valorTotal))
                acoes[acao] = value.round(2)
            }
        }

        return acoes.get(acao);
    }

    def pegaPrimeiroTrade(def params) {

        def startDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
        def endDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)
        def simbol = params.simbol

        String query = """select preco from UserTrade ut where data between :startDate and :endDate and tipoOperacao = 'C' """

        if (simbol) {
            query += """and instrument = :simbol """
        }

        query += """order by data asc"""

        Map<String, Object> queryParams = [:]
        queryParams.put("startDate", startDate)
        queryParams.put("endDate", endDate)
        if (simbol) {
            queryParams.put("simbol", simbol)
        }

        Float userTrade

        try {
            userTrade = UserTrade.executeQuery(query, queryParams).get(0)
        } catch (Exception e) {
            userTrade = null
        }

        return userTrade != null ? userTrade : null
    }


    def pegaTradeUltimaData(def params) {

        def startDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
        def endDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)
        def simbol = params.simbol

        String query = """select price from InstrumentQuote iq where date between :startDate and :endDate """

        if (simbol) {
            query += """and simbol = :simbol"""
        }

        Float instrumentQuote

        try {
            Map<String, Object> queryParams = [:]
            queryParams.put("startDate", startDate)
            queryParams.put("endDate", endDate)
            if (simbol) {
                queryParams.put("simbol", simbol)
            }
            instrumentQuote = InstrumentQuote.executeQuery(query, queryParams).get(0)
        } catch (Exception e) {
            instrumentQuote = null
        }

        return instrumentQuote != null ? instrumentQuote : null

    }


}
