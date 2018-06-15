package com.ubivelox.scp02;

import static org.junit.Assert.assertEquals;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ubivelox.gaia.GaiaException;
import com.ubivelox.scp02.Scp02.Card;
import com.ubivelox.scp02.Scp02.OffCard;


import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import exception.UbiveloxException;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(CApduService.class)
public class Scp02Test
{
    @Test
    public void test2() throws GaiaException, UbiveloxException
    { 
        settingAPDU("8050000008EC78EEA2438008A6", "00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC", "848200001070CA81178C079A4A114998A816CBF511");      
        getMutualAuthentication("EC78EEA2438008A6");
        
        settingAPDU("8050000008129662F59920C835", "000091510268819506390102000A381AD0F539849DBF08E431EDC88A", "84820300104EDEB2A02F676522F80AA680FE762E76");
        getMutualAuthentication("129662F59920C835");
        
        settingAPDU("8050000008276B46913289214B", "000091510268819506390102000BD547B3AA4F2EA6EEE912DF2E44A4", "84820300100186640BBDBA5D37E484BFC0B63996E6");      
        getMutualAuthentication("276B46913289214B");
        
        settingAPDU("80500000083A492DC3FA86384C", "000091510268819506390102000C1CDDC545ED0992D8F0BB71A35D9F", "8482030010DF15C0C6C1351EE042084673FA5A46C3");      
        getMutualAuthentication("3A492DC3FA86384C");
        
        settingAPDU("80500000085A9FEC552345B239", "000091510268819506390102000DAE37775A85C803EB4515F28E9956", "848203001003D59CBCB2341016FC86E4999C913D33");      
        getMutualAuthentication("5A9FEC552345B239");
        
        settingAPDU("8050000008C4DF0A31CD8B4D95", "000091510268819506390102000E20E75E9D474F41F0C7F7D0DB7F5B", "84820300105D28FF04DF25F04ACE782987DD84DBDA");      
        getMutualAuthentication("C4DF0A31CD8B4D95");
        
        settingAPDU("805000000856E75049691AE308", "000091510268819506390102000F767538D9E255459C4DAFFC13FEAE", "8482030010FD13EE725A87FF67514CB94EC62177B5");      
        getMutualAuthentication("56E75049691AE308");    
        
        
        settingAPDU("80500000086490AE0212C81FFC", "0000915102688195063901020010468755CE6D49C7053CD4399D2A29", "84820300107BE04857CC6ABDDB714A2181489D1A34");      
        getMutualAuthentication("6490AE0212C81FFC");   
        
        
    }
    
    public void getMutualAuthentication(String hostChallenge) throws GaiaException, UbiveloxException{
        assertEquals(OffCard.InitializeUpdate_C_APDU, Scp02.initializeUpdate(hostChallenge));
        
        CApduService capduService = PowerMockito.mock(CardImpl.class);//가짜 만들기
        
        when(capduService.sendApdu(anyString())).thenReturn(OffCard.ExternalAuthenticate_C_APDU);
        
        assertEquals(OffCard.ExternalAuthenticate_C_APDU, capduService.sendApdu(""));
    }
    
    private void settingAPDU(String initializeUpdate_C_APDU, String initializeUpdate_R_APDU, String externalAuthenticate_C_APDU)
    {
        OffCard.InitializeUpdate_C_APDU = initializeUpdate_C_APDU;
        Card.InitializeUpdate_R_APDU = initializeUpdate_R_APDU;
        OffCard.ExternalAuthenticate_C_APDU = externalAuthenticate_C_APDU;
        
    }
    
    
    
    @Test
    public void test() throws UbiveloxException, GaiaException
    {                                                                //FF02
//        settingAPDU("8050000008EC78EEA2438008A6", "00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC", "848200001070CA81178C079A4A114998A816CBF511");      
//        assertEquals("8050000008EC78EEA2438008A6", Scp02.initializeUpdate("EC78EEA2438008A6"));
//        assertEquals("848200001070CA81178C079A4A114998A816CBF511", Scp02.externalAuthenticate("00009151026881950639FF02000D4EB131EA95DE5D29FCFE72F724DC"));
        
                                                                     //0102
//        settingAPDU("8050000008129662F59920C835", "000091510268819506390102000A381AD0F539849DBF08E431EDC88A", "84820300104EDEB2A02F676522F80AA680FE762E76");
//        assertEquals("8050000008129662F59920C835", Scp02.initializeUpdate("129662F59920C835"));
//        assertEquals("84820300104EDEB2A02F676522F80AA680FE762E76", Scp02.externalAuthenticate("000091510268819506390102000A381AD0F539849DBF08E431EDC88A"));
    
        
                                                                       //0102
//        settingAPDU("8050000008276B46913289214B", "000091510268819506390102000BD547B3AA4F2EA6EEE912DF2E44A4", "84820300100186640BBDBA5D37E484BFC0B63996E6");      
//        assertEquals("8050000008276B46913289214B", Scp02.initializeUpdate("276B46913289214B"));
//        assertEquals("84820300100186640BBDBA5D37E484BFC0B63996E6", Scp02.externalAuthenticate("000091510268819506390102000BD547B3AA4F2EA6EEE912DF2E44A4"));
//        
//                                                                       //0200
//        settingAPDU("80500000083A492DC3FA86384C", "000091510268819506390102000C1CDDC545ED0992D8F0BB71A35D9F", "8482030010DF15C0C6C1351EE042084673FA5A46C3");      
//        assertEquals("80500000083A492DC3FA86384C", Scp02.initializeUpdate("3A492DC3FA86384C"));
//        assertEquals("8482030010DF15C0C6C1351EE042084673FA5A46C3", Scp02.externalAuthenticate("000091510268819506390102000C1CDDC545ED0992D8F0BB71A35D9F"));
//
//                                                                       //0200
//        settingAPDU("80500000085A9FEC552345B239", "000091510268819506390102000DAE37775A85C803EB4515F28E9956", "848203001003D59CBCB2341016FC86E4999C913D33");      
//        assertEquals("80500000085A9FEC552345B239", Scp02.initializeUpdate("5A9FEC552345B239"));
//        assertEquals("848203001003D59CBCB2341016FC86E4999C913D33", Scp02.externalAuthenticate("000091510268819506390102000DAE37775A85C803EB4515F28E9956"));
//      
//                                                                       //0200
//        settingAPDU("8050000008C4DF0A31CD8B4D95", "000091510268819506390102000E20E75E9D474F41F0C7F7D0DB7F5B", "84820300105D28FF04DF25F04ACE782987DD84DBDA");      
//        assertEquals("8050000008C4DF0A31CD8B4D95", Scp02.initializeUpdate("C4DF0A31CD8B4D95"));
//        assertEquals("84820300105D28FF04DF25F04ACE782987DD84DBDA", Scp02.externalAuthenticate("000091510268819506390102000E20E75E9D474F41F0C7F7D0DB7F5B"));
//      
//      
//                                                                       //0200
//        settingAPDU("805000000856E75049691AE308", "000091510268819506390102000F767538D9E255459C4DAFFC13FEAE", "8482030010FD13EE725A87FF67514CB94EC62177B5");      
//        assertEquals("805000000856E75049691AE308", Scp02.initializeUpdate("56E75049691AE308"));
//        assertEquals("8482030010FD13EE725A87FF67514CB94EC62177B5", Scp02.externalAuthenticate("000091510268819506390102000F767538D9E255459C4DAFFC13FEAE"));
//      
//      
//      
//
//        settingAPDU("80500000086490AE0212C81FFC", "0000915102688195063901020010468755CE6D49C7053CD4399D2A29", "84820300107BE04857CC6ABDDB714A2181489D1A34");      
//        assertEquals("80500000086490AE0212C81FFC", Scp02.initializeUpdate("6490AE0212C81FFC"));
//        assertEquals("84820300107BE04857CC6ABDDB714A2181489D1A34", Scp02.externalAuthenticate("0000915102688195063901020010468755CE6D49C7053CD4399D2A29"));
      
    }
    


}
