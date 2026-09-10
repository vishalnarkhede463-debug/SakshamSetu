# GovSkill Connect — Trusted Execution Environment (TEE)

## What was added

GovSkill Connect now has a **TEE-ready security boundary** around two sensitive backend operations:

- Eligibility evaluation
- Student/opportunity matching

The new class is:

`backend/src/com/govskill/services/TrustedExecutionEnvironment.java`

A status endpoint is also available:

`GET /api/security/tee-status`

## Important technical note

The local Java application cannot claim a real hardware-backed Intel SGX/TDX or ARM TrustZone enclave using Java SE alone. The project therefore defaults to:

`SOFTWARE_SIMULATION`

This is a development/demo security boundary using AES-256-GCM and process-memory key handling. For production hardware isolation, replace the provider behind this boundary with an actual SGX/TDX/TrustZone confidential-computing runtime.

## Demo configuration

Optional environment variable:

```text
GOVSKILL_TEE_MODE=SOFTWARE_SIMULATION
```

The API reports whether hardware mode was selected, but selecting `HARDWARE`, `SGX`, or `TDX` does not magically create a hardware enclave; the platform-specific provider must be integrated first.

## Hackathon explanation

> “GovSkill Connect introduces a TEE-ready security boundary for processing sensitive student and eligibility data. In our local prototype, AES-256-GCM protects data crossing the boundary and encryption keys are kept only in process memory. The same interface can be backed by Intel SGX/TDX or another confidential-computing provider in production.”
