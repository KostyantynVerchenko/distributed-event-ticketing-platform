default_registry('ghcr.io')

target_namespace = 'ticketing'
image_registry = 'ghcr.io/kostyantynverchenko'
image_tag = 'dev'
registry_secret = 'ghcr-cred'

allow_k8s_contexts('kind-ticketing')

local_resource(
    'setup-namespace-and-registry',
    cmd='''
set -euo pipefail
kubectl create namespace {ns} --dry-run=client -o yaml | kubectl apply -f -
kubectl create secret generic {secret} \
  --namespace {ns} \
  --from-file=.dockerconfigjson=${{DOCKER_CONFIG:-$HOME/.docker}}/config.json \
  --type=kubernetes.io/dockerconfigjson \
  --dry-run=client -o yaml | kubectl apply -f -
'''.format(ns=target_namespace, secret=registry_secret),

    cmd_bat='''
kubectl create namespace {ns} --dry-run=client -o yaml | kubectl apply -f -
if "%DOCKER_CONFIG%"=="" (set "DOCKER_CFG=%USERPROFILE%\\.docker") else (set "DOCKER_CFG=%DOCKER_CONFIG%")
kubectl create secret generic {secret} --namespace {ns} --from-file=.dockerconfigjson="%DOCKER_CFG%\\config.json" --type=kubernetes.io/dockerconfigjson --dry-run=client -o yaml | kubectl apply -f -
'''.format(ns=target_namespace, secret=registry_secret),
)


k8s_yaml(helm(
    './ticketing-chart',
    name='ticketing-dev',
    namespace=target_namespace,
    values=['ticketing-chart/values.yaml'],
    set=[
        'namespace=' + target_namespace,
        'imageRegistry=' + image_registry,
        'imageTag=' + image_tag,
        'registry.secretName=' + registry_secret,
        'registry.createSecret=false',
    ],
))

docker_build(image_registry + '/eureka-server', './services/eureka-server')
docker_build(image_registry + '/gateway-service', './services/gateway-service')
docker_build(image_registry + '/auth-service', './services/auth-service')
docker_build(image_registry + '/events-service', './services/events-service')
docker_build(image_registry + '/notifications-service', './services/notifications-service')
docker_build(image_registry + '/orders-service', './services/orders-service')
docker_build(image_registry + '/payment-service', './services/payment-service')

k8s_resource('eureka-server', port_forwards=['8761:8761'], resource_deps=['setup-namespace-and-registry'])
k8s_resource('gateway-service', port_forwards=['8084:8084'], resource_deps=['setup-namespace-and-registry'])
k8s_resource('auth-service', resource_deps=['setup-namespace-and-registry'])
k8s_resource('events-service', resource_deps=['setup-namespace-and-registry'])
k8s_resource('notifications-service', resource_deps=['setup-namespace-and-registry'])
k8s_resource('orders-service', resource_deps=['setup-namespace-and-registry'])
k8s_resource('payment-service', resource_deps=['setup-namespace-and-registry'])