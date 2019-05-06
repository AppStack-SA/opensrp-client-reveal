package org.smartregister.reveal.presenter;

import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.cloudant.models.Client;
import org.smartregister.cloudant.models.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.reveal.BaseUnitTest;
import org.smartregister.reveal.contract.FamilyProfileContract;
import org.smartregister.reveal.model.FamilyProfileModel;
import org.smartregister.reveal.util.PreferencesUtil;
import org.smartregister.reveal.util.TestingUtils;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.reveal.util.Constants.DatabaseKeys.STRUCTURE_ID;

/**
 * Created by samuelgithengi on 4/25/19.
 */
public class FamilyProfileFragmentPresenterTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private FamilyProfileContract.View view;

    @Mock
    private FamilyProfileModel model;

    @Mock
    private PreferencesUtil preferencesUtil;

    @Mock
    private SQLiteDatabase database;

    @Mock
    private FamilyProfileContract.Interactor interactor;

    private FamilyProfilePresenter presenter;

    private String familyId = UUID.randomUUID().toString();

    private String structureId = UUID.randomUUID().toString();

    @Before
    public void setUp() {
        presenter = new FamilyProfilePresenter(view, model, familyId, null, null, null);
        Whitebox.setInternalState(presenter, "preferencesUtil", preferencesUtil);
        Whitebox.setInternalState(presenter, "database", database);
        Whitebox.setInternalState(presenter, "interactor", interactor);
    }

    @Test
    public void testRefreshProfileTopSection() {
        CommonPersonObjectClient client = TestingUtils.getCommonPersonObjectClient();
        String oAname = "MTI_13";
        String district = "Chadiza";
        when(preferencesUtil.getCurrentOperationalArea()).thenReturn(oAname);
        when(preferencesUtil.getCurrentDistrict()).thenReturn(district);
        presenter.refreshProfileTopSection(client);
        verify(view).setProfileDetailOne(oAname);
        verify(view).setProfileDetailTwo(district);
    }

    @Test
    public void testGetStructureId() throws Exception {
        String sql = "SELECT DISTINCT structure_id FROM EC_FAMILY WHERE base_entity_id = ?";
        when(database.rawQuery(sql, new String[]{familyId})).thenReturn(createCursor());
        Whitebox.invokeMethod(presenter, "getStructureId", familyId);
        verify(database, timeout(ASYNC_TIMEOUT).atLeastOnce()).rawQuery(sql,
                new String[]{familyId});
        verify(model, timeout(ASYNC_TIMEOUT).atLeastOnce()).setStructureId(structureId);
        verify(view, timeout(ASYNC_TIMEOUT).atLeastOnce()).setStructureId(structureId);
        assertEquals(structureId, presenter.getStructureId());
    }

    @Test
    public void testGetView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testOnRegistrationSavedForNewForms() {
        presenter = spy(presenter);
        PreferencesUtil.getInstance().setCurrentCampaignId("FI_2019_TV01");
        Whitebox.setInternalState(presenter, "structureId", structureId);
        String baseEntityId = UUID.randomUUID().toString();
        FamilyEventClient eventClient = new FamilyEventClient(new Client(), new Event().withBaseEntityId(baseEntityId));
        when(model.getEventClient()).thenReturn(eventClient);
        presenter.onRegistrationSaved(false);
        verify(presenter, never()).onTasksGenerated();
        verify(view, never()).refreshTasks(structureId);
        verify(interactor).generateTasks(view.getApplicationContext(), baseEntityId);
    }

    @Test
    public void testOnRegistrationSavedForEditedForms() {
        presenter = spy(presenter);
        Whitebox.setInternalState(presenter, "structureId", structureId);
        presenter.onRegistrationSaved(true);
        verify(presenter).onTasksGenerated();
        verify(view).refreshTasks(structureId);
    }

    @Test
    public void testOnTasksGenerated() {
        presenter.onTasksGenerated();
        verify(view).refreshTasks(null);
    }


    private Cursor createCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                STRUCTURE_ID
        });
        cursor.addRow(new Object[]{
                structureId
        });
        return cursor;
    }


}