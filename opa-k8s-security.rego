package main

# === Deny if no namespace is specified (except for namespace resource itself) ===
violation contains msg if {
    input.review.kind.kind != "Namespace"
    not input.review.namespace
    msg := sprintf("Resource %s/%s must be in a namespace", [input.review.kind.kind, input.review.name])
}

# === Deny running as root (user 0) ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    container.securityContext.runAsUser == 0
    msg := sprintf("Container '%s' in %s/%s is running as root (runAsUser: 0)", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny privileged containers ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    container.securityContext.privileged == true
    msg := sprintf("Container '%s' in %s/%s is running in privileged mode", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny containers that can escalate privileges ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    container.securityContext.allowPrivilegeEscalation == true
    msg := sprintf("Container '%s' in %s/%s allows privilege escalation", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny containers without read-only root filesystem ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    not container.securityContext.readOnlyRootFilesystem
    msg := sprintf("Container '%s' in %s/%s does not have readOnlyRootFilesystem enabled", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny containers without resource limits ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    not container.resources.limits
    msg := sprintf("Container '%s' in %s/%s is missing resource limits", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny hostPID and hostIPC ===
violation contains msg if {
    input.review.object.spec.hostPID == true
    msg := sprintf("Host PID sharing is not allowed in %s/%s", [
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

violation contains msg if {
    input.review.object.spec.hostIPC == true
    msg := sprintf("Host IPC sharing is not allowed in %s/%s", [
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny dangerous hostPath mounts ===
violation contains msg if {
    volume := input.review.object.spec.volumes[_]
    volume.hostPath.path == "/proc"
    msg := sprintf("Mounting /proc is not allowed in %s/%s", [
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

violation contains msg if {
    volume := input.review.object.spec.volumes[_]
    volume.hostPath.path == "/sys"
    msg := sprintf("Mounting /sys is not allowed in %s/%s", [
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Require runAsUser to be set (non-root recommended) ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    not container.securityContext.runAsUser
    msg := sprintf("Container '%s' in %s/%s does not specify runAsUser (non-root recommended)", [
        container.name,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}

# === Deny :latest tag on images ===
violation contains msg if {
    container := input.review.object.spec.containers[_]
    endswith(container.image, ":latest")
    msg := sprintf("Image '%s' in %s/%s uses ':latest' tag - use specific version", [
        container.image,
        input.review.kind.kind,
        input.review.object.metadata.name
    ])
}