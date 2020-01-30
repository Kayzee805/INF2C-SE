package auctionhouse;

import java.util.ArrayList;
import java.util.HashMap;

public class Buyer
{
    private String bankAuthCode;

    private String name;
    private String address;
    private String bankAcc;
    


    public Buyer(String name, String address, String bankAcc, String bankAuthCode)
    {
        this.name = name;
        this.address = address;
        this.bankAcc = bankAcc;
        this.bankAuthCode = bankAuthCode;
    }

    public String getBuyerName()
    {
        return name;
    }

    public String getBuyerAddress()
    {
        return address;
    }

    public String getBuyerBankAcc()
    {
        return bankAcc;
    }

    public String getBuyerBankAuthCode()
    {
        return bankAuthCode;
    }

}
