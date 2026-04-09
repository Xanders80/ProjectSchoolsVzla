// K8s Manifest Generator - DevOps Tool for SMS
// Generates Kubernetes manifests for SMS deployment

export interface K8sConfig {
  appName: string;
  namespace: string;
  replicas: number;
  image: string;
  port: number;
  resources: {
    cpu: string;
    memory: string;
  };
}

export class K8sManifest {
  generateDeployment(config: K8sConfig): string {
    return `apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${config.appName}
  namespace: ${config.namespace}
  labels:
    app: ${config.appName}
spec:
  replicas: ${config.replicas}
  selector:
    matchLabels:
      app: ${config.appName}
  template:
    metadata:
      labels:
        app: ${config.appName}
    spec:
      containers:
        - name: ${config.appName}
          image: ${config.image}
          ports:
            - containerPort: ${config.port}
          resources:
            requests:
              cpu: ${config.resources.cpu}
              memory: ${config.resources.memory}
            limits:
              cpu: "1"
              memory: 1Gi
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: ${config.port}
            initialDelaySeconds: 60
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: ${config.port}
            initialDelaySeconds: 30
            periodSeconds: 5
`;
  }

  generateService(config: K8sConfig): string {
    return `apiVersion: v1
kind: Service
metadata:
  name: ${config.appName}-service
  namespace: ${config.namespace}
spec:
  selector:
    app: ${config.appName}
  ports:
    - protocol: TCP
      port: 80
      targetPort: ${config.port}
  type: ClusterIP
`;
  }

  generateIngress(config: K8sConfig, host: string): string {
    return `apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ${config.appName}-ingress
  namespace: ${config.namespace}
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  rules:
    - host: ${host}
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: ${config.appName}-service
                port:
                  number: 80
  tls:
    - hosts:
        - ${host}
      secretName: ${config.appName}-tls
`;
  }
}
