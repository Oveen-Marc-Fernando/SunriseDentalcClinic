package controller;

import dao.InventoryDAO;
import java.util.Collections;
import model.InventoryModel;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import support.ConsolePrintingRule;

/**
 * Unit tests for {@link InventoryManagementController}'s static DAO-facing
 * methods, faking out {@code InventoryDAO} via {@code mockConstruction} (see
 * RegisterControllerTest). The instance wizard methods
 * ({@code goNextFromStep1}, {@code submitFromStep2}, {@code goBackFromStep2})
 * are deliberately not covered here — they construct real Swing views
 * ({@code new view.OS_IM_2(...)}) and pop real {@code JOptionPane} dialogs,
 * which is UI behavior, not something a unit test drives.
 */
public class InventoryManagementControllerTest {

    private static MockedConstruction<InventoryDAO> construction;
    private static InventoryDAO mockInventoryDao;

    @BeforeClass
    public static void mockInventoryDaoConstruction() {
        construction = mockConstruction(InventoryDAO.class);
        InventoryManagementController.count(); // forces the static field's construction now, inside this scope
        mockInventoryDao = construction.constructed().get(0);
    }

    @AfterClass
    public static void closeConstructionMock() {
        construction.close();
    }

    @Rule
    public ConsolePrintingRule printResult = new ConsolePrintingRule();

    @Before
    public void resetMock() {
        reset(mockInventoryDao);
    }

    @Test
    public void getAll_delegatesToFindAll() {
        InventoryModel row = new InventoryModel();
        row.setProductId("P1");
        when(mockInventoryDao.findAll()).thenReturn(Collections.singletonList(row));
        assertEquals(1, InventoryManagementController.getAll().size());
    }

    @Test
    public void count_isTheSizeOfFindAll() {
        InventoryModel a = new InventoryModel();
        InventoryModel b = new InventoryModel();
        when(mockInventoryDao.findAll()).thenReturn(java.util.Arrays.asList(a, b));
        assertEquals(2, InventoryManagementController.count());
    }

    @Test
    public void delete_delegatesToDaoAndReturnsItsResult() {
        when(mockInventoryDao.delete("P1")).thenReturn(true);
        assertTrue(InventoryManagementController.delete("P1"));

        when(mockInventoryDao.delete("P2")).thenReturn(false);
        assertFalse(InventoryManagementController.delete("P2"));
    }

    @Test
    public void setPublished_delegatesToDaoWithBothArguments() {
        when(mockInventoryDao.setPublished("P1", false)).thenReturn(true);
        assertTrue(InventoryManagementController.setPublished("P1", false));
        verify(mockInventoryDao).setPublished("P1", false);
    }

    @Test
    public void updateProduct_delegatesToUpsert() {
        InventoryModel m = new InventoryModel();
        m.setProductId("P1");
        InventoryManagementController.updateProduct(m);

        ArgumentCaptor<InventoryModel> captor = ArgumentCaptor.forClass(InventoryModel.class);
        verify(mockInventoryDao).upsert(captor.capture());
        assertEquals("P1", captor.getValue().getProductId());
    }

    @Test
    public void nextProductId_delegatesToDao() {
        when(mockInventoryDao.nextProductId()).thenReturn("I104");
        assertEquals("I104", InventoryManagementController.nextProductId());
    }

    // ── Instance model construction (no DAO/UI involved) ────────────────────

    @Test
    public void defaultConstructor_startsWithAFreshEmptyModel() {
        InventoryManagementController c = new InventoryManagementController();
        assertNotNull(c.getInventoryModel());
    }

    @Test
    public void modelConstructor_keepsTheGivenModelInstance() {
        InventoryModel existing = new InventoryModel();
        existing.setProductId("P99");
        InventoryManagementController c = new InventoryManagementController(existing);
        assertSame(existing, c.getInventoryModel());
    }
}
