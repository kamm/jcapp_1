package pl.kamm;

import javacard.framework.Applet;
import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;


public class HelloWorldApplet extends Applet {
    
    //Definicja klasy instrukcji
    protected static final byte CLA = (byte) 0xb0;

    //Definiacja identyfikatora instrukcji
    protected static final byte INS_HELLO = (byte) 0x01;

    //Obsługa instrukcji HELLO
    private void processHello(APDU apdu){
        byte buffer[] = apdu.getBuffer();

        //Sprawdzenie czy argumenty P1 i P2 są równe 0x00
        if (Util.getShort(buffer, ISO7816.OFFSET_P1) != (short) 0x0000){ 
            ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
        }

        // Zbudowanie tablicy bajtów "Hello, world!"
        byte [] b = {0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x2c, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x21};

        //Skopiowanie do bufora
        Util.arrayCopy(b,(byte)0,buffer,(byte)0,(byte)b.length); 

        //Wysłanie bufora
        apdu.setOutgoingAndSend((short) 0, (short) b.length); 
    }

    public void process(APDU apdu) {
        //W przypadku operacji wybierania apletu nie rób nic
        if (selectingApplet()){
            return;
        }

        //Pobranie bufora danych
        byte buffer[] = apdu.getBuffer();

        //Obsługujemy tylko jedną, określoną klasę instrukcji
        if (buffer[ISO7816.OFFSET_CLA] != (CLA)){
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        //Na podstawie identyfikatora instrukcji wywołujemy obsługującą ją funkcję
        switch (buffer[ISO7816.OFFSET_INS]){ 
            case INS_HELLO:
                processHello(apdu);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                break;
        }
    }

    private HelloWorldApplet(byte bArray[], short bOffset, byte bLength) {
    }

    public static void install(byte bArray[], short bOffset, byte bLength) {
        HelloWorldApplet ai = new HelloWorldApplet(bArray, bOffset, bLength);

        if (bArray[bOffset] == 0) {
            ai.register();
        } else {
            ai.register(bArray, (short) (bOffset + 1), bArray[bOffset]);
        }
    }
}
