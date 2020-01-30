/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class AuctionHouseTest
{

    private static final double BUYER_PREMIUM = 10.0;
    private static final double COMMISSION = 15.0;
    private static final Money INCREMENT = new Money("10.00");
    private static final String HOUSE_ACCOUNT = "AH A/C";
    private static final String HOUSE_AUTH_CODE = "AH-auth";

    private AuctionHouse house;
    private MockMessagingService messagingService;
    private MockBankingService bankingService;

    /*
     * Utility methods to help shorten test text.
     */
    private static void assertOK(Status status)
    {
        assertEquals(Status.Kind.OK, status.kind);
    }

    private static void assertError(Status status)
    {
        assertEquals(Status.Kind.ERROR, status.kind);
    }

    private static void assertSale(Status status)
    {
        assertEquals(Status.Kind.SALE, status.kind);
    }

    private static void assertNOSale(Status status)
    {
        assertEquals(Status.Kind.NO_SALE, status.kind);
    }

    private static void assertPending(Status status)
    {
        assertEquals(Status.Kind.SALE_PENDING_PAYMENT, status.kind);
    }

    /*
     * Logging functionality
     */

    // Convenience field. Saves on getLogger() calls when logger object needed.
    private static Logger logger;

    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;

    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger()
    {

        logger = Logger.getLogger("auctionhouse");
        logger.setLevel(loggingLevel);

        // Ensure the root handler passes on all messages at loggingLevel and above
        // (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName)
    {
        return LS + "#############################################################" + LS
                + "TESTCASE: " + testCaseName + LS
                + "#############################################################";
    }

    @Before
    public void setup()
    {
        messagingService = new MockMessagingService();
        bankingService = new MockBankingService();

        house = new AuctionHouseImp(new Parameters(BUYER_PREMIUM, COMMISSION, INCREMENT,
                HOUSE_ACCOUNT, HOUSE_AUTH_CODE, messagingService, bankingService));

    }

    /*
     * Setup story running through all the test cases.
     * 
     * Story end point is made controllable so that tests can check story prefixes
     * and branch off in different ways.
     */
    private void runStory(int endPoint)
    {
        assertOK(house.registerSeller("SellerY", "@SellerY", "SY A/C"));
        assertOK(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C"));

        if (endPoint == 1) return;

        assertOK(house.addLot("SellerY", 2, "Painting", new Money("200.00")));
        assertOK(house.addLot("SellerY", 1, "Bicycle", new Money("80.00")));
        assertOK(house.addLot("SellerZ", 5, "Table", new Money("100.00")));
        assertOK(house.addLot("SellerZ", 10, "Chair", new Money("100.00")));

        // for close auction test
        assertOK(house.addLot("SellerZ", 100, "House", new Money("2000.00")));

        if (endPoint == 2) return;

        assertOK(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));
        assertOK(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));

        if (endPoint == 3) return;

        assertOK(house.noteInterest("BuyerA", 1));
        assertOK(house.noteInterest("BuyerA", 5));
        assertOK(house.noteInterest("BuyerB", 1));
        assertOK(house.noteInterest("BuyerB", 2));

        // for closeauction
        assertOK(house.noteInterest("BuyerA", 100));
        assertOK(house.noteInterest("BuyerC", 100));

        if (endPoint == 4) return;

        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 1));

        messagingService.expectAuctionOpened("@BuyerA", 1);
        messagingService.expectAuctionOpened("@BuyerB", 1);
        messagingService.expectAuctionOpened("@SellerY", 1);
        messagingService.verify();

        // for make bid purposes
        assertOK(house.openAuction("Auctioneer2", "@Auctioneer2", 5));

        messagingService.expectAuctionOpened("@BuyerA", 5);
        messagingService.expectAuctionOpened("@SellerZ", 5);
        messagingService.verify();

        // for close auction test at the end
        assertOK(house.openAuction("Auctioneer3", "@Auctioneer3", 100));
        messagingService.expectAuctionOpened("@BuyerA", 100);
        messagingService.expectAuctionOpened("@BuyerC", 100);
        messagingService.expectAuctionOpened("@SellerZ", 100);
        messagingService.verify();

        if (endPoint == 5) return;

        Money m70 = new Money("70.00");
        assertOK(house.makeBid("BuyerA", 1, m70));

        messagingService.expectBidReceived("@BuyerB", 1, m70);
        messagingService.expectBidReceived("@Auctioneer1", 1, m70);
        messagingService.expectBidReceived("@SellerY", 1, m70);
        messagingService.verify();
        if (endPoint == 6) return;

        Money m100 = new Money("100.00");
        assertOK(house.makeBid("BuyerB", 1, m100));

        messagingService.expectBidReceived("@BuyerA", 1, m100);
        messagingService.expectBidReceived("@Auctioneer1", 1, m100);
        messagingService.expectBidReceived("@SellerY", 1, m100);
        messagingService.verify();

        // for close auction at the end
        assertOK(house.makeBid("BuyerC", 100, m100));

        messagingService.expectBidReceived("@BuyerA", 100, m100);
        messagingService.expectBidReceived("@Auctioneer3", 100, m100);
        messagingService.expectBidReceived("@SellerZ", 100, m100);
        messagingService.verify();

        if (endPoint == 7) return;

        assertSale(house.closeAuction("Auctioneer1", 1));
        messagingService.expectLotSold("@BuyerA", 1);
        messagingService.expectLotSold("@BuyerB", 1);
        messagingService.expectLotSold("@SellerY", 1);
        messagingService.verify();

        bankingService.expectTransfer("BB A/C", "BB-auth", "AH A/C", new Money("110.00"));
        bankingService.expectTransfer("AH A/C", "AH-auth", "SY A/C", new Money("85.00"));
        bankingService.verify();

        // assertError(house.closeAuction(("Auctioneer2",2));

    }

    @Test
    public void testEmptyCatalogue()
    {
        logger.info(makeBanner("emptyLotStore"));

        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);

    }

    @Test
    public void testRegisterSeller()
    {
        logger.info(makeBanner("testRegisterSeller"));
        runStory(1);
    }

    @Test
    public void testRegisterSellerDuplicateNames()
    {
        logger.info(makeBanner("testRegisterSellerDuplicateNames"));
        runStory(1);
        assertError(house.registerSeller("SellerY", "@SellerZ", "SZ A/C"));
        // checks for already existing seller names
    }

    @Test
    public void testAddLot()
    {
        logger.info(makeBanner("testAddLot"));
        runStory(2);
    }

    @Test
    public void testAddLotErrors()
    {
        logger.info(makeBanner("testAddLotErrors"));
        runStory(2);
        assertError(house.addLot("SellerA", 90, "Table", new Money("100.00")));
        // checks for unregistered seller trying to add lot
        assertError(house.addLot("SellerY", 5, "Table", new Money("200.00")));
        // checks for already existing lots
        
        
        assertError(house.addLot("SellerY", 29, "Dummy", new Money("-20.00")));
        // checks for reserve price 0 or under
    }

    @Test
    public void testViewCatalogue()
    {
        logger.info(makeBanner("testViewCatalogue"));
        runStory(2);

        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        expectedCatalogue.add(new CatalogueEntry(1, "Bicycle", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(2, "Painting", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(5, "Table", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(10, "Chair", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(100, "House", LotStatus.UNSOLD));
        //had to add newly added lots to the catalogue here for further testing

        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);
    }

    @Test
    public void testRegisterBuyer()
    {
        logger.info(makeBanner("testRegisterBuyer"));
        runStory(3);
    }

    @Test
    public void testRegisterBuyerDuplicate()
    {
        logger.info(makeBanner("testRegisterBuyerDuplicate"));

        runStory(3);
        assertError(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        // checks for duplicate buyer name

    }

    @Test
    public void testNoteInterest()
    {
        logger.info(makeBanner("testNoteInterest"));
        runStory(4);
    }

    @Test
    public void testNoteInterestErrors()
    {
        logger.info(makeBanner("testNoteInterestErrors"));
        runStory(4);
        assertError(house.noteInterest("BuyerA", 1));
        // checks for already noted Interest
        assertError(house.noteInterest("BuyerZ", 5));
        // checks for not registered user
        assertError(house.noteInterest("BuyerA", 15));
        // checks for not registered Lot
    }

    @Test
    public void testOpenAuction()
    {
        logger.info(makeBanner("testOpenAuction"));
        runStory(5);
    }

    @Test
    public void testOpenAuctionErrors()
    {
        logger.info(makeBanner("testOpenAuctionErrors"));
        runStory(5);
        assertError(house.openAuction("Auctioneer1", "@Auctioneer1", 15));
        // opening auction for lot that is not registered

        assertError(house.openAuction("Auctioneer2", "@Auctioneer2", 10));
        messagingService.verify();
        // checks for no user interested
        assertError(house.openAuction("Auctioneer1", "@Auctioneer1", 1));
        // checks for lot already in auction

    }

    @Test
    public void testMakeBid()
    {
        logger.info(makeBanner("testMakeBid"));
        runStory(7);

    }

    @Test
    public void testMakeBidErrors()
    {
        logger.info(makeBanner("testMakeBidErrors"));
        runStory(7);

        Money m110 = new Money("100.00");
        assertError(house.makeBid("BuyerB", 1, m110));
        // bidding to less or equal to the current bid
        Money m111 = new Money("110.00");
        assertOK(house.makeBid("BuyerB", 1, m111));

        messagingService.expectBidReceived("@BuyerA", 1, m111);
        // not sent to BuyerB because they are the one that placed the bid
        messagingService.expectBidReceived("@Auctioneer1", 1, m111);
        messagingService.expectBidReceived("@SellerY", 1, m111);
        messagingService.verify();
        // bid has to be greater or equal than old bid plus the increment which is 10
        // for this test

        Money m3 = new Money("130.00");
        assertError(house.makeBid("BuyerC", 1, m3));
        // buyer who has not noted interested trying to bid
        assertError(house.makeBid("BuyerB", 2, m3));
        // buyer trying to bid on lot thats not in auction

        Money m0 = new Money("0.00");
        assertError(house.makeBid("BuyerA", 5, m0));
        // first bid has to be greater than 0
        assertError(house.makeBid("BuyerA", 22, m3));
        // trying to bid on a lot that doesnt exist

    }

    @Test
    public void testCloseAuctionWithSale()
    {
        logger.info(makeBanner("testCloseAuctionWithSale"));
        runStory(8);

    }

    @Test
    public void testCloseAuctionWithSaleErrors()
    {
        logger.info(makeBanner("testCloseAuctionWithSaleErrors"));
        runStory(7);
        // apparently closeAuction should only use status sale,no sale and sale pending
        // asked the instructor he said you can use errors for some scenario
        assertError(house.closeAuction("Auctioneer1", 2));
        // idk why this doesnt work...
        // trying to close a lot thats not open returns a null pointer exception error
        // fix** changed the order of the errors

        assertError(house.closeAuction("Auctioneer2", 15));
        // closing lot for unexisting lot
        assertError(house.closeAuction("Auctioneer2", 1));

    }

    @Test
    public void testCloseAuctionWithLessReserve()
    {
        logger.info(makeBanner("testCloseAuctionWithLessReserve"));
        runStory(8);
        assertNOSale(house.closeAuction("Auctioneer3", 100));
        // made a utility function assertNOSale to match with the return type
        // for when highest bid was less then reserve price

        // following is for status check 1. for buyer then the other for seller

    }

    @Test
    public void testCloseAuctionWithBadAccount()
    {
        logger.info(makeBanner("testCloseAuctionWithBadAccounts"));
        runStory(8);

        Money m3000 = new Money("3000.00");
        assertOK(house.makeBid("BuyerA", 100, m3000));
        // to check for pending payment
        messagingService.expectBidReceived("@BuyerC", 100, m3000);
        messagingService.expectBidReceived("@Auctioneer3", 100, m3000);
        messagingService.expectBidReceived("@SellerZ", 100, m3000);
        messagingService.verify();

        /*
         * made the bid so it was higher than the reserve price then added BuyerA's bank
         * account to the list of badAccounts and then tried to run the closeAuction
         * which should result to pending
         */
        bankingService.setBadAccount("BA A/C");
        assertPending(house.closeAuction("Auctioneer3", 100));
        // pending is expected to return because the transfer from buyer failed

    }
    @Test
    public void testNoteInterestSold()
    {
        logger.info(makeBanner("testNoteInterestSold"));
        runStory(8);
        //to check for when item is already sold
        assertOK(house.makeBid("BuyerA", 5, new Money("1000.00")));
        
        house.closeAuction("Auctioneer2", 5);
        assertError(house.noteInterest("BuyerA", 5));
        //trying to bid on a lot that is already sold i.e not unsold or not inAuction
        

    }
    
}

