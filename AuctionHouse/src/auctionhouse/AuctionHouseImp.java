/**
 * 
 */
package auctionhouse;

import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import auctionhouse.Status.Kind;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse
{

    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    // A map of buyers uniquely identified by their username
    private HashMap<String, Buyer> buyerList = new HashMap<String, Buyer>();
    // A map of sellers uniquely identified by their username
    private HashMap<String, Seller> sellerList = new HashMap<String, Seller>();
    // A map of lots uniquely identified by their lot number
    private HashMap<Integer, Lot> lotList = new HashMap<Integer, Lot>();
    // A map of CatalogueEntry items, sorted by lot number
    private TreeMap<Integer, CatalogueEntry> catalogueEntries = new TreeMap<Integer, CatalogueEntry>();
    // A map of lists of buyers interested for each lot
    // private HashMap<Integer, ArrayList<String>> interestedBuyers = new HashMap<Integer, ArrayList<String>>();
    // A map of the auctioneers allocated for the auction of each lot
    //private HashMap<Integer, Auctioneer> auctioneerLot = new HashMap<Integer, Auctioneer>();
    // A map of the highest bid for each lot
    // private HashMap<Integer, Money> lotBids = new HashMap<Integer, Money>();
    // A map of the highest bidder for each lot
    // private HashMap<Integer, String> highestBidder = new HashMap<Integer, String>();

    private Parameters parameters;

    private String startBanner(String messageName)
    {
        return LS + "-------------------------------------------------------------" + LS
                + "MESSAGE IN: " + messageName + LS
                + "-------------------------------------------------------------";
    }

    public AuctionHouseImp(Parameters parameters)
    {
        this.parameters = parameters;
    }

    public Status registerBuyer(String name, String address, String bankAccount,
            String bankAuthCode)
    {
        logger.fine(startBanner("registerBuyer " + name));
        // Check if buyer username is already used.
        if (!buyerList.containsKey(name))
        {
            
            buyerList.put(name, new Buyer(name, address, bankAccount, bankAuthCode));
            logger.fine("Buyer registered successfully");

            return Status.OK();
        }
        else
        {
            logger.warning(startBanner("Buyer is already registered."));
            return Status.error("Buyer is already registered.");
        }
    }

    public Status registerSeller(String name, String address, String bankAccount)
    {
        logger.fine(startBanner("registerSeller " + name));
        // Check if seller username is already used.
        if (!sellerList.containsKey(name))
        {
            sellerList.put(name, new Seller(name, address, bankAccount));
            logger.fine("Seller registered successfully");

            return Status.OK();
        }
        else
        {
            logger.warning(startBanner("Seller is already registered."));
            return Status.error("Seller is already registered.");
        }

    }

    public Status addLot(String sellerName, int number, String description, Money reservePrice)
    {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        if (!sellerList.containsKey(sellerName))
        {
            logger.warning(startBanner("Seller is not registered."));
            return Status.error("Seller is not registered.");
        }
        else if (catalogueEntries.containsKey(number))
        {
            logger.warning(startBanner("The lot number is already used."));
            return Status.error("The lot number is already used.");
        }
        else if (reservePrice.lessEqual(new Money("0.0")))
        {
            logger.warning(startBanner("Reserve price must be more than 0."));
            return Status.error("Reserve price must be more than 0.");
        }
        else
        {
            Lot temp = new Lot(number, description, LotStatus.UNSOLD, sellerName, reservePrice);
            lotList.put(number, temp);
            
            catalogueEntries.put(number, new CatalogueEntry(number, description, LotStatus.UNSOLD));
            logger.fine("Lot added to the catalogue successfully");
            return Status.OK();
        }
    }

    public List<CatalogueEntry> viewCatalogue()
    {
        logger.fine(startBanner("viewCatalog"));
        List<CatalogueEntry> catalogue = new ArrayList<CatalogueEntry>();
        logger.fine("Catalogue: " + catalogue.toString());
        for (int n : catalogueEntries.keySet())
        {
            catalogue.add(catalogueEntries.get(n));
        }
        return catalogue;
    }

    public Status noteInterest(String buyerName, int lotNumber)
    {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        if (!buyerList.containsKey(buyerName))
        {
            logger.warning(startBanner("User is not a registered buyer."));
            return Status.error("User is not a registered buyer.");
        }
        else if (!catalogueEntries.containsKey(lotNumber))
        {
            logger.warning(startBanner("Lot is not registered to the system."));
            return Status.error("Lot number does not exist.");
        }

        else if (lotList.get(lotNumber).getBuyers().contains(buyerName))
        {
            logger.warning(startBanner("User has already noted interest."));
            return Status.error("User has already noted interest.");
        }
        else if(lotList.get(lotNumber).getLotStatus() != LotStatus.UNSOLD 
                && lotList.get(lotNumber).getLotStatus() != LotStatus.IN_AUCTION) {
            
            logger.warning(startBanner("Lot has to be either Unsold or In auction to note Interest"));
            return Status.error("Lot has to be either Unsold or In auction to note Interest");
        }
        else
        {
            lotList.get(lotNumber).addBuyer(buyerName);
            logger.fine("Buyer noted interest on lot successfully");

            return Status.OK();
        }
    }

    public Status openAuction(String auctioneerName, String auctioneerAddress, int lotNumber)
    {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        if (!catalogueEntries.containsKey(lotNumber))
        {
            logger.warning(startBanner("Lot number does not exist."));
            return Status.error("Lot number does not exist.");
        }
        else if (lotList.get(lotNumber).getBuyers().size() == 0)
        {
            logger.warning(startBanner("No users interested to the lot."));
            return Status.error("No users interested to the lot.");
        }
        else if (lotList.get(lotNumber).getLotStatus() != LotStatus.UNSOLD)
        {
            logger.warning(startBanner("Lot is not available for auction."));
            return Status.error("Lot is not available for auction.");
        }
        else
        {
            logger.finer(startBanner("Message interested parties"));
            for (String name : lotList.get(lotNumber).getBuyers())
            {
                Buyer a = buyerList.get(name);
                String addressT = a.getBuyerAddress();
                parameters.messagingService.auctionOpened(addressT, lotNumber);
            }
            Auctioneer curAuctioneer = new Auctioneer(auctioneerName, auctioneerAddress);
            lotList.get(lotNumber).updateAuctioneer(curAuctioneer);
            lotList.get(lotNumber).setStatusinAuction();
            lotList.get(lotNumber).updateBid(new Money("0.00"));
            Seller curSeller = sellerList.get(lotList.get(lotNumber).getLotSellerName());
            String sellerAddress = curSeller.getSellerAddress();
            parameters.messagingService.auctionOpened(sellerAddress, lotNumber);

            logger.fine("Auction for lot opened successfully");

            return Status.OK();
        }
    }

    public Status makeBid(String buyerName, int lotNumber, Money bid)
    {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));
        if (!catalogueEntries.containsKey(lotNumber))
        {
            logger.warning(startBanner("Lot number does not exist."));
            return Status.error("Lot number does not exist.");
        }
        else if (!buyerList.containsKey(buyerName))
        {
            logger.warning(startBanner("User is not a registered buyer."));
            return Status.error("User is not a registered buyer.");
        }
        else if (!lotList.get(lotNumber).getBuyers().contains(buyerName))
        {
            logger.warning(startBanner("User has not noted interest to the lot."));
            return Status.error("User has not noted interest to the lot.");
        }
        else if (lotList.get(lotNumber).getLotStatus() != LotStatus.IN_AUCTION)
        {
            logger.warning(startBanner("The lot is not in auction."));
            return Status.error("The lot is not in auction.");
        }
        else if (bid.lessEqual(new Money("0.0")))
        {
            logger.warning(startBanner("The bid has to be more than 0."));
            return Status.error("The bid has to be more than 0.");
        }
        else if (lotList.get(lotNumber).getCurrentBid().equals(new Money("0"))
                || !bid.lessEqual(lotList.get(lotNumber).getCurrentBid().add(parameters.increment))
                || bid.equals(lotList.get(lotNumber).getCurrentBid().add(parameters.increment)))
        {
            lotList.get(lotNumber).updateBid(bid);
            logger.finer(startBanner("Message interested parties"));
            for (String name : lotList.get(lotNumber).getBuyers())
            {
                if (!name.equals(buyerName))
                {
                    Buyer curB = buyerList.get(name);
                    String addressT = curB.getBuyerAddress();
                    parameters.messagingService.bidAccepted(addressT, lotNumber, bid);
                }
            }
            String auctioneerAddress = lotList.get(lotNumber).getAuctioneer().getAuctioneerAddress();
            parameters.messagingService.bidAccepted(auctioneerAddress, lotNumber, bid);

            String sellerName = lotList.get(lotNumber).getLotSellerName();
            Seller curS = sellerList.get(sellerName);
            String sellerAddress = curS.getSellerAddress();
            parameters.messagingService.bidAccepted(sellerAddress, lotNumber, bid);

            lotList.get(lotNumber).updateBidder(buyerName);
            logger.fine("Buyer's bid was successfully submitted");

            return Status.OK();
        }
        else
        {
            logger.warning(startBanner("The bid submitted is not high enough."));
            return Status.error("The bid submitted is not high enough.");
        }
    }

    public Status closeAuction(String auctioneerName, int lotNumber)
    {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
        if (!lotList.containsKey(lotNumber))
        {
            logger.warning(startBanner("The lot does not exist."));
            return Status.error("The lot does not exist.");
        }
        else if (lotList.get(lotNumber).getLotStatus() != LotStatus.IN_AUCTION)
        {
            logger.warning(startBanner("Lot is not in auction."));
            return Status.error("Lot is not in auction.");
        }

        String expectedAName = lotList.get(lotNumber).getAuctioneer().getAuctioneerName();
        if (!expectedAName.equals(auctioneerName))
        {
            logger.warning(
                    startBanner("Auctioneer is not the one that opened the auction for the lot."));
            return Status.error("Auctioneer is not the one that opened the auction for the lot.");
        }

        String sellerName = lotList.get(lotNumber).getLotSellerName();
        Seller curS = sellerList.get(sellerName);
        String sellerAddress = curS.getSellerAddress();

        if (lotList.get(lotNumber).getCurrentBid().compareTo(lotList.get(lotNumber).getReservePrice()) < 0)
        {
            logger.finer(startBanner("Message interested parties for unsold lot"));
            lotList.get(lotNumber).setStatusUNSOLD();
            for (String name : lotList.get(lotNumber).getBuyers())
            {
                Buyer a = buyerList.get(name);
                String addressT = a.getBuyerAddress();
                parameters.messagingService.lotUnsold(addressT, lotNumber);
            }

            parameters.messagingService.lotUnsold(sellerAddress, lotNumber);
            
            return new Status(Kind.NO_SALE, "Reserve price not met.");

        }

        String senderAcc = buyerList.get(lotList.get(lotNumber).getHighest()).getBuyerBankAcc();
        String senderCode = buyerList.get(lotList.get(lotNumber).getHighest()).getBuyerBankAuthCode();
        String sellerAcc = sellerList.get(sellerName).getBankAcc();
        String houseAcc = parameters.houseBankAccount;
        String houseCode = parameters.houseBankAuthCode;

        Money commision = lotList.get(lotNumber).getCurrentBid().addPercent(parameters.commission)
                .subtract(lotList.get(lotNumber).getCurrentBid());
        Money sellerRecieve = lotList.get(lotNumber).getCurrentBid().subtract(commision);
        Money buyerSent = lotList.get(lotNumber).getCurrentBid().addPercent(parameters.buyerPremium);

        Status buyerStatus = parameters.bankingService.transfer(senderAcc, senderCode, houseAcc,
                buyerSent);

        Status sellerStatus = parameters.bankingService.transfer(houseAcc, houseCode, sellerAcc,
                sellerRecieve);

        if (buyerStatus.kind == Status.Kind.OK && sellerStatus.kind == Status.Kind.OK)
        {
            logger.finer(
                    startBanner("Message interested parties for sold lot and successful payment"));
            lotList.get(lotNumber).setStatusSold();
            for (String name : lotList.get(lotNumber).getBuyers())
            {
                Buyer a = buyerList.get(name);
                String addressT = a.getBuyerAddress();
                parameters.messagingService.lotSold(addressT, lotNumber);
            }

            parameters.messagingService.lotSold(sellerAddress, lotNumber);
            
            return new Status(Status.Kind.SALE, "Transaction Succesful");
        }
        else
        {
            logger.finer(startBanner(
                    "Message interested parties for sold lot but unsuccessful payment"));
            lotList.get(lotNumber).setStatusPendingPayment();
            return new Status(Status.Kind.SALE_PENDING_PAYMENT,
                    "Transaction Failed! The sale was not completed.");
        }
    }

}
