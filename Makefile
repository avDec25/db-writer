build:
	kubectx rancher-desktop
	nerdctl --namespace k8s.io build -t db-writer:latest .

	#kubectl create namespace local-k8s
create:
	kubectx rancher-desktop
	kubens local-k8s
	kubectl apply -f deploy/configmap.yaml
	kubectl apply -f deploy/deployment.yaml
	kubectl apply -f deploy/service.yaml

update:
	kubectl apply -f deploy/configmap.yaml
	kubectl rollout restart deployment db-writer-deployment

clean:
	kubectl delete namespace local-k8s