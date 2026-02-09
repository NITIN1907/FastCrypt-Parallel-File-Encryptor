# 🔐 Parallel File Encryptor (Java)

A **system-level parallel file encryption and decryption engine** implemented in Java, focusing on **concurrency control, safe file I/O, and deterministic execution**.

The application traverses directories recursively and models each file as an independent unit of work, processed concurrently using a **CPU-core–based `ExecutorService`**. Task flow is regulated through a **bounded producer–consumer queue** coordinated with semaphores to enforce backpressure and prevent unbounded resource usage.

Each file is guarded by a **custom binary header** to track encryption state, enabling strict validation that blocks double encryption and invalid decryption. File transformation is performed using **chunked `RandomAccessFile` I/O**, with explicit ownership transfer and cleanup to ensure correct resource management.

The design emphasizes **correctness, thread safety, and I/O integrity**, making it suitable for disk-bound, system-oriented Java workloads.

---

## ✨ Features

- Encrypt / decrypt entire directories recursively
- Parallel processing using CPU core–based worker threads
- Thread-safe bounded task queue
- Producer–Consumer architecture using semaphores
- Custom file header to prevent double encryption
- Strict mode safety checks
- Environment-based encryption key (`.env`)
- Explicit resource cleanup (RAII-style)
- Efficient large file handling using stream-based I/O

---

## 🔐 Encryption Strategy

- **Cipher**: Byte-wise shift cipher  
- **Key Source**: `.env` file  
- **Header Format**:
  - `MAGIC (4 bytes)` → `"CRYP"`
  - `COUNT (4 bytes)` → encryption state

### Header Rules

| File State        | Encrypt | Decrypt |
|------------------|---------|---------|
| Not encrypted     | ✅ Allowed | ❌ Blocked |
| Already encrypted | ❌ Blocked | ✅ Allowed |

---

## 📂 Project Structure
project_os/
├── .idea/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── org/
│ │ │ └── app/
│ │ │ ├── encryptDecrypt/
│ │ │ │ ├── Cryption.java
│ │ │ │ ├── CryptionMain.java
│ │ │ │ └── FileHeader.java
│ │ │ ├── handler/
│ │ │ │ ├── IO.java
│ │ │ │ └── ReadEnv.java
│ │ │ ├── processmanagement/
│ │ │ │ ├── ProcessManager.java
│ │ │ │ └── Task.java
│ │ │ └── Main.java
│ │ └── resources/
│ └── test/
│ └── java/
├── target/
├── tests/
├── .env
├── .gitignore
├── makeDirs.py
└── pom.xml



## 📊 Performance Benchmark (Actual Run)

- **Files processed**: 1000  
- **Operation**: Parallel Encryption  
- **Total execution time**: **4925.6 ms (~4.9 seconds)**  
- **Execution model**: Multi-threaded (CPU-core based worker pool)  
- **I/O model**: `RandomAccessFile` with chunked reads (8 KB buffer)

### 🔢 Throughput

- **~200 files/second**

> This benchmark includes:
> - Recursive directory traversal  
> - Task queueing and synchronization  
> - File header insertion and validation  
> - Strict-mode safety checks  
> - Parallel encryption and disk I/O  

---

## 📸 Benchmark Screenshots

> Screenshots captured during the above benchmark run.

<img width="519" height="220" alt="image" src="https://github.com/user-attachments/assets/bfa6352c-90b2-4daf-b44e-f19259425dba" />

## 🏁 Conclusion

This project demonstrates a **system-level approach to parallel file encryption and decryption** using Java.  
The primary focus is on **correctness, concurrency control, and safe resource management**, rather than UI or framework-heavy abstractions.

Key highlights:
- Bounded producer–consumer queue using semaphores  
- CPU-aware parallel execution model  
- Strict encryption/decryption validation  
- Explicit file ownership and cleanup (RAII-style)  
- Disk I/O–aware performance design  

The design reflects **real-world backend and OS-level engineering considerations**.

---

## 🧪 Testing & Validation

- Tested on directories containing **1000+ files**
- Verified strict-mode behavior:
  - Double encryption prevention
  - Invalid decryption prevention
- Confirmed parallel execution across multiple CPU cores

---

## 🔭 Future Improvements

- Replace shift cipher with AES-based encryption
- Add CLI flags (`--encrypt`, `--decrypt`, `--threads`)
- Implement progress tracking
- Optimize large-file handling using memory-mapped I/O
- Integrate structured logging and metrics

---

## 📌 Notes for Reviewers

- This is a **backend/system-level project**, not a UI application
- Performance is primarily **disk I/O bound**
- Design choices prioritize **safety and determinism** over raw throughput
- Concurrency primitives are used intentionally and explicitly

---

## 👨‍💻 Author

**Nitin Dogra**

Built to demonstrate **parallelism, concurrency control, and system design principles** in Java.

---




