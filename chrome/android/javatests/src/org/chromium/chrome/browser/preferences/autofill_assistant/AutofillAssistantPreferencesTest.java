// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.chrome.browser.preferences.autofill_assistant;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.SmallTest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import org.chromium.base.ContextUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.test.BaseJUnit4ClassRunner;
import org.chromium.base.test.util.Feature;
import org.chromium.chrome.browser.ChromeFeatureList;
import org.chromium.chrome.browser.history.HistoryActivity;
import org.chromium.chrome.browser.preferences.ChromeSwitchPreference;
import org.chromium.chrome.browser.preferences.MainPreferences;
import org.chromium.chrome.browser.preferences.Preferences;
import org.chromium.chrome.browser.preferences.PreferencesTest;
import org.chromium.chrome.browser.test.ChromeBrowserTestRule;
import org.chromium.chrome.test.util.browser.Features;
import org.chromium.chrome.test.util.browser.Features.DisableFeatures;
import org.chromium.chrome.test.util.browser.Features.EnableFeatures;

/**
 * Tests for the "Autofill Assisatnt" settings screen.
 */
@RunWith(BaseJUnit4ClassRunner.class)
public class AutofillAssistantPreferencesTest {
    @Rule
    public final ChromeBrowserTestRule mBrowserTestRule = new ChromeBrowserTestRule();

    @Rule
    public TestRule mProcessor = new Features.InstrumentationProcessor();

    @Rule
    public IntentsTestRule<HistoryActivity> mHistoryActivityTestRule =
            new IntentsTestRule<>(HistoryActivity.class, false, false);

    /**
     * Set the |PREF_AUTOFILL_ASSISTANT_SWITCH| shared preference to the given |value|.
     * @param value The value to set the preference to.
     */
    private void setAutofillAssistantSwitch(boolean value) {
        ContextUtils.getAppSharedPreferences()
                .edit()
                .putBoolean(AutofillAssistantPreferences.PREF_AUTOFILL_ASSISTANT_SWITCH, value)
                .apply();
    }

    /**
     * Get the |PREF_AUTOFILL_ASSISTANT_SWITCH| shared preference.
     * @param defaultValue The default value to use if the preference does not exist.
     * @return The value of the shared preference.
     */
    private boolean getAutofillAssistantSwitch(boolean defaultValue) {
        return ContextUtils.getAppSharedPreferences().getBoolean(
                AutofillAssistantPreferences.PREF_AUTOFILL_ASSISTANT_SWITCH, defaultValue);
    }

    /**
     * Ensure that the on/off switch in "Autofill Assistant" settings works.
     */
    @Test
    @SmallTest
    @Feature({"Preferences"})
    @EnableFeatures(ChromeFeatureList.AUTOFILL_ASSISTANT)
    public void testAutofillAssistantSwitch() throws Exception {
        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                setAutofillAssistantSwitch(true);
            }
        });

        final Preferences preferences =
                PreferencesTest.startPreferences(InstrumentationRegistry.getInstrumentation(),
                        AutofillAssistantPreferences.class.getName());

        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                AutofillAssistantPreferences autofillAssistantPrefs =
                        (AutofillAssistantPreferences) preferences.getFragmentForTest();
                ChromeSwitchPreference onOffSwitch =
                        (ChromeSwitchPreference) autofillAssistantPrefs.findPreference(
                                AutofillAssistantPreferences.PREF_AUTOFILL_ASSISTANT_SWITCH);
                Assert.assertTrue(onOffSwitch.isChecked());

                PreferencesTest.clickPreference(autofillAssistantPrefs, onOffSwitch);
                Assert.assertFalse(getAutofillAssistantSwitch(true));
                PreferencesTest.clickPreference(autofillAssistantPrefs, onOffSwitch);
                Assert.assertTrue(getAutofillAssistantSwitch(false));

                preferences.finish();
                setAutofillAssistantSwitch(false);
            }
        });

        final Preferences preferences2 =
                PreferencesTest.startPreferences(InstrumentationRegistry.getInstrumentation(),
                        AutofillAssistantPreferences.class.getName());
        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                AutofillAssistantPreferences autofillAssistantPrefs =
                        (AutofillAssistantPreferences) preferences2.getFragmentForTest();
                ChromeSwitchPreference onOffSwitch =
                        (ChromeSwitchPreference) autofillAssistantPrefs.findPreference(
                                AutofillAssistantPreferences.PREF_AUTOFILL_ASSISTANT_SWITCH);
                Assert.assertFalse(onOffSwitch.isChecked());
            }
        });
    }

    /**
     * Ensure that the "Autofill Assistant" setting is shown when the feature is enabled.
     */
    @Test
    @SmallTest
    @Feature({"Preferences"})
    @EnableFeatures(ChromeFeatureList.AUTOFILL_ASSISTANT)
    public void testAutofillAssistantPreferenceEnabled() throws Exception {
        final Preferences preferences = PreferencesTest.startPreferences(
                InstrumentationRegistry.getInstrumentation(), MainPreferences.class.getName());

        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                MainPreferences mainPrefs = (MainPreferences) preferences.getFragmentForTest();
                Assert.assertThat(mainPrefs.findPreference(MainPreferences.PREF_AUTOFILL_ASSISTANT),
                        is(not(nullValue())));
            }
        });
    }

    /**
     * Ensure that the "Autofill Assistant" setting is not shown when the feature is disabled.
     */
    @Test
    @SmallTest
    @Feature({"Preferences"})
    @DisableFeatures(ChromeFeatureList.AUTOFILL_ASSISTANT)
    public void testAutofillAssistantPreferenceDisabled() throws Exception {
        final Preferences preferences = PreferencesTest.startPreferences(
                InstrumentationRegistry.getInstrumentation(), MainPreferences.class.getName());

        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            @Override
            public void run() {
                MainPreferences mainPrefs = (MainPreferences) preferences.getFragmentForTest();
                Assert.assertThat(mainPrefs.findPreference(MainPreferences.PREF_AUTOFILL_ASSISTANT),
                        is(nullValue()));
            }
        });
    }
}
