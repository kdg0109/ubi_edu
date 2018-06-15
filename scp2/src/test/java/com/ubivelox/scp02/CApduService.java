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

import exception.UbiveloxException;

public interface CApduService
{
    // C-APDU 구현
    public String sendApdu(String hexString) throws GaiaException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException, InvalidAlgorithmParameterException, UbiveloxException;
}
