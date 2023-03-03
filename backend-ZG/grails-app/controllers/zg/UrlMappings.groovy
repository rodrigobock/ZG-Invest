package zg

class UrlMappings {

    static mappings = {

        // Mantido apenas um mapping pois é o unico necessário
        get "/$controller(.$format)?"(action: "getSpecificValues")

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
