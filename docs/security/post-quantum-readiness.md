# Sentinel Post-Quantum Cryptography (PQC) Readiness Architecture

## 1. Overview
As quantum computing matures, classical public-key cryptography (RSA, ECDSA, ECDH) faces potential vulnerability to Shor's algorithm. Sentinel is architected for **Post-Quantum Cryptographic Readiness**, allowing seamless hybrid transition to NIST PQC standards.

---

## 2. NIST Standardized PQC Target Algorithms

| Domain | Classical Algorithm | Post-Quantum Replacement | Standard |
| :--- | :--- | :--- | :--- |
| **Key Encapsulation / Exchange** | RSA / ECDH | **ML-KEM (CRYSTALS-Kyber)** | FIPS 203 |
| **Digital Signatures (JWT)** | HMAC-SHA256 / ECDSA | **ML-DSA (CRYSTALS-Dilithium)** | FIPS 204 |
| **Stateful Hash-Based Signatures** | RSA Signatures | **SLH-DSA (SPHINCS+)** | FIPS 205 |

---

## 3. Hybrid Cryptographic Migration Path

```
 +-------------------------------------------------------------------------+
 |                      HYBRID JWT SIGNATURE SCHEME                        |
 |                                                                         |
 |  [JWT Header]  {"alg": "HYBRID_MLDSA_HMAC256", "typ": "JWT"}            |
 |                                                                         |
 |  [JWT Payload] {"sub": "user123", "roles": ["ADMIN"], "exp": 1770000000}|
 |                                                                         |
 |  [Signature]   HMAC-SHA256(Header.Payload)  +  ML-DSA-Signature        |
 +-------------------------------------------------------------------------+
```

1. **Phase 1 (Current)**: Classical HMAC-SHA256 & BCrypt password hashing.
2. **Phase 2 (Hybrid Co-existence)**: Dual signature verification where Gateway checks classical HMAC signature while validating ML-DSA post-quantum signature headers.
3. **Phase 3 (Full PQC Cutover)**: Full transition to ML-KEM key exchanges and ML-DSA token authentication.
