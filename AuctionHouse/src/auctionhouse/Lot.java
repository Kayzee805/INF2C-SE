package auctionhouse;

import java.util.ArrayList;
import java.util.HashMap;

public class Lot extends CatalogueEntry
{
    private String sellerName;
    private Money reservePrice;
    private ArrayList<String> interestedBuyerList= new ArrayList<String>();
    private Money currentBid;
    private String highestBidder;
    private Auctioneer auctioneerN;
    
    

    public Lot(int lotNumber, String description, LotStatus status, String sellerName,
            Money reservePrice)
    {
        super(lotNumber, description, status);
        this.sellerName = sellerName;
        this.reservePrice = reservePrice;
        this.currentBid = null;
        this.highestBidder = null; 
        this.auctioneerN = null;
    }
    
    public void updateBidder(String name) {
        this.highestBidder = name;
    }
    public String getHighest() {
        return highestBidder;
    }
    public void updateAuctioneer(Auctioneer name)
    {
        this.auctioneerN = name;
    }
    public Auctioneer getAuctioneer()
    {
        return this.auctioneerN;
    }
    public void updateBid(Money m) {
        this.currentBid = m;
    }
    public Money getCurrentBid() {
        return currentBid;
    }
    
    public void addBuyer(String name) {
        interestedBuyerList.add(name);
    }
    public ArrayList<String> getBuyers(){
        return interestedBuyerList;
    }
    public String getLotSellerName()
    {
        return sellerName;
    }

    public int getlotNumber()
    {
        return lotNumber;
    }

    public Money getReservePrice()
    {
        return reservePrice;
    }

    public LotStatus getLotStatus()
    {
        return status;
    }

    public void setStatusSold()
    {
        status = LotStatus.SOLD;
    }

    public void setStatusinAuction()
    {
        status = LotStatus.IN_AUCTION;
    }

    public void setStatusPendingPayment()
    {
        status = LotStatus.SOLD_PENDING_PAYMENT;
    }

    public void setStatusUNSOLD()
    {
        status = LotStatus.UNSOLD;
    }

}