# C2PA Android Kotlin API Developer Guide

This document describes the classes and methods available in the C2PA Android Kotlin API. The API provides a Kotlin wrapper around the C2PA C library via JNI for content authentication functionality.

## Overview

The C2PA Android API consists of several classes that provide different levels of abstraction:

- **C2PA**: Low-level JNI wrapper with static native methods
- **C2PAManager**: Higher-level convenience wrapper for common operations
- **C2PABuilder**: RAII wrapper for building and signing manifests
- **C2PAReader**: RAII wrapper for reading manifests from assets
- **C2PASigner**: RAII wrapper for digital signing operations
- **C2PAException**: Exception class for C2PA operation failures

## Core Classes

### C2PA

The main class that provides direct access to the C2PA C library through JNI. All methods are static and correspond to functions in the native C API.

#### Library Management

- **`getVersion(): String?`** - Returns the C2PA library version string
- **`getError(): String?`** - Retrieves the last error message from the library
- **`loadSettings(settings: String, format: String): Int`** - Loads configuration settings (typically JSON format)

#### File Operations

- **`readFile(path: String, dataDir: String?): String?`** - Reads a manifest store from a file, returns JSON representation
- **`readIngredientFile(path: String, dataDir: String): String?`** - Reads an ingredient from a file, returns JSON representation

#### Builder Operations

- **`builderFromJson(manifestJson: String): Long`** - Creates a builder from JSON manifest definition, returns native pointer
- **`builderFree(builderPtr: Long)`** - Frees a builder instance
- **`builderSetNoEmbed(builderPtr: Long)`** - Sets the no-embed flag on a builder
- **`builderSetRemoteUrl(builderPtr: Long, remoteUrl: String): Int`** - Sets a remote URL for the manifest
- **`builderSign(builderPtr: Long, format: String, sourcePtr: Long, destPtr: Long, signerPtr: Long): Long`** - Signs a manifest using the builder
- **`builderToArchive(builderPtr: Long, streamPtr: Long): Int`** - Writes builder to archive format
- **`builderAddResource(builderPtr: Long, uri: String, streamPtr: Long): Int`** - Adds a resource to the builder
- **`builderAddIngredientFromStream(builderPtr: Long, ingredientJson: String, format: String, sourcePtr: Long): Int`** - Adds an ingredient from a stream

#### Reader Operations

- **`readerFromStream(format: String, streamPtr: Long): Long`** - Creates a reader from a stream, returns native pointer
- **`readerFree(readerPtr: Long)`** - Frees a reader instance
- **`readerJson(readerPtr: Long): String?`** - Gets JSON representation from a reader
- **`readerResourceToStream(readerPtr: Long, uri: String, streamPtr: Long): Long`** - Extracts a resource to a stream

#### Signer Operations

- **`signerFromInfo(alg: String, signCert: String, privateKey: String, taUrl: String?): Long`** - Creates a signer from signing information
- **`signerFree(signerPtr: Long)`** - Frees a signer instance
- **`signerReserveSize(signerPtr: Long): Long`** - Gets the signature reserve size

### C2PAManager

A higher-level wrapper that provides convenient methods with automatic error handling.

- **`getVersion(): String`** - Gets library version with exception on failure
- **`loadSettings(settingsJson: String)`** - Loads JSON settings with error checking
- **`readManifestFromFile(path: String, dataDir: String? = null): String`** - Reads manifest from file with error handling
- **`readIngredientFromFile(path: String, dataDir: String): String`** - Reads ingredient from file with error handling
- **`checkError()`** - Private method that checks for and throws pending errors

### C2PABuilder

RAII (Resource Acquisition Is Initialization) wrapper for the C2PA builder that implements `AutoCloseable` for automatic resource management.

#### Constructor
- **`C2PABuilder(manifestJson: String)`** - Creates builder from JSON manifest definition

#### Methods
- **`setNoEmbed()`** - Sets the no-embed flag
- **`setRemoteUrl(url: String)`** - Sets remote URL for the manifest
- **`addResource(uri: String, streamPtr: Long)`** - Adds a resource to the builder
- **`addIngredientFromStream(ingredientJson: String, format: String, sourcePtr: Long)`** - Adds an ingredient from stream
- **`sign(format: String, sourcePtr: Long, destPtr: Long, signerPtr: Long): Long`** - Signs the manifest
- **`toArchive(streamPtr: Long)`** - Exports builder to archive format
- **`close()`** - Releases native resources (called automatically with try-with-resources)

### C2PAReader

RAII wrapper for the C2PA reader that implements `AutoCloseable`.

#### Constructor
- **`C2PAReader(format: String, streamPtr: Long)`** - Creates reader from stream with specified format

#### Methods
- **`toJson(): String`** - Returns JSON representation of the manifest
- **`resourceToStream(uri: String, streamPtr: Long): Long`** - Extracts a resource to stream
- **`close()`** - Releases native resources

### C2PASigner

RAII wrapper for the C2PA signer that implements `AutoCloseable`.

#### Constructor
- **`C2PASigner(alg: String, signCert: String, privateKey: String, taUrl: String? = null)`** - Creates signer with algorithm, certificate, private key, and optional timestamp authority URL

#### Methods
- **`reserveSize(): Long`** - Gets the signature reserve size
- **`getPtr(): Long`** - Internal method to get native pointer for use with other operations
- **`close()`** - Releases native resources

### C2PAException

Exception class for C2PA operation failures.

- **`C2PAException(message: String)`** - Constructor that takes an error message

## Usage Patterns

### Basic Error Handling
All wrapper classes automatically check for errors and throw `C2PAException` on failure. The low-level `C2PA` class returns error codes or null values that should be checked manually.

### Resource Management
The wrapper classes (`C2PABuilder`, `C2PAReader`, `C2PASigner`) implement `AutoCloseable` and should be used with try-with-resources blocks to ensure proper cleanup:

```kotlin
try (val builder = C2PABuilder(manifestJson)) {
    builder.setNoEmbed()
    // ... use builder
} // automatically calls close()
```

### Native Pointers
Stream pointers (`streamPtr`) are native memory addresses that must be managed by the calling code. These typically represent input/output streams for asset data.

## Thread Safety

The C2PA library is not thread-safe. Concurrent access to the same builder, reader, or signer instances should be synchronized externally.