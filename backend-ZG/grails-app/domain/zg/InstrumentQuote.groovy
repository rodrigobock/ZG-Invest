package zg

class InstrumentQuote {

    String simbol
    Float price
    Date date

    static constraints = {
        simbol(nullable: false)
        price(nullable: false)
        date(nullable: false)
    }

}
