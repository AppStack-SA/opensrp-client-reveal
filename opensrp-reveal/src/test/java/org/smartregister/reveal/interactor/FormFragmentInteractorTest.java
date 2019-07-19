package org.smartregister.reveal.interactor;


import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.reveal.BaseUnitTest;
import org.smartregister.reveal.contract.BaseFormFragmentContract;

import java.util.ArrayList;
import java.util.UUID;

import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TEXT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.reveal.util.Constants.DatabaseKeys.BASE_ENTITY_ID;
import static org.smartregister.reveal.util.Constants.DatabaseKeys.FIRST_NAME;
import static org.smartregister.reveal.util.Constants.DatabaseKeys.LAST_NAME;

/**
 * Created by samuelgithengi on 6/18/19.
 */
public class FormFragmentInteractorTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private BaseFormFragmentContract.Presenter presenter;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<JSONArray> jsonArgumentCaptor;

    private BaseFormFragmentInteractor interactor;

    @Before
    public void setUp() {
        org.smartregister.Context.bindtypes = new ArrayList<>();
        interactor = new BaseFormFragmentInteractor(presenter);
        Whitebox.setInternalState(interactor, "commonRepository", commonRepository);
        Whitebox.setInternalState(interactor, "sqLiteDatabase", sqLiteDatabase);
    }

    @Test
    public void testFindNumberOfMembers() {
        when(commonRepository.countSearchIds(anyString())).thenReturn(12);
        String structureId = UUID.randomUUID().toString();
        JSONObject form = new JSONObject();
        interactor.findNumberOfMembers(structureId, form);
        verify(presenter, timeout(ASYNC_TIMEOUT)).onFetchedMembersCount(12, form);
        verify(commonRepository, timeout(ASYNC_TIMEOUT)).countSearchIds(anyString());
    }

    @Test
    public void testFindMemberDetails() throws JSONException {
        JSONObject form = new JSONObject();
        String structureId = UUID.randomUUID().toString();
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(createCursor());
        interactor.findMemberDetails(structureId, form);
        verify(presenter, timeout(ASYNC_TIMEOUT)).onFetchedFamilyMembers(jsonArgumentCaptor.capture(), eq(form));
        assertEquals(1, jsonArgumentCaptor.getValue().length());
        JSONObject member = jsonArgumentCaptor.getValue().getJSONObject(0);
        assertEquals("69df212c-33a7-4443-a8d5-289e48d90468", member.getString(KEY));
        assertEquals("Rey Mister", member.getString(TEXT));

    }

    private Cursor createCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                BASE_ENTITY_ID,
                FIRST_NAME,
                LAST_NAME
        });
        cursor.addRow(new Object[]{
                "69df212c-33a7-4443-a8d5-289e48d90468",
                "Rey",
                "Mister"
        });
        return cursor;
    }

}