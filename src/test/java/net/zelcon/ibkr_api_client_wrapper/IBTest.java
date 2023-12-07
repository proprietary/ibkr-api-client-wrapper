package net.zelcon.ibkr_api_client_wrapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ib.client.*;

import net.zelcon.ibkr_api_client_wrapper.errors.TWSConnectionTimeoutException;
import net.zelcon.ibkr_api_client_wrapper.errors.TWSException;

public class IBTest {
    @Test
    @DisplayName("Smoke test")
    public void smokeTest() {
        assertTrue(true);
    }

    @Test
    public void environmentVariablesExistTest() {
        assertNotNull(System.getenv("TWS_HOST"));
        assertTrue(System.getenv("TWS_HOST").length() > 0);
        assertNotNull(System.getenv("TWS_PORT"));
        assertTrue(System.getenv("TWS_PORT").length() > 0);
        assertDoesNotThrow(() -> {
            Integer.parseInt(System.getenv("TWS_PORT"));
        });
    }

    @Test
    public void testGetCurrentTime() {
        try (IB ib = IB.connect(System.getenv("TWS_HOST"), Integer.parseInt(System.getenv("TWS_PORT")), 1)) {
            ib.reqCurrentTime().thenAccept((currentTime) -> {
                assertNotNull(currentTime);
                assertTrue(currentTime > 0);
            }).join();
        } catch (TWSConnectionTimeoutException e) {
            fail(e);
        }
    }

    @Test
    public void testGetCommonStockContractDetails() {
        try (final IB ib = IB.connect(System.getenv("TWS_HOST"), Integer.parseInt(System.getenv("TWS_PORT")), 1)) {
            Contract nvda = new Contract();
            nvda.symbol("NVDA");
            nvda.exchange("SMART");
            nvda.secType("STK");
            nvda.currency("USD");
            ib.reqContractDetails(nvda).thenAccept((contractDetails) -> {
                assertNotNull(contractDetails);
                assertTrue(contractDetails.size() > 0);
                ContractDetails firstContractDetails = contractDetails.get(0).getContractDetails();
                assertNotNull(firstContractDetails);
                assertEquals("NVDA", firstContractDetails.contract().symbol());
                assertEquals("SMART", firstContractDetails.contract().exchange());
                assertEquals("STK", firstContractDetails.contract().secType().getApiString());
                assertEquals("USD", firstContractDetails.contract().currency());
                assertEquals("Technology", firstContractDetails.industry());
                assertEquals("Semiconductors", firstContractDetails.category());
            }).join();
        } catch (TWSConnectionTimeoutException e) {
            fail(e);
        } catch (TWSException e) {
            fail(e);
        }
    }
}
