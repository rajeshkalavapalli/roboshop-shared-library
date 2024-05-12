#!groovy

def decidePipeline (map configMap) {
    application = configMap.get("application")
    switch(GIT_BRANCH) {
        case 'nodejsVM'
            nodejsVM(configMap)
            break
        case 'javaVM'
            javaVM(configMap)
            break
        case 'nodejsEKS'
            nodejsEKS(config)
        default:
            error "allication is no recognise"
            break

    }
}