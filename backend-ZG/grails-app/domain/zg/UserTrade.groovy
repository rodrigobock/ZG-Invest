package zg

class UserTrade {

    Date data
    String tipoOperacao
    String mercado
    String prazo
    String instrument
    String especificacao
    Integer quantidade
    Float preco
    Float valorTotal

    static constraints = {
        data(nullable: false)
        tipoOperacao(nullable: false)
        mercado(nullable: false)
        prazo(nullable: true)
        instrument(nullable: false)
        especificacao(nullable: false)
        quantidade(nullable: false)
        preco(nullable: false)
        valorTotal(nullable: false)
    }

}
