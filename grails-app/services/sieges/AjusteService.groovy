package sieges

import groovy.json.JsonSlurper

class AjusteService {
    def probarJson(){
        def jsonSlurper = new JsonSlurper()
        def object
        try{
            if(true){
                return 12
            }
            object = jsonSlurper.parseText('{ "name": fgfghdfhfdgh"John", "ID" : "1"}')
        }
        catch(Exception exception){
            println("ok")
            return 0
            println("Error")
        }
        return 1
    }
}
