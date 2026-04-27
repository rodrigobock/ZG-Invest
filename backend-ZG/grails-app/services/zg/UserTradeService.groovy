package zg

import grails.gorm.transactions.Transactional
import grails.util.Holders

import java.text.SimpleDateFormat
import java.math.RoundingMode

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

        return results.list()
    }

    def calculaPorcentagemRendimento(acao, def params) {
        def resultadoDosCalculos = [:]
        def param = params.clone()
        param.simbol = acao

        def valorCompra = pegaPrimeiroTrade(param)
        def valorFinalPeriodo = pegaTradeUltimaData(param)

        if (valorCompra != null && valorFinalPeriodo != null && valorCompra > 0) {
            def resultadoCalculo = ((new BigDecimal(valorFinalPeriodo) / new BigDecimal(valorCompra)) - 1) * 100
            def resultadoArredondado = resultadoCalculo.setScale(2, RoundingMode.HALF_UP)
            resultadoDosCalculos.put(acao, resultadoArredondado)
        } else {
            resultadoDosCalculos.put(acao, "Não foram encontrados registros de compra válidos!")
        }

        return params.simbol ? resultadoDosCalculos[params.simbol] : resultadoDosCalculos.get(acao)
    }

    def calculaRetornoEmReais(acao, transacoes) {
        def acoes = [:]
        transacoes.each { t ->
            if (t[1].equals(acao)) {
                def movimentacao = t[0]
                def valorTotal = t[2]
                def value = acoes.getOrDefault(acao, 0.0) + (movimentacao == "C" ? valorTotal : -Math.abs(valorTotal))
                acoes[acao] = value.round(2)
            }
        }
        return acoes.get(acao)
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

        Map<String, Object> queryParams = [startDate: startDate, endDate: endDate]
        if (simbol) queryParams.put("simbol", simbol)

        try {
            def result = UserTrade.executeQuery(query, queryParams, [max: 1])
            return result && !result.isEmpty() ? result.get(0) : null
        } catch (Exception e) {
            return null
        }
    }

    def pegaTradeUltimaData(def params) {
        def startDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.startDate)
        def endDate = new SimpleDateFormat("yyyy-MM-dd").parse(params.endDate)
        def simbol = params.simbol

        String query = """select price from InstrumentQuote iq where date between :startDate and :endDate """
        if (simbol) {
            query += """and simbol = :simbol """
        }
        query += """order by date desc"""

        Map<String, Object> queryParams = [startDate: startDate, endDate: endDate]
        if (simbol) queryParams.put("simbol", simbol)

        try {
            def result = InstrumentQuote.executeQuery(query, queryParams, [max: 1])
            return result && !result.isEmpty() ? result.get(0) : null
        } catch (Exception e) {
            return null
        }
    }

    // --- NOVOS MÉTODOS PARA PERFORMANCE CUMULATIVA E DIÁRIA ---

    def getDailyPortfolioPerformance(String simbol, Date startDate, Date endDate) {
        String tradeQuery = """
            select data, tipoOperacao, quantidade, valorTotal 
            from UserTrade 
            where data <= :endDate and instrument = :simbol
            order by data asc
        """
        def trades = UserTrade.executeQuery(tradeQuery, [endDate: endDate, simbol: simbol])
        
        String quoteQuery = """
            select date, price 
            from InstrumentQuote 
            where date >= :startDate and date <= :endDate and simbol = :simbol
            order by date asc
        """
        def quotes = InstrumentQuote.executeQuery(quoteQuery, [startDate: startDate, endDate: endDate, simbol: simbol])
        
        Map<String, Float> quoteMap = [:]
        quotes.each { q ->
            String d = new SimpleDateFormat("yyyy-MM-dd").format(q[0])
            quoteMap[d] = q[1]
        }
        
        def results = []
        Calendar cal = Calendar.getInstance()
        cal.setTime(startDate)
        
        Integer qtdAcoes = 0
        BigDecimal custoTotal = 0.0
        Float lastKnownPrice = 0.0
        
        def tradesBefore = trades.findAll { it[0] < startDate }
        tradesBefore.each { trade ->
            String tipo = trade[1]
            Integer qtd = trade[2]
            Float valorTotal = trade[3]
            
            if (tipo == 'C') {
                qtdAcoes += qtd
                custoTotal += valorTotal
            } else if (tipo == 'V') {
                if (qtdAcoes > 0) {
                    BigDecimal precoMedio = custoTotal / qtdAcoes
                    qtdAcoes -= qtd
                    custoTotal -= (precoMedio * qtd)
                }
            }
        }
        
        def tradesDuring = trades.findAll { !it[0].before(startDate) }
        int tradeIdx = 0
        
        while (!cal.getTime().after(endDate)) {
            Date currentDate = cal.getTime()
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(currentDate)
            
            while (tradeIdx < tradesDuring.size() && new SimpleDateFormat("yyyy-MM-dd").format(tradesDuring[tradeIdx][0]) == dateStr) {
                def trade = tradesDuring[tradeIdx]
                String tipo = trade[1]
                Integer qtd = trade[2]
                Float valorTotal = trade[3]
                
                if (tipo == 'C') {
                    qtdAcoes += qtd
                    custoTotal += valorTotal
                } else if (tipo == 'V') {
                    if (qtdAcoes > 0) {
                        BigDecimal precoMedio = custoTotal / qtdAcoes
                        qtdAcoes -= qtd
                        custoTotal -= (precoMedio * qtd)
                    }
                }
                tradeIdx++
            }
            
            if (quoteMap.containsKey(dateStr)) {
                lastKnownPrice = quoteMap[dateStr]
            }
            
            BigDecimal valorAtual = qtdAcoes * lastKnownPrice
            BigDecimal returnPercentage = 0.0
            if (custoTotal > 0 && lastKnownPrice > 0) {
                returnPercentage = ((valorAtual / custoTotal) - 1) * 100
            }
            
            results.add([
                date: dateStr,
                simbol: simbol,
                qtdAcoes: qtdAcoes,
                custoTotal: custoTotal.setScale(2, RoundingMode.HALF_UP),
                valorAtual: valorAtual.setScale(2, RoundingMode.HALF_UP),
                returnPercentage: returnPercentage.setScale(2, RoundingMode.HALF_UP),
                currentPrice: lastKnownPrice
            ])
            
            cal.add(Calendar.DATE, 1)
        }
        return results
    }

    def getCumulativePortfolioPerformance(String simbol, Date startDate, Date endDate) {
        def daily = getDailyPortfolioPerformance(simbol, startDate, endDate)
        if (!daily || daily.isEmpty()) return null
        return daily.last()
    }
}
