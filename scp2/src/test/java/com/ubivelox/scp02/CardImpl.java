package com.ubivelox.scp02;



import com.ubivelox.gaia.GaiaException;
import com.ubivelox.gaia.util.GaiaUtils;
import com.ubivelox.scp02.Scp02.Card;

import exception.UbiveloxException;

public class CardImpl implements CApduService
{
   
 // off-Card I.U의 헥사 스트링을 받아 off-Card로 보내는 R-APDU
    @Override
    public String sendApdu(final String initializeUpdateCAPDU) throws GaiaException, UbiveloxException
    {
        GaiaUtils.checkHexaString(initializeUpdateCAPDU);
        String hostChallenge = initializeUpdateCAPDU.substring(10, initializeUpdateCAPDU.length());

        String initializeUpdateRAPDU = Card.InitializeUpdate_R_APDU;
        String sequenceCounter = initializeUpdateRAPDU.substring(24, 28);
        String cardChallenge = initializeUpdateRAPDU.substring(28, 40);

        String sessionkey = Scp02.getSessionKeyENC("S-ENC", sequenceCounter);
        byte[] sessionkeyArray = GaiaUtils.convertHexaStringToByteArray(sessionkey + sessionkey.substring(0, sessionkey.length() / 2));

        String cardCryptogramTmp = Ddes.encrypt(hostChallenge + sequenceCounter + cardChallenge + "8000000000000000", "DESede", "DESede/CBC/NoPadding", sessionkeyArray);

        if ( initializeUpdateRAPDU.substring(initializeUpdateRAPDU.length()-16).equals(cardCryptogramTmp.substring(cardCryptogramTmp.length() - 16)) )
        {
            return initializeUpdateRAPDU;
        }
        else
        {
            throw new UbiveloxException("일치하지 않음");
        }
    }
}