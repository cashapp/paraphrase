/*
 * Copyright (C) 2026 Cash App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.cash.paraphrase.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.Issue

public class ParaphraseIssueRegistry : IssueRegistry() {
  override val issues: List<Issue> = listOf(UnusedFormattedResourcesDetector.ISSUE)

  override val vendor: Vendor =
    Vendor(
      vendorName = "Cash App",
      identifier = "app.cash.paraphrase:paraphrase-lint",
      feedbackUrl = "https://github.com/cashapp/paraphrase/issues",
    )
}
