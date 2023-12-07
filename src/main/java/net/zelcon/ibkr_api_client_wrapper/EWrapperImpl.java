package net.zelcon.ibkr_api_client_wrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;
import java.lang.System.Logger;
import java.text.MessageFormat;

import com.ib.client.*;

import net.zelcon.ibkr_api_client_wrapper.errors.TWSException;
import net.zelcon.ibkr_api_client_wrapper.response_types.ContractDetailsResponse;
import net.zelcon.ibkr_api_client_wrapper.response_types.HistoricalBar;

public class EWrapperImpl implements EWrapper {
    private static final Logger logger = System.getLogger(EWrapperImpl.class.getName());
    private final RequestBus requestBus;

    public EWrapperImpl(final RequestBus requestBus) {
        this.requestBus = requestBus;
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickPrice'");
    }

    @Override
    public void tickSize(int tickerId, int field, Decimal size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickSize'");
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta,
            double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickOptionComputation'");
    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickGeneric'");
    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickString'");
    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints,
            double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact,
            double dividendsToLastTradeDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickEFP'");
    }

    @Override
    public void orderStatus(int orderId, String status, Decimal filled, Decimal remaining, double avgFillPrice,
            int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orderStatus'");
    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openOrder'");
    }

    @Override
    public void openOrderEnd() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'openOrderEnd'");
    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAccountValue'");
    }

    @Override
    public void updatePortfolio(Contract contract, Decimal position, double marketPrice, double marketValue,
            double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePortfolio'");
    }

    @Override
    public void updateAccountTime(String timeStamp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAccountTime'");
    }

    @Override
    public void accountDownloadEnd(String accountName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accountDownloadEnd'");
    }

    @Override
    public void nextValidId(int orderId) {
        logger.log(Logger.Level.INFO, "Next valid order ID: " + orderId);
        RequestIdGenerator.getInstance().reset(orderId);
    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {
        requestBus.getPublisher(reqId).ifPresent(publisher -> {
            publisher.submit(new ContractDetailsResponse(reqId, contractDetails));
        });
    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {
        requestBus.getPublisher(reqId).ifPresent(publisher -> {
            publisher.submit(new ContractDetailsResponse(reqId, contractDetails));
        });
    }

    @Override
    public void contractDetailsEnd(int reqId) {
        requestBus.getPublisher(reqId).ifPresent(IBReqPublisher::close);
    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execDetails'");
    }

    @Override
    public void execDetailsEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execDetailsEnd'");
    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, Decimal size) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMktDepth'");
    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price,
            Decimal size, boolean isSmartDepth) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateMktDepthL2'");
    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateNewsBulletin'");
    }

    @Override
    public void managedAccounts(String accountsList) {
        logger.log(Logger.Level.INFO, "Managed accounts: " + accountsList);
        this.requestBus.getManagedAccountsPublisher().submit(accountsList);
    }

    @Override
    public void receiveFA(int faDataType, String xml) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveFA'");
    }

    @Override
    public void historicalData(int reqId, Bar bar) {
        this.requestBus.getPublisher(reqId).ifPresent(pub -> {
            pub.submit(new HistoricalBar(reqId, bar));
        });
    }

    @Override
    public void scannerParameters(String xml) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scannerParameters'");
    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark,
            String projection, String legsStr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scannerData'");
    }

    @Override
    public void scannerDataEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'scannerDataEnd'");
    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, Decimal volume,
            Decimal wap, int count) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'realtimeBar'");
    }

    @Override
    public void currentTime(long time) {
        logger.log(Logger.Level.INFO, "currentTime: " + time);
        this.requestBus.getCurrentTimePublisher().submit(time);
    }

    @Override
    public void fundamentalData(int reqId, String data) {
        logger.log(Logger.Level.WARNING,
                "Received `fundamentalData`: This is a deprecated TWS API method.");
    }

    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deltaNeutralValidation'");
    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickSnapshotEnd'");
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'marketDataType'");
    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'commissionReport'");
    }

    @Override
    public void position(String account, Contract contract, Decimal pos, double avgCost) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'position'");
    }

    @Override
    public void positionEnd() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'positionEnd'");
    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accountSummary'");
    }

    @Override
    public void accountSummaryEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accountSummaryEnd'");
    }

    @Override
    public void verifyMessageAPI(String apiData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyMessageAPI'");
    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyCompleted'");
    }

    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyAndAuthMessageAPI'");
    }

    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyAndAuthCompleted'");
    }

    @Override
    public void displayGroupList(int reqId, String groups) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayGroupList'");
    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayGroupUpdated'");
    }

    @Override
    public void error(Exception e) {
        logger.log(Logger.Level.ERROR, e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void error(String str) {
        logger.log(Logger.Level.ERROR, str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        logger.log(Logger.Level.ERROR,
                "Error: " + id + " " + errorCode + " " + errorMsg + " " + advancedOrderRejectJson);
        requestBus.getPublisher(id).ifPresent(pub -> {
            final var except = new TWSException(errorCode, errorMsg);
            if (advancedOrderRejectJson != null && advancedOrderRejectJson.length() > 0) {
                except.setAdvancedOrderRejectJson(advancedOrderRejectJson);
            }
            pub.closeExceptionally(except);
        });
    }

    @Override
    public void connectionClosed() {
        logger.log(Logger.Level.INFO, "Connection closed");
        requestBus.close();
    }

    @Override
    public void connectAck() {
        logger.log(Logger.Level.INFO, "Connection acknowledged");
    }

    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, Decimal pos,
            double avgCost) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'positionMulti'");
    }

    @Override
    public void positionMultiEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'positionMultiEnd'");
    }

    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value,
            String currency) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accountUpdateMulti'");
    }

    @Override
    public void accountUpdateMultiEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'accountUpdateMultiEnd'");
    }

    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId,
            String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'securityDefinitionOptionalParameter'");
    }

    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'securityDefinitionOptionalParameterEnd'");
    }

    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'softDollarTiers'");
    }

    @Override
    public void familyCodes(FamilyCode[] familyCodes) {
        logger.log(Logger.Level.TRACE,
                "Family Codes: " +
                        Arrays.stream(familyCodes)
                                .map(fc -> MessageFormat.format("FamilyCode: {0} (accountID={1})", fc.familyCodeStr(),
                                        fc.accountID()))
                                .collect(Collectors.joining(", ")));
        this.requestBus.getFamilyCodesPublisher().submit(familyCodes);
    }

    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'symbolSamples'");
    }

    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {
        logger.log(Logger.Level.TRACE, "historicalDataEnd: " + reqId + " " + startDateStr + " " + endDateStr);
        requestBus.getPublisher(reqId).ifPresent(IBReqPublisher::close);
    }

    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mktDepthExchanges'");
    }

    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline,
            String extraData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickNews'");
    }

    @Override
    public void smartComponents(int reqId, Map<Integer, Entry<String, Character>> theMap) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'smartComponents'");
    }

    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickReqParams'");
    }

    @Override
    public void newsProviders(NewsProvider[] newsProviders) {
        logger.log(Logger.Level.TRACE,
                "News Providers: " +
                        Arrays.stream(newsProviders)
                                .map(np -> MessageFormat.format("NewsProvider: {0} (code={1})", np.providerName(),
                                        np.providerCode()))
                                .collect(Collectors.joining(", ")));
        this.requestBus.getNewsProvidersPublisher().submit(newsProviders);

    }

    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'newsArticle'");
    }

    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalNews'");
    }

    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalNewsEnd'");
    }

    @Override
    public void headTimestamp(int reqId, String headTimestamp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'headTimestamp'");
    }

    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'histogramData'");
    }

    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalDataUpdate'");
    }

    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rerouteMktDataReq'");
    }

    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rerouteMktDepthReq'");
    }

    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'marketRule'");
    }

    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pnl'");
    }

    @Override
    public void pnlSingle(int reqId, Decimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL,
            double value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pnlSingle'");
    }

    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalTicks'");
    }

    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalTicksBidAsk'");
    }

    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalTicksLast'");
    }

    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, Decimal size,
            TickAttribLast tickAttribLast, String exchange, String specialConditions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickByTickAllLast'");
    }

    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, Decimal bidSize,
            Decimal askSize, TickAttribBidAsk tickAttribBidAsk) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickByTickBidAsk'");
    }

    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'tickByTickMidPoint'");
    }

    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orderBound'");
    }

    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completedOrder'");
    }

    @Override
    public void completedOrdersEnd() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completedOrdersEnd'");
    }

    @Override
    public void replaceFAEnd(int reqId, String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'replaceFAEnd'");
    }

    @Override
    public void wshMetaData(int reqId, String dataJson) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'wshMetaData'");
    }

    @Override
    public void wshEventData(int reqId, String dataJson) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'wshEventData'");
    }

    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone,
            List<HistoricalSession> sessions) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'historicalSchedule'");
    }

    @Override
    public void userInfo(int reqId, String whiteBrandingId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userInfo'");
    }
}
