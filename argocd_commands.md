$ kubectl create secret docker-registry regcred --docker-server=192.168.0.33 --docker-username=user1 --docker-password=Harbor12345 --docker-email=


$ kubectl get secret argocd-initial-admin-secret -n argocd -o jsonpath="{.data.password}" | base64 -d

$ curl -k -L -H "Authorization: Bearer [Token]" https://192.168.0.41:30674/api/v1/applications

$ curl -k -L -H "Authorization: Bearer [Token]]" -X POST https://192.168.0.41:30674/api/v1/applications/hello-web/sync
