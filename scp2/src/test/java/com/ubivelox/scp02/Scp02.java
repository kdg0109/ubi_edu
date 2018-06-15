package com.ubivelox.scp02;

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

    public static class OffCard
    {
        public static String InitializeUpdate_C_APDU = "8050000008EC78EEA2438008A6";
        public static String ExternalAuthenticate_C_APDU = "848200001070CA81178C079A4A114998A816CBF511";
    }

    public static class Card
    {
        public static String InitializeUpdate_R_APDU = "00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC";
    }

    public static class Key
    {
        public static final String ENCKey = "404043434545464649494A4A4C4C4F4F";
        public static final String MACKey = "505053535555565659595A5A5C5C5F5F";
        public static final String DEKKey = "606162636465666768696A6B6C6D6E6F6061626364656667";

     // @formatter:off
        public final static byte[] ENC = new byte[] { 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46
                                                    , 0x49, 0x49, 0x4A, 0x4A, 0x4C, 0x4C, 0x4F, 0x4F
                                                    , 0x40, 0x40, 0x43, 0x43, 0x45, 0x45, 0x46, 0x46};

        public final static byte[] MAC = new byte[] { 0x50, 0x50, 0x53, 0x53, 0x55, 0x55, 0x56, 0x56
                                                      , 0x59, 0x59, 0x5A, 0x5A, 0x5C, 0x5C, 0x5F, 0x5F
                                                      , 0x50, 0x50, 0x53, 0x53, 0x55, 0x55, 0x56, 0x56};
        
        public final static byte[] DEK = new byte[] { 0x60, 0x60, 0x63, 0x63, 0x65, 0x65, 0x66, 0x66
                                         , 0x69, 0x69, 0x6A, 0x6A, 0x6C, 0x6C, 0x6F, 0x6F
                                         , 0x60, 0x60, 0x63, 0x63, 0x65, 0x65, 0x66, 0x66};

        // @formatter:on
    }
    




    // off-Card가 Card로 보내는 APDU
    public static String initializeUpdate(final String hexString) throws GaiaException, UbiveloxException
    {
        GaiaUtils.checkHexaString(hexString);
        
        if(hexString.length() != 16) {
            throw new UbiveloxException("data가 일치 하지 않음");
        }
        String hostChallenge = hexString;
        String cAPDU = OffCard.InitializeUpdate_C_APDU.substring(0, 10) + hostChallenge;
        return cAPDU;
    }

    public static String getSessionKeyENC(final String sessionTypeOrg, final String sequence_counter) throws UbiveloxException, GaiaException
    {
        GaiaUtils.checkNullOrEmpty(sessionTypeOrg, sequence_counter);
        // constant for C-MAC: '0101'
        // constant for R-MAC: '0102'
        // constant for S-ENC: '0182'
        // constant for DEK: '0181'
        
        byte[] keyType = null;
        String sessionType = "";

        if ( sessionTypeOrg.contains("C-MAC") )
        {
            keyType = Key.MAC;
            sessionType = "0101";
        }
        else if ( sessionTypeOrg.contains("R-MAC") )
        {
            sessionType = "0102";
        }
        else if ( sessionTypeOrg.contains("S-ENC") )
        {
            keyType = Key.ENC;
            sessionType = "0182";
        }
        else if ( sessionTypeOrg.contains("DEK") )
        {
            keyType = Key.DEK;
            sessionType = "0181";
        }

        String S_ENC = Ddes.encrypt(sessionType + sequence_counter + "000000000000000000000000", "DESede", "DESede/CBC/NoPadding", keyType);
        // System.out.println("S_ENC : " + S_ENC);
        return S_ENC;
    }

 


 // off-Card가 Card로 보내는 ExternalAuthenticate APDU
    public static String externalAuthenticate(final String InitializeUpdate_R_APDU) throws UbiveloxException, GaiaException
    {
        GaiaUtils.checkHexaString(InitializeUpdate_R_APDU);
//        CardImpl cardImpl = new CardImpl();
//        String initializeUpdateResponse = cardImpl.sendApdu(OffCard.InitializeUpdate_C_APDU);

        
        // 84 82 00 00 10 70CA81178C079A4A 114998A816CBF511
        // host cryptogram과 MAC 생성
        String initializeUpdate_C_APDU = OffCard.InitializeUpdate_C_APDU;
    
        String hostChallenge = initializeUpdate_C_APDU.substring(10, initializeUpdate_C_APDU.length());
        String sequenceCounter = InitializeUpdate_R_APDU.substring(24, 28);
        String cardChallenge = InitializeUpdate_R_APDU.substring(28, 40);
             
        String sessionkey = getSessionKeyENC("S-ENC", sequenceCounter);
            
        byte[] sessionkeyByteArray = GaiaUtils.convertHexaStringToByteArray(sessionkey + sessionkey.substring(0, sessionkey.length() / 2));

        String hostCryptogramTmp = Ddes.encrypt(sequenceCounter + cardChallenge + hostChallenge + "8000000000000000", "DESede", "DESede/CBC/NoPadding", sessionkeyByteArray);

        String hostCryptogram = hostCryptogramTmp.substring(hostCryptogramTmp.length() - 16, hostCryptogramTmp.length());
        System.out.println("hostCryptogram : " + hostCryptogram);

            // S-MAC 구하고 07EFCCEB0BB0CC01 A22E0CE1E1E395F8

        sessionkey = getSessionKeyENC("C-MAC", sequenceCounter);
        
        sessionkeyByteArray = GaiaUtils.convertHexaStringToByteArray(sessionkey);

        System.out.println("S-MAC : " + GaiaUtils.convertByteArrayToHexaString(sessionkeyByteArray));

            // 848200001070CA81178C079A4A8000000000000000
            // C-MAC 구해야함 Retail Mac

        String externalAuthenticate_C_APDU = OffCard.ExternalAuthenticate_C_APDU;

        String dataTmp = externalAuthenticate_C_APDU.substring(0, externalAuthenticate_C_APDU.length() - 16) + "800000";
        System.out.println("dataTmp : " + dataTmp + " / " + dataTmp.length());

        byte[] result = Ddes.retailMac(sessionkeyByteArray, GaiaUtils.convertHexaStringToByteArray(dataTmp));
        System.out.println("result : " + GaiaUtils.convertByteArrayToHexaString(result));
        System.out.println("result2 : " + externalAuthenticate_C_APDU.substring(externalAuthenticate_C_APDU.length()-16));

        if(GaiaUtils.convertByteArrayToHexaString(result).equals(externalAuthenticate_C_APDU.substring(externalAuthenticate_C_APDU.length()-16))) {
            return OffCard.ExternalAuthenticate_C_APDU; 
        }
        
        throw new UbiveloxException("C-MAC 에러");
    }

    
}
