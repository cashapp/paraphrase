// Copyright Square, Inc.
package app.cash.gingham.tests

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.gingham.getString
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NamedTest {
  private val context = InstrumentationRegistry.getInstrumentation().context

  @Test fun numberedSparseOne() {
    val formattedResource = FormattedResources.named_one("Z")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("one", "Z")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B")
  }

  @Test fun numberedSparseThree() {
    val formattedResource = FormattedResources.named_three("Z", "Y", "X")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("one", "Z", "two", "Y", "three", "X")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B Y C X D")
  }
}
