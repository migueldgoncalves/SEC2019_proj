import pteidlib.PTEID_Certif;
import pteidlib.PteidException;
import pteidlib.pteid;
import sun.security.pkcs11.wrapper.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class eIDLib_PKCS11_test {

    // Available certificates in Cartao de Cidadao
    private static final int CITIZEN_AUTHENTICATION_CERTIFICATE = 0; // WILL require PIN
    private static final int CITIZEN_SIGNATURE_CERTIFICATE = 1; //WILL require PIN, has legal value!
    private static final int SIGNATURE_SUB_CA = 2;
    private static final int AUTHENTICATION_SUB_CA = 3;
    private static final int Baltimore_CyberTrust_Root = 4;
    private static final int Cartao_de_Cidadao_001 = 5;
    private static final int Cartao_de_Cidadao_002 = 6;
    private static final int Cartao_de_Cidadao_003 = 7;
    private static final int Cartao_de_Cidadao_004 = 8;
    private static final int COMODO_RSA_Certification_Authority = 9;
    private static final int ECRaizEstado = 10;
    private static final int Global_Chambersign_Root_2008 = 11;
    private static final int MULTICERT_Root_Certification_Authority_01 = 12;

    private static final int CERTIFICATE_TO_USE = Cartao_de_Cidadao_001;

    // Slot - A logical reader that potentially contains a token
    // Token - The logical view of a cryptographic device defined by Cryptoki
    private static final int SLOT_ID = 0; //0 is the only valid value if you have a single smart card

    public static void main(String[] args) {

        try {
            System.out.println("            //Load the PTEidlibj");

            System.loadLibrary("pteidlibj"); //You need this line
            pteid.Init(""); // Initializes the eID Lib, requires card reader AND card inserted
            pteid.SetSODChecking(false); // Don't check the integrity of the ID, address and photo (!)

            PKCS11 pkcs11;
            String osName = System.getProperty("os.name");

            java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

            String libName = "libbeidpkcs11.so"; //For Linux - Will be changed below if Windows or Mac
            if (-1 != osName.indexOf("Windows"))
                libName = "pteidpkcs11.dll";
            else if (-1 != osName.indexOf("Mac"))
                libName = "pteidpkcs11.dylib";

            // access the ID and Address data via the pteidlib
            System.out.println("            -- accessing the ID  data via the pteidlib interface");

            // There are 13 certificates and 2 private keys in the card
            X509Certificate cert = getCertFromByteArray(getCertificateInBytes(CERTIFICATE_TO_USE)); //Does NOT require PINs
            System.out.println("Printing certificate: " + cert);

            // access the ID and Address data via the pteidlib
            System.out.println("            -- generating signature via the PKCS11 interface");

            Class pkcs11Class = Class.forName("sun.security.pkcs11.wrapper.PKCS11"); //Returns a PKCS11 class object

            // Returns Method object: "getInstance" is the method name, with arguments (String, String, CK_C_INITIALIZE_ARGS, boolean)
            Method getInstanceMethode = pkcs11Class.getDeclaredMethod("getInstance", String.class, String.class, CK_C_INITIALIZE_ARGS.class, boolean.class);
            // Invokes getInstance method with args (libName, "C_GetFunctionList", null, false). Obj can be null because getInstance is static
            pkcs11 = (PKCS11) getInstanceMethode.invoke(null, new Object[]{libName, "C_GetFunctionList", null, false});

            //Open the PKCS11 session
            System.out.println("            //Open the PKCS11 session");

            // C_OpenSession
            // Opens a connection between an application and a particular token or sets up an application callback for token insertion
            // SLOT_ID - Identifies logic reader potentially with token, in this case the smart card connected
            // PKCS11Constants.CKF_SERIAL_SESSION - Corresponds to CK_FLAGS parameter, it is a valid argument
            // RETURNS session's handle

            long p11_session = pkcs11.C_OpenSession(SLOT_ID, PKCS11Constants.CKF_SERIAL_SESSION, null, null);
            //CK_SESSION_INFO info = pkcs11.C_GetSessionInfo(p11_session);

            // Get available keys
            System.out.println("            //Get available keys");
            // An array of CK_ATTRIBUTEs is called a “template” and is used for creating, manipulating and searching for objects
            CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[1]; //A array of CK_ATTRIBUTE size 1
            // CK_ATTRIBUTE is a structure that includes the type, value, and length of an attribute
            attributes[0] = new CK_ATTRIBUTE();
            // Type - Attribute type
            attributes[0].type = PKCS11Constants.CKA_CLASS; // CKA_CLASS define object class
            // pValue - Pointer to the value of the attribute
            attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY); // CKO_PRIVATE_KEY objects hold private keys

            // C_FindObjectsInit
            // Initializes a search for token and session objects that match a template
            // p11_session - Session's handle
            // attributes - The value of this argument should point to a search template that
            // specifies the attribute values to match

            // After calling C_FindObjectsInit, the application may call C_FindObjects one or more times to obtain handles
            // for objects matching the template, and then eventually call C_FindObjectsFinal to finish the active search
            // operation. At most one search operation may be active at a given time in a given session.

            pkcs11.C_FindObjectsInit(p11_session, attributes);

            // C_FindObjects
            // to be continued...

            long[] keyHandles = pkcs11.C_FindObjects(p11_session, 5);

            // points to auth_key
            System.out.println("            //points to auth_key. No. of keys:" + keyHandles.length);

            long signatureKey = keyHandles[0];        //test with other keys to see what you get
            pkcs11.C_FindObjectsFinal(p11_session);

            // initialize the signature method
            System.out.println("            //initialize the signature method");
            CK_MECHANISM mechanism = new CK_MECHANISM();
            mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
            mechanism.pParameter = null;
            pkcs11.C_SignInit(p11_session, mechanism, signatureKey);

            //...

            // sign
            System.out.println("            //sign");
            //byte[] signature = pkcs11.C_Sign(p11_session, "data".getBytes(Charset.forName("UTF-8"))); //WILL ask for authentication PIN
            //System.out.println("            //signature:"+encoder.encode(signature));

            //...

            pteid.Exit(pteid.PTEID_EXIT_LEAVE_CARD); //OBRIGATORIO Termina a eID Lib

        } catch (Throwable e) {
            System.out.println("[Catch] Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Returns the n-th certificate, starting from 0
    private static byte[] getCertificateInBytes(int n) {
        byte[] certificate_bytes = null;
        try {
            PTEID_Certif[] certs = pteid.GetCertificates();
            certificate_bytes = certs[n].certif; //gets the byte[] with the n-th certif*/
        } catch (PteidException e) {
            e.printStackTrace();
        }
        return certificate_bytes;
    }

    public static X509Certificate getCertFromByteArray(byte[] certificateEncoded) throws CertificateException {
        CertificateFactory f = CertificateFactory.getInstance("X.509");
        InputStream in = new ByteArrayInputStream(certificateEncoded);
        X509Certificate cert = (X509Certificate) f.generateCertificate(in);
        return cert;
    }
}