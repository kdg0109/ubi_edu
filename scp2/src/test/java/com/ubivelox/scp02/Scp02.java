package com.ubivelox.scp02;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.ubivelox.gaia.GaiaException;
import com.ubivelox.gaia.util.GaiaUtils;

import exception.UbiveloxException;

public class Scp02
{

    // D1 : 8050000008EC78EEA2438008A6
    // D2 : 00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC
    // D3 : 848200001070CA81178C079A4A114998A816CBF511
    // ENC Key : 404043434545464649494A4A4C4C4F4F
    // MAC Key : 404043434545464649494A4A4C4C4F4F
    // DE Key : 404043434545464649494A4A4C4C4F4F

    CApduService capduService;
    
    public static class OffCard
    {
      public static final String  D1 = "8050000008EC78EEA2438008A6";
      public static final String  D3 = "848200001070CA81178C079A4A114998A816CBF511";
    }
    
    public static class Card
    {
        public static final String  D2 = "00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC";
    }
    
    public static class Key
    {
        public static final String  ENCKey = "404043434545464649494A4A4C4C4F4F";
        public static final String  MACKey = "404043434545464649494A4A4C4C4F4F";
        public static final String  DEKKey = "404043434545464649494A4A4C4C4F4F";
        
     // @formatter:off
        public final static byte[] ENC = new byte[] { 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46
                                       , 0x49, 0x49, 0x4A, 0x4A, 0x4C, 0x4C, 0x4F, 0x4F
                                       , 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46};

        public final static byte[] MAC = new byte[] { 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46
                                         , 0x49, 0x49, 0x4A, 0x4A, 0x4C, 0x4C, 0x4F, 0x4F
                                         , 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46};

        public final static byte[] DEK = new byte[] { 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46
                                         , 0x49, 0x49, 0x4A, 0x4A, 0x4C, 0x4C, 0x4F, 0x4F
                                         , 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46};
    }
    




    // off-Card가 Card로 보내는 APDU
    public static String initializeUpdate(final String hexString) throws GaiaException
    {
        GaiaUtils.checkHexaString(hexString);
        String hostChallenge = hexString;
        String cAPDU = "8050000008" + hostChallenge;
        return cAPDU;
    }

    public static String getSessionKeyENC(final String sessionTypeOrg, final String sequence_counter) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException, GaiaException
    {
        GaiaUtils.checkNullOrEmpty(sessionTypeOrg, sequence_counter);
        // constant for C-MAC: '0101'
        // constant for R-MAC: '0102'
        // constant for S-ENC: '0182'
        // constant for DEK: '0181'
        
        String sessionType = "";

        if ( sessionTypeOrg.contains("C-MAC") )
        {
            sessionType = "0101";
        }
        else if ( sessionTypeOrg.contains("R-MAC") )
        {
            sessionType = "0102";
        }
        else if ( sessionTypeOrg.contains("S-ENC") )
        {
            sessionType = "0182";
        }
        else if ( sessionTypeOrg.contains("DEK") )
        {
            sessionType = "0181";
        }

        String S_ENC = Ddes.encrypt(sessionType + sequence_counter + "000000000000000000000000", "DESede", "DESede/CBC/NoPadding", Key.ENC);
        // System.out.println("S_ENC : " + S_ENC);
        return S_ENC;
    }




 // off-Card가 Card로 보내는 ExternalAuthenticate APDU
    public static String externalAuthenticate(final String D2) throws UbiveloxException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException, GaiaException
    {
        GaiaUtils.checkHexaString(D2);
        CardImpl cardImpl = new CardImpl();
        String initializeUpdateResponse = cardImpl.sendApdu(OffCard.D1);

        if ( D2.equals(initializeUpdateResponse) )
        {
            initializeUpdateResponse += "9000";
        }
        else
        {
            initializeUpdateResponse += "6A88";
        }

        if ( initializeUpdateResponse.substring(initializeUpdateResponse.length() - 4, initializeUpdateResponse.length())
                                     .equals("9000") )
        {
            // 84 82 00 00 10 70CA81178C079A4A 114998A816CBF511
            // host cryptogram과 MAC 생성
            String D1 = OffCard.D1;

            String hostChallenge = D1.substring(10, D1.length());
            String sequenceCounter = D2.substring(24, 28);
            String cardChallenge = D2.substring(28, 40);
            
            String sessionkey = getSessionKeyENC("S-ENC", sequenceCounter);
            
            byte[] sessionkeyByteArray = GaiaUtils.convertHexaStringToByteArray(sessionkey + sessionkey.substring(0, sessionkey.length() / 2));

            String hostCryptogramTmp = Ddes.encrypt(sequenceCounter + cardChallenge + hostChallenge + "8000000000000000", "DESede", "DESede/CBC/NoPadding", sessionkeyByteArray);

            String hostCryptogram = hostCryptogramTmp.substring(hostCryptogramTmp.length() - 16, hostCryptogramTmp.length());
            System.out.println(hostCryptogram);

            // S-MAC 구하고 07EFCCEB0BB0CC01 A22E0CE1E1E395F8

            sessionkey = getSessionKeyENC("C-MAC", sequenceCounter);
            sessionkeyByteArray = GaiaUtils.convertHexaStringToByteArray(sessionkey);

            System.out.println("S-MAC 생성 : " + GaiaUtils.convertByteArrayToHexaString(sessionkeyByteArray));

            // 848200001070CA81178C079A4A8000000000000000
            // C-MAC 구해야함 Retail Mac

            String D3 = OffCard.D3;

            String dataTmp = D3.substring(0, D3.length() - 16) + "800000";
            System.out.println("dataTmp : " + dataTmp + " / " + dataTmp.length());

            byte[] result = retailMac(sessionkeyByteArray, GaiaUtils.convertHexaStringToByteArray(dataTmp));
            System.out.println("result : " + GaiaUtils.convertByteArrayToHexaString(result));

            return OffCard.D3;
        }
        throw new UbiveloxException("일치하지 않음");
    }





    public static byte[] retailMac(final byte[] key, final byte[] data) throws GaiaException
    {
        GaiaUtils.checkNullOrEmpty(key, data);
        int loc = 0;
        byte[] edata;
        // Create Keys
        byte[] key1 = Arrays.copyOf(key, 8);
        byte[] key2 = Arrays.copyOfRange(key, 8, 16);

        try
        {
            
            Cipher cipherA = Ddes.setInit("DES", Cipher.ENCRYPT_MODE, "DES/CBC/NoPadding", key1);

            Cipher cipherB = Ddes.setInit("DES", Cipher.DECRYPT_MODE, "DES/CBC/NoPadding", key2);

            
            // Encrypt block by block with Key-A
            edata = cipherA.doFinal(data);

            byte[] x = new byte[8];
            System.arraycopy(data, loc, x, 0, 8);

            edata = cipherA.doFinal(x);

            for ( loc = 8; loc < data.length; loc += 8 )
            {
                System.arraycopy(data, loc, x, 0, 8);
                byte[] y = xor_array(edata, x);
                edata = cipherA.doFinal(y);
            }
            // Decrypt the resulting block with Key-B
            edata = cipherB.doFinal(edata);
            // Encrypt the resulting block with Key-A
            edata = cipherA.doFinal(edata);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
        return edata;
    }





    private static byte[] xor_array(final byte[] aFirstArray, final byte[] aSecondArray) throws GaiaException
    {
        GaiaUtils.checkNullOrEmpty(aFirstArray, aSecondArray);
        
        byte[] result = new byte[aFirstArray.length];

        for ( int i = 0; i < result.length; i++ )
        {
            result[i] = (byte) (aFirstArray[i] ^ aSecondArray[i]);
        }
        return result;
    }
    
    
}
