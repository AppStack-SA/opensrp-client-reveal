package org.smartregister.reveal.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowToast;
import org.smartregister.domain.Task;
import org.smartregister.reveal.BaseUnitTest;
import org.smartregister.reveal.R;
import org.smartregister.reveal.adapter.StructureTaskAdapter;
import org.smartregister.reveal.contract.StructureTasksContract;
import org.smartregister.reveal.model.StructureTaskDetails;
import org.smartregister.reveal.presenter.ValidateUserLocationPresenter;
import org.smartregister.reveal.util.Constants.BusinessStatus;
import org.smartregister.reveal.util.LocationUtils;
import org.smartregister.reveal.util.PreferencesUtil;
import org.smartregister.reveal.util.RevealJsonFormUtils;
import org.smartregister.reveal.util.TestingUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.ona.kujaku.utils.Constants.RequestCode;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.reveal.util.Constants.JSON_FORM_PARAM_JSON;
import static org.smartregister.reveal.util.Constants.REQUEST_CODE_GET_JSON_FRAGMENT;

/**
 * Created by samuelgithengi on 4/24/19.
 */
public class StructureTasksFragmentTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private StructureTasksContract.Presenter presenter;

    @Mock
    private ProgressDialog progressDialog;

    @Mock
    private LocationUtils locationUtils;

    @Mock
    private RevealJsonFormUtils jsonFormUtils;

    @Mock
    private StructureTaskAdapter adapter;

    @Mock
    private ValidateUserLocationPresenter locationPresenter;

    private StructureTasksFragment fragment;

    private Context context = RuntimeEnvironment.application;

    private AppCompatActivity activity;

    @Before
    public void setUp() {
        fragment = new StructureTasksFragment();
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().get();
        activity.setContentView(R.layout.activity_family_profile);
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Tasks").commit();
    }

    @Test
    public void testOnCreate() {
        assertNotNull(Whitebox.getInternalState(fragment, "tabLayout"));
        assertNotNull(Whitebox.getInternalState(fragment, "jsonFormUtils"));
        assertNotNull(Whitebox.getInternalState(fragment, "locationUtils"));
    }

    @Test
    public void testOnCreateView() {
        PreferencesUtil.getInstance().setCurrentCampaignId("IRS_2019_S1");
        fragment.onCreateView(LayoutInflater.from(context), null, null);
        assertNotNull(Whitebox.getInternalState(fragment, "taskRecyclerView"));
        assertNotNull(Whitebox.getInternalState(fragment, "progressDialog"));
        assertNotNull(Whitebox.getInternalState(fragment, "adapter"));
    }

    @Test
    public void testSetStructure() {
        String id = UUID.randomUUID().toString();
        Whitebox.setInternalState(fragment, "presenter", presenter);
        fragment.setStructure(id);
        verify(presenter).findTasks(id);
    }

    @Test
    public void testDisplayToast() {
        fragment.displayToast("message");
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo("message"));
    }


    @Test
    public void testShowProgressDialog() {
        Whitebox.setInternalState(fragment, "progressDialog", progressDialog);
        fragment.showProgressDialog(R.string.saving_title, R.string.saving_message);
        verify(progressDialog).show();
        verify(progressDialog).setTitle(R.string.saving_title);
        verify(progressDialog).setMessage(context.getString(R.string.saving_message));

    }


    @Test
    public void testHideProgressDialog() {
        Whitebox.setInternalState(fragment, "progressDialog", progressDialog);
        fragment.hideProgressDialog();
        verify(progressDialog).dismiss();

    }


    @Test
    public void testRequestUserLocation() {
        Whitebox.setInternalState(fragment, "locationUtils", locationUtils);
        fragment.requestUserLocation();
        verify(locationUtils).checkLocationSettingsAndStartLocationServices(any(), any());
        assertTrue(Whitebox.getInternalState(fragment, "hasRequestedLocation"));
    }

    @Test
    public void testGetUserCurrentLocation() {
        Location location = new Location(getClass().getName());
        when(locationUtils.getLastLocation()).thenReturn(location);
        Whitebox.setInternalState(fragment, "locationUtils", locationUtils);
        assertEquals(location, fragment.getUserCurrentLocation());
        verify(locationUtils).getLastLocation();
    }


    @Test
    public void testGetJsonFormUtils() {
        Whitebox.setInternalState(fragment, "jsonFormUtils", jsonFormUtils);
        assertEquals(jsonFormUtils, fragment.getJsonFormUtils());
    }

    @Test
    public void testStartForm() {
        JSONObject form = new JSONObject();
        Whitebox.setInternalState(fragment, "jsonFormUtils", jsonFormUtils);
        fragment.startForm(form);
        verify(jsonFormUtils).startJsonForm(form, activity, REQUEST_CODE_GET_JSON_FRAGMENT);
    }

    @Test
    public void testSetTaskDetailsList() {
        Whitebox.setInternalState(fragment, "adapter", adapter);
        List<StructureTaskDetails> taskDetailsList = Collections.singletonList(TestingUtils.getStructureTaskDetails());
        fragment.setTaskDetailsList(taskDetailsList);
        verify(adapter).setTaskDetailsList(taskDetailsList);

    }

    @Test
    public void testUpdateTask() {
        Whitebox.setInternalState(fragment, "adapter", adapter);
        fragment.updateTask("task_id_11", Task.TaskStatus.COMPLETED, BusinessStatus.COMPLETE);
        verify(adapter).updateTask("task_id_11", Task.TaskStatus.COMPLETED, BusinessStatus.COMPLETE);
    }

    @Test
    public void testRefreshTasks() {
        Whitebox.setInternalState(fragment, "presenter", presenter);
        fragment.refreshTasks("121212");
        verify(presenter).findTasks("121212");
    }

    @Test
    public void testOnActivityResultForTurnOnLocationAccepted() {
        Whitebox.setInternalState(fragment, "locationUtils", locationUtils);
        Whitebox.setInternalState(fragment, "presenter", presenter);
        Whitebox.setInternalState(fragment, "hasRequestedLocation", true);
        when(presenter.getLocationPresenter()).thenReturn(locationPresenter);
        fragment.onActivityResult(RequestCode.LOCATION_SETTINGS, RESULT_OK, null);
        verify(locationUtils).requestLocationUpdates(any());
        verify(locationPresenter).waitForUserLocation();
        assertFalse(Whitebox.getInternalState(fragment, "hasRequestedLocation"));

    }

    @Test
    public void testOnActivityResultForTurnOnLocationDeclined() {
        Whitebox.setInternalState(fragment, "presenter", presenter);
        Whitebox.setInternalState(fragment, "hasRequestedLocation", true);
        when(presenter.getLocationPresenter()).thenReturn(locationPresenter);
        fragment.onActivityResult(RequestCode.LOCATION_SETTINGS, RESULT_CANCELED, null);
        verify(locationPresenter).onGetUserLocationFailed();
        assertFalse(Whitebox.getInternalState(fragment, "hasRequestedLocation"));
    }

    @Test
    public void testOnActivityResultForJSonForm() {
        Whitebox.setInternalState(fragment, "presenter", presenter);
        Intent data = new Intent();
        String form = new JSONObject().toString();
        data.putExtra(JSON_FORM_PARAM_JSON, form);
        fragment.onActivityResult(REQUEST_CODE_GET_JSON_FRAGMENT, RESULT_OK, data);
        verify(presenter).saveJsonForm(form);
        assertFalse(Whitebox.getInternalState(fragment, "hasRequestedLocation"));
    }

    @Test
    public void testSetPresenter() {
        fragment.setPresenter(presenter);
        assertEquals(presenter, Whitebox.getInternalState(fragment, "presenter"));
    }

    @Test
    public void testNewInstance() {
        Bundle bundle = new Bundle();
        fragment = StructureTasksFragment.newInstance(bundle,context);
        assertNotNull(fragment);
        assertEquals(bundle, fragment.getArguments());
        assertNotNull(Whitebox.getInternalState(fragment, "presenter"));
    }

    @Test
    public void testOnClickListener() {
        View.OnClickListener onClickListener = Whitebox.getInternalState(fragment, "onClickListener");
        fragment.setPresenter(presenter);
        assertNotNull(onClickListener);
        View view = new View(context);
        StructureTaskDetails taskDetails = TestingUtils.getStructureTaskDetails();
        view.setTag(R.id.task_details, taskDetails);
        view.setOnClickListener(onClickListener);
        view.performClick();
        verify(presenter).onTaskSelected(taskDetails);
    }

}