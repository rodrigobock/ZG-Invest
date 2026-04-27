package zg

class UrlMappings {

    static mappings = {

        get "/portfolio/daily"(controller: "userTrade", action: "daily")
        get "/portfolio/cumulative"(controller: "userTrade", action: "cumulative")
        
        get "/$controller(.$format)?"(action: "getSpecificValues")

        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
