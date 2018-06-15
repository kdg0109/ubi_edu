package com.ubivelox.scp02;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.ubivelox.gaia.GaiaException;
import com.ubivelox.scp02.Scp02.Card;
import com.ubivelox.scp02.Scp02.OffCard;

import exception.UbiveloxException;

public class Scp02Test
{
    @Test
    public void test() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, GaiaException, UbiveloxException
    {
        assertEquals(OffCard.D1, Scp02.initializeUpdate("EC78EEA2438008A6"));
        assertEquals(OffCard.D3, Scp02.externalAuthenticate(Card.D2));
    }

    @Test
    public void test2() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, GaiaException, UbiveloxException
    {
        assertEquals(OffCard.D1, Scp02.initializeUpdate("EC78EEA2438008A6"));
        
        CApduService capduService = new CardImpl();
        String initializeUpdateResponse = capduService.sendApdu(OffCard.D1);
        
        assertEquals(OffCard.D3, Scp02.externalAuthenticate(initializeUpdateResponse));
    }
//    @Test
//    public void testError()
//    {
//        getCountException(20, null, "에러 : Factor가 null 입니다.");
//
//    }
//    
//    private void getCountException(final int num, final Factor factor, final String errorMessage)
//    {
//        try
//        {
//            Factorial.getCount(num, factor);
//            fail();
//        }
//        catch ( UbiveloxException e )
//        {
//            assertEquals(errorMessage, e.getMessage());
//        }
//    }
}
