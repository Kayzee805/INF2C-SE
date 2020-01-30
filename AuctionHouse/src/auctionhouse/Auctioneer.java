package auctionhouse;

public class Auctioneer
{
    private String name;
    private String address;

    public Auctioneer(String name, String address)
    {
        this.name = name;
        this.address = address;
    }

    public String getAuctioneerName()
    {
        return name;
    }

    public String getAuctioneerAddress()
    {
        return address;
    }

}
