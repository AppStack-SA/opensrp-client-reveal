package org.smartregister.reveal.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.smartregister.reveal.util.Constants.Intervention.LARVAL_DIPPING;
import static org.smartregister.reveal.util.Constants.Intervention.MOSQUITO_COLLECTION;
import static org.smartregister.reveal.util.Constants.JsonForm.THAILAND_LARVAL_DIPPING_FORM;
import static org.smartregister.reveal.util.Constants.JsonForm.THAILAND_MOSQUITO_COLLECTION_FORM;
import static org.smartregister.reveal.util.Constants.LARVAL_DIPPING_EVENT;
import static org.smartregister.reveal.util.Constants.MOSQUITO_COLLECTION_EVENT;

/**
 * Created by Vincent Karuri on 25/04/2019
 */
public class RevealJsonFormUtilsTest {

    private RevealJsonFormUtils jsonFormUtils = new RevealJsonFormUtils();

    @Test
    public void testGetFormNameShouldReturnThailandLarvalDippingFormForLarvalDippingEvent() {
        assertEquals(jsonFormUtils.getFormName(LARVAL_DIPPING_EVENT, null), THAILAND_LARVAL_DIPPING_FORM);
    }

    @Test
    public void testGetFormNameShouldReturnThailandLarvalDippingFormForLarvalDippingIntervention() {
        assertEquals(jsonFormUtils.getFormName(null, LARVAL_DIPPING), THAILAND_LARVAL_DIPPING_FORM);
    }

    @Test
    public void testGetFormNameShouldReturnThailandMosquitoCollectionFormForLarvalDippingEvent() {
        assertEquals(jsonFormUtils.getFormName(MOSQUITO_COLLECTION_EVENT, null), THAILAND_MOSQUITO_COLLECTION_FORM);
    }

    @Test
    public void testGetFormNameShouldReturnThailandMosquitoCollectionFormForLarvalDippingIntervention() {
        assertEquals(jsonFormUtils.getFormName(null, MOSQUITO_COLLECTION), THAILAND_MOSQUITO_COLLECTION_FORM);
    }
}