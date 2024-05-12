#!groovy
def decidePipeline (Map configMap) {
    application = configMap.get("application")
    switch(GIT_BRANCH) {
        case 'nodejsVM':
            nodejsVM(configMap)
            break
        case 'javaVM':
            javaVM(configMap)
            break
        case 'nodejsEKS':
            nodejsEKS(config)
        default:
            error "application is no recognise"
            break

    }
}
