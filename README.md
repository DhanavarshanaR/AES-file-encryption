AES File Encryption in Java

A simple and secure Java program that encrypts and decrypts files using AES-128 (CBC mode) with PBKDF2 (SHA-256) for key derivation.

Salt and IV are automatically generated and stored safely in the encrypted output file.

Features

1)AES-128 encryption (CBC mode)

2)PBKDF2WithHmacSHA256 for secure password-based key derivation

3)Random salt and IV generated per file

4)Salt + IV stored at the beginning of the encrypted file

5)Works with any file type (text, images, documents, etc.)

6)Simple console interface (encrypt/decrypt)

How It Works

Encryption

1)A random 16-byte salt is generated.

2)AES key is derived from the password using PBKDF2 with 65,536 iterations.

3)A random 16-byte IV is generated.

4)Output file begins with salt + IV.

5)File content is encrypted and written to the output.

Decryption

1)Reads salt and IV from the encrypted file.

2)Recreates the AES key using the same PBKDF2 method.

3)Decrypts the remaining encrypted data.

Usage

Compile

javac AESFileEncryption.java

Run

java AESFileEncryption

Example

Encryption:

Enter mode (encrypt/decrypt): encrypt

Enter input file path: project.txt

Enter output file path: project.enc

Enter password: MySecret123

File encrypted successfully.


Decryption:

Enter mode (encrypt/decrypt): decrypt

Enter input file path: project.enc

Enter output file path: project_dec.txt

Enter password: MySecret123

File decrypted successfully.

Encrypted File Format

[16 bytes salt][16 bytes IV][Encrypted data...]


Each encrypted file contains everything needed for decryption (except the password).

Security Notes

1)PBKDF2 with 65,536 iterations slows down brute-force attacks.

2)Use a strong password for better security.

3)AES key is never stored; it is always derived from the password.

Requirements

1)Java 8 or newer

2)No external libraries



This project is open source project. Use and modify freely.

