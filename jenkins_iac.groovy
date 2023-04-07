import groovy.json.JsonSlurper

//configuration root path
def config_root_path = "${WORKSPACE}/jenkins_jobs"

//linux tree command
def tree_command = "tree -J ${config_root_path}"

// get root tree of jobs config in json format
def getJsonConfigTree(command) {
    def tree = ['bash', '-c', "${command}"].execute().text
    return new JsonSlurper().parseText(tree)
}

def jsonProjectsStruct = getJsonConfigTree(tree_command)

println jsonProjectsStruct
