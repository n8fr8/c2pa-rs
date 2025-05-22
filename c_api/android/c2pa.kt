package org.c2pa

/**
 * Kotlin wrapper for the C2PA C API via JNI
 * 
 * This class provides a Kotlin interface to the C2PA library for content authentication.
 * All methods call into native C code via JNI.
 */
class C2PA {
    companion object {
        init {
            System.loadLibrary("c2pa_c_api")
        }
        
        // Native methods that correspond to the JNI functions in android.rs
        
        /**
         * Get the version string of the C2PA library
         * @return Version string
         */
        @JvmStatic
        external fun getVersion(): String?
        
        /**
         * Get the last error message from the C2PA library
         * @return Error message string or null if no error
         */
        @JvmStatic
        external fun getError(): String?
        
        /**
         * Load settings from a configuration string
         * @param settings Configuration string
         * @param format Format of the configuration (e.g., "json")
         * @return 0 on success, non-zero on error
         */
        @JvmStatic
        external fun loadSettings(settings: String, format: String): Int
        
        /**
         * Read a manifest store from a file
         * @param path Path to the file
         * @param dataDir Optional data directory for relative paths
         * @return JSON string representing the manifest store or null on error
         */
        @JvmStatic
        external fun readFile(path: String, dataDir: String?): String?
        
        /**
         * Read an ingredient from a file
         * @param path Path to the file
         * @param dataDir Data directory for relative paths
         * @return JSON string representing the ingredient or null on error
         */
        @JvmStatic
        external fun readIngredientFile(path: String, dataDir: String): String?
        
        /**
         * Create a C2PA builder from a JSON manifest definition
         * @param manifestJson JSON string defining the manifest
         * @return Native pointer to the builder (as Long) or 0 on error
         */
        @JvmStatic
        external fun builderFromJson(manifestJson: String): Long
        
        /**
         * Free a C2PA builder
         * @param builderPtr Native pointer to the builder
         */
        @JvmStatic
        external fun builderFree(builderPtr: Long)
        
        /**
         * Set the no-embed flag on a builder
         * @param builderPtr Native pointer to the builder
         */
        @JvmStatic
        external fun builderSetNoEmbed(builderPtr: Long)
        
        /**
         * Set the remote URL on a builder
         * @param builderPtr Native pointer to the builder
         * @param remoteUrl Remote URL string
         * @return 0 on success, non-zero on error
         */
        @JvmStatic
        external fun builderSetRemoteUrl(builderPtr: Long, remoteUrl: String): Int
        
        /**
         * Create a C2PA reader from a stream
         * @param format Format of the asset (e.g., "image/jpeg")
         * @param streamPtr Native pointer to the stream
         * @return Native pointer to the reader (as Long) or 0 on error
         */
        @JvmStatic
        external fun readerFromStream(format: String, streamPtr: Long): Long
        
        /**
         * Free a C2PA reader
         * @param readerPtr Native pointer to the reader
         */
        @JvmStatic
        external fun readerFree(readerPtr: Long)
        
        /**
         * Get JSON representation from a C2PA reader
         * @param readerPtr Native pointer to the reader
         * @return JSON string or null on error
         */
        @JvmStatic
        external fun readerJson(readerPtr: Long): String?
        
        /**
         * Create a C2PA signer from signing information
         * @param alg Signing algorithm (e.g., "ES256")
         * @param signCert Signing certificate
         * @param privateKey Private key
         * @param taUrl Optional timestamp authority URL
         * @return Native pointer to the signer (as Long) or 0 on error
         */
        @JvmStatic
        external fun signerFromInfo(alg: String, signCert: String, privateKey: String, taUrl: String?): Long
        
        /**
         * Free a C2PA signer
         * @param signerPtr Native pointer to the signer
         */
        @JvmStatic
        external fun signerFree(signerPtr: Long)
        
        /**
         * Get the signature reserve size for a signer
         * @param signerPtr Native pointer to the signer
         * @return Size to reserve for signature or -1 on error
         */
        @JvmStatic
        external fun signerReserveSize(signerPtr: Long): Long
        
        /**
         * Sign a manifest using the builder
         * @param builderPtr Native pointer to the builder
         * @param format Format of the asset
         * @param sourcePtr Native pointer to the source stream
         * @param destPtr Native pointer to the destination stream
         * @param signerPtr Native pointer to the signer
         * @return Size of manifest bytes written or -1 on error
         */
        @JvmStatic
        external fun builderSign(builderPtr: Long, format: String, sourcePtr: Long, destPtr: Long, signerPtr: Long): Long
        
        /**
         * Write builder to archive
         * @param builderPtr Native pointer to the builder
         * @param streamPtr Native pointer to the stream
         * @return 0 on success, non-zero on error
         */
        @JvmStatic
        external fun builderToArchive(builderPtr: Long, streamPtr: Long): Int
        
        /**
         * Add a resource to the builder
         * @param builderPtr Native pointer to the builder
         * @param uri URI of the resource
         * @param streamPtr Native pointer to the resource stream
         * @return 0 on success, non-zero on error
         */
        @JvmStatic
        external fun builderAddResource(builderPtr: Long, uri: String, streamPtr: Long): Int
        
        /**
         * Add an ingredient to the builder from a stream
         * @param builderPtr Native pointer to the builder
         * @param ingredientJson JSON string defining the ingredient
         * @param format Format of the ingredient asset
         * @param sourcePtr Native pointer to the source stream
         * @return 0 on success, non-zero on error
         */
        @JvmStatic
        external fun builderAddIngredientFromStream(builderPtr: Long, ingredientJson: String, format: String, sourcePtr: Long): Int
        
        /**
         * Write a reader resource to a stream
         * @param readerPtr Native pointer to the reader
         * @param uri URI of the resource to extract
         * @param streamPtr Native pointer to the destination stream
         * @return Size of data written or -1 on error
         */
        @JvmStatic
        external fun readerResourceToStream(readerPtr: Long, uri: String, streamPtr: Long): Long
    }
}

/**
 * Exception thrown when C2PA operations fail
 */
class C2PAException(message: String) : Exception(message)

/**
 * Higher-level wrapper class for easier C2PA usage
 */
class C2PAManager {
    
    /**
     * Get the C2PA library version
     */
    fun getVersion(): String {
        return C2PA.getVersion() ?: throw C2PAException("Failed to get version")
    }
    
    /**
     * Check for and throw any pending C2PA errors
     */
    private fun checkError() {
        val error = C2PA.getError()
        if (error != null) {
            throw C2PAException(error)
        }
    }
    
    /**
     * Load C2PA settings from JSON
     */
    fun loadSettings(settingsJson: String) {
        val result = C2PA.loadSettings(settingsJson, "json")
        if (result != 0) {
            checkError()
            throw C2PAException("Failed to load settings")
        }
    }
    
    /**
     * Read manifest from file
     */
    fun readManifestFromFile(path: String, dataDir: String? = null): String {
        val result = C2PA.readFile(path, dataDir)
        if (result == null) {
            checkError()
            throw C2PAException("Failed to read file: $path")
        }
        return result
    }
    
    /**
     * Read ingredient from file  
     */
    fun readIngredientFromFile(path: String, dataDir: String): String {
        val result = C2PA.readIngredientFile(path, dataDir)
        if (result == null) {
            checkError()
            throw C2PAException("Failed to read ingredient file: $path")
        }
        return result
    }
}

/**
 * RAII wrapper for C2PA Builder
 */
class C2PABuilder(manifestJson: String) : AutoCloseable {
    private var builderPtr: Long = 0
    
    init {
        builderPtr = C2PA.builderFromJson(manifestJson)
        if (builderPtr == 0L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to create builder: ${error ?: "Unknown error"}")
        }
    }
    
    fun setNoEmbed() {
        checkValid()
        C2PA.builderSetNoEmbed(builderPtr)
    }
    
    fun setRemoteUrl(url: String) {
        checkValid()
        val result = C2PA.builderSetRemoteUrl(builderPtr, url)
        if (result != 0) {
            val error = C2PA.getError()
            throw C2PAException("Failed to set remote URL: ${error ?: "Unknown error"}")
        }
    }
    
    fun addResource(uri: String, streamPtr: Long) {
        checkValid()
        val result = C2PA.builderAddResource(builderPtr, uri, streamPtr)
        if (result != 0) {
            val error = C2PA.getError()
            throw C2PAException("Failed to add resource: ${error ?: "Unknown error"}")
        }
    }
    
    fun addIngredientFromStream(ingredientJson: String, format: String, sourcePtr: Long) {
        checkValid()
        val result = C2PA.builderAddIngredientFromStream(builderPtr, ingredientJson, format, sourcePtr)
        if (result != 0) {
            val error = C2PA.getError()
            throw C2PAException("Failed to add ingredient: ${error ?: "Unknown error"}")
        }
    }
    
    fun sign(format: String, sourcePtr: Long, destPtr: Long, signerPtr: Long): Long {
        checkValid()
        val result = C2PA.builderSign(builderPtr, format, sourcePtr, destPtr, signerPtr)
        if (result == -1L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to sign: ${error ?: "Unknown error"}")
        }
        return result
    }
    
    fun toArchive(streamPtr: Long) {
        checkValid()
        val result = C2PA.builderToArchive(builderPtr, streamPtr)
        if (result != 0) {
            val error = C2PA.getError()
            throw C2PAException("Failed to create archive: ${error ?: "Unknown error"}")
        }
    }
    
    private fun checkValid() {
        if (builderPtr == 0L) {
            throw C2PAException("Builder has been closed")
        }
    }
    
    override fun close() {
        if (builderPtr != 0L) {
            C2PA.builderFree(builderPtr)
            builderPtr = 0L
        }
    }
}

/**
 * RAII wrapper for C2PA Reader
 */
class C2PAReader(format: String, streamPtr: Long) : AutoCloseable {
    private var readerPtr: Long = 0
    
    init {
        readerPtr = C2PA.readerFromStream(format, streamPtr)
        if (readerPtr == 0L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to create reader: ${error ?: "Unknown error"}")
        }
    }
    
    fun toJson(): String {
        checkValid()
        val result = C2PA.readerJson(readerPtr)
        if (result == null) {
            val error = C2PA.getError()
            throw C2PAException("Failed to get JSON: ${error ?: "Unknown error"}")
        }
        return result
    }
    
    fun resourceToStream(uri: String, streamPtr: Long): Long {
        checkValid()
        val result = C2PA.readerResourceToStream(readerPtr, uri, streamPtr)
        if (result == -1L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to extract resource: ${error ?: "Unknown error"}")
        }
        return result
    }
    
    private fun checkValid() {
        if (readerPtr == 0L) {
            throw C2PAException("Reader has been closed")
        }
    }
    
    override fun close() {
        if (readerPtr != 0L) {
            C2PA.readerFree(readerPtr)
            readerPtr = 0L
        }
    }
}

/**
 * RAII wrapper for C2PA Signer
 */
class C2PASigner(alg: String, signCert: String, privateKey: String, taUrl: String? = null) : AutoCloseable {
    private var signerPtr: Long = 0
    
    init {
        signerPtr = C2PA.signerFromInfo(alg, signCert, privateKey, taUrl)
        if (signerPtr == 0L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to create signer: ${error ?: "Unknown error"}")
        }
    }
    
    fun reserveSize(): Long {
        checkValid()
        val result = C2PA.signerReserveSize(signerPtr)
        if (result == -1L) {
            val error = C2PA.getError()
            throw C2PAException("Failed to get reserve size: ${error ?: "Unknown error"}")
        }
        return result
    }
    
    internal fun getPtr(): Long = signerPtr
    
    private fun checkValid() {
        if (signerPtr == 0L) {
            throw C2PAException("Signer has been closed")
        }
    }
    
    override fun close() {
        if (signerPtr != 0L) {
            C2PA.signerFree(signerPtr)
            signerPtr = 0L
        }
    }
}