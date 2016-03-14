package com.foodenak.itpscanner.services

import android.util.Log
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.*
import java.util.*
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by ITP on 5/23/2015.
 */
class CustomTrustManagers @Throws(KeyStoreException::class, InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, CertificateException::class)
constructor(trustStore: KeyStore) : X509TrustManager {

    private val originalX509TrustManager: X509TrustManager

    private val customX905TrustManager: X509TrustManager

    private val mParameters: PKIXParameters

    private val mValidator: CertPathValidator

    private val mFactory: CertificateFactory

    init {
        this.originalX509TrustManager = createTrustManager(null)

        this.customX905TrustManager = createTrustManager(trustStore)

        mParameters = PKIXParameters(trustStore)
        mParameters.isRevocationEnabled = false

        val validatorType = CertPathValidator.getDefaultType()
        mValidator = CertPathValidator.getInstance(validatorType)

        mFactory = CertificateFactory.getInstance("X509")
    }

    @Throws(KeyStoreException::class, NoSuchAlgorithmException::class)
    private fun createTrustManager(store: KeyStore?): X509TrustManager {
        val algorithm = TrustManagerFactory.getDefaultAlgorithm()
        val factory = TrustManagerFactory.getInstance(algorithm)
        factory.init(store)

        val originalTrustManagers = factory.trustManagers
        return originalTrustManagers[0] as X509TrustManager
    }

    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        try {
            originalX509TrustManager.checkServerTrusted(chain, authType)
        } catch (originalException: CertificateException) {
            Log.i(TAG, "fail with original trust manager", originalException)
            try {
                customX905TrustManager.checkServerTrusted(chain, authType)
            } catch (customException: CertificateException) {
                Log.i(TAG, "fail with custom trust manager", customException)
                try {
                    val reorderedChain = reorderCertificateChain(chain)
                    val certPath = mFactory.generateCertPath(Arrays.asList(*reorderedChain))

                    mValidator.validate(certPath, mParameters)
                } catch (ex: Exception) {
                    Log.i(TAG, "fail with custom validator", ex)
                    throw originalException
                }

            }

        }

    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }

    /**
     * Puts the certificate chain in the proper order, to deal with out-of-order
     * certificate chains as are sometimes produced by Apache's mod_ssl

     * @param chain the certificate chain, possibly with bad ordering
     * *
     * @return the re-ordered certificate chain
     */
    private fun reorderCertificateChain(chain: Array<X509Certificate>): Array<X509Certificate?> {

        val reorderedChain = arrayOfNulls<X509Certificate>(chain.size)
        val certificates = Arrays.asList(*chain)

        var position = chain.size - 1
        val rootCert = findRootCert(certificates)
        reorderedChain[position] = rootCert

        var cert = rootCert
        while ((cert) != null && position > 0) {
            reorderedChain[--position] = cert
            cert = findSignedCert(cert, certificates)
        }

        return reorderedChain
    }

    /**
     * A helper method for certificate re-ordering.
     * Finds the root certificate in a possibly out-of-order certificate chain.

     * @param certificates the certificate change, possibly out-of-order
     * *
     * @return the root certificate, if any, that was found in the list of certificates
     */
    private fun findRootCert(certificates: List<X509Certificate>): X509Certificate? {
        var rootCert: X509Certificate? = null

        for (cert in certificates) {
            val signer = findSigner(cert, certificates)
            if (signer == null || signer == cert) {
                // no signer present, or self-signed
                return cert;
            }
        }

        return rootCert
    }

    /**
     * A helper method for certificate re-ordering.
     * Finds the first certificate in the list of certificates that is signed by the sigingCert.
     */
    private fun findSignedCert(signingCert: X509Certificate, certificates: List<X509Certificate>): X509Certificate? {
        var signed: X509Certificate? = null

        for (cert in certificates) {
            val signingCertSubjectDN = signingCert.subjectDN
            val certIssuerDN = cert.issuerDN
            if (certIssuerDN == signingCertSubjectDN && cert != signingCert) {
                signed = cert
                break
            }
        }

        return signed
    }

    /**
     * A helper method for certificate re-ordering.
     * Finds the certificate in the list of certificates that signed the signedCert.
     */
    private fun findSigner(signedCert: X509Certificate, certificates: List<X509Certificate>): X509Certificate? {
        var signer: X509Certificate? = null

        for (cert in certificates) {
            val certSubjectDN = cert.subjectDN
            val issuerDN = signedCert.issuerDN
            if (certSubjectDN == issuerDN) {
                signer = cert
                break
            }
        }

        return signer
    }

    companion object {

        private val TAG = "TrustManagers"
    }
}
