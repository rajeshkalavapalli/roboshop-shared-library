#!groovy
def decidePipeline (Map configMap) {
    application = configMap.get("application")
    switch(application) {
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
