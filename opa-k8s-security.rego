# opa-k8s-security.rego
# Common Kubernetes security policies for Gatekeeper (OPA)

package kubernetes.security

import future.keywords.in

# === Deny if no namespace is specified (except for namespace resource itself) ===
violation[msg] {
    input.review.kind.kind != "Namespace"
    not input.review.namespace
    msg := sprintf("Resource %s/%s must be in a namespace", [input.review.kind.kind, input.review.name])
}

# === Deny running as root (user 0) ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    container.securityContext.runAsUser == 0
    msg := sprintf("Container %s in %s/%s is running as root (runAsUser: 0)", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Deny privileged containers ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    container.securityContext.privileged == true
    msg := sprintf("Container %s in %s/%s is running in privileged mode", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Deny containers that can escalate privileges ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    container.securityContext.allowPrivilegeEscalation == true
    msg := sprintf("Container %s in %s/%s allows privilege escalation", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Deny containers without read-only root filesystem ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    not container.securityContext.readOnlyRootFilesystem
    msg := sprintf("Container %s in %s/%s does not have readOnlyRootFilesystem enabled", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Deny containers without resource limits/requests ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    not container.resources.limits
    msg := sprintf("Container %s in %s/%s is missing resource limits", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Deny deployments without hostNetwork === (optional - only if you want to enforce no host networking)
# violation[msg] {
#     input.review.spec.template.spec.hostNetwork == true
#     msg := sprintf("Host networking is not allowed in %s/%s", [input.review.kind.kind, input.review.name])
# }

# === Deny pods that can access host PID / IPC ===
violation[msg] {
    input.review.spec.template.spec.hostPID == true
    msg := sprintf("Host PID sharing is not allowed in %s/%s", [input.review.kind.kind, input.review.name])
}

violation[msg] {
    input.review.spec.template.spec.hostIPC == true
    msg := sprintf("Host IPC sharing is not allowed in %s/%s", [input.review.kind.kind, input.review.name])
}

# === Deny pods that mount /proc, /sys, etc. ===
violation[msg] {
    volume := input.review.spec.template.spec.volumes[_]
    volume.hostPath.path == "/proc"
    msg := sprintf("Mounting /proc is not allowed in %s/%s", [input.review.kind.kind, input.review.name])
}

violation[msg] {
    volume := input.review.spec.template.spec.volumes[_]
    volume.hostPath.path == "/sys"
    msg := sprintf("Mounting /sys is not allowed in %s/%s", [input.review.kind.kind, input.review.name])
}

# === Require non-root user and group for containers (optional) ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    not container.securityContext.runAsUser
    msg := sprintf("Container %s in %s/%s does not specify runAsUser (non-root recommended)", [
        container.name,
        input.review.kind.kind,
        input.review.name
    ])
}

# === Optional: Deny latest tag on images ===
violation[msg] {
    container := input.review.spec.template.spec.containers[_]
    endswith(container.image, ":latest")
    msg := sprintf("Image %s in %s/%s uses ':latest' tag (use specific version)", [
        container.image,
        input.review.kind.kind,
        input.review.name
    ])
}