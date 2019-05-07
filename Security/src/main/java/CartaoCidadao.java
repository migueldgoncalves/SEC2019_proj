import pteidlib.PTEID_Certif;
import pteidlib.PteidException;
import pteidlib.pteid;
import sun.security.pkcs11.wrapper.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * This class deals with access to Cartao de Cidadao (CC) data and operations.
 * WARNING: Do not access it concurrently.
 */
public class CartaoCidadao {

    /**
     * There are two main available certificates in the CC,
     * with the corresponding private keys:
     * The authentication certificate (represented by 0);
     * and the digital signature certificate (represented by 1).
     * WARNING: The digital signature keys have legal value when used!
     * There are other 11 certificates available in the CC, without the
     * corresponding private keys
     */
    private static final int CERTIFICATE_TO_USE = 0;

    /**
     * Max number of object handles (references) to get from a search in the CC.
     */
    private static final int MAX_OBJECT_COUNT = 5;

    /**
     * WARNING: Setting this parameter to 1 will return digital signature
     * private key handle, which has legal value!
     */
    private static final int AUTHENTICATION_PRIVATE_KEY_HANDLE = 0;

    /**
     * Token - The logical view of a cryptographic device defined by Cryptoki,
     * such as a smart card.
     * Slot - A logical reader that potentially contains a token.
     * 0 is the only valid value for this parameter if you introduce a single
     * smart card.
     */
    private static final int SLOT_ID = 0;

    /**
     * Instance of implementation of the PKCS11 library - Needed to access CC
     */
    private static PKCS11 pkcs11 = null;

    /**
     * A library required in Linux - Name will be changed in setUp() if Windows or Mac.
     */
    private static String libName = "libbeidpkcs11.so";

    /**
     * Sets up the CC library in the system in order to access the CC.
     * Must be called before accessing CC's data and operations.
     * Doesn't need to be called again in runtime until exitPteid() is called.
     * This method does NOT require PIN.
     */
    public static void setUp() {

        try {
            System.out.println("            //Load the PTEidlibj");

            System.loadLibrary("pteidlibj"); //This line is needed, loads CC library into the system
            pteid.Init(""); // Initializes the eID Lib, requires card reader AND card inserted
            pteid.SetSODChecking(false); // Setting to false doesn't check integrity of ID, address and photo - Allows use of test CCs

            String osName = System.getProperty("os.name");

            if (osName.contains("Windows"))
                libName = "pteidpkcs11.dll";
            else if (osName.contains("Mac"))
                libName = "pteidpkcs11.dylib";

        } catch (Exception e) {
            System.out.println("Could not set up the Cartao de Cidadao");
            e.printStackTrace();
        }
    }

    /**
     * Signs a message with one of the private keys from inserted CC.
     * Respective PIN will be asked before signing with either authentication
     * or digital signature private keys.
     * @param message A string whose signature is needed, cannot be null.
     * @return A byte array with the signature of the received string
     */
    public static byte[] sign(String message) {

        try {
            System.out.println("            -- generating signature via the PKCS11 interface");

            // 1 - The first thing to do is to get an instance of the PKCS11 interface implementation

            Class pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11"); //Returns a PKCS11 class object

            // getInstance method
            // public static synchronized PKCS11 getInstance(String pkcs11ModulePath,
            //            String functionList, CK_C_INITIALIZE_ARGS pInitArgs,
            //            boolean omitInitialize)
            // RETURNS PKCS11 object

            // Returns Method object: "getInstance" is the method name, and receives arguments (String, String, CK_C_INITIALIZE_ARGS, boolean)
            Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class);
            // Invokes getInstance method with args (libName, "C_GetFunctionList", null, false). Object can be null because getInstance() is static
            pkcs11 = (PKCS11) getInstanceMethode.invoke(null, new Object[]{libName, "C_GetFunctionList", null, false});

            // 2 - A session is needed to access CC data and operations - It must be opened beforehand

            System.out.println("            //Open the PKCS11 session");

            // C_OpenSession
            // public native long C_OpenSession(long slotID, long flags,
            //      Object pApplication, CK_NOTIFY Notify)
            // Opens a connection between an application and a particular token or sets up an application callback
            //      for token insertion
            // SLOT_ID - Identifies logic reader potentially with token, in this case the smart card connected
            // PKCS11Constants.CKF_SERIAL_SESSION - Corresponds to CK_FLAGS parameter, it is a valid argument
            // pApplication - Passed to callback, null in this case
            // Notify - Notify the callback function, also null in this case
            // RETURNS session's handle

            long p11_session = pkcs11.C_OpenSession(SLOT_ID, PKCS11Constants.CKF_SERIAL_SESSION, null, null);

            // 3 - Private keys in the CC cannot be obtained, only their handles. Before searching for them, it is necessary
            // to specify the desired object type to search for

            System.out.println("            //Get available keys");

            // An array of CK_ATTRIBUTEs is called a “template” and is used for creating, manipulating and searching for objects
            CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1]; //An array of CK_ATTRIBUTE size 1 - Just one object type will be searched
            // CK_ATTRIBUTE is a structure that includes the type, value, and length of an attribute
            attributes[0] = new CK_ATTRIBUTE();
            // Type - Attribute type
            attributes[0].type = PKCS11Constants.CKA_CLASS; // CKA_CLASS defines object class
            // pValue - Pointer to the value of the attribute
            attributes[0].pValue = PKCS11Constants.CKO_PRIVATE_KEY; // CKO_PRIVATE_KEY objects hold private keys

            // 4 - Now that the objects to search for are established, the search operation can be initialized
            // It needs to be initialized before the search, and closed after searches are completed

            // C_FindObjectsInit
            // public native void C_FindObjectsInit(long hSession, CK_ATTRIBUTE[] pTemplate)
            // Initializes a search for token and session objects that match a template
            // p11_session - Session's handle
            // attributes - The value of this argument should point to a search template that
            // specifies the attribute values to match
            // RETURNS void

            // 5 - After calling C_FindObjectsInit, the application may call C_FindObjects one or more times to obtain handles
            // for objects matching the template, and then eventually call C_FindObjectsFinal to finish the active search
            // operation. At most one search operation may be active at a given time in a given session.

            pkcs11.C_FindObjectsInit(p11_session, attributes);

            // C_FindObjects
            // public native long[] C_FindObjects(long hSession, long ulMaxObjectCount)
            // Continues a search for token and session objects that match a template, obtaining additional object handles
            // p11_session - Session's handle
            // MAX_OBJECT_COUNT - Max number of object handles to get in the search
            // RETURNS object handles

            long[] keyHandles = pkcs11.C_FindObjects(p11_session, MAX_OBJECT_COUNT);

            // Authentication private key handle
            long signatureKey = keyHandles[AUTHENTICATION_PRIVATE_KEY_HANDLE];

            // C_FindObjectsFinal
            // public native void C_FindObjectsFinal(long hSession)
            // Finishes a search for token and session objects
            // p11_session - Session handle
            // RETURNS void

            // 6 - The desired private key handle was found, now the search can be closed
            // Afterwards, method will proceed to sign the message

            pkcs11.C_FindObjectsFinal(p11_session);

            System.out.println("            //initialize the signature method");

            // 7 - CK_MECHANISM is a structure that specifies a particular mechanism and any parameters it requires
            // In this case, a mechanism of getting a message hash with SHA1 hash function will be defined

            CK_MECHANISM mechanism = new CK_MECHANISM();
            // "mechanism" defines mechanism type (There are several available)
            mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
            // pParameter is a pointer to the parameter required by mechanism type - In this case no parameter is needed
            mechanism.pParameter = null;

            // 8 - As with search operation, signing operation must also be initialized before use

            // C_SignInit
            // public synchronized void C_SignInit(long hSession, CK_MECHANISM pMechanism, long hKey)
            // Initializes a signature (private key encryption) operation, where the signature is (will be) an appendix to
            //      the data, and plaintext cannot be recovered from the signature
            // p11_session - Session's handle
            // mechanism - Signature mechanism
            // signatureKey - Handle to private key
            // RETURNS void

            pkcs11.C_SignInit(p11_session, mechanism, signatureKey);

            System.out.println("            //sign");

            // C_Sign
            // public native byte[] C_Sign(long hSession, byte[] pData)
            // Signs (encrypts with private key) data in a single part, where the signature is (will be) an appendix to the
            //      data, and plaintext cannot be recovered from the signature
            // p11_session - Session's handle
            // pData - Data to sign
            // RETURNS signature

            // WILL ask for authentication PIN, is the only line in the class that asks for PIN
            byte[] signature = pkcs11.C_Sign(p11_session, message.getBytes(Charset.forName("UTF-8")));

            // 9 - Signature of received string was obtained - It can now be printed and returned
            // Closing session takes place in exitPteid()

            for (int i = 0; i < signature.length; i++) {
                System.out.print(signature[i]);
            }
            System.out.println("\n");

            return signature;
        } catch (Throwable e) {
            System.out.println("There was a problem signing the message");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes CC session and exits from CC library.
     */
    public static void exitPteid() {
        try {
            pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD); //OBRIGATORIO Termina a eID Lib
            pkcs11 = null;
        } catch (Exception e) {
            System.out.println("There was a problem exiting from CC library");
            e.printStackTrace();
        }
    }

    /**
     * Gets the byte array with the authentication certificate from the CC.
     * This method does NOT require PIN.
     * @return Byte array with the authentication public key certificate,
     * which can be passed to getCertFromByteArray() to convert to X509 cert.
     */
    protected static byte[] getCertificateInBytes() {
        byte[] certificate_bytes = null;
        try {
            PTEID_Certif[] certs = pteid.GetCertificates();
            certificate_bytes = certs[CERTIFICATE_TO_USE].certif; //Gets the byte[] with the selected certificate
        } catch (PteidException e) {
            System.out.println("Could not get desired certificate bytes");
            e.printStackTrace();
        }
        return certificate_bytes;
    }

    /**
     * Gets a X509 public key certificate from a byte array with certificate.
     * This method does NOT require PIN.
     * @param certificateEncoded The byte array with the public key certificate
     * @return Same certificate in X509 standard.
     * @throws CertificateException
     */
    protected static X509Certificate getCertFromByteArray(byte[] certificateEncoded) throws CertificateException {
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(certificateEncoded);
        X509Certificate cert = (X509Certificate) f.generateCertificate(in);
        return cert;
    }

    /**
     * Gets the public authentication key of the inserted CC.
     * This method does NOT require PIN.
     * @return The public authentication key of the inserted CC.
     */
    public static PublicKey getPublicKeyFromCertificate() {
        try {
            return getCertFromByteArray(getCertificateInBytes()).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}