package app.cash.gingham.tests

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.gingham.FormattedResource
import app.cash.gingham.getString
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NumberedTest {
  private val context = InstrumentationRegistry.getInstrumentation().context

  @Test fun numberedContiguousOne() {
    val formattedResource = FormattedResources.numbered_contiguous_one("Z")
    assertThat(formattedResource.arguments as? Array<Any>)
      .asList()
      .containsExactly("Z")
      .inOrder()

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B")
  }

  @Test fun numberedContiguousThree() {
    val formattedResource = FormattedResources.numbered_contiguous_three("Z", "Y", "X")
    assertThat(formattedResource.arguments as? Array<Any>)
      .asList()
      .containsExactly("Z", "Y", "X")
      .inOrder()

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B Y C X D")
  }

  @Test fun numberedSparseOne() {
    val formattedResource = FormattedResources.numbered_sparse_one("Z")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("1", "Z")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B")
  }

  @Test fun numberedSparseThree() {
    val formattedResource = FormattedResources.numbered_sparse_three("Z", "Y", "X")
    assertThat(formattedResource.arguments as? Map<String, Any>)
      .containsExactly("1", "Z", "3", "Y", "5", "X")

    val formatted = context.getString(formattedResource)
    assertThat(formatted).isEqualTo("A Z B Y C X D")
  }
}
