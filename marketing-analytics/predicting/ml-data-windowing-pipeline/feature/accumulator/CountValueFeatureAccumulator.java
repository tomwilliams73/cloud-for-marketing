// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.corp.gtech.ads.datacatalyst.components.mldatawindowingpipeline.feature.accumulator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

/**
 * Feature accumulator to extract the sum of values of a fact. Example: Extract the count of 'New
 * York' from all the values of the fact 'city' as a feature.
 */
public class CountValueFeatureAccumulator extends FeatureAccumulator {

  private final Bag<String> valueBag;

  public CountValueFeatureAccumulator(
      String column, ImmutableMap<String, String> valueToFeatureName, String defaultValue) {
    super(column, valueToFeatureName, defaultValue);
    valueBag = new HashBag<>();
  }

  @Override
  public void accumulate(String value) {
    if (!getValueToFeatureName().containsKey(value) && !Strings.isNullOrEmpty(getDefaultValue())) {
      valueBag.add(getDefaultValue());
    } else {
      valueBag.add(value);
    }
  }

  @Override
  public Map<String, Object> getFeatures() {

    ImmutableMap.Builder<String, Object> features = ImmutableMap.builder();

    for (Map.Entry<String, String> valueEntry : getValueToFeatureName().entrySet()) {
      // Get all the count if valueToFeatureName's key and factName are equal and there is only 1
      // value.
      if (valueEntry.getKey().equals(getFactName()) && getValueToFeatureName().size() == 1) {
        features.put(valueEntry.getValue(), valueBag.size());
      } else {
        features.put(valueEntry.getValue(), valueBag.getCount(valueEntry.getKey()));
      }
    }
    return features.build();
  }
}
