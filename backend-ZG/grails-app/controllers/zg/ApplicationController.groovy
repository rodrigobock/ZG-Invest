package zg

import grails.converters.JSON

class ApplicationController {

    def index() {
        render([status: "ok"] as JSON)
    }
}
