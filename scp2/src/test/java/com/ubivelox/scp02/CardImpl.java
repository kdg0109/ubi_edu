package com.ubivelox.scp02;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.ubivelox.gaia.GaiaException;
import com.ubivelox.gaia.util.GaiaUtils;

import exception.UbiveloxException;

public class CardImpl implements CApduService
{
   
 // off-Card I.U의 헥사 스트링을 받아 off-Card로 보내는 R-APDU
    @Override
    public String sendApdu(final String D1) throws GaiaException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException, UbiveloxException
    {
        GaiaUtils.checkHexaString(D1);
        String hostChallenge = D1.substring(10, D1.length());

        String D2 = "00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC";
        String sequenceCounter = D2.substring(24, 28);
        String cardChallenge = D2.substring(28, 40);

        String sessionkey = Scp02.getSessionKeyENC("S-ENC", sequenceCounter);
        byte[] sessionkeyArray = GaiaUtils.convertHexaStringToByteArray(sessionkey + sessionkey.substring(0, sessionkey.length() / 2));

        String cardCryptogramTmp = Ddes.encrypt(hostChallenge + sequenceCounter + cardChallenge + "8000000000000000", "DESede", "DESede/CBC/NoPadding", sessionkeyArray);

        if ( D2.substring(D2.length()-16).equals(cardCryptogramTmp.substring(cardCryptogramTmp.length() - 16)) )
        {
            return D2;
        }
        else
        {
            throw new UbiveloxException("일치하지 않음");
        }
    }
}