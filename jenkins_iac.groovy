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

for (jsonProjectStruct in jsonProjectsStruct[0].contents) {
    jsonProjectStruct.info = jsonProjectStruct.name + " builds"
    
    println dslBuildProject(jsonProjectStruct, [])

    // println dslBuildView(jsonProjectStruct)
}

//Jenkins DSL language - create folder in jenkins
def dslBuildFolder(folderPath, folderProject) {
    folderProject.info = (folderProject.info) ?: folderProject.name

    folder(folderPath){
        displayName(folderProject.info)
        description(folderProject.info)
    }
}

//Jenkins DSL language - create job in jenkins
// def dslBuildJob(jobPath, jobName) {

//     pipelineJob("$jobPath/$jobName") {
//         logRotator {
//             numToKeep(30)
//         }
//         concurrentBuild(false)
//         definition {
//             cpsScm {

//                 scm {
//                     git{

//                         remote{
//                             url('https://my_repo')
//                             credentials('my_credentials')
//                         }

//                         branch('*/main')
//                     }
//                 }

//                 lightweight(true)

//                 scriptPath('jenkinsfiles/my_app_dev.jenkinsfile')
//             }
//         }        
//     }
// }

//recursive function to build nested folder and job structure
def dslBuildProject(jsonProjectStruct, rootFolderPath) {
    def localFolderPath = rootFolderPath.clone()
    localFolderPath.add(jsonProjectStruct.name)    

    // build folders structure
    if (jsonProjectStruct.type == "directory") {
        dslBuildFolder(localFolderPath.join('/'), jsonProjectStruct)
        for (content in jsonProjectStruct.contents) {
            content.info = content.name
            dslBuildProject(content, localFolderPath)
        }
    }

    // build jobs
    // if (jsonProjectStruct.type == "file") {
    //     def jobName = jsonProjectStruct.name =~ /(.*)\.jenkinsfile/
    //     dslBuildJob(rootFolderPath.join('/'), jobName[0][1])
    // }

    return "Completion of project building: ${localFolderPath.join('/')}"
}



println jsonProjectsStruct
